package com.example.pattern_recognition

import android.content.Intent
import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButtonCamera.setOnClickListener{
            buttonEvent(1);
        }
        ButtonCamera.setOnClickListener{
            buttonEvent(2);
        }
    }

    private fun buttonEvent(i:Int) {
        val intent =  Intent()
        if (i==1){
            intent.setClass(this,CameraActivity::class.java)
        }
        if (i==2){
            intent.setClass(this,DataInquiry::class.java)
        }

    }

}
