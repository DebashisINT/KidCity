package com.kcteam.faceRec;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kcteam.CustomStatic;
import com.kcteam.R;
import com.kcteam.faceRec.tflite.SimilarityClassifier;
import com.kcteam.faceRec.tflite.TFLiteObjectDetectionAPIModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class FaceStartActivity extends AppCompatActivity {
    private FloatingActionButton fabAdd;
    private Button button;
    private ImageView ivFace;
    private FaceDetector faceDetector;
    Matrix cropToFrameTransform = new Matrix();
    private Bitmap rgbFrameBitmap = null;
    private Bitmap faceBmp = null;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    private Bitmap portraitBmp = null;
    private static final int TF_OD_API_INPUT_SIZE = 112;
    public static SimilarityClassifier detector;

    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_EXTERNAL= Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String TF_OD_API_MODEL_FILE = "mobile_face_net.tflite";
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";

    private Button btn_getImg;
    Intent takePictureIntent =new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File cropFile = null;
    String fflliinnmm = "",mCurrentPhotoPath="";
    String absolPath="";

    SharedPreferences sharedPreferences_FaceImg;
    SharedPreferences.Editor editor;

    Bitmap bitmapppp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_start);



        sharedPreferences_FaceImg=getSharedPreferences("FACE_IMG", Context.MODE_PRIVATE);

        ivFace=findViewById(R.id.dlg_image);

        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            //LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(options);

        faceDetector = detector;

        //String ppath=sharedPreferences_FaceImg.getString("path","");
   /*     if(ppath.length()>0 && ppath!=null && !ppath.equals("")){
            BitmapFactory.Options optionss = new BitmapFactory.Options();
            optionss.inPreferredConfig = Bitmap.Config.ARGB_8888;


            File imgFile = new File(ppath);
            imgFile.getTotalSpace();
            String ppth =imgFile.getAbsolutePath();
            TempData.ppath=ppth;

            registerFace(BitmapFactory.decodeFile(ppth, optionss));
        }*/
/*        if(TempData.ppath!=""){
            BitmapFactory.Options optionss = new BitmapFactory.Options();
            optionss.inPreferredConfig = Bitmap.Config.ARGB_8888;
            registerFace(BitmapFactory.decodeFile(TempData.ppath, optionss));
        }*/



        btn_getImg=findViewById(R.id.btn_get_image);
        btn_getImg.setOnClickListener(v -> getImage());




        new GetImageFromUrl().execute(CustomStatic.FaceUrl);


    }


    private void getImage(){
        try{
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    if (photoFile != null) {
                        Uri photoUri=null;
                        if (Build.VERSION.SDK_INT >= 24) {
                            photoUri= FileProvider.getUriForFile(
                                    this,
                                    getPackageName() + ".provider",
                                    photoFile
                            );
                        } else
                            Uri.fromFile(photoFile);
                        cropFile = photoFile;
                        mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
                        TempData.ppath = photoFile.getAbsolutePath();
                        absolPath = photoFile.getAbsolutePath();


                        editor=sharedPreferences_FaceImg.edit();
                        editor.putString("path",absolPath);
                        editor.commit();

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, 105);
                    }
                } catch ( Exception ex) {
                    ex.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    File createImageFile() {
        // Create an image file name
        //val imageFileName = "fieldtrackingsystem" + java.util.UUID.randomUUID()
        String imageFileName = "field";
        File storageDir =new File(
                //Environment.getExternalStorageDirectory().toString()
                //27-09-2021
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                        + File.separator + "field" + File.separator
        );
        storageDir.mkdirs();
        fflliinnmm = imageFileName;
        // Save a file: path for use with ACTION_VIEW intents
        try {
            return File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105) {
            cropImage();
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();

            saveBitmapIntoSDCardImage(this,getContactBitmapFromURI(this,resultUri));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            File imgFile = new File(absolPath);
            String ppth =imgFile.getAbsolutePath();

            registerFace(BitmapFactory.decodeFile(ppth, options));

        }
        if (requestCode == 111) {
            if(resultCode == Activity.RESULT_OK){
                Boolean result=data.getBooleanExtra("valueD",false);
                //super.onBackPressed();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("value",result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }


    }

    private void cropImage() {
        Uri selectedImageUri;
        selectedImageUri = Uri.parse(mCurrentPhotoPath);
        CropImage.activity(selectedImageUri)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setMinCropWindowSize(585,585)
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(100)
                .start(this);
    }

    Bitmap getContactBitmapFromURI(Context context, Uri uri) {
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    File saveBitmapIntoSDCardImage(Context context, Bitmap finalBitmap) {
        //String root = Environment.getExternalStorageDirectory().toString();
        //27-09-2021
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File myDir = new File(mCurrentPhotoPath);
        myDir.mkdirs();
        String fname = fflliinnmm + ".jpg";
        File file =new File(myDir, fname);
        try {
            //val out = FileOutputStream(file)
            FileOutputStream out =new FileOutputStream(cropFile);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            TempData.ppath=mCurrentPhotoPath;

            //MainActivity4.pth=mCurrentPhotoPath
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    private void registerFace(Bitmap mBitmap){
        try {
            if(mBitmap==null){
                Toast.makeText(this,"No File",Toast.LENGTH_SHORT).show();
                return;
            }

            ivFace.setImageBitmap(mBitmap);

            previewWidth = mBitmap.getWidth();
            previewHeight = mBitmap.getHeight();
            rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
            portraitBmp = mBitmap;
            InputImage image = InputImage.fromBitmap(mBitmap, 0);
            faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Bitmap.Config.ARGB_8888);

            faceDetector
                    .process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {
                            if (faces.size() == 0) {
                                return;
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            //action
                                            onFacesDetected(1, faces, true);//no need to add currtime
                                        }
                                    }.start();
                                }
                            });

                        }

                    });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // Face Processing
    private Matrix createTransform(
            final int srcWidth,
            final int srcHeight,
            final int dstWidth,
            final int dstHeight,
            final int applyRotation) {

        Matrix matrix = new Matrix();
        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                // LOGGER.w("Rotation of %d % 90 != 0", applyRotation);
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);

            // Rotate around origin.
            matrix.postRotate(applyRotation);
        }

//        // Account for the already applied rotation, if any, and then determine how
//        // much scaling is needed for each axis.
//        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
//        final int inWidth = transpose ? srcHeight : srcWidth;
//        final int inHeight = transpose ? srcWidth : srcHeight;

        if (applyRotation != 0) {

            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f);
        }

        return matrix;

    }

    private void onFacesDetected(long currTimestamp, List<Face> faces, boolean add) {

        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);


        final List<SimilarityClassifier.Recognition> mappedRecognitions =
                new LinkedList<SimilarityClassifier.Recognition>();


        //final List<Classifier.Recognition> results = new ArrayList<>();

        // Note this can be done only once
        int sourceW = rgbFrameBitmap.getWidth();
        int sourceH = rgbFrameBitmap.getHeight();
        int targetW = portraitBmp.getWidth();
        int targetH = portraitBmp.getHeight();
        Matrix transform = createTransform(
                sourceW,
                sourceH,
                targetW,
                targetH,
                90);
        Bitmap mutableBitmap = portraitBmp.copy(Bitmap.Config.ARGB_8888, true);
        final Canvas cv = new Canvas(mutableBitmap);

        // draws the original image in portrait mode.
        cv.drawBitmap(rgbFrameBitmap, transform, null);

        final Canvas cvFace = new Canvas(faceBmp);

        boolean saved = false;

        for (Face face : faces) {
            //results = detector.recognizeImage(croppedBitmap);

            final RectF boundingBox = new RectF(face.getBoundingBox());

            //final boolean goodConfidence = result.getConfidence() >= minimumConfidence;
            final boolean goodConfidence = true; //face.get;
            if (boundingBox != null && goodConfidence) {

                // maps crop coordinates to original
                cropToFrameTransform.mapRect(boundingBox);

                // maps original coordinates to portrait coordinates
                RectF faceBB = new RectF(boundingBox);
                transform.mapRect(faceBB);

                // translates portrait to origin and scales to fit input inference size
                //cv.drawRect(faceBB, paint);
                float sx = ((float) TF_OD_API_INPUT_SIZE) / faceBB.width();
                float sy = ((float) TF_OD_API_INPUT_SIZE) / faceBB.height();
                Matrix matrix = new Matrix();
                matrix.postTranslate(-faceBB.left, -faceBB.top);
                matrix.postScale(sx, sy);

                cvFace.drawBitmap(portraitBmp, matrix, null);

                //canvas.drawRect(faceBB, paint);

                String label = "";
                float confidence = -1f;
                Integer color = Color.BLUE;
                Object extra = null;
                Bitmap crop = null;

                if (add) {
                    try {
                        crop = Bitmap.createBitmap(portraitBmp,
                                (int) faceBB.left,
                                (int) faceBB.top,
                                (int) faceBB.width(),
                                (int) faceBB.height());
                    }catch (Exception eon){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FaceStartActivity.this,"Failed to detect",Toast.LENGTH_LONG);

                            }
                        });
                    }
                }

                final long startTime = SystemClock.uptimeMillis();
                final List<SimilarityClassifier.Recognition> resultsAux = detector.recognizeImage(faceBmp, add);
                Long lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                if (resultsAux.size() > 0) {

                    SimilarityClassifier.Recognition result = resultsAux.get(0);

                    extra = result.getExtra();
//          Object extra = result.getExtra();
//          if (extra != null) {
//            LOGGER.i("embeeding retrieved " + extra.toString());
//          }

                    float conf = result.getDistance();
                    if (conf < 1.0f) {

                        confidence = conf;
                        label = result.getTitle();
                        if (result.getId().equals("0")) {
                            color = Color.GREEN;
                        }
                        else {
                            color = Color.RED;
                        }
                    }

                }
                Matrix flip = new Matrix();


                flip.postScale(1, -1, previewWidth / 2.0f, previewHeight / 2.0f);

                //flip.postScale(1, -1, targetW / 2.0f, targetH / 2.0f);
                flip.mapRect(boundingBox);



                final SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                        "0", label, confidence, boundingBox);

                result.setColor(color);
                result.setLocation(boundingBox);
                result.setExtra(extra);
                result.setCrop(crop);
                mappedRecognitions.add(result);

            }


        }

        //    if (saved) {
//      lastSaved = System.currentTimeMillis();
//    }

        SimilarityClassifier.Recognition rec = mappedRecognitions.get(0);
        detector.register("", rec);

        Intent intent = new Intent(this, DetectorActivity.class);
        startActivityForResult(intent, 111);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }





    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap>{
        //ImageView imageView;
        public GetImageFromUrl(){
            //this.imageView = img;
        }
        @Override
        protected Bitmap doInBackground(String... url) {
            String stringUrl = url[0];
            bitmapppp = null;
            InputStream inputStream;
            try {
                inputStream = new java.net.URL(stringUrl).openStream();
                bitmapppp = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmapppp;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            registerFace(bitmap);
            //imageView.setImageBitmap(bitmap);
        }
    }













}