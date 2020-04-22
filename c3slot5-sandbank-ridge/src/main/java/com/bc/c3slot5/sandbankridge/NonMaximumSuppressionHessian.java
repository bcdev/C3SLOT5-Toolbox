package com.bc.c3slot5.sandbankridge;

import java.util.Arrays;

public class NonMaximumSuppressionHessian {


    public double[][] nonMaxSuppressionOfSourceBand(double[] sourceData,
                                                  int sourceWidth,
                                                  int sourceHeight) {

        double[] nonMaxSuppressedData = new double[sourceWidth * sourceHeight];
        Arrays.fill(nonMaxSuppressedData, Double.NaN);

        int[] nonMaxData = new int[sourceWidth * sourceHeight];
        Arrays.fill(nonMaxData, 0);

        double[][] finalResultData = new double[2][sourceWidth * sourceHeight];

        double maxValue;
        int memoryNewJ;
        int memoryNewI;

        for (int j = SandbankRidgeOp.nonMaxSuppressionKernelRadius;
             j < sourceHeight - SandbankRidgeOp.nonMaxSuppressionKernelRadius; j++) {
            for (int i = SandbankRidgeOp.nonMaxSuppressionKernelRadius;
                 i < sourceWidth - SandbankRidgeOp.nonMaxSuppressionKernelRadius; i++) {

                finalResultData[0][j * (sourceWidth) + i]= sourceData[j * (sourceWidth) + i];

                if (!Double.isNaN(sourceData[j * (sourceWidth) + i])) {

                    if (sourceData[j * (sourceWidth) + i] > SandbankRidgeOp.nonMaxSuppressionThresholdHessian) {

                        nonMaxSuppressedData[j * (sourceWidth) + i] = sourceData[j * (sourceWidth) + i];
                        nonMaxData[j * (sourceWidth) + i] = 1;
                        maxValue = Double.MIN_VALUE;
                        memoryNewJ = j;
                        memoryNewI = i;
                        for (int ii = -1; ii <= 1; ii++) {
                            for (int jj = -1; jj <= 1; jj++) {
                                if (ii != 0 && jj != 0 && Double.isNaN(sourceData[(j + jj) * (sourceWidth) + (i + ii)]) &&
                                        maxValue < sourceData[(j + jj) * (sourceWidth) + (i + ii)]) {
                                    maxValue = sourceData[(j + jj) * (sourceWidth) + (i + ii)];
                                    memoryNewJ = j + jj;
                                    memoryNewI = i + ii;
                                }
                            }
                        }
                        if (memoryNewJ != j && memoryNewI != i){
                            nonMaxSuppressedData[memoryNewJ * (sourceWidth) + memoryNewI] = sourceData[memoryNewJ * (sourceWidth) + memoryNewI];
                            nonMaxData[memoryNewJ * (sourceWidth) + memoryNewI] = 1;
                        }

                    }
                } else {
                    nonMaxSuppressedData[j * (sourceWidth) + i] = Double.NaN;
                }

                finalResultData[1][j * (sourceWidth) + i]= nonMaxSuppressedData[j * (sourceWidth) + i];

            }  /* endfor i*/
        } /* endfor j*/


        return finalResultData;
    }

}




