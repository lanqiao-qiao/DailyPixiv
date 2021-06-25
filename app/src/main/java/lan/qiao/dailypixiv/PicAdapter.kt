package lan.qiao.dailypixiv

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.activity_test1.*
import java.lang.StringBuilder

class PicAdapter(val picList:List<String>,val c: Context):RecyclerView.Adapter<PicAdapter.ViewHolder>()
{
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view)
    {
        val picImage:ImageView=view.findViewById(R.id.picimage)
        //val picName:TextView=view.findViewById(R.id.picname)
        val AC=view
        var q=0
        var po=-1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.pic_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        Log.d("TEST",position.toString())
        val p=picList[position]
        holder.picImage.setOnClickListener {
            Log.d("TESTT",position.toString())
            val sid=getS(p)
            Log.d("TESTT",sid)
            val intent= Intent(c,BigpicActivity::class.java)
            intent.putExtra("url",p)
            intent.putExtra("id",sid)
            c.startActivity(intent)
        }
        //holder.picName.text=p
        //if(holder.q==0)
        //{
            val glideUri= GlideUrl(p,
                LazyHeaders.Builder()
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .addHeader("Referer","https://www.pixiv.net/")
                    .build())
            Glide.with(holder.itemView).load(glideUri).into(holder.picImage)//.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        holder.po=position
        //    holder.q=1
        //}


    }

    override fun getItemCount()=picList.size

    fun getS(s:String):String
    {
        var qian=0
        var hou=0
        val len=s.length-1
        for(i in len downTo 0)
        {
            if(s.get(i)=='/')
            {
                qian=i
                break
            }
        }
        for(i in qian until len)
        {
            if(s.get(i)=='_')
            {
                hou=i
                break
            }
        }

        var ss=StringBuilder()
        for(i in qian+1 until hou)
        {
            ss.append(s.get(i))
        }
        return ss.toString()
    }
}