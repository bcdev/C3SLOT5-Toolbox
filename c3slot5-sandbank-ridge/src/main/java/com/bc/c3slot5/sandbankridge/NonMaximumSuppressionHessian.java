package com.bc.c3slot5.sandbankridge;

import java.util.Arrays;
import java.util.Objects;

public class NonMaximumSuppressionHessian {


    public double[][] nonMaxSuppressionOfSourceBand(double[] sourceData,
                                                    int sourceWidth,
                                                    int sourceHeight,
                                                    double nonMaxSuppressionThresholdHessian,
                                                    String filterTypeHessian,
                                                    String filterTypeHessianUsed) {

        double[] nonMaxSuppressedData = new double[sourceWidth * sourceHeight];
        Arrays.fill(nonMaxSuppressedData, Double.NaN);

        int[] nonMaxData = new int[sourceWidth * sourceHeight];
        Arrays.fill(nonMaxData, 0);

        double[][] finalResultData = new double[2][sourceWidth * sourceHeight];

        double maxValue;
        int memoryNewJ;
        int memoryNewI;

        for (int j = SandbankRidgeOp.nonMaxSuppressionKernelRadius;
             j < sourceHeight - SandbankRidgeOp.nonMaxSuppressionKernelRadius; j = j + 2) {
            for (int i = SandbankRidgeOp.nonMaxSuppressionKernelRadius;
                 i < sourceWidth - SandbankRidgeOp.nonMaxSuppressionKernelRadius; i = i + 2) {

                finalResultData[0][j * (sourceWidth) + i] = sourceData[j * (sourceWidth) + i];

                if (!Double.isNaN(sourceData[j * (sourceWidth) + i])) {

                    if (sourceData[j * (sourceWidth) + i] > nonMaxSuppressionThresholdHessian) {
                        maxValue = Double.MIN_VALUE;
                        memoryNewJ = j;
                        memoryNewI = i;
                        for (int ii = -1; ii <= 1; ii++) {
                            for (int jj = -1; jj <= 1; jj++) {

                                if (Double.isNaN(sourceData[(j + jj) * (sourceWidth) + (i + ii)]) &&
                                        maxValue < sourceData[(j + jj) * (sourceWidth) + (i + ii)]) {
                                    maxValue = sourceData[(j + jj) * (sourceWidth) + (i + ii)];
                                    memoryNewJ = j + jj;
                                    memoryNewI = i + ii;
                                }
                            }
                        }
                        nonMaxSuppressedData[memoryNewJ * (sourceWidth) + memoryNewI] = sourceData[memoryNewJ * (sourceWidth) + memoryNewI];
                        nonMaxData[memoryNewJ * (sourceWidth) + memoryNewI] = 1;
                    }
                } else {
                    nonMaxSuppressedData[j * (sourceWidth) + i] = Double.NaN;
                }
            }  /* endfor i*/
        } /* endfor j*/
        if (filterTypeHessianUsed.equals(filterTypeHessian)) {
            int mkr;
            mkr = SandbankRidgeOp.gaussFilterKernelRadius + 2 + 1;
            for (int j = 0; j < sourceHeight; j++) {
                for (int i = 0; i < sourceWidth; i++) {
                    if ((j == mkr && (i >= mkr && i <= sourceWidth - (mkr + 1))) ||
                            (j == sourceHeight - (mkr + 2) && (i >= mkr && i <= sourceWidth - (mkr + 1))) ||
                            (i == mkr && (j >= mkr && j <= sourceHeight - (mkr + 1))) ||
                            (i == sourceWidth - (mkr + 1) && (j >= mkr && j <= sourceHeight - (mkr + 1)))) {
                        nonMaxSuppressedData[j * (sourceWidth) + i] = Double.NaN;
                    }

                }
            }
        }
        for (int k = 0; k < sourceHeight * sourceWidth; k++) {
            finalResultData[1][k] = nonMaxSuppressedData[k];
        }
        return finalResultData;
    }

}




