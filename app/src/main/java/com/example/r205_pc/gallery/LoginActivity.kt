package com.example.r205_pc.gallery

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import kotlinx.android.synthetic.main.activity_login.*
import com.example.r205_pc.gallery.utils.AppData
import android.webkit.ValueCallback
import android.webkit.CookieSyncManager




class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val AppID = "0caae2f3f4fb4c5fbb8d0002d25295ca"
        val yandexOAuthPath = Uri.parse("https://oauth.yandex.ru/authorize").buildUpon()
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("client_id", AppID).build()
        CookieSyncManager.createInstance(this)
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        loginWebView.clearCache(true)
        loginWebView.setWebViewClient(CustomWebViewClient())
        loginWebView.loadUrl(yandexOAuthPath.toString())


    }
    inner class CustomWebViewClient : WebViewClient(){
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val uri = request.url
            Log.d("CustomWebViewClient", request.url.toString())
            if(uri.host.equals("yx0caae2f3f4fb4c5fbb8d0002d25295ca.oauth.yandex.ru")){//Если совпадает с Callback URL, то вытаскиваем access_token
                val strUri = uri.toString()
                val access_token = strUri.substring(strUri.indexOf("#access_token=") + "#access_token=".length, strUri.indexOf("&token_type="))
                Log.d("CustomWebViewClient", access_token)
                AppData.setOAuth(access_token, this@LoginActivity)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            return false //Allow WebView to load url
        }
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val uri = Uri.parse(url)
            Log.d("CustomWebViewClient", url)
            if(uri.host.equals("yx0caae2f3f4fb4c5fbb8d0002d25295ca.oauth.yandex.ru")){//Если совпадает с Callback URL, то вытаскиваем access_token
                val strUri = uri.toString()
                val access_token = strUri.substring(strUri.indexOf("#access_token=") + "#access_token=".length, strUri.indexOf("&token_type="))
                Log.d("CustomWebViewClient", access_token)
                AppData.setOAuth(access_token, this@LoginActivity)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            return false //Allow WebView to load url
        }
    }
}
