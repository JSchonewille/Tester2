package com.example.jeff.tester2;
import android.os.Bundle;
import android.renderscript.Int4;
import android.text.Editable;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Sander on 19-9-2014.
 */
public class Smoothener {
    private int queuesize ;
    Queue<Integer> avgqueue = new LinkedList<Integer>();

    public Smoothener() {

    }


    public int smoothen (int Irssi)
    {
        int output = Math.abs(Irssi);
        int avg = 0;
        if (avgqueue.size()<30) {
            if (avgqueue.add((output))) {

            }
        }
        else {avgqueue.remove();
            avgqueue.add(output);
        }

        for (int item : avgqueue) {
        avg += item;
        }
        avg = avg/avgqueue.size();
        avg *=-1;
        return avg;
    }

}
