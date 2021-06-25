package lan.qiao.dailypixiv

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_scanf.*
import lan.qiao.networktest1.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ScanfActivity : AppCompatActivity()
{
    lateinit var uid:String
    lateinit var url:String
    lateinit var url2:String
    lateinit var ul:String
    lateinit var ur:String
    val Lurl = ArrayList<String>()
    val context=this

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanf)
        //https://i.pximg.net/img-original/img/2021/06/10/00/58/55/90446978_p0.png
        //https://i.pximg.net/c/240x480/img-master/img/2021/06/10/00/58/55/90446978_p0_master1200.jpg
        scanfbut.setOnClickListener {
            uid=scanftext.text.toString()
            url="https://www.pixiv.net/ajax/user/"+uid+"/profile/all?lang=zh"
            url2="https://www.pixiv.net/ajax/user/1554775/profile/illusts?ids%5B%5D=89828769&ids%5B%5D=8232932&work_category=illustManga&is_first_page=1&lang=zh"
            ul="https://www.pixiv.net/ajax/user/"+uid+"/profile/illusts?"
            ur="work_category=illustManga&is_first_page=1&lang=zh"
            HttpUtil.sendOkHttpRequests(url, object : Callback {
                override fun onResponse(call: Call, response: Response)
                {
                    val responseData = response.body?.string()
                    val jsonobj = JSONObject(responseData)
                    //val b = jsonobj.getJSONObject("body")
                    //Log.d("Scanf",b.toString())
                    val bd = jsonobj.getJSONObject("body").getJSONObject("illusts")
                    //Log.d("Scanf",bd.toString())
                    val size=bd.length()
                    Log.d("Scanf",size.toString())
                    val it=bd.keys()
                    var i=0
                    while(it.hasNext())
                    {
                        var s=it.next()
                        ul+="ids%5B%5D="+s+"&"
                        i=i+1
                        //Log.d("Scanf",i.toString())
                        //Lurl.add(s)
                        if(i==48)
                        {
//                            DoAgain(ul+ur,0)
//                            i=0
//                            ul="https://www.pixiv.net/ajax/user/"+uid+"/profile/illusts?"
                            break
                        }
                    }
                    ul+=ur
                    Log.d("Scanf",ul)
                    DoAgain(ul,1)
//                    val intent= Intent(context,TrueActivity::class.java)
//                    intent.putExtra("url",Lurl)
//                    startActivity(intent)
                }
                override fun onFailure(call: Call, e: IOException)
                {
                    e.printStackTrace()
                }
            })
        }

    }

    fun DoAgain(url:String,a:Int)
    {
        HttpUtil.sendOkHttpRequests(url, object : Callback {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call, response: Response)
            {
                val responseData = response.body?.string()
                val jsonobj = JSONObject(responseData)
                val bd=jsonobj.getJSONObject("body").getJSONObject("works")
                Log.d("Scanf",bd.toString())
                val it=bd.keys()
                while(it.hasNext())
                {
                    val s=it.next()
                    val ss=bd.getJSONObject(s).getString("url")
                    Log.d("Scanf",ss.toString())
                    Lurl.add(ss)
                }
//                if(a==1)
//                {
//                    Log.d("Scanf",Lurl.size.toString())
//                    val intent= Intent(context,TrueActivity::class.java)
//                    Lurl.sortWith(Comparator.reverseOrder())
//                    intent.putExtra("url",Lurl)
//                    startActivity(intent)
//                }
                val intent= Intent(context,TrueActivity::class.java)
                intent.putExtra("url",Lurl)
                startActivity(intent)

            }
            override fun onFailure(call: Call, e: IOException)
            {
                e.printStackTrace()
            }
        })
    }
}