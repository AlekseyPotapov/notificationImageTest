package ru.taptap.testglide

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.single.BasePermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val imageView: ImageView by lazy { findViewById<ImageView>(R.id.image) }
    private val buttonView: Button by lazy { findViewById<Button>(R.id.addButton) }
    private val rootView: ViewGroup by lazy { findViewById<ViewGroup>(R.id.root) }

    private val preferences: PreferencesSource by lazy { PreferencesSource(this) }

    private val scope = CoroutineScope(
        Job() + Dispatchers.IO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonView.setOnClickListener {
            Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(ReadExternalStoragePermissionListener())
                .check()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PICTURE_TAG -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    getPictureFromGallery(data)
                }
            }
        }
    }

    private fun getPictureFromGallery(data: Intent) {
        scope.launch(Dispatchers.IO) {
            val fileUri: Uri = try {
                when {
                    data.data != null -> data.data!!
                    !data.dataString.isNullOrEmpty() -> Uri.parse(data.dataString)
                    else -> {
                        return@launch
                    }
                }
            } catch (e: Exception) {
                return@launch
            }

            if (fileUri.path == null) {
                return@launch
            }

            preferences.imageUri = fileUri.toString()

            withContext(Dispatchers.Main) {
                startService(ImageService.startService(this@MainActivity, fileUri))
                imageView.load(fileUri)
            }
        }
    }

    inner class ReadExternalStoragePermissionListener : BasePermissionListener() {
        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))

            //todo

            startActivityForResult(
                Intent.createChooser(
                    intent,
                    getString(R.string.label_select_picture)
                ), SELECT_PICTURE_TAG
            )
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                .with(rootView, R.string.permission_external_storage_denied)
                .withOpenSettingsButton(R.string.permission_external_storage_denied_settings)
                .withCallback(object : Snackbar.Callback() {
                    override fun onShown(sb: Snackbar?) {
                        super.onShown(sb) // todo тут можно добавить статистику
                    }

                    override fun onDismissed(
                        transientBottomBar: Snackbar?,
                        event: Int
                    ) {
                        super.onDismissed(
                            transientBottomBar,
                            event
                        ) // todo тут можно добавить статистику
                    }
                })
                .build()
        }
    }

    companion object {
        private const val SELECT_PICTURE_TAG = 4277
    }
}

fun ImageView.load(uri: Uri) {
    Glide.with(context)
        .load(uri)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .into(this)
        .waitForLayout()
}