package com.example.r205_pc.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.example.r205_pc.gallery.utils.AppData
import com.example.r205_pc.gallery.utils.YandexAPI
import java.io.File
import android.widget.AdapterView
import com.example.r205_pc.gallery.utils.FileUtil


//Активность для отображения картинок в виде таблицы
class MainActivity : AppCompatActivity() {
    private val TAG:String = this::class.simpleName!!
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var mainDir = File(Environment.getExternalStorageDirectory().absolutePath)
    private var imageAdapter:ImageAdapter? = null
    private var workingDir = ""
    private var workingDirIsEmpty = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verifyStoragePermissions(this)
        if(AppData.getOAuth(this) == ""){
            login()
        }

        mainDir = File(Environment.getExternalStorageDirectory().absolutePath + "/" + getString(R.string.main_app_folder) + "/")
        if(!mainDir.exists()){
            mainDir.mkdir()//Создание главной директории приложения, в которую кэшируются картинки
        }

        workingDir = if(intent.hasExtra(Intent.EXTRA_TEXT)){
             intent.getStringExtra(Intent.EXTRA_TEXT).removePrefix(mainDir.absolutePath)
        }else{
             "/"
        }
        downloadImage(workingDir)

        imageAdapter = ImageAdapter(this, mainDir.absolutePath + workingDir)
        imageAdapter?.refreshFolderImages()
        gridView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val intent:Intent
                if(File(imageAdapter?.images?.get(position)).isFile) {//Если элемент является файлом, то вызываем активность просмотра файла
                    intent = Intent(this@MainActivity, PictureActivity::class.java)
                    intent.putExtra(Intent.EXTRA_TEXT, FileUtil.getFullFileNameByPreview(File(imageAdapter?.images?.get(position))).toString())
                }else{//Если элемент является папкой, то вызываем активность просмотра таблицы картинок
                    intent = Intent(this@MainActivity, MainActivity::class.java)
                    intent.putExtra(Intent.EXTRA_TEXT, imageAdapter?.images?.get(position))
                }

                this@MainActivity.startActivity(intent)
            }
        })

        gridView.adapter = imageAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when(item?.itemId){
            R.id.exitFromYandex -> {
                AppData.setOAuth("", this)
                login()
            }
            R.id.refreshItem -> {
                downloadImage(workingDir)
            }
            R.id.aboutAppItem ->{
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        return true
    }

    private fun downloadImage(path:String){
        if(!File(mainDir.absolutePath + path).listFiles().isEmpty()){
            workingDirIsEmpty = false
       }
        DownloadImagesTask().execute(path)
    }

    private fun login(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun enterLoadingState(){
        gridView.visibility = INVISIBLE
        progressBar.visibility = VISIBLE
    }

    private fun enterDisplayGridState(){
        gridView.visibility = VISIBLE
        progressBar.visibility = INVISIBLE
    }

    private fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }
     inner class DownloadImagesTask() : AsyncTask<String, Void, Void?>(){
         override fun onPreExecute() {
             super.onPreExecute()
             if(workingDirIsEmpty){
                 enterLoadingState()
             }
         }

        override fun doInBackground(vararg paths: String?): Void? {
            for(path in paths){
                if(path != null){
                    val info = YandexAPI.getYandexFolderInfo(path, AppData.getOAuth(this@MainActivity)).also { Log.d(TAG, it) }
                    if(info != ""){
                        YandexAPI.synchronizeFolder(info, mainDir.toString(), AppData.getOAuth(this@MainActivity))
                    }

                }

            }
            return null
        }

         override fun onPostExecute(result: Void?) {
             super.onPostExecute(result)
             enterDisplayGridState()
             imageAdapter?.refreshFolderImages()
         }
    }
}
