package com.kcteam.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RotateImage {
    static ExifInterface ei;
    private static String TAG = RotateImage.class.getSimpleName();

    public static void ReRotateImage(String imgfilepath) {
        try {
            ei = new ExifInterface(imgfilepath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e(TAG, "orientation: " + orientation);
            // Crashlytics.setInt("PICTURE ORIENTAION",orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    //rotateImage(bitmap, 90);
                    Log.e(TAG, "ORIENTATION_ROTATE_90");
                    rotateImage(imgfilepath, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    // rotateImage(bitmap, 180);
                    Log.e(TAG, "ORIENTATION_ROTATE_180");
                    rotateImage(imgfilepath, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    // rotateImage(bitmap, 180);
                    Log.e(TAG, "ORIENTATION_ROTATE_270");
                    rotateImage(imgfilepath, 270);
                    break;
                // etc.
                default:
                    Log.e(TAG, "ORIENTATION_ROTATE_0");
                    // rotateImage1(imgfilepath, 0);
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static Bitmap rotateImage(String strImagePath, int degree) {

        // Bitmap img = BitmapFactory.decodeFile(strImagePath);
        // Bitmap img=convertBitmapNew(strImagePath);//convertBitmap(strImagePath);

        Bitmap img = BitmapHelper.decodeFile(new File(strImagePath), 1024, 1024, true);

        /*NEW CODE*/
        if ((img.getWidth() >= 1024) && (img.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((img.getWidth() >= 1024) && (img.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                img = BitmapFactory.decodeFile(strImagePath, bmpOptions);
            }
            Log.e(TAG,"camera Resize1: " + bmpOptions.inSampleSize);
        }
        /*--------*/

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);

        try {
            FileOutputStream bmpFile = new FileOutputStream(strImagePath);
            rotatedImg.compress(Bitmap.CompressFormat.JPEG, 100, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e(TAG,"camera Error on saving file");
        }
        img.recycle();
        return rotatedImg;
    }


/*SAMPLE*/
   /* private static Bitmap rotateImage1(String strImagePath, int degree) {

       // Bitmap img = BitmapFactory.decodeFile(strImagePath);
        Bitmap img=convertBitmap(strImagePath);
        Log.d("camera", "Entry Rotate: ");
        *//*NEW CODE*//*
        if ((img.getWidth() >= 1024) && (img.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((img.getWidth() >= 1024) && (img.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
               // img = BitmapFactory.decodeFile(strImagePath, bmpOptions);
                img = BitmapFactory.decodeFile(strImagePath, bmpOptions);
            }
            Log.d("camera", "Resize: " + bmpOptions.inSampleSize);
        }
        *//*--------*//*

        Matrix matrix = new Matrix();
       // matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);

        try {
            FileOutputStream bmpFile = new FileOutputStream(strImagePath);
            rotatedImg.compress(Bitmap.CompressFormat.JPEG, 100, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e("camera", "Error on saving file");
        }
        img.recycle();
        return rotatedImg;
    }
*/

    public static Bitmap convertBitmap(String path) {
        Log.e(TAG, "camera Entry Rotate: ");
        Bitmap bitmap = null;

        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        // bfOptions.inDither=false;                     //Disable Dithering mode
        bfOptions.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        bfOptions.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        bfOptions.inTempStorage = new byte[32 * 1024];

        /*New code 20/04/16*/
        bfOptions.inJustDecodeBounds = false;
        bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bfOptions.inDither = true;
        /******************/


        File file = new File(path);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }


    public static Bitmap convertBitmapNew(String path) {
        Bitmap bitmap = null;
        /******************************/
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, 1024, 1024);
        options.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(path, options);
        /******************/
        return bitmap;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.e(TAG, "" + inSampleSize);

        return inSampleSize;
    }
}
