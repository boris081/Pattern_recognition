package com.example.pattern_recognition.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.*
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pattern_recognition.R
import com.example.pattern_recognition.UploadActivity
import kotlinx.android.synthetic.main.activity_camera.*
import java.util.*


class CameraActivity : AppCompatActivity() {

    private var mainHandler : Handler? = null
    private var childHandler: Handler? = null
    private var mImageReader: ImageReader? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCameraDevice: CameraDevice? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null

    private var imageViewData: ByteArray? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        surfaceInit()

        picButton.setOnClickListener({ takePicture() })

        uploadButton.setOnClickListener {
            val intent = Intent(this,UploadActivity::class.java)
            intent.putExtra("BitmapImage", imageViewData)
            startActivity(intent)
        }
    }

    fun surfaceInit() {
        mSurfaceHolder = surfaceView.holder
        mSurfaceHolder?.setKeepScreenOn(true)
        mSurfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun surfaceCreated(holder: SurfaceHolder) { //SurfaceView创建
                // 初始化Camera
                initCamera2()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) { //SurfaceView销毁
                // 释放Camera资源
                if (null != mCameraDevice) {
                    mCameraDevice!!.close()
                    mCameraDevice = null
                }
            }
        })
    }

    fun initCamera2() {
        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper)
        mainHandler = Handler(Looper.getMainLooper())
        mImageReader = ImageReader.newInstance(800, 450, ImageFormat.JPEG, 1)
        mImageReader?.setOnImageAvailableListener({ reader: ImageReader ->
            // 拿到拍照照片數據
            val image = reader.acquireLatestImage()
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer[bytes] //緩衝區寫入字組
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageViewData = bytes
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap) //圖片顯示到imageview

            }
            image.close()
        }, mainHandler)

        //獲取相機管理
        val mCameraManager = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            //偵測camera ID
            val cameraNumber = mCameraManager.cameraIdList
            if (cameraNumber.size > 2) {
                mCameraManager.openCamera("1", stateCallback, mainHandler)
            } else {
                mCameraManager.openCamera("0", stateCallback, mainHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            //開啟預覽
            takePreview()
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onDisconnected(camera: CameraDevice) {
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                this@CameraActivity.mCameraDevice = null
            }
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Toast.makeText(this@CameraActivity, "88", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePreview() {
        try { // 創建需要CaptureRequest.Builder
            val previewRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(mSurfaceHolder?.surface!!)
            // 創建CameraCaptureSession，負責拍照請求跟預覽請求
            mCameraDevice!!.createCaptureSession(
                Arrays.asList(
                    mSurfaceHolder?.surface,
                    mImageReader!!.surface
                ), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (null == mCameraDevice) return
                        // 準備完成，開始預覽
                        mCameraCaptureSession = cameraCaptureSession
                        try { // 自動對焦
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            // 開啟閃光燈
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                            )
                            // 顯示預覽
                            val previewRequest = previewRequestBuilder.build()
                            mCameraCaptureSession!!.setRepeatingRequest(
                                previewRequest,
                                null,
                                childHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {}
                }, childHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun takePicture() {
        if (mCameraDevice == null) return
        // 創建拍照需要的CaptureRequest.Builder
        val captureRequestBuilder: CaptureRequest.Builder
        try {
            captureRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader!!.surface)
            // 自動對焦
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            // 自動曝光
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
            // 獲取手機方向
            val rotation =
                this@CameraActivity.windowManager.defaultDisplay.rotation
            // 調整拍照方向
//            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
//拍照
            val mCaptureRequest = captureRequestBuilder.build()
            mCameraCaptureSession!!.capture(mCaptureRequest, null, childHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

}



