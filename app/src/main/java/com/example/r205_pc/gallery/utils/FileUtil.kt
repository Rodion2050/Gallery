package com.example.r205_pc.gallery.utils

import android.util.Log
import java.io.File

/**
 * Created by r205-pc on 01.05.2018.
 */
class FileUtil{
    companion object {
        fun getPreviewFileName(original: File): File{
            val originalPath = original.absolutePath
            return File(originalPath.replace(original.name, "${original.nameWithoutExtension}_preview.${original.extension}"))
        }
        fun getFullFileNameByPreview(preview:File):File{
            val previewPath = preview.absolutePath
            return File(previewPath.replace(preview.name, "${preview.nameWithoutExtension.removeSuffix("_preview")}.${preview.extension}"))
        }
    }
}