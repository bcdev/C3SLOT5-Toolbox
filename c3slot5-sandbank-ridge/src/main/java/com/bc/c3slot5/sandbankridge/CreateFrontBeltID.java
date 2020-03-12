package com.bc.c3slot5.sandbankridge;

import java.awt.*;

public class CreateFrontBeltID {


    public int[] compute(double[][] frontBelts, int[] frontIDArray, Rectangle targetRectangle) {

        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;
        int targetLength = targetHeight * targetWidth;
        int[] frontBeltIDArray = new int[targetLength];
        int counter = 0;
        int temp = 0;
        int gg = 0;

        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                frontBeltIDArray[k] = frontIDArray[k];
            }
        }

        while (true) {
            gg++;
            //for (int gg = 0; gg < 3; gg++) {
            //System.out.printf("FrontRepeat %d \n", ID_counter++);
            for (int j = 0; j < targetHeight; j++) {
                for (int i = 0; i < targetWidth; i++) {
                    int k = j * (targetWidth) + i;
                    //System.out.printf("frontBeltIDArray %d %d %f %d \n", i, j, frontBelts[0][k], frontBeltIDArray[k]);
                    if (!Double.isNaN(frontBelts[0][k]) && frontBeltIDArray[k] == 0) {
                        //System.out.printf("FrontID %d \n",  ID_counter);
                        temp = findClosestFrontID(i, j, targetWidth, targetHeight, /*frontIDArray,*/ frontBeltIDArray, frontBelts);
                        //System.out.printf("temp %d %d \n", temp, frontBeltIDArray[k]);
                        if (temp > 0) counter++;
                    }
                }
            }

            //System.out.printf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>temporary counter %d %d\n", gg, counter);
            if (counter == 0) break;
            else counter = 0;

        }
        return frontBeltIDArray;
    }

    private int findClosestFrontID(int i,
                                   int j,
                                   int targetWidth,
                                   int targetHeight,
                                   //int[] frontIDArray,
                                   int[] frontBeltIDArray,
                                   double[][] frontBelts) {
        double mx = -Double.MAX_VALUE;
        int id = 0;
        for (int jk = -1; jk <= 1; jk++) {
            for (int ik = -1; ik <= 1; ik++) {
                int kk = (j + jk) * (targetWidth) + (i + ik);
                if ((i + ik >= 0) && (i + ik <= targetWidth - 1) && (j + jk >= 0) && (j + jk <= targetHeight - 1)) {
                    //if (i > 0 && i < targetWidth-1 && j > 0 && j < targetHeight-1) {
                    if (frontBeltIDArray[kk] > 0 && frontBelts[0][kk] > mx) {
                        mx = frontBelts[0][kk];
                        id = frontBeltIDArray[kk];
                    }
                }
            }

        }
        frontBeltIDArray[j * (targetWidth) + i] = id;
        return id;
    }
}
