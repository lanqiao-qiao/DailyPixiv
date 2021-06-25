package lan.qiao.dailypixiv

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.activity_signalpic.*
import kotlinx.android.synthetic.main.activity_test1.*
import lan.qiao.networktest1.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder

class SignalpicActivity : AppCompatActivity()
{
    var ST="no"
    val context=this
    var name="?"
    lateinit var bt:Bitmap

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signalpic)
        setSupportActionBar(signtoolbar)
        sinbut.setOnClickListener {
            val t=sintex.text.toString()
            name=t
            val surl="https://www.pixiv.net/artworks/"+t
            sendRequestWithOkHttp(surl)
        }
    }

    private fun sendRequestWithOkHttp(surl:String)
    {
        HttpUtil.sendOkHttpRequests(surl, object : Callback
        {
            override fun onResponse(call: Call, response: Response)
            {
                val responseData=response.body?.string()
                //showResponse(responseData)
                //parse(responseData)
                //parseJSONWithGSON(responseData)
                DoResponse(responseData as String)
            }

            override fun onFailure(call: Call, e: IOException)
            {
                e.printStackTrace()
            }
        })
    }

    private fun DoResponse(response:String)
    {
        //Log.d("Sin",response)
        val wei=response.indexOf("\"original\":")
        //Log.d("Sin",response.get(wei+12).toString())
        val start=wei+12
        val sb=StringBuilder()
        for(i in start until start+200)
        {
            sb.append(response.get(i))
            if(response.get(i+1)=='\"'&&response.get(i+2)=='}')
                break
        }
        Log.d("Sin",sb.toString())

        HttpUtil.sendOkHttpRequests(sb.toString(), object : Callback
        {
            override fun onResponse(call: Call, response: Response)
            {
                val inp=response.body?.byteStream()
                try
                {
                    val bitmap= BitmapFactory.decodeStream(inp)
                    bt=bitmap
                    runOnUiThread {
                        sinimg.setImageBitmap(bitmap)
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                finally
                {
                    inp?.close()
                }
            }
            override fun onFailure(call: Call, e: IOException)
            {
                e.printStackTrace()
            }
        })

        /*runOnUiThread {
            val glideUri= GlideUrl(sb.toString(),
                LazyHeaders.Builder()
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .addHeader("Referer","https://www.pixiv.net/")
                    .build())
            Glide.with(this).load(glideUri).into(sinimg)
        }*/
        ST=sb.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.downpng ->{
                addBitmapToAlbum(bt, name, "image/png", Bitmap.CompressFormat.PNG)
                Toast.makeText(context,"下载完成：${name}.png",Toast.LENGTH_SHORT).show()
            }
            R.id.downjpg -> {
                addBitmapToAlbum(bt,name,"image/jpeg",Bitmap.CompressFormat.JPEG)
                Toast.makeText(context,"下载完成：${name}.jpeg",Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    fun addBitmapToAlbum(bitmap:Bitmap,displayName:String,mimeType:String,compressFormat:Bitmap.CompressFormat)
    {
        val values= ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME,displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE,mimeType)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/Dialypixiv")
            //values.put(MediaStore.MediaColumns.RELATIVE_PATH,)
            //values.put(MediaStore.MediaColumns.DATA,"${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/DailyPixiv/$displayName")
        }
        else
        {
            values.put(MediaStore.MediaColumns.DATA,"${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName")
        }
        var uri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        if(uri!=null)
        {
            val outputStream=contentResolver.openOutputStream(uri)
            if(outputStream!=null)
            {
                bitmap.compress(compressFormat,100,outputStream)
                outputStream.close()
            }
        }
    }
}