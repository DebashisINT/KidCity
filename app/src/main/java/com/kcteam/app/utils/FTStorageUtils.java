package com.kcteam.app.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.kcteam.app.Pref;
import com.kcteam.features.location.model.ShopDurationRequestData;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * DStorageUtils
 */

public class FTStorageUtils {

    private static String APP_FOLDERNAME = "kcteamApp/FTS";
    private static String folderPath;
    public static Uri IMG_URI = null;
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;


    private FTStorageUtils() {
        throw new UnsupportedOperationException(
                "Should not create instance of Util class. Please use as static..");
    }

    /**
     * Check the SD card
     *
     * @return true if SD card is present
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * Get App Folder Path
     *
     * @param context
     * @return
     */
    public static String getFolderPath(Context context) {
        if (checkSDCardAvailable()) {
            try {
                //File sdPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_FOLDERNAME);
                //27-09-2021
                File sdPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + APP_FOLDERNAME);
                if (!sdPath.exists()) {
                    sdPath.mkdirs();
                    folderPath = sdPath.getAbsolutePath();
                } else if (sdPath.exists()) {
                    folderPath = sdPath.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //folderPath = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_FOLDERNAME;
            //27-09-2021
            folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + APP_FOLDERNAME;
        } else {
            try {
                File internalDir = new File(context.getFilesDir(), APP_FOLDERNAME);
                if (!internalDir.exists()) {
                    internalDir.mkdirs();
                    folderPath = internalDir.getAbsolutePath();
                } else if (internalDir.exists()) {
                    folderPath = internalDir.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return folderPath;
    }


    /**
     * Override a existing file
     *
     * @param _folderPath
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File overWriteFile(String _folderPath, String fileName) throws IOException {

        if (TextUtils.isEmpty(_folderPath))
            throw new Error("Folder Path is empty");

        if (TextUtils.isEmpty(fileName))
            throw new Error("File Name is empty");

        File logFile = new File(_folderPath, fileName);
        logFile.createNewFile();

        return logFile;
    }


    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }


    public static boolean checkShopPositionWithinRadious(Location currentLocation, Location shopLocation, int radius) {
        return ((currentLocation.distanceTo(shopLocation)) <= radius);
    }


    public static boolean isAppInForeground(Context context) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

            return foregroundTaskPackageName.toLowerCase().equals(context.getPackageName().toLowerCase());
        } else {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
                return true;
            }

            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            // App is foreground, but screen is locked, so show notification
            return km.inKeyguardRestrictedInputMode();
        }
    }


    public static String formatMonth(String month) {

        try {
            SimpleDateFormat monthParse = new SimpleDateFormat("MM");
            SimpleDateFormat monthDisplay = new SimpleDateFormat("MMM");
            return monthDisplay.format(monthParse.parse(month));
        } catch (ParseException e) {
            e.printStackTrace();
            return " ";
        }
    }
    public static String formatMm(String month) {

        try {
            SimpleDateFormat monthParse = new SimpleDateFormat("MM");
            SimpleDateFormat monthDisplay = new SimpleDateFormat("MM");
            return monthDisplay.format(monthParse.parse(month));
        } catch (ParseException e) {
            e.printStackTrace();
            return " ";
        }
    }

    // Only for yyyy-MM-ddTHH:mm:ss format input
    public static Date getStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString); //"20130526160000"
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static Date getStringTimeToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString); //"20130526160000"
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }


    public static Date getStrinTODateType2(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }


    public static boolean isMyServiceRunning(Class<?> serviceClass, Context mContext) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMyActivityRunning(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> alltasks = am.getRunningTasks(1);

        for (ActivityManager.RunningTaskInfo aTask : alltasks) {
            if (aTask.topActivity.getClassName().equals("com.kcteam.features.dashboard.presentation.DashboardActivity")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppInBackground(Context context) {
        try {
            boolean isInBackground = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }

            return isInBackground;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static List<ShopDurationRequestData> removeDuplicateData(List<ShopDurationRequestData> shopDataList) {
        List<ShopDurationRequestData> newShopList = null;
        try {
            Set set = new TreeSet(new Comparator<ShopDurationRequestData>() {

                @Override
                public int compare(ShopDurationRequestData shopDurationRequestData, ShopDurationRequestData t1) {

                    long firstDateNumber = AppUtils.Companion.getTimeStampFromDateOnly(AppUtils.Companion.changeAttendanceDateFormatToCurrent(shopDurationRequestData.getVisited_date()));
                    long secondDateNumber = AppUtils.Companion.getTimeStampFromDateOnly(AppUtils.Companion.changeAttendanceDateFormatToCurrent(t1.getVisited_date()));

                    if (shopDurationRequestData.getShop_id().equalsIgnoreCase(t1.getShop_id()) && firstDateNumber == secondDateNumber)
                        return 0;

                    return 1;
                }
            });

            set.addAll(shopDataList);

            newShopList = new ArrayList<>(set);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newShopList;
    }

    /**
     * convert path url to file url
     *
     * @param context activity context
     * @param absPath path url
     * @return file url
     */
    public static Uri getImageContentUri(Context context, String absPath) {
        Log.e("FTStorageUtils","getImageContentUri: " + absPath);

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }

    public static String stringToPdf(String data, Context context, String fileName, Bitmap image, String header, float widthMeasurement) {
        String extstoragedir = getFolderPath(context);
        File folder = new File(extstoragedir, "pdf");
        //File folder = new File(fol, "pdf");
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            final File file = new File(folder, fileName);
            if (file.exists()) {
                file.delete();
                if(file.exists()) {
                    file.getCanonicalFile().delete();
                    if(file.exists()) {
                        context.deleteFile(file.getName());
                    }
                }
            }
            file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file);


            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(600, 8000, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            if (image != null) {
                Bitmap imageBitmap = Bitmap.createScaledBitmap(image, 100, 100, true);
                canvas.drawBitmap(imageBitmap, 10, 10, null);
            }

            paint.setUnderlineText(true);
            paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
            paint.setTextSize(20);
            canvas.drawText(header, canvas.getWidth() / widthMeasurement, 135, paint);

            int x = 10, y = 136;
            paint.setUnderlineText(false);
            paint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));
            paint.setTextSize(12);
            for (String line:data.split("\n")) {
                if (line.startsWith("Total Amount:")) {
                    paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
                    paint.setUnderlineText(false);
                    paint.setTextSize(15);
                }
                /*else if (line.startsWith("Sales Person:")) {
                    paint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));
                    paint.setUnderlineText(false);
                }*/

                canvas.drawText(line, x, y, paint);
                y += paint.descent() - paint.ascent();
            }

            //canvas.drawText(data, 10, 10, paint);


            document.finishPage(page);
            document.writeTo(fOut);
            document.close();


            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String bitmapToPdf(Context context, String fileName, Bitmap image) {
        String extstoragedir = getFolderPath(context);
        File folder = new File(extstoragedir, "pdf");
        //File folder = new File(fol, "pdf");
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            final File file = new File(folder, fileName);
            if (file.exists()) {
                file.delete();
                if(file.exists()) {
                    file.getCanonicalFile().delete();
                    if(file.exists()) {
                        context.deleteFile(file.getName());
                    }
                }
            }
            file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file);


            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(600, 1000, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            if (image != null) {
                Bitmap imageBitmap = Bitmap.createScaledBitmap(image, 500, 900, true);
                canvas.drawBitmap(imageBitmap, 10, 10, null);
            }




            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Bitmap screenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache()/*view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888*/);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public static void checkFile(File dir, String datapath, Context context) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles(context, datapath);
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(context, datapath);
            }
        }
    }

    private static void copyFiles(Context context, String datapath) {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = context.getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri saveImageExternal(Bitmap image, Context context, String imageName) {
        //TODO - Should be processed in another thread
        Uri uri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageName);

            if (file.exists()) {
                file.delete();
                if(file.exists()) {
                    file.getCanonicalFile().delete();
                    if(file.exists()) {
                        context.deleteFile(file.getName());
                    }
                }
            }

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }
}
