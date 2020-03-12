package com.bc.c3slot5.sandbankridge;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: michael
 * Date: 16.02.2011
 * Time: 16:00:55
 * To change this template use File | Settings | File Templates.
 */
public class CreateFrontBelt {


    public double[][] compute(double[] frontsArrayMag, double[] frontsArrayDir, double[][] frontLines, int[] frontIDArray, Rectangle targetRectangle) {

        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;
        int targetLength = targetHeight * targetWidth;
        double[][] frontBeltArray = new double[2][targetLength];
        //FrontsOperator.beltThreshold and FrontsOperator.beltRepeatValue

        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                frontBeltArray[0][k] = Double.NaN;
                frontBeltArray[1][k] = Double.NaN;
            }
        }


        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                if (!Double.isNaN(frontLines[0][k])) {
                    frontBeltArray[0][k] = frontLines[0][k];
                    frontBeltArray[1][k] = frontLines[1][k];
                }
            }
        }
        for (int repeatValue = 0; repeatValue < SandbankRidgeOp.beltRepeatValue; repeatValue++) {
            for (int j = 1; j < targetHeight - 1; j++) {
                for (int i = 1; i < targetWidth - 1; i++) {
                    int k = j * (targetWidth) + i;
                    if (!Double.isNaN(frontBeltArray[0][k])) {
                        for (int jk = -1; jk <= 1; jk++) {
                            for (int ik = -1; ik <= 1; ik++) {
                                int kk = (j + jk) * (targetWidth) + (i + ik);
                                if (frontsArrayMag[kk] >= SandbankRidgeOp.beltThreshold) {
                                    frontBeltArray[0][kk] = frontsArrayMag[kk];
                                    frontBeltArray[1][kk] = frontsArrayDir[kk];
                                }
                            }
                        }
                    }
                }
            }
        }
        return frontBeltArray;
    }
}