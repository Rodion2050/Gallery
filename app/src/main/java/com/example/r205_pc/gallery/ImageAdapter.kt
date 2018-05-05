package com.example.r205_pc.gallery

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ThumbnailUtils
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Adapter
import android.widget.BaseAdapter
import android.widget.ImageView
import java.io.File
import java.text.AttributedCharacterIterator
import java.text.FieldPosition
import android.graphics.Paint.Align
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Bitmap
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Build
import android.support.v7.widget.RecyclerView
import com.example.r205_pc.gallery.utils.FileUtil


/**
 * Created by r205-pc on 12.04.2018.
 */
class ImageAdapter(private val mContext: Context, private val mainDir:String) :BaseAdapter() {
    private val TAG:String = this::class.simpleName!!
    val images : ArrayList<String> = arrayListOf<String>("")//Временное значение


    fun refreshFolderImages(){
        images.clear()
        val files = File(mainDir).listFiles()
        var i = 0
        for(file in files){
            if(file.isDirectory){          //Если file - директория, то проверяем, содержит ли она картинки.
                var containsImages = false //Если да, то добавляем ее в список элементов для обработки
                for (f in file.listFiles()){
                    if(f.isFile && f.extension.equals("jpg") || f.extension.equals("jpeg")){
                        containsImages = true
                        break
                    }
                }
                if(containsImages){
                    images.add(file.absolutePath)
                }
            }else if(file.name.contains("_preview")){
                images.add(files[i].absolutePath)
            }
            i++
        }
        notifyDataSetChanged()
    }
    override fun getView(position: Int, convertView: View?, group: ViewGroup?): View {
        var imageView:ImageView
        if (convertView == null) {
            // Инициализация некоторых свойств
            imageView = ImageView(mContext)
            imageView.adjustViewBounds = true
            //imageView.layoutParams = AbsListView.LayoutParams(100, 100)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(0, 0, 0, 0)
//            imageView.setOnClickListener {
//                val intent:Intent
//                if(File(images[position]).isFile) {//Если элемент является файлом, то вызываем активность просмотра файла
//                    intent = Intent(mContext, PictureActivity::class.java)
//                    intent.putExtra(Intent.EXTRA_TEXT, FileUtil.getFullFileNameByPreview(File(images[position])).toString())
//                }else{//Если элемент является папкой, то вызываем активность просмотра таблицы картинок
//                    intent = Intent(mContext, MainActivity::class.java)
//                    intent.putExtra(Intent.EXTRA_TEXT, images[position])
//                }
//
//                mContext.startActivity(intent)}
        } else {
            imageView = convertView as ImageView
        }
        val file = File(images[position])
        val width = 150
        val height = 150
        var thumbnail:Bitmap? = null
        if(file.isDirectory){//Устанавливаем превью из 4-х картинок
            var i = 0
            var largeImage: Bitmap? = null
            for(f in file.listFiles()){
                if(f.isFile && f.name.contains("_preview")){
                    if(largeImage == null) {
                        largeImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    }
                    var bitmap = BitmapFactory.decodeFile(f.absolutePath)
                    val smallImage = ThumbnailUtils.extractThumbnail(bitmap,width/2,height/2)
                    val canvas = Canvas(largeImage)
                    if(i == 0){
                        canvas.drawBitmap(smallImage, 0f, 0f, null)
                    }else if(i == 1){
                        canvas.drawBitmap(smallImage, 0f + width/2, 0f, null)
                    }else if(i == 2){
                        canvas.drawBitmap(smallImage, 0f, 0f + height/2, null)
                    }else if(i == 3){
                        canvas.drawBitmap(smallImage, 0f + width/2, 0f + height/2 , null)
                    }else if(i == 4){
                        break
                    }
                    i++
                }
                thumbnail = largeImage
            }
        }else{
            thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(images[position]),width,height)
        }

        imageView.setImageBitmap(thumbnail)
        return imageView
    }


    override fun getItem(p0: Int): Any? = null

    override fun getItemId(p0: Int): Long = 0L

    override fun getCount():Int = images.size
}