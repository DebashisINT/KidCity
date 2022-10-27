package com.kcteam.app.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.kcteam.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * Created by amit on 18/5/16.
 */
public class ProcessImageUtils_v1 {
    private static final String TAG = "ProcessImageUtils_v1: ";
    Context context;
    File sourceFile;
    File destFile;

    String sourceFileExt = "";
    private int fileSize;
    private boolean isDocument = false;

    // File mediaStorageDir;
    public ProcessImageUtils_v1(Context context, File SourceFile, int fileSize) {
        this.context = context;
        this.sourceFile = SourceFile;
        this.fileSize = fileSize;

        String sourceFilepath = SourceFile.getAbsolutePath();
        this.sourceFileExt = AppUtils.Companion.getFileExt(sourceFilepath.substring(sourceFilepath.lastIndexOf("/") + 1));

        // this.mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.cameraFolderName));
    }

    // File mediaStorageDir;
    public ProcessImageUtils_v1(Context context, File SourceFile, int fileSize, boolean isDocument) {
        this.context = context;
        this.sourceFile = SourceFile;
        this.fileSize = fileSize;
        this.isDocument = isDocument;

        String sourceFilepath = SourceFile.getAbsolutePath();
        this.sourceFileExt = AppUtils.Companion.getFileExt(sourceFilepath.substring(sourceFilepath.lastIndexOf("/") + 1));

        // this.mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.cameraFolderName));
    }

    public File ProcessImage() throws IOException {


        destFile = createImageFile();
        Log.e(TAG, "source Img:" + sourceFile);
        Log.e(TAG, "destination Img:" + destFile);
        if (destFile != null) {

            copyFile(sourceFile, destFile);
            //copyExif(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            resizeImage(destFile.getAbsolutePath(), 1024 * fileSize);
            copyExif(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            RotateImage.ReRotateImage(destFile.getAbsolutePath());

            if (sourceFile.exists()) {
                // sourceFile.delete();
                // deleteFileFromMediaStore(context.getContentResolver(),sourceFile);
            }

            // notifyMediaStoreScanner(destFile);
            // DeleteRecursive(mediaStorageDir);


        } else {
            return null;
        }

        return destFile;

    }

    //30-08-21

    public File ProcessImageSelfie() throws IOException {


        destFile = createImageFile();
        Log.e(TAG, "source Img:" + sourceFile);
        Log.e(TAG, "destination Img:" + destFile);
        if (destFile != null) {

            copyFile(sourceFile, destFile);
            //copyExif(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            resizeImage(destFile.getAbsolutePath(), 1024 * fileSize);
            copyExif(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            //RotateImage.ReRotateImage(destFile.getAbsolutePath());

            if (sourceFile.exists()) {
                // sourceFile.delete();
                // deleteFileFromMediaStore(context.getContentResolver(),sourceFile);
            }

            // notifyMediaStoreScanner(destFile);
            // DeleteRecursive(mediaStorageDir);


        } else {
            return null;
        }

        return destFile;

    }



    private File createImageFile() {
        String imageFileName = "IMG_" + System.currentTimeMillis();
        //  File dir = new File(Environment.getExternalStorageDirectory() + File.separator + ""+context.getResources().getString(R.string.app_name));
        File dir /*= new File(context.getFilesDir() + File.separator + "" + context.getResources().getString(R.string.app_name))*/;

        if (isDocument)
            //dir = new File(Environment.getExternalStorageDirectory() + File.separator);
            //27-09-2021
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator);
        else {
            dir = new File(context.getFilesDir() + File.separator + "" + context.getResources().getString(R.string.app_name));

            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        File image = null;
        try {
            //image = File.createTempFile(imageFileName, ".jpg", dir);

            image = new File(dir, imageFileName + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }

    private static void copyExif(String oldPath, String newPath) throws IOException {
        ExifInterface oldExif = new ExifInterface(oldPath);
        String orientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
        Log.e(TAG, "ORIENTATION:== " + orientation);


        ExifInterface newExif = new ExifInterface(newPath);
        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
        newExif.saveAttributes();

        Log.e(TAG, "NEW_ORIENTATION==> " + newExif.getAttribute(ExifInterface.TAG_ORIENTATION));

  /*      String[] attributes = new String[]
                {
                        ExifInterface.TAG_APERTURE,
                        ExifInterface.TAG_DATETIME,
                        ExifInterface.TAG_DATETIME_DIGITIZED,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_FLASH,
                        ExifInterface.TAG_FOCAL_LENGTH,
                        ExifInterface.TAG_GPS_ALTITUDE,
                        ExifInterface.TAG_GPS_ALTITUDE_REF,
                        ExifInterface.TAG_GPS_DATESTAMP,
                        ExifInterface.TAG_GPS_LATITUDE,
                        ExifInterface.TAG_GPS_LATITUDE_REF,
                        ExifInterface.TAG_GPS_LONGITUDE,
                        ExifInterface.TAG_GPS_LONGITUDE_REF,
                        ExifInterface.TAG_GPS_PROCESSING_METHOD,
                        ExifInterface.TAG_GPS_TIMESTAMP,
                        ExifInterface.TAG_IMAGE_LENGTH,
                        ExifInterface.TAG_IMAGE_WIDTH,
                        ExifInterface.TAG_ISO,
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.TAG_SUBSEC_TIME,
                        ExifInterface.TAG_SUBSEC_TIME_DIG,
                        ExifInterface.TAG_SUBSEC_TIME_ORIG,
                        ExifInterface.TAG_WHITE_BALANCE
                };

        ExifInterface newExif = new ExifInterface(newPath);
        for (int i = 0; i < attributes.length; i++)
        {
            String value = oldExif.getAttribute(attributes[i]);
            if (value != null)
                newExif.setAttribute(attributes[i], value);
        }
        newExif.saveAttributes();*/
    }


    private static Bitmap resizeImage(String strImagePath, int fileSize) {

        int MAX_IMAGE_SIZE = fileSize; // max final file size
        try {
            Bitmap bmpPic = BitmapFactory.decodeFile(strImagePath);
            if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
                bmpOptions.inSampleSize = 1;
                while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                    bmpOptions.inSampleSize++;
                    bmpPic = BitmapFactory.decodeFile(strImagePath, bmpOptions);
                }
                Log.e(TAG, "camera Resize: " + bmpOptions.inSampleSize);
            }

            int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
            int streamLength = MAX_IMAGE_SIZE;
            while (streamLength >= MAX_IMAGE_SIZE) {
                ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
                compressQuality -= 5;
                Log.e(TAG, "camera Quality: " + compressQuality);
                bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
                byte[] bmpPicByteArray = bmpStream.toByteArray();
                streamLength = bmpPicByteArray.length;
                Log.e(TAG, "camera Size: " + streamLength);
            }
        /*try {*/
            FileOutputStream bmpFile = new FileOutputStream(strImagePath);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
            return bmpPic;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "camera Error on saving file");
            return null;
        }
    }


    private final void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }




    /*public  void deleteFileFromMediaStore(ContentResolver contentResolver, File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }


     *//*  contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ file.getAbsolutePath() });*//*
    }


    public void DeleteRecursive(File mediaStorageDir1)
    {

        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.cameraFolderName));
        if(mediaStorageDir.exists()) {
            Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + mediaStorageDir.getPath());
            if (mediaStorageDir.isDirectory()) {
                String[] children = mediaStorageDir.list();
                for (int i = 0; i < children.length; i++) {
                    File temp = new File(mediaStorageDir, children[i]);
                    if (temp.isDirectory()) {
                        Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                        DeleteRecursive(temp);
                    } else {
                        Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                        boolean b = temp.delete();
                        deleteFileFromMediaStore(context.getContentResolver(),temp);
                        if (b == false) {
                            Log.d("DeleteRecursive", "DELETE FAIL");
                        }
                    }
                }

            }
            mediaStorageDir.delete();
        }
    }*/
}



