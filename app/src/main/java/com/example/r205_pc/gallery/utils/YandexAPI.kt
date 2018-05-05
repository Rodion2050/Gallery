package com.example.r205_pc.gallery.utils

import android.net.Uri
import android.util.Log
import com.example.r205_pc.gallery.utils.FileUtil.Companion.getPreviewFileName
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL

/**
 * Created by r205-pc on 17.04.2018.
 */
/**
 * Класс содержит функции по работе с Яндекс Диском
 */
class YandexAPI{
    companion object {
        private val YandexGetFolderInfoURL = "https://cloud-api.yandex.net:443/v1/disk/resources"
        private val PATH_PARAM = "path"
        private val TAG = YandexAPI::class.simpleName
        /**
         * @param path путь к папке на Диске
         * @param OAuth токен авторизации
         * @return Полученные JSON данные о папке
         */
        fun getYandexFolderInfo(path:String, OAuth:String):String{
            val uri = Uri.parse(YandexGetFolderInfoURL).buildUpon().appendQueryParameter(PATH_PARAM, path).build()
            return getDataFromURL(buildURL(uri), OAuth)
        }

        /**Функция получает данные о папке и загружает фотографии на устройство
         * @param data Информация о папке на Диске в формате JSON
         * @param mainDir Путь к главной директории для сохранения картинок на устройстве
         * @param OAuth токен авторизации
         */
        fun synchronizeFolder(data:String, mainDir:String, OAuth: String){
            try{
                val jsonObj = JSONObject(data)
                Log.d(TAG, data)
                val files = getJSONFilesInfo(jsonObj)
                val threads = arrayListOf<Thread>()
                for(i in 0 until files.length()){
                    val fileInfoJSON = JSONObject(files[i].toString())
                    val file = File(mainDir + fileInfoJSON.getString("path").removeRange(0, "disk:".length))

                    if(fileInfoJSON.get("type") == "dir"){
                        downloadSomeImagesFromFolder(fileInfoJSON.getString("path").removeRange(0, "disk:".length), mainDir, OAuth)//Загружаем в папку некоторые картинки для превью
                    }else if(fileInfoJSON.getString("media_type") == "image" && fileInfoJSON.getString("mime_type") == "image/jpeg"
                            && (!file.exists() || !MD5.checkMD5(fileInfoJSON.getString("md5"), file))){
                        file.createNewFile()
                        threads.add(Thread(object :Runnable{
                            override fun run() {
                                downloadDataToFile(URL(fileInfoJSON.getString("preview")), getPreviewFileName(file), OAuth)
                                downloadDataToFile(URL(fileInfoJSON.getString("file")), file, OAuth)
                            }
                        }))
                        threads[threads.lastIndex].start()
                    }
                }
                deleteFilesThatAreNotInDisk(files, mainDir + getJSONFolderInfo(jsonObj).getString("path").removeRange(0, "disk:".length))
                for (thread in threads){
                    thread.join()
                }
            }catch (e:JSONException){
                e.printStackTrace()
            }catch (e:IOException){
                e.printStackTrace()
            }

        }

        private fun getJSONFilesInfo(jsonObj: JSONObject)  = getJSONFolderInfo(jsonObj).getJSONArray("items")

        private fun getJSONFolderInfo(jsonObj: JSONObject) = jsonObj.getJSONObject("_embedded")


        /**Функция загружает 4 фотографии для превью папки
         * @param path Путь к папке на Диске
         * @param mainDir Путь к главной директории для сохранения картинок на устройстве
         * @param OAuth токен авторизации
         */
        private fun downloadSomeImagesFromFolder(path:String, mainDir:String, OAuth: String){
            try{
                val folderInfo = JSONObject(getYandexFolderInfo(path, OAuth))
                val jsonFolderInfo = folderInfo.getJSONObject("_embedded")//Информация о папке
                val files = jsonFolderInfo.getJSONArray("items")
                var imagesCount = 0

                for (i in 0 until files.length()){
                    val fileInfoJSON = JSONObject(files[i].toString())
                    val type = fileInfoJSON.get("type")
                    val file = File(mainDir + fileInfoJSON.getString("path").removeRange(0, "disk:".length))

                    if(type=="file" && fileInfoJSON.getString("media_type")=="image" && fileInfoJSON.getString("mime_type")=="image/jpeg"){
                        val folder = File(mainDir + path)
                        if(!folder.exists()){
                            folder.mkdir()
                        }
                        downloadDataToFile(URL(fileInfoJSON.getString("preview")), FileUtil.getPreviewFileName(file), OAuth)
                        imagesCount++
                        if(imagesCount == 4){
                            return //Скачиваем 4 картинки и выходим
                        }
                    }
                }
            }catch (e:JSONException){
                e.printStackTrace()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
        private fun deleteFilesThatAreNotInDisk(files:JSONArray, dir:String){
            try{
                val currDir = File(dir)
                for(file in currDir.listFiles()){
                    val fileName = file.name
                    var contains = false
                    for(i in 0 until files.length()){
                        val diskFile = File(JSONObject(files[i].toString()).getString("path").removeRange(0, "disk:".length))
                        if(diskFile.name == fileName||
                                "${diskFile.nameWithoutExtension}_preview" == File(fileName).nameWithoutExtension){
                            contains = true
                            break
                        }
                    }
                    if(!contains){
                        file.delete()
                    }
                }
            }catch (e:IOException){
                e.printStackTrace()
            }catch (e:JSONException){
                e.printStackTrace()
            }

        }
    }

}
