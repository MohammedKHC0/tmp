package com.shadow3.codroid

import android.content.Context
import android.util.Log
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

class AssetsUtils {
    companion object {
        fun copy(context: Context, filePath: String, targetPath: Path) {
            val fileList = context.assets.list(filePath)
            if ((fileList?.size ?: 0) > 0) {
                try {
                    Files.createDirectory(targetPath)
                } catch (e: IOException) {
                    Log.e("create dir", targetPath.toString())
                    Log.e("create dir", e.toString())
                }

                fileList?.forEach { file ->
                    val newFilePath = Path(filePath).resolve(file).toString()
                    val newTargetPath = targetPath.resolve(file)
                    copy(context = context, filePath = newFilePath, targetPath = newTargetPath)
                }
            } else {
                val inputStream = context.assets.open(filePath)
                Files.copy(inputStream, targetPath)
            }
        }
    }
}