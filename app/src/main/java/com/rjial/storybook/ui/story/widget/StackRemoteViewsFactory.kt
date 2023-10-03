package com.rjial.storybook.ui.story.widget

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import androidx.core.os.bundleOf
import com.rjial.storybook.R
import com.rjial.storybook.repository.StoryImageDatabaseRepository
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
        runBlocking(Dispatchers.IO) {
            val storyImgRepo = StoryImageDatabaseRepository(context)
            storyImgRepo.getAllStoryImagesFlow().collect {
                for (items in it)  {
                    mWidgetItemsUrl.add(items.imageUrl)
                    Log.d("IMAGE_URL", items.imageUrl)
                }
            }
        }
//        val okHttpClient = OkHttpClient()
//        val okHttpRequest = Request.Builder()
//            .url("https://story-api.dicoding.dev/images/stories/photos-1696316847674_TQjLezSt.jpg")
//            .build()
//        try {
//            okHttpClient.newCall(okHttpRequest).execute().use {
//                if(!it.isSuccessful) throw IOException("Unexpected code $it")
//
//                val bitmap = BitmapFactory.decodeStream(it.body!!.byteStream())
//                mWidgetItems.add(bitmap)
//            }
//        } catch (exc: IOException) {
//            Log.e("IMAGE_WIDGET_ERROR", exc.toString())
//        } catch (exc: Exception)  {
//            Log.e("IMAGE_WIDGET_ERROR", exc.toString())
//        }
//        mWidgetItems.add(BitmapFactory.decodeResource(context.resources, R.drawable.darth_vader))
//        mWidgetItems.add(BitmapFactory.decodeResource(context.resources, R.drawable.storm_trooper))
//        mWidgetItems.add(BitmapFactory.decodeResource(context.resources, R.drawable.starwars))
//        mWidgetItems.add(BitmapFactory.decodeResource(context.resources, R.drawable.falcon))
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItemsUrl.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
//        val request = Picasso.Builder(context.applicationContext).build().load(mWidgetItemsUrl[position])
//        request.get()
//        request.into(object: Target {
//            override fun onBitmapLoaded(
//                bitmap: Bitmap?,
//                from: Picasso.LoadedFrom?
//            ) {
//                if (bitmap != null) {
//                    Log.d("IMAGE_WIDGET_LOAD_IMAGE", mWidgetItemsUrl[position])
//                    rv.setImageViewBitmap(R.id.imageView, bitmap)
//                }
//            }
//
//            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
//                if (errorDrawable != null) {
//                    rv.setImageViewBitmap(R.id.imageView, errorDrawable.toBitmap())
//                }
//            }
//
//            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
//            }
//
//        })
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
//        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

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