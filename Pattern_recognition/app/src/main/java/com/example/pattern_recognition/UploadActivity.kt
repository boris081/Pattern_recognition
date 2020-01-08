package com.example.pattern_recognition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_upload.*

class UploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val getImage = intent.getBundleExtra("Image").getByte("cameraImage")
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        imagetest.setImageBitmap(bitmap)
        Toast.makeText(this,getImage,Toast.LENGTH_LONG).show();
    }
}
