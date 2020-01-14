package com.example.pattern_recognition

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload.*
import okhttp3.*
import org.json.JSONObject
import java.io.*
import java.net.URL
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject.NULL


class UploadActivity : AppCompatActivity() {

    @SuppressLint("WrongConstant", "ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val bmp = intent.getByteArrayExtra("BitmapImage")
        val bitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.size)
        val uri = BitmaptoFile(bitmap)
        Toast.makeText(this, uri.toString(), 1).show()
        uploadAIserver(bitmap);
    }

    fun BitmaptoFile(bmp: Bitmap): Uri {
//        var file =  "/storage/emulated/0/Images"
        var file =  getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File(file , "image1.jpg")

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.toString())
    }

    fun uploadAIserver(bmp:Bitmap) {
        if (bmp != NULL) { //取得圖檔的路徑位置
            try {
                var file =  getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                file = File(file , "image1.jpg")
                val body= MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image1.jpg", file.asRequestBody(null)).build()
                val request= Request.Builder()
                    .url("http://584193cf.ngrok.io/test/")
                    .post(body)
                    .build()
                val call= OkHttpClient().newBuilder().build().newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("get ", e.toString())
                    }
                    override fun onResponse(call: Call, response: Response) {
                        val res= JSONObject(response.body!!.string())
                        runOnUiThread{
                            DownloadImageTask(imageView).execute("http://584193cf.ngrok.io/output/image1.jpg")
                            textView.text=res.getJSONObject("respond").getString("result")
                        }
                    }
                })
            } catch (e: FileNotFoundException) {
                Log.e("Exception", e.message.toString())
            }
        }
    }

    class DownloadImageTask(private val bmImage: ImageView) : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            val urldisplay = params[0]
            var mIcon11: Bitmap? = null
            try {
                val `in`: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            bmImage.setImageBitmap(result)
        }
    }
}



