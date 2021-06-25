package lan.qiao.dailypixiv

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.isDigitsOnly
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.activity_test1.*
import lan.qiao.networktest1.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.*
import java.lang.Exception

class Test1Activity : AppCompatActivity()
{
    val u="https://i.pximg.net/img-original/img/2020/12/01/14/40/41/86025621_p0.png"
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
        val context=this
        val Lurl=intent.getStringArrayListExtra("url") as ArrayList<String>
        Log.d("Test",Lurl.toString())
        test_but1.setOnClickListener {
            val glideUri=GlideUrl(u,LazyHeaders.Builder()
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                .addHeader("Referer","https://www.pixiv.net/")
                .build())
            Glide.with(this).load(glideUri).into(test_image)

        }
        test_but2.setOnClickListener {
            //val bitmap= Bitmap.createBitmap(test_image.width,test_image.height,Bitmap.Config.RGB)
            //val bitmap=test_image.drawable.toBitmap()
            HttpUtil.sendOkHttpRequests(u, object : Callback
            {
                override fun onResponse(call: Call, response: Response)
                {
                    val inp=response.body?.byteStream()
                    try
                    {
                        val bitmap=BitmapFactory.decodeStream(inp)
                        addBitmapToAlbum(bitmap,"1","image/png",Bitmap.CompressFormat.PNG,test_image)
                        //test_image.setImageBitmap(bitmap)
                        Looper.prepare();
                        Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show()
                        Looper.loop();
                    }
                    catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                    finally
                    {
                        inp?.close()
                        //Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onFailure(call: Call, e: IOException)
                {
                    e.printStackTrace()
                }
            })
            //addBitmapToAlbum(bitmap,"1","image/jpeg",Bitmap.CompressFormat.JPEG)
        }
        test_but3.setOnClickListener {
            //val file=File(context.getExternalFilesDir(null)!!.absolutePath+"/Pic/"+"test.png")
            //file.createNewFile()
            HttpUtil.sendOkHttpRequests(u, object : Callback
            {
                override fun onResponse(call: Call, response: Response)
                {
                    val inp=response.body?.byteStream()
                    try
                    {
                        //val bitmap=BitmapFactory.decodeStream(inp)
                        val file=File(context.getExternalFilesDir(null)!!.absolutePath+"/Pic/"+"test1.png")
                        //val file=File("/storage/emulated/0/Android/media/DailyPixiv/"+"test1.png")
                        if(!file.exists())
                            file.createNewFile()
                        val fileout=FileOutputStream(file)
                        var j=0
                        while(j!=-1)
                        {
                            j=inp!!.read()
                            fileout.write(j)
                        }
                        //inp.close()
                        fileout.close()
                    }
                    catch (e:Exception)
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
        }
        test_but4.setOnClickListener {
            HttpUtil.sendOkHttpRequests(u, object : Callback
            {
                override fun onResponse(call: Call, response: Response)
                {
                    val inp=response.body?.byteStream()
                    try
                    {
                        val bitmap=BitmapFactory.decodeStream(inp)
                        saveBitmap(bitmap,context.getExternalFilesDir(null)!!.absolutePath+"/Pic/"+"testBitmap1.jpeg")
                        //val file=File(Environment.DIRECTORY_DCIM+"/Dialypixiv"+"testBitmap1.jpeg")
                        //if(!file.exists())
                         //   file.createNewFile()
                        //saveBitmap(bitmap,Environment.DIRECTORY_DCIM+"/Dialypixiv"+"testBitmap1.jpeg")
                    }
                    catch (e:Exception)
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
        }
    }

    fun saveBitmap(bitmap:Bitmap,filePath:String)
    {
        //val bitmap= Bitmap.createBitmap(view.width,view.height,Bitmap.Config.RGB_565)
//        val canvas=Canvas(bitmap)
        //val outStream=null
        val file= File(filePath)
        if(file.isDirectory)
        {
            return
        }
        val outStream=FileOutputStream(file)
        try
        {
            //outStream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outStream);
            outStream.flush()
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
        finally
        {
            try
            {
                bitmap.recycle()
                outStream.close()
            }
            catch (e:IOException)
            {
                e.printStackTrace()
            }
        }
    }

    fun addBitmapToAlbum(bitmap:Bitmap,displayName:String,mimeType:String,compressFormat:Bitmap.CompressFormat,view:ImageView)
    {
        val values=ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME,displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE,mimeType)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DCIM+"/Dialypixiv")
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
        runOnUiThread {
            view.setImageBitmap(bitmap)
        }
    }

}