package com.example.pattern_recognition

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pattern_recognition.mysql.MysqlCon
import kotlinx.android.synthetic.main.activity_data.*


class DataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        Thread(Runnable {
            val con = MysqlCon()
            con.run()
                val data = con.getData()
            Log.v("OK", data)
            mysqlTextview.post(Runnable { mysqlTextview.setText(data) })
        }).start()
    }
}
