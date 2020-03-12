package com.bc.c3slot5.sandbank;

import java.util.Arrays;

public class ArrayNaNisator {


    public void preparedArrayNaNisator(double[] sourceData,
                                       int sourceWidth,
                                       int sourceHeight,
                                       int[] flagArray) {


        double[] preparedData = new double[sourceData.length];
        Arrays.fill(preparedData, Double.NaN);


        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                //System.out.printf("%d  %d  %d\n", i, j, flagArray[(j) * (sourceWidth) + (i)] );
                int k = j * (sourceWidth) + i;
                if (flagArray[k] == PreparingOfSourceBand.OCEAN_FLAG) {
                    preparedData[k] = sourceData[k];
                    //System.out.printf("%d  %d %f\n", i, j, preparedData[(j) * (sourceWidth) + (i)] );
                }


            }
        }


        System.arraycopy(preparedData, 0, sourceData, 0, sourceData.length);


    }
}



