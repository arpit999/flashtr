package com.flashtr.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by hirenkanani on 04/12/15.
 */
public class Helper {

    public static String getTempFile() {
        return Environment.getExternalStorageDirectory().getPath()    + "/Androidhub4you/";

    }

    public static File createFileInSDCard(String path,String fileName){
        File dir = new File(path);
        try{
            if(!dir.exists() && dir.mkdirs()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        File file = null;
        try {
            if(dir.exists()){
                file=new File(dir, fileName);
                file.createNewFile();
            }else{

            }
        }catch (Exception e1) {
            e1.printStackTrace();
        }
        return file;
    }

    public static Bitmap decodeFile(File f) {
        Bitmap b = null;
        final int IMAGE_MAX_SIZE = 400;
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2.0, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (Exception e) {
        }
        return b;
    }
}
