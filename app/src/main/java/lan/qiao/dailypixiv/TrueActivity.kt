package lan.qiao.dailypixiv

import android.media.AudioRecord.MetricsConstants.SOURCE
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.activity_true.*
import kotlin.concurrent.thread

class TrueActivity : AppCompatActivity()
{
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true)
        setSupportActionBar(Truetoolbar)

        swipeRefresh.setColorSchemeResources(R.color.teal_200)
        swipeRefresh.setOnRefreshListener {
            runOnUiThread{
                Thread.sleep(2000)
                swipeRefresh.isRefreshing=false
            }
        }

        /*for(i in 0 until 50)
        {
            val glideUri= GlideUrl(Lurl[i],
                LazyHeaders.Builder()
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .addHeader("Referer","https://www.pixiv.net/")
                    .build())
            Glide.with(this).load(glideUri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).preload()
        }*/
        val Lurl=intent.getStringArrayListExtra("url") as ArrayList<String>
        //Log.d("True",Lurl.toString())
        //Lurl.sortWith(Comparator.reverseOrder())
        val layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recycle.layoutManager=layoutManager
        val adapter=PicAdapter(Lurl,this)
        recycle.adapter=adapter
    }

//    override fun onStop()
//    {
//        super.onStop()
//        super.onDestroy()
//    }
}