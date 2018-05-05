package com.example.r205_pc.gallery.utils

import android.net.Uri
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.URL
import java.util.*
import android.os.AsyncTask.execute
import android.util.Log


/**
 * Created by r205-pc on 16.04.2018.
 */
fun buildURL(uri:Uri):URL{
    val url = URL(uri.toString())
    return url
}
fun getDataFromURL(url: URL, OAuth:String):String{
    val client = OkHttpClient()
    val requestBuilder = Request.Builder()
    requestBuilder.url(url)
    requestBuilder.header("Accept", "application/json")
    requestBuilder.header("Content-Type", "application/json")
    requestBuilder.header("Authorization", "OAuth " + OAuth)
    val request = requestBuilder.build()

    try {
        val response = client.newCall(request).execute()
        return response.body().string()
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }

}
fun downloadDataToFile(url:URL, file: File, OAuth: String){
    val client = OkHttpClient()
    val requestBuilder = Request.Builder()
    requestBuilder.url(url)
    requestBuilder.header("Accept", "application/json")
    //requestBuilder.header("Content-Type", "application/json")
    requestBuilder.header("Authorization", "OAuth " + OAuth)
    Log.d("Network", url.toString())
    val request = requestBuilder.build()

    try {
        val response = client.newCall(request).execute()
        val input = response.body().byteStream()
        val output = FileOutputStream(file)
        val bufferSize = 1000000
        val buffer = ByteArray(bufferSize)
        var length: Int = input.read(buffer)
        while (length > 0) {
            output.write(buffer, 0, length)
            length = input.read(buffer)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
//fun downloadDataToFile(url:URL, file: File){
//    val connection = url.openConnection()
//    val input:InputStream
//    val output:OutputStream
//    try{
//        connection.connect()
//        input = connection.getInputStream()
//        output = FileOutputStream(file)
//        val bufferSize = 1000000
//        val buffer = ByteArray(bufferSize)
//        var length: Int = input.read(buffer)
//        while (length > 0) {
//            output.write(buffer, 0, length)
//            length = input.read(buffer)
//        }
//    }catch (e:IOException){
//        e.printStackTrace()
//    }
//}