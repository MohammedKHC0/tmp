import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_logcat/flutter_logcat.dart';
import 'package:flutter_pty/flutter_pty.dart';
import 'package:terminal_view/virtual_keyboard.dart';
import 'package:xterm/xterm.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      title: 'terminal', debugShowCheckedModeBanner: false, home: Home(),
      // shortcuts: ,
    );
  }
}

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  // ignore: library_private_types_in_public_api
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  static const platform = MethodChannel('com.shadow3.codroid/terminal');
  final terminalController = TerminalController();
  late final Pty pty;
  final keyboard = VirtualKeyboard(defaultInputHandler);
  late final terminal = Terminal(
    maxLines: 10000,
    inputHandler: keyboard,
  );

  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance.endOfFrame.then(
      (_) {
        if (mounted) _startPty();
      },
    );
    platform.setMethodCallHandler(_handleMethodCall);
  }

  Future<void> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'write':
        final String text = call.arguments;
        pty.write(const Utf8Encoder().convert(text));
        break;
      case 'clear':
        Log.i("clear");
        const clear = "clear\n";
        pty.write(const Utf8Encoder().convert(clear));
        break;
      case 'input_key':
        final String input = call.arguments;
        final TerminalKey key = _parseKeyArg(input);
        terminal.keyInput(key);
        break;
      case 'set_control_state':
        final bool state = call.arguments;
        keyboard.ctrl = state;
        break;
      case 'set_alt_state':
        final bool state = call.arguments;
        keyboard.alt = state;
        break;
      case 'set_shift_state':
        final bool state = call.arguments;
        keyboard.shift = state;
        break;
      default:
        throw MissingPluginException('Not implemented');
    }
  }

  TerminalKey _parseKeyArg(String arg) {
    switch (arg) {
      case "control":
        return TerminalKey.control;
      case "alt":
        return TerminalKey.alt;
      case "home":
        return TerminalKey.home;
      case "end":
        return TerminalKey.end;
      case "pageDown":
        return TerminalKey.pageDown;
      case "pageUp":
        return TerminalKey.pageUp;
      case "arrowUp":
        return TerminalKey.arrowUp;
      case "arrowDown":
        return TerminalKey.arrowDown;
      case "arrowLeft":
        return TerminalKey.arrowLeft;
      case "arrowRight":
        return TerminalKey.arrowRight;
      case "tab":
        return TerminalKey.tab;
      case "esc":
        return TerminalKey.escape;
      case "enter":
        return TerminalKey.enter;
      default:
        throw MissingPluginException('Not implemented key');
    }
  }

  void _startPty() {
    pty = Pty.start(
      "/system/bin/sh",
      columns: terminal.viewWidth,
      rows: terminal.viewHeight,
    );

    pty.output
        .cast<List<int>>()
        .transform(const Utf8Decoder())
        .listen(terminal.write);

    pty.exitCode.then((code) {
      terminal.write('the process exited with exit code $code');
    });

    terminal.onOutput = (data) {
      pty.write(const Utf8Encoder().convert(data));
    };

    terminal.onResize = (w, h, pw, ph) {
      pty.resize(h, w);
    };
  }

  @override
  Widget build(BuildContext context) {
    const theme = TerminalTheme(
      cursor: Color(0XAAAEAFAD),
      selection: Color(0XAAAEAFAD),
      foreground: Color(0XFFCCCCCC),
      background: Color(0XFF1E1E1E),
      black: Color(0XFF000000),
      red: Color(0XFFCD3131),
      green: Color(0XFF0DBC79),
      yellow: Color(0XFFE5E510),
      blue: Color(0XFF2472C8),
      magenta: Color(0XFFBC3FBC),
      cyan: Color(0XFF11A8CD),
      white: Color(0XFFE5E5E5),
      brightBlack: Color(0XFF666666),
      brightRed: Color(0XFFF14C4C),
      brightGreen: Color(0XFF23D18B),
      brightYellow: Color(0XFFF5F543),
      brightBlue: Color(0XFF3B8EEA),
      brightMagenta: Color(0XFFD670D6),
      brightCyan: Color(0XFF29B8DB),
      brightWhite: Color(0XFFFFFFFF),
      searchHitBackground: Color(0XFFFFFF2B),
      searchHitBackgroundCurrent: Color(0XFF31FF26),
      searchHitForeground: Color(0XFF000000),
    );
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: SafeArea(
        child: TerminalView(
          terminal,
          theme: theme,
          controller: terminalController,
          autofocus: true,
          backgroundOpacity: 0.7,
          onSecondaryTapDown: (details, offset) async {
            final selection = terminalController.selection;
            if (selection != null) {
              final text = terminal.buffer.getText(selection);
              terminalController.clearSelection();
              await Clipboard.setData(ClipboardData(text: text));
            } else {
              final data = await Clipboard.getData('text/plain');
              final text = data?.text;
              if (text != null) {
                terminal.paste(text);
              }
            }
          },
        ),
      ),
    );
  }
}
