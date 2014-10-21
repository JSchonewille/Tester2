package library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Sander on 19-9-2014.
 */
public class Smoothener {
    HashMap<String, LinkedList<Integer>> map = new HashMap<String, LinkedList<Integer>>();
    private int queuesize;

    public Smoothener(int size) {
        queuesize = size;
    }


    public int smoothen(int Irssi, int major, int minor) {
        String key = Integer.toString(major);
        int output = Math.abs(Irssi);
        key += Integer.toString(minor);

        if (map.get(key) != null) {
            // gets the list from the map
            LinkedList<Integer> existingList = map.get(key);
            if (existingList.size() < queuesize) {
                if (existingList.add(output)) {
                }

            } else {
                existingList.removeFirst();
                existingList.add(output);

            }
            map.put(key, existingList);
        } else {
            LinkedList<Integer> firstlist = new LinkedList<Integer>();
            firstlist.add(output);
            map.put(key, firstlist);
        }


        int avg = 0;

        LinkedList<Integer> avgqueue = map.get(key);

        for (int item : avgqueue) {
            avg += item;
        }
        avg = avg / avgqueue.size();
        avg *= -1;
        return avg;
    }

    public void clearqueue() {
        map.clear();
    }
}
