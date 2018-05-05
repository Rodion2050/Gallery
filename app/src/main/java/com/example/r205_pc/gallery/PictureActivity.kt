package com.example.r205_pc.gallery

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_picture.*
//Активность для отображения отдельных картинок
class PictureActivity : AppCompatActivity() {
    private val TAG:String = this::class.simpleName!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            picture_iv.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra(Intent.EXTRA_TEXT)))
        }
    }
}
