package com.kcteam.app.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
//import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TessOCR {

    private Context context;
    //private TessBaseAPI mTess;
    private String datapath = "";

    public TessOCR(Context context) {
        this.context = context;

        /*mTess = new TessBaseAPI();
        datapath = context.getFilesDir() + "/tesseract/";
        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, "eng");*/
    }

    private void checkFile(File dir) {
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

    private void copyFiles(Context context, String datapath) {
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

    /*public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }*/

    public String extractName(String str){
        System.out.println("Getting the Name");
        final String NAME_REGEX = "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";
        Pattern p = Pattern.compile(NAME_REGEX, Pattern.MULTILINE);
        Matcher m =  p.matcher(str);
        if(m.find()){
            System.out.println(m.group());
            return m.group();
        }
        return "";
    }

    public String extractEmail(String str) {
        System.out.println("Getting the email");
        final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            return m.group();
        }
        return "";
    }

    public String extractPhone(String str){
        System.out.println("Getting Phone Number");
        final String PHONE_REGEX="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
        Pattern p = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            return m.group();
        }
        return "";
    }

    public synchronized boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }

        /*if (strNum.matches("[0-9]+")) {
            Log.e(TAG, "Under Only Numeric: "+strNum+ " length "+strNum.length());
        }*/

        //{16,26}
        return (strNum.matches("[0-9]+"));
        //pattern.matcher(strNum).matches();
    }

    public ArrayList<String> parseResults(ArrayList<String> bCardText) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> numberMatches = null;
        ArrayList<String> data = new ArrayList<>();

        for (int i = 0; i < bCardText.size(); i++) {
            //numberMatches = phoneNumberUtil.findNumbers(bCardText.get(i), Locale.getDefault().getCountry());

            Phonenumber.PhoneNumber numberProto = null;

            try {
                numberProto = phoneNumberUtil.parse(bCardText.get(i), "91");
                if (numberProto != null && phoneNumberUtil.isValidNumber(numberProto)) {
                    data.add(String.valueOf(numberProto.getNationalNumber()));
                    return data;
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }

        /*if (numberMatches != null) {
            for (PhoneNumberMatch number : numberMatches) {
                String s = number.rawString();
                data.add(s);
            }
        }*/


        return data;
    }

    public void onDestroy() {
        /*if (mTess != null)
            mTess.end();*/
    }
}
