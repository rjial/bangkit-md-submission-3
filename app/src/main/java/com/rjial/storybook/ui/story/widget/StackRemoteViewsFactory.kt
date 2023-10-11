package com.rjial.storybook.ui.story.widget

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import androidx.core.os.bundleOf
import com.rjial.storybook.R
import com.rjial.storybook.data.database.database.StoryListDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

class StackRemoteViewsFactory(private val context: Context): RemoteViewsFactory {
    private val mWidgetItemsUrl = ArrayList<String>()
    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val storyListDatabase = StoryListDatabase.getInstance(context)
        runBlocking(Dispatchers.IO) {
            storyListDatabase.storyListDao().getAllStoriesSus().forEach {
                mWidgetItemsUrl.add(it.photoUrl)
                Log.d("IMAGE_URL", it.photoUrl)
            }
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItemsUrl.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        val okHttpClient = OkHttpClient()
        val okHttpRequest = Request.Builder()
            .url(mWidgetItemsUrl[position])
            .build()
        try {
            okHttpClient.newCall(okHttpRequest).execute().use {
                if(!it.isSuccessful) throw IOException("Unexpected code $it")

                val bitmap = BitmapFactory.decodeStream(it.body!!.byteStream())
                rv.setImageViewBitmap(R.id.imageView, bitmap)
            }
        } catch (exc: IOException) {
            Log.e("IMAGE_WIDGET_ERROR", exc.toString())
        } catch (exc: Exception)  {
            Log.e("IMAGE_WIDGET_ERROR", exc.toString())
        }

        val extras = bundleOf(
            ImagesBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}