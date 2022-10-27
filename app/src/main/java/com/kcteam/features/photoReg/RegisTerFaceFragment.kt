package com.kcteam.features.photoReg

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.kcteam.MySingleton
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.faceRec.FaceStartActivity
import com.kcteam.faceRec.tflite.SimilarityClassifier.Recognition
import com.kcteam.faceRec.tflite.TFLiteObjectDetectionAPIModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.photoReg.api.GetUserListPhotoRegProvider
import com.kcteam.features.photoReg.model.FaceRegResponse
import com.kcteam.features.photoReg.model.UserListResponseModel
import com.kcteam.features.photoReg.model.UserPhotoRegModel
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.pnikosis.materialishprogress.ProgressWheel
import com.theartofdev.edmodo.cropper.CropImage
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register_face.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.util.*

class RegisTerFaceFragment: BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    private var imagePath: String = ""
    private lateinit var nameTV: AppCustomTextView
    private lateinit var phoneTV: AppCustomTextView
    private lateinit var registerTV: Button
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var ll_phone : LinearLayout

    private lateinit var shopLargeImg:ImageView

    var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    lateinit var imgUri:Uri
    var facePicTag:Boolean = false

    ////
    protected var previewWidth = 0
    protected var previewHeight = 0
    private var portraitBmp: Bitmap? = null
    private var rgbFrameBitmap: Bitmap? = null
    private var faceBmp: Bitmap? = null
    var faceDetector: FaceDetector? = null
    private val TF_OD_API_MODEL_FILE = "mobile_face_net.tflite"
    val TF_OD_API_IS_QUANTIZED = false
    val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"
    val TF_OD_API_INPUT_SIZE = 112

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object{
        var user_id: String? = null
        var user_name: String? = null
        var user_login_id: String? = null
        var user_contactid: String? = null
        fun getInstance(objects: Any): RegisTerFaceFragment {
            val regisTerFaceFragment = RegisTerFaceFragment()
            if (!TextUtils.isEmpty(objects.toString())) {

                var obj = objects as UserListResponseModel

                user_id=obj!!.user_id.toString()
                user_name=obj!!.user_name
                user_login_id=obj!!.user_login_id
                user_contactid=obj!!.user_contactid
            }
            return regisTerFaceFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_register_face, container, false)
        initView(view)
        return view
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView(view:View){
        nameTV = view.findViewById(R.id.tv_frag_reg_face_name)
        phoneTV = view.findViewById(R.id.tv_frag_reg_face_phone)
        registerTV = view.findViewById(R.id.btn_frag_reg_face_register)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        registerTV.setOnClickListener(this)

        nameTV.text = user_name!!
        phoneTV.text = user_login_id!!

        shopLargeImg = view.findViewById(R.id.iv_frag_reg_face)

        ll_phone = view.findViewById(R.id.ll_regis_face_phone);
        ll_phone.setOnClickListener(this)

        faceDetectorSetUp()
        faceDetectorSetUpRandom()

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            initPermissionCheck()
        else {
            launchCamera()
        }*/
        //showPictureDialog()

        //startActivity(Intent(mContext, CustomCameraActivity::class.java))

        launchCamera()
        //checkCustom("http://3.7.30.86:82/CommonFolder/AadharImage/119842021-11-22image_1637569676078.jpg","http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg")
    }

    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> selectImageInAlbum()
                1 -> launchCamera()
            }
        }
        pictureDialog.show()
    }

    fun selectImageInAlbum() {
        if (PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_STORAGE)
        }
    }


    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                launchCamera()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/

            (mContext as DashboardActivity).captureImage()
        }
    }

    fun setImage(imgRealPath: Uri, fileSizeInKB: Long) {
        imgUri=imgRealPath
        imagePath = imgRealPath.toString()

        getBitmap(imgRealPath.path)


            /*Picasso.get()
                    .load(imgRealPath)
                    .resize(500, 500)
                    .into(shopLargeImg)*/

    }

    private fun registerFaceApi(){
        progress_wheel.spin()
        var obj= UserPhotoRegModel()
        //obj.user_id= Pref.user_id
        obj.user_id= user_id
        obj.session_token=Pref.session_token

        //obj.registration_date_time=AppUtils.getCurrentDateTimeDDMMYY()
        obj.registration_date_time=AppUtils.getCurrentDateTime()

        val repository = GetUserListPhotoRegProvider.providePhotoReg()
        BaseActivity.compositeDisposable.add(
                repository.addUserFaceRegImg(obj,imagePath,mContext,user_contactid)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as FaceRegResponse
                            if(response.status== NetworkConstant.SUCCESS){
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.face_reg_success))
                                Handler(Looper.getMainLooper()).postDelayed({
                                    progress_wheel.stopSpinning()
                                    //extractAadhaarDtls()
                                    (mContext as DashboardActivity).loadFragment(FragType.ProtoRegistrationFragment, false, "")

                                }, 500)

                                XLog.d(" RegisTerFaceFragment : FaceImageDetection/FaceImage" +response.status.toString() +", : "  + ", Success: "+AppUtils.getCurrentDateTime().toString())
                            }else{
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_reg_face))
                                XLog.d("RegisTerFaceFragment : FaceImageDetection/FaceImage : " + response.status.toString() +", : "  + ", Failed: "+AppUtils.getCurrentDateTime().toString())
                            }
                        },{
                            error ->
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_reg_face))
                            if (error != null) {
                                XLog.d("RegisTerFaceFragment : FaceImageDetection/FaceImage : " + " : "  + ", ERROR: " + error.localizedMessage)
                            }
                        })
        )
    }

    override fun onClick(p0: View?) {
        if(p0!=null){
            when(p0.id){
                R.id.btn_frag_reg_face_register -> {
                    //if(registerTV.isEnabled==false){
                    if(!facePicTag){
                        Toaster.msgShort(mContext,"Please capture valid image")
                        return
                    }

                    if(imagePath.length>0 && imagePath!="") {
                        val simpleDialogg = Dialog(mContext)
                        simpleDialogg.setCancelable(false)
                        simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialogg.setContentView(R.layout.dialog_yes_no)
                        val dialogHeader = simpleDialogg.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val dialogHeaderHH = simpleDialogg.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                        dialogHeader.text="Do you want to Register ?"
                        dialogHeaderHH.text="Hi "+Pref.user_name!!+"!"
                        val dialogYes = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                        val dialogNo = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

                        dialogYes.setOnClickListener( { view ->
                            simpleDialogg.cancel()
                            if (AppUtils.isOnline(mContext)){
                                registerFaceApi()
                            }else{
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                            }

                        })
                        dialogNo.setOnClickListener( { view ->
                            simpleDialogg.cancel()
                        })
                        simpleDialogg.show()
                    }


                       // registerFaceApi()
                }

                R.id.ll_regis_face_phone ->{
                    IntentActionable.initiatePhoneCall(mContext, phoneTV.text.toString())
                }

            }
        }
    }





    @SuppressLint("UseRequireInsteadOfGet")
    private fun saveImageToGallery() {
        iv_frag_reg_face.setRotation(90f)
        iv_frag_reg_face.setDrawingCacheEnabled(true)
        val b: Bitmap = iv_frag_reg_face.getDrawingCache()
        MediaStore.Images.Media.insertImage(activity!!.contentResolver, b, imgUri.toString(), "")
    }

////////////////////////////////////////////////////////////


    fun faceDetectorSetUp(){
        try {
            FaceStartActivity.detector = TFLiteObjectDetectionAPIModel.create(
                    mContext.getAssets(),
                    TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE,
                    TF_OD_API_INPUT_SIZE,
                    TF_OD_API_IS_QUANTIZED)
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (e: IOException) {
            e.printStackTrace()
            //LOGGER.e(e, "Exception initializing classifier!");
            val toast = Toast.makeText(mContext, "Classifier could not be initialized", Toast.LENGTH_SHORT)
            toast.show()
            //finish()
        }
        val options = FaceDetectorOptions.Builder()
                //.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

        val detector = FaceDetection.getClient(options)

        faceDetector = detector
    }


    lateinit var face1:Face
    lateinit var face2:Face

    private fun registerFace(mBitmap: Bitmap?) {

/*        val face_dec:com.google.android.gms.vision.face.FaceDetector = com.google.android.gms.vision.face.FaceDetector.Builder(mContext)
                .setTrackingEnabled(false)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                .build()

        val frame: Frame = Frame.Builder().setBitmap(mBitmap).build()
        //val faces: SparseArray = face_dec.detect(frame)
        val facesa: SparseArray<com.google.android.gms.vision.face.Face> = face_dec.detect(frame)
        var ss="asf"
        Toaster.msgShort(mContext,"facesa"+facesa.size().toString())*/

        try {
            if (mBitmap == null) {
                //Toast.makeText(this, "No File", Toast.LENGTH_SHORT).show()
                return
            }
            //ivFace.setImageBitmap(mBitmap)
            previewWidth = mBitmap.width
            previewHeight = mBitmap.height
            rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
            portraitBmp = mBitmap
            val image = InputImage.fromBitmap(mBitmap, 0)
            faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Bitmap.Config.ARGB_8888)
            faceDetector?.process(image)?.addOnSuccessListener(OnSuccessListener<List<Face>> { faces ->
                if (faces.size == 0) {
                    Toaster.msgShort(mContext,"Please choose proper face")
                    facePicTag=false
                    return@OnSuccessListener
                }
                Handler().post {
                    object : Thread() {
                        override fun run() {
                            //action
                            //onFacesDetected(1, faces, true) //no need to add currtime
                            face1=faces.get(0)
                            activateRegisterFace()
                        }
                    }.start()
                }
            })


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun activateRegisterFace(){
        doAsync {
            uiThread {
                //registerTV.isEnabled=true
                //faceDetector?.close()
                facePicTag=true
                //GetImageFromUrl().execute("http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg")
                //Toaster.msgShort(mContext,"face present in pic")
            }
        }


    }

    //fun getBitmap(path: String?): Bitmap? {
    fun getBitmap(path: String?) {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            shopLargeImg.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        facePicTag=true
        //registerFace(bitmap)






        //return bitmap
    }


    ///////////////////////////////////////testtttttttttt
    var faceDetectorRandom: FaceDetector? = null

    fun faceDetectorSetUpRandom(){
        try {
            FaceStartActivity.detector = TFLiteObjectDetectionAPIModel.create(
                    mContext.getAssets(),
                    TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE,
                    TF_OD_API_INPUT_SIZE,
                    TF_OD_API_IS_QUANTIZED)
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (e: IOException) {
            e.printStackTrace()
            //LOGGER.e(e, "Exception initializing classifier!");
            val toast = Toast.makeText(mContext, "Classifier could not be initialized", Toast.LENGTH_SHORT)
            toast.show()
            //finish()
        }
        val options = FaceDetectorOptions.Builder()
                //.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

        val detector = FaceDetection.getClient(options)

        faceDetectorRandom = detector
    }

    private fun registerFaceRandom(mBitmap: Bitmap?) {

/*        val face_dec:com.google.android.gms.vision.face.FaceDetector = com.google.android.gms.vision.face.FaceDetector.Builder(mContext)
                .setTrackingEnabled(false)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                .build()

        val frame: Frame = Frame.Builder().setBitmap(mBitmap).build()
        //val faces: SparseArray = face_dec.detect(frame)
        val facesa: SparseArray<com.google.android.gms.vision.face.Face> = face_dec.detect(frame)
        var ss="asf"
        Toaster.msgShort(mContext,"facesa"+facesa.size().toString())*/

        try {
            if (mBitmap == null) {
                //Toast.makeText(this, "No File", Toast.LENGTH_SHORT).show()
                return
            }
            //ivFace.setImageBitmap(mBitmap)
            previewWidth = mBitmap.width
            previewHeight = mBitmap.height
            rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
            portraitBmp = mBitmap
            val image = InputImage.fromBitmap(mBitmap, 0)
            faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Bitmap.Config.ARGB_8888)
            faceDetectorRandom?.process(image)?.addOnSuccessListener(OnSuccessListener<List<Face>> { faces ->
                if (faces.size == 0) {
                    Toaster.msgShort(mContext,"Please choose proper face")
                    facePicTag=false
                    return@OnSuccessListener
                }
                Handler().post {
                    object : Thread() {
                        override fun run() {
                            //action
                            //onFacesDetected(1, faces, true) //no need to add currtime
                            face2=faces.get(0)
                           var ss="asd"
                            if(face1==face2){
                                var a="as"
                            }else{
                                var b="ty"
                            }
                            onFacesDetected(1, faces, true)
                        }
                    }.start()
                }
            })


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    var cropToFrameTransform: Matrix? = Matrix()

    fun onFacesDetected(currTimestamp: Long, faces: List<Face>, add: Boolean) {
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.0f
        val mappedRecognitions: MutableList<Recognition> = LinkedList()


        //final List<Classifier.Recognition> results = new ArrayList<>();

        // Note this can be done only once
        val sourceW = rgbFrameBitmap!!.width
        val sourceH = rgbFrameBitmap!!.height
        val targetW = portraitBmp!!.width
        val targetH = portraitBmp!!.height
        val transform = createTransform(
                sourceW,
                sourceH,
                targetW,
                targetH,
                90)
        val mutableBitmap = portraitBmp!!.copy(Bitmap.Config.ARGB_8888, true)
        val cv = Canvas(mutableBitmap)

        // draws the original image in portrait mode.
        cv.drawBitmap(rgbFrameBitmap!!, transform!!, null)
        val cvFace = Canvas(faceBmp!!)
        val saved = false
        for (face in faces) {
            //results = detector.recognizeImage(croppedBitmap);
            val boundingBox = RectF(face.boundingBox)

            //final boolean goodConfidence = result.getConfidence() >= minimumConfidence;
            val goodConfidence = true //face.get;
            if (boundingBox != null && goodConfidence) {

                // maps crop coordinates to original
                cropToFrameTransform?.mapRect(boundingBox)

                // maps original coordinates to portrait coordinates
                val faceBB = RectF(boundingBox)
                transform.mapRect(faceBB)

                // translates portrait to origin and scales to fit input inference size
                //cv.drawRect(faceBB, paint);
                val sx = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.width()
                val sy = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.height()
                val matrix = Matrix()
                matrix.postTranslate(-faceBB.left, -faceBB.top)
                matrix.postScale(sx, sy)
                cvFace.drawBitmap(portraitBmp!!, matrix, null)

                //canvas.drawRect(faceBB, paint);
                var label = ""
                var confidence = -1f
                var color = Color.BLUE
                var extra: Any? = null
                var crop: Bitmap? = null
                if (add) {
                    try {
                        crop = Bitmap.createBitmap(portraitBmp!!,
                                faceBB.left.toInt(),
                                faceBB.top.toInt(),
                                faceBB.width().toInt(),
                                faceBB.height().toInt())
                    } catch (eon: java.lang.Exception) {
                        //runOnUiThread(Runnable { Toast.makeText(mContext, "Failed to detect", Toast.LENGTH_LONG) })
                    }
                }
                val startTime = SystemClock.uptimeMillis()
                val resultsAux = FaceStartActivity.detector.recognizeImage(faceBmp, add)
                val lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
                if (resultsAux.size > 0) {
                    val result = resultsAux[0]
                    extra = result.extra
                    //          Object extra = result.getExtra();
//          if (extra != null) {
//            LOGGER.i("embeeding retrieved " + extra.toString());
//          }
                    val conf = result.distance
                    if (conf < 1.0f) {
                        confidence = conf
                        label = result.title
                        color = if (result.id == "0") {
                            Color.GREEN
                        } else {
                            Color.RED
                        }
                    }
                }
                val flip = Matrix()
                flip.postScale(1f, -1f, previewWidth / 2.0f, previewHeight / 2.0f)

                //flip.postScale(1, -1, targetW / 2.0f, targetH / 2.0f);
                flip.mapRect(boundingBox)
                val result = Recognition(
                        "0", label, confidence, boundingBox)
                result.color = color
                result.location = boundingBox
                result.extra = extra
                result.crop = crop
                mappedRecognitions.add(result)
            }
        }

        //    if (saved) {
//      lastSaved = System.currentTimeMillis();
//    }

        Log.e("xc", "startabc" )
        val rec = mappedRecognitions[0]
        FaceStartActivity.detector.register("", rec)
        //val intent = Intent(mContext, DetectorActivity::class.java)
        //startActivityForResult(intent, 171)
//        startActivity(new Intent(this,DetectorActivity.class));
//        finish();

        // detector.register("Sakil", rec);
        /*   runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivFace.setImageBitmap(rec.getCrop());
                //showAddFaceDialog(rec);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.image_edit_dialog, null);
                ImageView ivFace = dialogLayout.findViewById(R.id.dlg_image);
                TextView tvTitle = dialogLayout.findViewById(R.id.dlg_title);
                EditText etName = dialogLayout.findViewById(R.id.dlg_input);

                tvTitle.setText("Register Your Face");
                ivFace.setImageBitmap(rec.getCrop());
                etName.setHint("Please tell your name");
                detector.register("sam", rec); //for register a face

                //button.setPressed(true);
                //button.performClick();
            }

        });*/

        // updateResults(currTimestamp, mappedRecognitions);
    }

    fun createTransform(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, applyRotation: Int): Matrix? {
        val matrix = Matrix()
        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                // LOGGER.w("Rotation of %d % 90 != 0", applyRotation);
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

//        // Account for the already applied rotation, if any, and then determine how
//        // much scaling is needed for each axis.
//        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
//        final int inWidth = transpose ? srcHeight : srcWidth;
//        final int inHeight = transpose ? srcWidth : srcHeight;
        if (applyRotation != 0) {

            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }
        return matrix
    }


    inner class GetImageFromUrl : AsyncTask<String?, Void?, Bitmap?>() {
        fun GetImageFromUrl() {
            //this.imageView = img;
        }
        override fun doInBackground(vararg url: String?): Bitmap {
            var bitmappppx: Bitmap? = null
            val stringUrl = url[0]
            bitmappppx = null
            val inputStream: InputStream
            try {
                inputStream = URL(stringUrl).openStream()
                bitmappppx = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmappppx!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            registerFaceRandom(result)
        }

    }

    //////////////////////////
    private fun checkCustom( doc1:String, doc2:String) {
            try {
                val jsonObject = JSONObject()
                val notificationBody = JSONObject()
                notificationBody.put("document1", encodedPictureString.toString())
                //notificationBody.put("document1", doc1)
                notificationBody.put("document2", doc2)
                jsonObject.put("data", notificationBody)
                val jsonArray = JSONArray()
                jsonObject.put("task_id", "11986")
                jsonObject.put("group_id", "11986")
                sendCustomNotification(jsonObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
    }


    fun sendCustomNotification(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest("https://eve.idfy.com/v3/tasks/sync/compare/face", notification,
                object : Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {
                        var jObj:JSONObject= JSONObject()
                        jObj=response!!.getJSONObject("result")
                        var tt=jObj.getBoolean("is_a_match")
                        var ttt=jObj.getDouble("match_score")
                        var gg="asd"
                        //checkCustom("http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg","http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg")
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        var yy="wre"

                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["api-key"] = "dfe0a602-7e79-4a5b-af00-509fc0e8349a"
                params["Content-Type"] = "application/json"
                params["account-id"] = "aaa73f1c1bdb/fa4cf738-2dda-41db-b0e5-0b406ebe6d2f"
                return params
            }
        }
        jsonObjectRequest.setRetryPolicy(DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        MySingleton.getInstance(mContext.applicationContext)!!.addToRequestQueue(jsonObjectRequest)

    }

    private var encodedPictureString = "iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAQAAABpN6lAAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QAAKqNIzIAAAAJcEhZcwAADdcAAA3XAUIom3gAAAAHdElNRQfiBQkLJhWA5lj9AAAIe0lEQVR42u2df2yV1RnHP/eC1axwhzKnLCgoSxse3SJLYSS7OO7VxqRpogO3VbYiW9wP/IeYmG2gG1XSH7IsyxKSJU4lQAJ1aoMJaSTdWjeuCYEmNopHWkNWoQIbk2F/hKWV3f1xe9t7e9977/vrnPde2m9y06b3fc55vt+e97znPO85zwlhDCpCFdVUs4JFRFg4+YFhRiY/VzhDP/0MyLApr0LaaVcSJc4aqlniwOwC/Zygm4SMlakAqoIocWKs5gYPxUxwkm56SMh4GQmg1tJIA7f4WORl2jkgx0teALWMRhqp8t9RAAY4wAH5uEQFUDXs4BHtvUqSw7RIb4kJoNbxDA9ppp6JozTLsRIRQD3ATtYZJJ/GMZ6TvwYsgFrK73k0APJpvM5TMhSQAGo+22hiQYD0AUZp4g/yuXEBVJQ/cm/A5NM4xVZJGBRAzWMXv9I/inSAJG38Wq4ZEUAt5RDRoBlbIMFjzvuDsGP6dfSVJH2I0qfqnBo5agEqRCu/KKmmPxNJdrNdkloEUBXsoyFohjbQzuP2p062BVAL6KA2aG420cUGGfVVAHUrndQEzcsBeqmTS74JoO6kS9sMTxcGqJWzvgigbiVRdvRTEkSLt4Kij0G1gM6ypA9VdKqiA/UiAqgKOsrq3s9GDR2qwoMAKsS+sun5rVHLPlXwNi/cAlrL4rlfGA20Fvq6gDqqjiMlPeqziyT10ulYALWUPhYH7btP+JT78k2T8twCah6Hrhv6sJhDap4jAdhVojM+t4iyy/oLy1tARfn7dXH3ZyLJ/VZRIwuaaj7vlkywy0+cYlVu7NDqFthmiP5VTvMWb3Gaq0bqu5dtuX/MaQFqKR9qj/SO0M5LciKj1m/yBA3a6x1l5cynQa4Ar2mO8yfZww6r2bqK0MbPNfc9r8t3CwqgHuAvWh04yxbpyf+1irOXO7V68GD226SZfcBOrZUPEi1EH6SbKINafZjBMEsAtU7rO75zxORcsYvkHDGKXuUB61QWx+wW8IzGiieol0E7F8og9Uxo9CSLZYYAqkbrC+4Wec/upfIezRo9eUhlRDgyW8AOjZW+75BSC+9r9CaD6dRTQC3jHxofQRulw5mB2sAb2rxJcld6oc10C2jUSH+QNx3bvKnxaRCiMf1rpgD6sMf5e1u5xh6NHs0UQK3VGvk9ZNDKHqrU2iwBtP7/P5LzbszkPB9p9GpzhgCqQmvw828BWBbH91MB81QLiPq6qnMmXC5e8WRZHLekYl4pAeIaKwL3q7g8rf8qivi0ADGtFdl6S+uzpR3EJgVQlayelQKsVpWpFhD1tKC9ONwHvPSGym4gmhJAbw8AdwRgaQ/xlABrNFezzLXlcs2erUkJUK25Gvc03EtnD9UQVhFHe3nM0liu2bMlKhI2sPrDPQ3dAkBVWPsNADF1oxszdaPm8QlAtQkBFvMdV3YPax2gTwmwQnsl8IQrqx8b8GxFmEUGqomru52aqDuMrE5aFCZioJqQizbwI+cr2V0gEmahgWrgKfU1J5erlfzSiF8LzbQAuIlXVaVt+jfRzheM+GWsBcBKB0HO3/F1Q14ZawEAW9Tjdi5T3+NJYz5FTHQ003hFNav5BcmHVZPWaHAOQuoSXzIqwjtsyreMXd3OQQOjv0z8O8yI0QrhW/Sp+yzpL6HPMH0YCWMsWcUUbs7zhB/hy8Z9GTbfAmCcU1Z/llHOGPclgBbwX36Yd09Xm9aFEVYYDnPFaIUXeVBey/elvEyci0b9uRI22Ow+41m+Ku8UukQSVLOTz4z5dCZMv5GKrvJb7pbm4klxZFie5y5a0Jw+ZxL9IVXDSc2VjPMnmuWCMyN1G8/yUyqcWTnG6pCKaG1w19jPc27zvqjlPM8PtE6LvxgCdV5TXDjJn/mNDHgrRN1DMw9ron9BvhIGTb3Av4hLg1f6IB/II8T5pxYf+1MvRk54LigXx/mGvO1XYdJDjRYvT6QE6Pa94Bf5tnziZ4EyxP3s9d3PbgiBquQ/vr4f7pCNvrsKgDrIYz4WN8HNMhYGGfP1QXiaLXroAz9B+VjaSRlLrxDp8VjUNEbZINqmVzLGRh8nbz2QFsC/XuBJ+VAXfQA5zc98K6x7WoAEl30p8hwHddIHoN2n2cvl1Bq0MICM0+5LoS+6SWXkDJLkZV8KejU1KU8PMw/4UOQEL+mmD8BeXGcOy8D+1I9JAeQ4nsdsHBYjc3m5yBHPhQyk03NOTzS8t4FXTNAH8GFINMU2UwAH+Zcs8YExAWxvvsmDpIUA8jGHPRV6DVdrwl3hE/7nyf7w9AQ9c67d4qnQC/qfAGnIBA7DKzOQwTRDAOnlqIdCde7187e2o5k5abOjLV42q5kVwEaOqLzIYpklgBzDfaracmkBx7LT8fq3efqipqiNNW7jdpeWMzZPm98+HyyKbZ83lEAhKFgkUMgJOcsQTUH7qQ1NudmE5pKo5F4nn7PV87C49JBkq1X+acu3LpKgLWh/fUebde7pPBum1Tzevq5ySSVYbz1Un0umls9Ghth8nfQESTbnT7ld4M2rdLI7aN99we782QSLZZTc7lOwNEi0s73Q10WyRqgKjpR1VtEu6gun2S6aNkMtoKds88r2EiuWYnsusXLxcuQStT4Ezc3Tr7WTXdzW+hs5SxTfjjYygl6idjKL2z5hQi4RoytoVrbRRcxebnkHR2zIKPVl8lBsp97u6QKOzhiRcTbxQomPDpO8wCYnR/M5zh6l6thfsnOET9lcaNTniwCz/pgdkCHW01pit0KSVta7OXVs1h+15XodriRYxdPY7m21YZSnWeWW/txxe3MHLs4duemfT7P60NUpEWbzsbsZMszeg5czRJjNR29nCTFbD1+3kCJCFdVUs4JFRFg4+YFhRiY/VzhDP/0MiLH9rP8HDytK9JNtt2IAAAAldEVYdGRhdGU6Y3JlYXRlADIwMTgtMDUtMDlUMTE6Mzg6MjErMDI6MDCkPCDvAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDE4LTA1LTA5VDExOjM4OjIxKzAyOjAw1WGYUwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAAASUVORK5CYII="
    private var dataMemberPicture: ByteArray? = null
    lateinit var customImageString: String

    fun setImageData( result: CropImage.ActivityResult) {
        val resultUri: Uri = result.getUri()
        var imageStream: InputStream? = null
        try {
            imageStream = mContext.getContentResolver().openInputStream(resultUri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
        encodedPictureString = encodeToBase64(yourSelectedImage!!).toString()
        dataMemberPicture = Base64.decode(encodedPictureString, Base64.DEFAULT)
        var tt="pp"
        customImageString=Base64.encodeToString(dataMemberPicture, Base64.DEFAULT)


   /*     val baos = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_cus)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        encodedPictureString=imageString*/

        checkCustom("http://3.7.30.86:82/CommonFolder/AadharImage/119842021-11-22image_1637569676078.jpg","http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg")

    }
    fun encodeToBase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        val imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)
        Log.e("LOOK", imageEncoded)
        return imageEncoded
    }

//var bitPi= android.util.Base64.encodeToString("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", android.util.Base64.DEFAULT)

    fun extractAadhaarDtls(){
        try {
            val jsonObject = JSONObject()
            val notificationBody = JSONObject()
            notificationBody.put("document1", "http://3.7.30.86:82/CommonFolder/AadharImage/119842021-11-22image_1637569676078.jpg")
            notificationBody.put("consent", "yes")
            jsonObject.put("data", notificationBody)
            val jsonArray = JSONArray()
            jsonObject.put("task_id", "11986")
            jsonObject.put("group_id", "11986")


            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest("https://eve.idfy.com/v3/tasks/sync/extract/ind_aadhaar", jsonObject,
                    object : Response.Listener<JSONObject?> {
                        override fun onResponse(response: JSONObject?) {
                            var jObj:JSONObject= JSONObject()
                            jObj=response!!.getJSONObject("result")
                            var tt=jObj.getJSONObject("extraction_output")
                            var ttt=tt.getString("date_of_birth")
                            var tttt=tt.getString("name_on_card")
                            var aad_no=tt.getString("id_number")
                            var gg="asd"
                            Toaster.msgShort(mContext,"DOB "+ttt.toString()+" Name "+tttt.toString()+" aadhaar No "+aad_no.toString())
                            //checkCustom("http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg","http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg")
                        }
                    },
                    object : Response.ErrorListener {
                        override fun onErrorResponse(error: VolleyError?) {
                            var yy="wre"

                        }
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["api-key"] = "dfe0a602-7e79-4a5b-af00-509fc0e8349a"
                    params["Content-Type"] = "application/json"
                    params["account-id"] = "aaa73f1c1bdb/fa4cf738-2dda-41db-b0e5-0b406ebe6d2f"
                    return params
                }
            }
            jsonObjectRequest.setRetryPolicy(DefaultRetryPolicy(
                    120000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
            MySingleton.getInstance(mContext.applicationContext)!!.addToRequestQueue(jsonObjectRequest)



        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}