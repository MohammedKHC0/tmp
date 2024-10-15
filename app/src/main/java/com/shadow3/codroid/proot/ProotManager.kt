package com.shadow3.codroid.proot

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.shadow3.codroid.AssetsUtils
import com.shadow3.codroid.jni.NativeTarHelper
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists

class ProotManager {
    companion object {
        val projectDirPath: Path
            get() {
                return Path(mContext!!.dataDir.path).resolve("archlinux-aarch64").resolve("root")
                    .resolve("CodroidProjects")
            }

        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        fun init(context: Context) {
            mContext = context
            initProotAssets()
            makeProjectDir()
        }

        fun loginToProot(exec: (String) -> Unit) {
            val dataDir = Path(mContext!!.dataDir.path)
            try {
                Files.createDirectory(dataDir.resolve("proot_tmp"))
            } catch (_: IOException) {
            }

            val envCommand = "export PROOT_TMP_DIR=${dataDir.resolve("proot_tmp")}\n"
            exec(envCommand)

            val loginCommand = "${
                dataDir.resolve("bin").resolve("proot")
            } -r ${
                dataDir.resolve("archlinux-aarch64")
            } -0 -b /dev -b /sys -b /proc --link2symlink -p -L -w /root /bin/bash -l\n"
            exec(loginCommand)
        }

        fun buildProcess(vararg commands: String): ProcessBuilder {
            val dataDir = Path(mContext!!.dataDir.path)
            val args = listOf(
                dataDir.resolve("bin").resolve("proot").toString(),
                "-r",
                dataDir.resolve("archlinux-aarch64").toString(),
                "-0",
                "-b",
                "/dev",
                "-b",
                "/sys",
                "-b",
                "/proc",
                "--link2symlink",
                "-p",
                "-L",
                "-w",
                "/root"
            ) + commands
            Log.d("ProotManager", args.toString())
            val processBuilder = ProcessBuilder(args)
            processBuilder.environment()["PROOT_TMP_DIR"] = dataDir.resolve("proot_tmp").toString()
            return processBuilder
        }

        fun androidPathToProotPath(androidPath: Path): Path {
            val dataDir = mContext!!.dataDir
            return Path(
                androidPath.toAbsolutePath().toString().replace(
                    oldValue = dataDir.resolve("archlinux-aarch64").toString(),
                    newValue = ""
                )
            )
        }

        private fun initProotAssets() {
            val dataDir = Path(mContext!!.dataDir.path)

            if (!dataDir.resolve("bin").exists()) {
                AssetsUtils.copy(
                    context = mContext!!, filePath = "bin", targetPath = dataDir.resolve("bin")
                )
                File(dataDir.resolve("bin").toString()).walk().forEach { file ->
                    if (file.isFile) {
                        Files.setPosixFilePermissions(
                            Path(path = file.path), setOf(
                                PosixFilePermission.OWNER_EXECUTE,
                                PosixFilePermission.OWNER_READ,
                                PosixFilePermission.OWNER_WRITE,
                                PosixFilePermission.OTHERS_READ,
                                PosixFilePermission.OTHERS_EXECUTE,
                                PosixFilePermission.GROUP_READ,
                                PosixFilePermission.GROUP_EXECUTE,
                            )
                        )
                    }
                }
            }

            if (!dataDir.resolve("rootfs").exists()) {
                AssetsUtils.copy(
                    context = mContext!!,
                    filePath = "rootfs",
                    targetPath = dataDir.resolve("rootfs")
                )

                NativeTarHelper().untar(
                    tarPath = dataDir.resolve("rootfs/archlinux-aarch64.tar").toString(),
                    targetPath = dataDir.toString()
                )
                Files.delete(dataDir.resolve("rootfs/archlinux-aarch64.tar"))
            }
        }

        private fun makeProjectDir() {
            if (projectDirPath.notExists()) {
                Files.createDirectory(projectDirPath)
            }
        }
    }
}
