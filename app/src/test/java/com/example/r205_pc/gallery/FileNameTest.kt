package com.example.r205_pc.gallery

import com.example.r205_pc.gallery.utils.YandexAPI
import org.junit.Test
import java.io.File

/**
 * Created by r205-pc on 01.05.2018.
 */
class FileNameTest {
    @Test
    fun getPreviewFileNameTest(){
        val file = File("/storage/picture.jpg")
        assert("/storage/picture_prew.jpg" == YandexAPI.getPreviewFileName(file))
    }
}