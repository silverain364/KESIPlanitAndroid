package com.example.kesi.basic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageSetting {
    companion object {
        private const val TAG = "ImageSetting"
        fun resizeImage(image:File, absolutePath: String, resizeRatio: Int): File {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true; //메모리에 이미지를 로드하지 않고 이미지 크기만 확인
            BitmapFactory.decodeFile(image.absolutePath, options)

            options.inSampleSize = resizeRatio
            options.inJustDecodeBounds = false;

            val scaledBitmap = BitmapFactory.decodeFile(image.absolutePath, options)

            val checkDirectory = File(absolutePath)
            if(!checkDirectory.exists()) checkDirectory.mkdirs();

            val resizeFile = File(absolutePath, image.name)
            var outputStream: OutputStream? = null

            try {
                outputStream = FileOutputStream(resizeFile, true)
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }catch (_: Exception) {}
            finally { //메모리 해제
                outputStream?.close()
                scaledBitmap?.recycle()
            }


            return resizeFile;
        }
    }
}