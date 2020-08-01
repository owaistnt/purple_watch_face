package com.artsman.purple

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderChooserIntent
import android.support.wearable.complications.ProviderInfoRetriever
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import java.util.concurrent.Executors


class ComplicationConfigActivity : Activity(), View.OnClickListener{
    val TAG="ComplicationConfigActivity"

    private var mLeftComplicationId: Int=-1
    private var mSelectedComplicationId: Int=-1

    private var mWatchFaceComponentName: ComponentName? = null
    private var mProviderInfoRetriever: ProviderInfoRetriever? =null

    private var mLeftComplicationBackground: ImageView? = null
    private var mLeftComplication: ImageButton? = null

    private var mDefaultAddComplicationDrawable: Drawable? = null

    companion object{
        private val COMPLICATION_CONFIG_REQUEST_CODE = 1001
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        mDefaultAddComplicationDrawable = getDrawable(R.drawable.add_complication)

        mLeftComplicationId=MyWatchFace.Companion.getComplicationId()
        mWatchFaceComponentName = ComponentName(applicationContext, MyWatchFace::class.java)

        // Sets up left complication preview.
        mLeftComplicationBackground = findViewById<View>(R.id.left_complication_background) as ImageView
        mLeftComplication = findViewById<View>(R.id.left_complication) as ImageButton

        // Sets default as "Add Complication" icon.
        mLeftComplication?.setImageDrawable(mDefaultAddComplicationDrawable)

        mLeftComplication?.setOnClickListener(this)

        // Initialization of code to retrieve active complication data for the watch face.
        mProviderInfoRetriever = ProviderInfoRetriever(
            applicationContext,
            Executors.newCachedThreadPool()
        )
        mProviderInfoRetriever?.init()
        retrieveInitialComplicationData()

    }

    private fun retrieveInitialComplicationData(){
        mProviderInfoRetriever?.retrieveProviderInfo(object :
            ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
            override fun onProviderInfoReceived(
                watchFaceComplicationId: Int,
                info: ComplicationProviderInfo?
            ) {
                Log.d(TAG, "\n\nonProviderInfoReceived: $info");
                updateComplicationViews(watchFaceComplicationId, info)
            }
        }, mWatchFaceComponentName, mLeftComplicationId)
    }

    private fun updateComplicationViews(
        watchFaceComplicationId: Int,
        info: ComplicationProviderInfo?
    ) {

        Log.d(TAG, "updateComplicationViews(): id: $watchFaceComplicationId")
        Log.d(TAG, "\tinfo: $info")
        info?.let {
            mLeftComplication?.setImageIcon(info.providerIcon)
            mLeftComplicationBackground?.visibility=View.VISIBLE
        }
    }


    override fun onClick(v: View?) {
        val supportedTypes= MyWatchFace.getComplicationSupportedType()
        mSelectedComplicationId=mLeftComplicationId

        if (mSelectedComplicationId > 0) {
            startActivityForResult(
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                    getApplicationContext(),
                    mWatchFaceComponentName,
                    mSelectedComplicationId,
                    *supportedTypes),
                COMPLICATION_CONFIG_REQUEST_CODE)

        }else{
            Log.d(TAG, "Complication not supported by watch face.");
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (COMPLICATION_CONFIG_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {

            // Retrieves information for selected Complication provider.
            val complicationProviderInfo: ComplicationProviderInfo? = data?.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO)
            Log.d(TAG, "Provider: $complicationProviderInfo")
            complicationProviderInfo?.let {
                if (mSelectedComplicationId >= 0) {
                    updateComplicationViews(mSelectedComplicationId, complicationProviderInfo)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mProviderInfoRetriever?.release()
    }


}