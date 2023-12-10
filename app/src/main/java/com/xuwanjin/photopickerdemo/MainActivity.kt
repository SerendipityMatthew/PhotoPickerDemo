package com.xuwanjin.photopickerdemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.xuwanjin.photopickerdemo.ui.theme.PhotoPickerDemoTheme


class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }



    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PhotoPickerDemoTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        AllPhotoPicker()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}

@Composable
fun ColumnScope.AllPhotoPicker() {
    val pictureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                Log.d(MainActivity.TAG, "GetContent= $uri")
            }
        )
    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            pictureLauncher.launch("image/*")
        }
    ) {
        Text(text = "GetContent, launch Image/* ")
    }


    val pictureLauncher2 =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { uri ->
                Log.d(MainActivity.TAG, "pictureLauncher2: selected image uri = $uri")
            })
    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            val pickIntent =
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
            pictureLauncher2.launch(pickIntent)
        }
    ) {
        Text(text = "ACTION_PICK, INTERNAL_CONTENT_URI")
    }

    val pictureLauncher3 =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { uri ->
                Log.d(MainActivity.TAG, "pictureLauncher2: selected image uri = $uri")
            })
    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            val pickIntent =
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            pictureLauncher3.launch(pickIntent)
        }
    ) {
        Text(text = "ACTION_PICK, EXTERNAL_CONTENT_URI")
    }

    val pictureLauncher4 =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { uri ->
                Log.d(MainActivity.TAG, "pictureLauncher2: selected image uri = $uri")
            })
    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            val pickIntent =
                Intent(
                    MediaStore.ACTION_PICK_IMAGES,
                ).apply {
                    type = "image/*"
                }
            pictureLauncher4.launch(pickIntent)
        }
    ) {
        Text(text = "ACTION_PICK_IMAGES")
    }


    val pictureLauncher5 =
        rememberLauncherForActivityResult(contract = PickContentLegacyDocumentTree()
            .apply {


            },
            onResult = { uri ->
                Log.d(MainActivity.TAG, "pictureLauncher2: selected image uri = $uri")
            })
    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            pictureLauncher5.launch(Unit)
        }
    ) {
        Text(text = "ACTION_OPEN_DOCUMENT,image/* ")
    }

    val pictureLauncher6 = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            pictureLauncher6.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    ) {
        Text(text = "PickVisualMediaRequest, ImageOnly ")
    }


    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2)
    ) {
        Log.d(MainActivity.TAG, "MainScreen:multiplePhotoPicker uris :  $it")
    }

    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            multiplePhotoPicker.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    ) {
        Text(text = "PickMultipleVisualMedia, maxItems = 2 ")
    }

}

class PickContentLegacyDocumentTree : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == ComponentActivity.RESULT_OK) {
            val data = intent?.data
            return data
        }

        return null
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhotoPickerDemoTheme {
        Greeting("Android")
    }
}

val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
    )
} else {
    arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
}