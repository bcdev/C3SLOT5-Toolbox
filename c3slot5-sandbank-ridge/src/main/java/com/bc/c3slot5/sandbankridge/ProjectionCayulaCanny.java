package com.bc.c3slot5.sandbankridge;

import org.esa.snap.core.gpf.Tile;

import java.awt.*;
import java.util.Arrays;

public class ProjectionCayulaCanny {


    public void compute(Rectangle targetRectangle,
                        Tile flagTile,
                        double[] frontsCannyArrayMag,
                        double[] frontsCannyArrayDir,
                        double[] frontsSumCayulaArray,
                        Tile targetTileNonMaxSuppression,
                        Tile targetTileGradientMagnitude,
                        Tile targetTileGradientDirection,
                        Tile targetTileFronts) {

        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;
        int targetLength = targetHeight * targetWidth;

        int countValue;

        int[] flagArray = flagTile.getSamplesInt();
        double[] gradientArrayMag = targetTileGradientMagnitude.getSamplesDouble();
        double[] gradientArrayDir = targetTileGradientDirection.getSamplesDouble();

        double[] frontsCayulaArray = targetTileFronts.getSamplesDouble();

        double[] sourceData = targetTileNonMaxSuppression.getSamplesDouble();

        int[] neighboursArray = new int[9];
        Arrays.fill(neighboursArray, 0);

        for (int j = 1; j < targetHeight - 1; j++) {
            for (int i = 1; i < targetWidth - 1; i++) {

                int k = (j) * (targetWidth) + (i);
                frontsSumCayulaArray[k] = frontsSumCayulaArray[k] + frontsCayulaArray[k];
                if (frontsSumCayulaArray[k] > 1) frontsSumCayulaArray[k]=1;

                if (frontsCayulaArray[k] > (SandbankRidgeOp.frontValue / 2.)) {
                     // There is no Canny Front under this Cayula Front Point
                    if (Double.isNaN(frontsCannyArrayMag[k]) == true) {
                        countValue = 0;
                        for (int jj = -1; jj <= 1; jj++) {
                            for (int ii = -1; ii <= 1; ii++) {
                                // Under Cayula no Canny found and 3x3 Around area Canny found -> do nothing
                                if (Double.isNaN(frontsCannyArrayMag[(j + jj) * (targetWidth) + (i + ii)]) == false) {
                                    countValue = 1;
                                    break;
                                }
                            }
                        }
                        if (countValue == 0) {
                            // Under Cayula no Canny found and 3x3 Around area no Canny found -> take from the thinning Field
                            // under the same point ...
                            if (Double.isNaN(sourceData[k]) == false) {
                                frontsCannyArrayMag[k] = gradientArrayMag[k];
                                frontsCannyArrayDir[k] = gradientArrayDir[k];
                            } else {
                                // Under Cayula no Canny found and 3x3 Around area no Canny found -> take from the thinning Field
                            // under the 3x3 Around area ...
                                for (int jk = -1; jk <= 1; jk++) {
                                    for (int ik = -1; ik <= 1; ik++) {
                                        int kk = (j + jk) * (targetWidth) + (i + ik);
                                        if (Double.isNaN(frontsCannyArrayMag[kk]) == false) {
                                            frontsCannyArrayMag[kk] = gradientArrayMag[kk];
                                            frontsCannyArrayDir[kk] = gradientArrayDir[kk];
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
