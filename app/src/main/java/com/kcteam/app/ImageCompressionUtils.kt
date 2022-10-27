package com.kcteam.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Pratishruti on 07-11-2017.
 */
class ImageCompressionUtils {
    companion object {
        @Throws(IOException::class)
        fun compressImage(imageFile: File, reqWidth: Int, reqHeight: Int, compressFormat: Bitmap.CompressFormat, quality: Int, destinationPath: String): File {
            var fileOutputStream: FileOutputStream? = null
            val file = File(destinationPath).parentFile
            if (!file.exists()) {
                file.mkdirs()
            }
            try {
                fileOutputStream = FileOutputStream(destinationPath)
                // write the compressed bitmap at the destination specified by destinationPath.
                getRightAngleImage(imageFile, reqWidth, reqHeight).compress(compressFormat, quality, fileOutputStream);
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.flush()
                    fileOutputStream.close()
                }
            }

            return File(destinationPath)
        }


        fun getRightAngleImage(imageFile: File, reqWidth: Int, reqHeight: Int): Bitmap {

//            try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false

            var scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options)

            //check the rotation of the image and display it properly
            val exif: ExifInterface
            exif = ExifInterface(imageFile.getAbsolutePath())
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
            } else if (orientation == 3) {
                matrix.postRotate(180f)
            } else if (orientation == 8) {
                matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
            return scaledBitmap
        }

        fun rotateImage(degree: Int, imagePath: String): String {

            if (degree <= 0) {
                return imagePath;
            }
            try {
                var b = BitmapFactory.decodeFile(imagePath);

                var matrix = Matrix()
                if (b.getWidth() > b.getHeight()) {
                    matrix.setRotate(degree.toFloat());
                    b = Bitmap.createScaledBitmap(b, b.getWidth(), b.getHeight(), false);
                }

                var fOut = FileOutputStream(imagePath);
                var imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                var imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

                var out = FileOutputStream(imagePath);
                if (imageType.equals("png")) {
                    b.compress(Bitmap.CompressFormat.PNG, 70, out);
                } else if (imageType.equals("jpeg") || imageType.equals("jpg")) {
                    b.compress(Bitmap.CompressFormat.JPEG, 70, out);
                }
                fOut.flush();
                fOut.close();
                b.recycle();
            } catch (e: Exception) {
                e.printStackTrace();
            }

            return imagePath;
        }

        fun getRealPathFromURI(context: Context, contentURI: Uri): String {
            var result: String
            var cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath().toString();
            } else {
                cursor.moveToFirst();
                var idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }

        fun performCrop(activity: Activity, picUri: Uri, crop_request_code: Int) {

            val cropIntent = Intent("com." +
                    "android.camera.action.CROP")
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*")
            //set crop properties
            cropIntent.putExtra("crop", "true")
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2)
            cropIntent.putExtra("aspectY", 1)
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256)
            cropIntent.putExtra("outputY", 256)
            //retrieve data on return
            cropIntent.putExtra("return-data", true)
            //start the activity - we handle returning in onActivityResult
            activity.startActivityForResult(cropIntent, crop_request_code)
        }

        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }
    }

}