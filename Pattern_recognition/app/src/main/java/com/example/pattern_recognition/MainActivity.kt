package com.example.pattern_recognition


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val packageName = "com.example.pattern_recognition"

        //跳頁至拍照畫面
        ButtonCamera.setOnClickListener {
            intentActivity("$packageName.camera.CameraActivity")
        }

        //跳至MYSQL畫面
        ButtonData.setOnClickListener {
            intentActivity("$packageName.DataActivity")
        }
    }

    //Intent fun
    @Throws(ClassNotFoundException::class)
    private fun intentActivity(s: String) {
        val page = Intent(this@MainActivity, Class.forName(s))
        startActivity(page)
    }

}
