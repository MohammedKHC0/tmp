package com.shadow3.codroid

import io.flutter.plugin.common.MethodChannel

enum class InputKeys {
    Control, Alt, Home, End, PageDown, PageUp, ArrowUp, ArrowDown, ArrowLeft, ArrowRight, Tab, Esc, Enter,
}

class FlutterTerminalMethodChannel {
    companion object {
        fun write(methodChannel: MethodChannel, cmd: String) {
            methodChannel.invokeMethod("write", cmd)
        }

        fun clear(methodChannel: MethodChannel) {
            methodChannel.invokeMethod("clear", null)
        }

        fun inputKey(
            methodChannel: MethodChannel,
            key: InputKeys,
        ) {
            val inputKey = when (key) {
                InputKeys.Control -> "control"
                InputKeys.Alt -> "alt"
                InputKeys.Home -> "home"
                InputKeys.End -> "end"
                InputKeys.PageDown -> "pageDown"
                InputKeys.PageUp -> "pageUp"
                InputKeys.ArrowUp -> "arrowUp"
                InputKeys.ArrowDown -> "arrowDown"
                InputKeys.ArrowLeft -> "arrowLeft"
                InputKeys.ArrowRight -> "arrowRight"
                InputKeys.Tab -> "tab"
                InputKeys.Esc -> "esc"
                InputKeys.Enter -> "enter"
            }

            methodChannel.invokeMethod("input_key", inputKey)
        }

        fun setControlState(methodChannel: MethodChannel, state: Boolean) {
            methodChannel.invokeMethod("set_control_state", state)
        }

        fun setAltState(methodChannel: MethodChannel, state: Boolean) {
            methodChannel.invokeMethod("set_alt_state", state)
        }

        fun setShiftState(methodChannel: MethodChannel, state: Boolean) {
            methodChannel.invokeMethod("set_shift_state", state)
        }
    }
}