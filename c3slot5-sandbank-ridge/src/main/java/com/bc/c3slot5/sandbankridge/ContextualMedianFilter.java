package com.bc.c3slot5.sandbankridge;

import java.util.Arrays;


public class ContextualMedianFilter implements Filter {

    static final int ALLOWED_5FILTERED_THRESHOLD = 15;
    static final int ALLOWED_3FILTERED_THRESHOLD = 6;


    @Override
    public void compute(double[] sourceData,
                        int sourceWidth,
                        int sourceHeight,
                        int[] flagArray,
                        /*Tile targetBandFilter,*/
                        int fiveKernelRadius) {

        int threeKernelRadius = 1;
        int fiveKernelSize = fiveKernelRadius * 2 + 1;
        int threeKernelSize = threeKernelRadius * 2 + 1;
        int[][] fivePointExtremum = new int[sourceWidth][sourceHeight];
        int[][] threePointExtremum = new int[sourceWidth][sourceHeight];
        double[][] fiveKernelData = new double[fiveKernelSize][fiveKernelSize];
        double[][] threeKernelData = new double[threeKernelSize][threeKernelSize];
        double[] threeKernelData1Dim = new double[threeKernelSize * threeKernelSize];

        double[] preparedData = new double[sourceData.length];
        System.arraycopy(sourceData, 0, preparedData, 0, sourceData.length);


        for (int y = fiveKernelRadius; y < sourceHeight - fiveKernelRadius; y++) {
            for (int x = fiveKernelRadius; x < sourceWidth - fiveKernelRadius; x++) {

                if (!Double.isNaN(sourceData[y * (sourceWidth) + x])) {

                    //int counter5 = 0;
                    for (int i = 0; i < fiveKernelSize; i++) {
                        for (int j = 0; j < fiveKernelSize; j++) {
                            fiveKernelData[i][j]
                                    = sourceData[(y - fiveKernelRadius + j) * (sourceWidth) + x - fiveKernelRadius + i];
                        }
                    }
                    //Set the values of the non-used pixel to the value of central pixel
                    fiveKernelData[1][0] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                    fiveKernelData[3][0] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                    fiveKernelData[0][1] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                    fiveKernelData[4][1] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                    fiveKernelData[0][3] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                    fiveKernelData[4][3] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                    fiveKernelData[1][4] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                    fiveKernelData[3][4] = fiveKernelData[fiveKernelRadius][fiveKernelRadius];

                    int k = 0;
                    //int counter3 = 0;
                    for (int i = 0; i < threeKernelSize; i++) {
                        for (int j = 0; j < threeKernelSize; j++) {
                            threeKernelData[i][j]
                                  = sourceData[(y - threeKernelRadius + j) * (sourceWidth) + x - threeKernelRadius + i];
                            threeKernelData1Dim[k] = threeKernelData[i][j];
                            k++;
                        }
                    }


                    //System.out.printf("%d   %d\n", x, y);
                    // Search 5-point Max or Min
                    fivePointExtremum[x][y] = searchExtremum(fiveKernelRadius, fiveKernelData, fiveKernelSize);
                    threePointExtremum[x][y] = searchExtremum(threeKernelRadius, threeKernelData, threeKernelSize);

                    if (fivePointExtremum[x][y] != 1) {
                        if (threePointExtremum[x][y] == 1) {
                            Arrays.sort(threeKernelData1Dim);
                            preparedData[y * (sourceWidth) + x]
                                    = threeKernelData1Dim[((threeKernelSize * threeKernelSize) - 1) / 2];
                            /*median*/
                        } else {
                            if (threePointExtremum[x][y] == 0) {
                                preparedData[y * (sourceWidth) + x]
                                        = fiveKernelData[fiveKernelRadius][fiveKernelRadius];
                            }
                        }
                    }
                }
            }
        }
        /*FrontsOperator.makeFilledBand(preparedData, sourceWidth, sourceHeight,
                targetBandFilter, FrontsOperator.maxKernelRadius); */

        System.arraycopy(preparedData, 0, sourceData, 0, sourceData.length);

    }

    private int searchExtremum(int kernelradius, double[][] kernelData, int kernelSize) {
        double inputMin;
        double inputMax;
        inputMin = +Double.MAX_VALUE;
        inputMax = -Double.MAX_VALUE;

        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                inputMin = Math.min(inputMin, kernelData[i][j]);
                inputMax = Math.max(inputMax, kernelData[i][j]);
            }
        }

        if (inputMin == kernelData[kernelradius][kernelradius] || inputMax == kernelData[kernelradius][kernelradius]) {
            return 1;
        } else {
            return 0;
        }
    }


}
