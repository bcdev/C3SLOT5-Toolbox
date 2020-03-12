package com.bc.c3slot5.sandbankridge;

import java.awt.*;

public class ShortFrontSuppression {

    public void compute(double[] endFrontPointsFoundData,
                        double[][] generalGradientArray,
                        Rectangle targetRectangle) {

        int RED_DUCK = EndPointsFound.RED_DUCK;
        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;

        cutPoint(generalGradientArray, targetWidth, targetHeight);

        cutDouble(endFrontPointsFoundData, generalGradientArray, targetWidth, targetHeight);

        cutPoint(generalGradientArray, targetWidth, targetHeight);
    }

    private void cutDouble(double[] endFrontPointsFoundData, double[][] generalGradientArray, int targetWidth, int targetHeight) {
        int RED_DUCK = EndPointsFound.RED_DUCK;
        int countValue;
        for (int j = 1; j < targetHeight-1; j++) {
            for (int i = 1; i < targetWidth-1; i++) {
                int k = j * (targetWidth) + i;
                if (endFrontPointsFoundData[k] == RED_DUCK) {
                    countValue = 1;
                    for (int jj = -1; jj <= 1; jj++) {
                        for (int ii = -1; ii <= 1; ii++) {
                            int kk = (j + jj) * (targetWidth) + (i + ii);
                            if (ii == 0 && jj == 0) continue;
                            if (endFrontPointsFoundData[kk] == RED_DUCK) {
                                countValue = 0;
                                break;
                            }
                        }
                    }
                    if (countValue == 0) {
                        generalGradientArray[0][k] = Double.NaN;
                        generalGradientArray[1][k] = Double.NaN;

                    }
                }
            }
        }
    }

    private void cutPoint(double[][] generalGradientArray, int targetWidth, int targetHeight) {
        int countValue;
        for (int j = 1; j < targetHeight-1; j++) {
            for (int i = 1; i < targetWidth-1; i++) {
                int k = j * (targetWidth) + i;
                countValue = 0;
                if (!Double.isNaN(generalGradientArray[0][k])) {
                    for (int jj = -1; jj <= 1; jj++) {
                        for (int ii = -1; ii <= 1; ii++) {
                            int kk = (j + jj) * (targetWidth) + (i + ii);
                            if (ii == 0 && jj == 0) continue;
                            if (!Double.isNaN(generalGradientArray[0][kk])) {
                                countValue++;
                            }
                        }
                    }
                    if (countValue == 0) {
                        generalGradientArray[0][k] = Double.NaN;
                        generalGradientArray[1][k] = Double.NaN;
                    }
                }
            }
        }
    }

}
