package library;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.QRCode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jeff on 1-10-2014.
 */
public class Qrmaker {

    public Bitmap encode(String input) throws WriterException {

        File myFile = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.example.jeff.tester2");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        String finaldata = Uri.encode(input, "ISO-8859-1");
        Bitmap mBitmap = null;


        try {

            BitMatrix bm = qrCodeWriter.encode(finaldata, BarcodeFormat.QR_CODE, 300, 300);
            mBitmap = toBitmap(bm);
        }
        catch (Exception e) {

        }

        //storeImage(mBitmap);
        return mBitmap;
    }

    public static Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.parseColor("#FF96AAC3"));
            }
        }
        int trim =45;
         bmp = Bitmap.createBitmap(bmp,trim,trim,(bmp.getWidth()- (trim *2)),(bmp.getHeight()-(trim*2)));
        //bmp = Bitmap.createBitmap();
        return bmp;
    }


    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
           // Log.d(TAG,
            //        "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
           // Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {

        }
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
