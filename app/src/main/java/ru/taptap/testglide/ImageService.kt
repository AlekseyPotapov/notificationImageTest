package ru.taptap.testglide

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.IBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ru.taptap.testglide.notification.TrainingNotification
import java.lang.IllegalStateException

class ImageService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    private val preferencesSource by lazy { PreferencesSource(App.context) }
    private val trainingNotification by lazy { TrainingNotification(App.context) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val uriString = intent?.getStringExtra(EXTRA_URI) ?: throw IllegalStateException("Что-то не то")
        val uriString = preferencesSource.imageUri
        val uri = Uri.parse(uriString)

        Glide.with(App.context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .load(uri)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val builder = trainingNotification.getBuilder()
                        .setContentTitle("Тестовая нотификация")
                        .setLargeIcon(resource)

                    startForeground(
                        TRAINING_NOTIFICATION_ID,
                        builder.build()
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })

        return Service.START_STICKY
    }

    companion object {

        private const val TRAINING_NOTIFICATION_ID = 7211
        private const val EXTRA_URI = "extra_uri"

        fun startService(context: Context, uri: Uri): Intent =
            Intent(context, ImageService::class.java).apply {
                putExtra(EXTRA_URI, uri.toString())
            }
    }
}