package com.bc.c3slot5.sandbankridge;

import org.esa.snap.core.gpf.Tile;

import java.awt.*;
import java.util.Arrays;

public class ProjectionCayulasCanny {

    public double[][] compute(Rectangle targetRectangle,
                        Tile flagTile,
                        double[] frontsCannyArrayMag,
                        double[] frontsCannyArrayDir,
                        double[] frontsSumCayulaArray,
                        double[] sourceData,
                        // Tile targetTileNonMaxSuppression,
                        Tile targetTileGradientMagnitude,
                        Tile targetTileGradientDirection) {

        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;
        int targetLength = targetHeight * targetWidth;

        int countValue;

        int[] flagArray = flagTile.getSamplesInt();
        double[] gradientArrayMag = targetTileGradientMagnitude.getSamplesDouble();
        double[] gradientArrayDir = targetTileGradientDirection.getSamplesDouble();

        // double[] sourceData = targetTileNonMaxSuppression.getSamplesDouble();

        double[][] generalGradientArray = new double[2][targetLength];
        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                generalGradientArray[0][k] = frontsCannyArrayMag[k];
                generalGradientArray[1][k] = frontsCannyArrayDir[k];
            }
        }

        int[] neighboursArray = new int[9];
        Arrays.fill(neighboursArray, 0);

        for (int j = 1; j < targetHeight-1; j++) {
            for (int i = 1; i < targetWidth-1; i++) {
                int k = (j) * (targetWidth) + (i);
                if (frontsSumCayulaArray[k] > (SandbankRidgeOp.frontValue / 2.)) {
                    for (int jk = -1; jk <= 1; jk++) {
                        for (int ik = -1; ik <= 1; ik++) {
                            int kk = (j + jk) * (targetWidth) + (i + ik);
                            if (!Double.isNaN(sourceData[kk])) {
                                frontsCannyArrayMag[kk] = sourceData[kk];
                                frontsCannyArrayDir[kk] = gradientArrayDir[kk];
                                generalGradientArray[0][kk] = sourceData[kk];
                                generalGradientArray[1][kk] = gradientArrayDir[kk];
                            }
                        }
                    }
                }
            }
        }


        return generalGradientArray;
    }
}
