package com.example.jeff.tester2;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Jeff on 24-9-2014.
 */
public class DistanceCalc {
    private int queuesize ;
    Queue<Double> avgqueue = new LinkedList<Double>();

    public DistanceCalc(int size)
    {
        queuesize = size;
    }


    public int distance(int avgrssi, int tx)
    {
        int inputRssi = Math.abs(avgrssi);
        int inputTx = Math.abs(tx);

        double Queinput = (inputRssi - (inputTx * 0.93));
        Queinput = Queinput * 0.4646721;

        double avg = 0.0;
        if (avgqueue.size()<queuesize) {
            if (avgqueue.add((Queinput))) {

            }
        }
        else {avgqueue.remove();
            avgqueue.add(Queinput);
        }

        for (double item : avgqueue) {
            avg += item;
        }

        avg = avg/avgqueue.size();
        int output = (int) Math.ceil(avg);
        if (output < 0) { output = 0;};
        return  output;
    }

    public void emptyQueue () { avgqueue.clear();}
}
