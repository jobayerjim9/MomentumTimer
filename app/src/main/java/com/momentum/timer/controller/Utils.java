package com.momentum.timer.controller;

import android.graphics.Color;
import android.util.Log;

import com.momentum.timer.models.Constants;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static ArrayList<Integer> getColors(int size) {
        ArrayList<Integer> colors=new ArrayList<>();
        ArrayList<String> cardColor= Constants.cardColors;

        for (int i=0;i<size;i++) {
            int random=ThreadLocalRandom.current().nextInt(0,cardColor.size());
            Log.d("randomIndex",random+" "+cardColor.size());
            colors.add(Color.parseColor(cardColor.get(random)));
        }
        return colors;

    }
}
