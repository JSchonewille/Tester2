package library;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.WriterException;

/**
 * Created by Jeff on 22-10-2014.
 */
public class ibeacon {
    private int major;
    private int minor;
    private int kleur = Color.WHITE;
    private int counter = 0;
    private int seconds = 0;
    private boolean shown = false;
    Qrmaker qmaker;
    private Bitmap Qr;

    public ibeacon(int majorid, int minorid) {
        major = majorid;
        minor = minorid;
        qmaker = new Qrmaker();
        try
        {
            Qr = qmaker.encode(major + " - " + minor );
        } catch (WriterException e) {
            e.printStackTrace();
        }


    }


    public boolean isBeacon(int majorvalue, int minorvalue) {
        if (majorvalue == major && minorvalue == minor) {
            return true;
        } else {
            return false;
        }
    }

    public void setKleur(int kleur) {
        this.kleur = kleur;
    }

    public int getKleur() {
        return kleur;
    }

    public void Counterup() {
        counter++;
        if (counter % 10 == 0) {
            seconds++;
        }

    }

    public int getSeconds() {
        return seconds;
    }

    public Bitmap getQr()
    {
        return Qr;
    }

    public void setShown()
    {
        shown = true;
    }

    public boolean getShown()
    {
        return shown;
    }


}
