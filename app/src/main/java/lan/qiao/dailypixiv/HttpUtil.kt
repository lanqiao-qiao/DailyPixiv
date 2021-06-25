package lan.qiao.networktest1

import okhttp3.OkHttpClient
import okhttp3.Request


object HttpUtil
{
    fun sendOkHttpRequests(address: String,callback:okhttp3.Callback)
    {
        val client=OkHttpClient()
        val request=Request.Builder()
            .url(address)
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
            .addHeader("Referer","https://www.pixiv.net/")
            .build()
        client.newCall(request).enqueue(callback)
    }
}