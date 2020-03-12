package com.bc.c3slot5.sandbank;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: michael
 * Date: 16.02.2011
 * Time: 16:00:55
 * To change this template use File | Settings | File Templates.
 */
public class CreateFrontID {


    public int[] compute(double[][] frontLines, Rectangle targetRectangle) {

        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;
        int targetLength = targetHeight * targetWidth;
        int[] frontIDArray = new int[targetLength];

        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                if (!Double.isNaN(frontLines[0][j * (targetWidth) + i])) {
                    frontIDArray[k] = -1;
                } else {
                    frontIDArray[k] = -2;
                }
            }
        }

        int ID_counter = 0;
        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                if (frontIDArray[k] == -1) {
                    ID_counter++;
                    //System.out.printf("FrontID %d \n", ID_counter);
                    frontIDArray[k] = ID_counter;
                    makeFrontID(i, j, targetWidth, targetHeight, frontIDArray, ID_counter);

                }
            }
        }

        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                if (frontIDArray[k] == -2) {
                    frontIDArray[k] = 0;
                }
            }
        }
        return frontIDArray;
    }

    private void makeFrontID(int i,
                             int j,
                             int targetWidth,
                             int targetHeight,
                             int[] frontIDArray,
                             int ID_counter) {

        for (int jk = -1; jk <= 1; jk++) {
            for (int ik = -1; ik <= 1; ik++) {
                int kk = (j + jk) * (targetWidth) + (i + ik);
                if ((i + ik >= 0) && (i + ik <= targetWidth - 1) && (j + jk >= 0) && (j + jk <= targetHeight - 1)) {
                    //if (i > 0 && i < targetWidth-1 && j > 0 && j < targetHeight-1) {
                    if (frontIDArray[kk] == -1) {
                        frontIDArray[kk] = ID_counter;
                        makeFrontID(i + ik, j + jk, targetWidth, targetHeight, frontIDArray, ID_counter);
                    }
                }
            }

        }
    }
}