package com.bc.c3slot5.sandbankridge;

import org.esa.snap.core.gpf.Tile;

import java.awt.*;
import java.sql.Array;
import java.util.Arrays;


public class PreparingOfSourceBand {

    static final int OUTOFPRODUCT_FLAG = 5000;
    static final int UNVALID_FLAG = 1000;
    static final int LAND_FLAG = 100;
    static final int CLOUD_FLAG = 10;
    static final int CLOUDSHADOW_FLAG = 5;
    static final int OCEAN_FLAG = 1;

    private static final int FILL_NEIGHBOUR_VALUE = 4;


    public void preparedOfSourceBand(double[] sourceData,
                                     int sourceWidth,
                                     int sourceHeight,
                                     int[] flagArray) {


        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                    if (flagArray[j * (sourceWidth) + i] == LAND_FLAG) {  //  land!!!
                        sourceData[j * (sourceWidth) + i] = Double.NaN;
                    }
                }
            }
    }
}


