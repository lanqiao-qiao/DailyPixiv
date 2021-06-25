package lan.qiao.dailypixiv

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import lan.qiao.networktest1.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity()
{
    val REQUEST_EXTERNAL_STORAGE = 1
    val PERMISSIONS_STORAGE= arrayOf("android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE")

    val Lurl = ArrayList<String>()
    val Lid = ArrayList<String>()
    var St = "no"
    val context=this
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //verifyStoragePermissions(this)


        val page = ""
        sendRequestWithOkHttp(page)
        but1.setOnClickListener {
            if (St == "no")
                Toast.makeText(this, "未加载完成", Toast.LENGTH_SHORT).show()
            else
                showResponse(St.toString())
        }
        but2.setOnClickListener {
            val intent = Intent(this, Test1Activity::class.java)
            intent.putExtra("url", Lurl)
            startActivity(intent)
        }
        but3.setOnClickListener {
            sendRequestWithOkHttp2()
        }
        but4.setOnClickListener {
            if (Lurl.size != 50)
                Toast.makeText(this, "未加载完成", Toast.LENGTH_SHORT).show()
            else
            {
                val intent = Intent(this, TrueActivity::class.java)
                intent.putExtra("url", Lurl)
                intent.putExtra("id", Lid)
                startActivity(intent)
            }

        }

        but5.setOnClickListener {
            val intent = Intent(this, SignalpicActivity::class.java)
            startActivity(intent)
        }
        butt6.setOnClickListener {
                val permission = ContextCompat.checkSelfPermission (this, "android.permission.WRITE_EXTERNAL_STORAGE");
                if (permission != PackageManager.PERMISSION_GRANTED)
                { // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                }
                else
                {
                    //makeRootDirectory("/storage/emulated/0/DailyPixiv/")
                    makeRootDirectory(context.getExternalFilesDir(null)!!.absolutePath+"/Pic")
                    makeRootDirectory("/storage/emulated/0/Android/media/DailyPixiv/")
                    //File(Environment.DIRECTORY_DCIM+"/Dialypixiv/1111.jpg").createNewFile()
                    //makeRootDirectory(context.media)
                    //Environment.getExternalStorageDirectory（）
                }

        }
        but7.setOnClickListener {
            val intent=Intent(this,ScanfActivity::class.java)
            startActivity(intent)
        }
    }


    private fun sendRequestWithOkHttp(page: String)
    {
        HttpUtil.sendOkHttpRequests(
            "https://www.pixiv.net/ranking.php?p=1&format=json",
            object : Callback
            {
                override fun onResponse(call: Call, response: Response)
                {
                    val responseData = response.body?.string()
                    //showResponse(responseData)
                    parse(responseData)
                    //parseJSONWithGSON(responseData)
                }

                override fun onFailure(call: Call, e: IOException)
                {
                    e.printStackTrace()
                }
            })
    }

    private fun sendRequestWithOkHttp2()
    {
        HttpUtil.sendOkHttpRequests(
            "https://www.pixiv.net/users/1554775/artworks",
            object : Callback
            {
                override fun onResponse(call: Call, response: Response)
                {
                    val responseData = response.body?.string()
                    showResponse(responseData)
                    //parse(responseData)
                    //parseJSONWithGSON(responseData)
                }

                override fun onFailure(call: Call, e: IOException)
                {
                    e.printStackTrace()
                }
            })
    }


    private fun showResponse(response: String?)
    {
        runOnUiThread {
            text1.text = response
            val s: String = response as String
            Log.d("test1", s)
        }
    }

    private fun parse(jsonData: String?)
    {
        val st = StringBuilder()
        try
        {
            val jsonobj = JSONObject(jsonData)
            //val c=jsonobj.getString("content")
            val cs = jsonobj.getJSONArray("contents")
            //Log.d("JSON",c)
            for (i in 0 until cs.length())
            {
                val id = cs.getJSONObject(i).getString("illust_id")
                Lid.add(id)
                val title = cs.getJSONObject(i).getString("title")
                val url1 = cs.getJSONObject(i).getString("url")
                Lurl.add(url1)
                val url2 = cs.getJSONObject(i).getString("profile_img")
                //Log.d("JSON",i.toString()+": "+id+"  "+title)
                //st.append(i.toString() + ": " + id + "  " + title + "  " + url1 + "   " + url2 + "\n")
                st.append(i.toString() + ": " + id + "  " + title + "\n")
            }
            St = st.toString()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun parseJSONWithGSON(jsonData: String?)
    {
        val gson = Gson()
        /*
        val typeOf=object: TypeToken<List<Content>>() {}.type
        val appList=gson.fromJson<List<Content>>(jsonData,typeOf)
        for(app in appList)
        {
            Log.d("JSONonject","content is ${app.content}")
        }
        */
        val c = gson.fromJson(jsonData, Content::class.java)
        Log.d("JSON", c.contents)
    }

    fun makeRootDirectory(filePath: String)
    {
        //val file: File
        try
        {
            val file = File(filePath)
            if (!file.exists())
            {
                Log.d("Main",file.path)
                Log.d("Main",file.mkdirs().toString())
            }
            else
            {
                Log.d("Main","已存在")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }


    fun verifyStoragePermissions(activity: Activity)
    {
        try
        {
            //检测是否有写的权限
            val permission = ContextCompat.checkSelfPermission (activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED)
            {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
        catch (e:Exception)
        {
            e.printStackTrace();
        }

    }

}