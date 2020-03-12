package com.bc.c3slot5.sandbankridge;


import java.util.Arrays;


public class EndPointsFound {

    static final int RED_DUCK = 255;

    public double[] compute(double[][] thinningSourceData,
                        int sourceWidth,
                        int sourceHeight) {


        int sourceLength = sourceHeight * sourceWidth;
        int countValue;
        double[] endPointsArray = new double[sourceLength];
        //double[] sourceData = targetTilePreparedSourceBand.getSamplesDouble();
        int[] neighboursArray = new int[9];

        Arrays.fill(endPointsArray, 0);
        Arrays.fill(neighboursArray, 0);

        for (int j = 1; j < sourceHeight - 1; j++) {
            for (int i = 1; i < sourceWidth - 1; i++) {

//                if (thinningSourceData[0][(j) * (sourceWidth) + (i)] > 0.0) {
                 if (!Double.isNaN(thinningSourceData[0][(j) * (sourceWidth) + (i)])) {
                    // todo Fronts Threshold auf -99,9 setzen, wegen SST in grad Celsius
                     // maybe done
                    countValue = 0;
                    for (int jj = -1; jj <= 1; jj++) {
                        for (int ii = -1; ii <= 1; ii++) {
                            if (ii == 0 && jj == 0) continue;
                            if (thinningSourceData[0][(j + jj) * (sourceWidth) + (i + ii)] > 0.0) {
                                neighboursArray[(jj + 1) * 3 + (ii + 1)] = 1;
                                countValue = countValue + 1;

                            } else {
                                neighboursArray[(jj + 1) * 3 + (ii + 1)] = 0;
                            }
                        }
                    }
                    if (countValue == 1) {
                        endPointsArray[(j) * (sourceWidth) + (i)] = RED_DUCK;
                    } else {
                        if (countValue == 2) {
                            if ((neighboursArray[0] == 1 && neighboursArray[1] == 1) ||
                                    (neighboursArray[1] == 1 && neighboursArray[2] == 1) ||
                                    (neighboursArray[0] == 1 && neighboursArray[3] == 1) ||
                                    (neighboursArray[2] == 1 && neighboursArray[5] == 1) ||
                                    (neighboursArray[3] == 1 && neighboursArray[6] == 1) ||
                                    (neighboursArray[5] == 1 && neighboursArray[8] == 1) ||
                                    (neighboursArray[6] == 1 && neighboursArray[7] == 1) ||
                                    (neighboursArray[7] == 1 && neighboursArray[8] == 1)) {

                                endPointsArray[(j) * (sourceWidth) + (i)] = RED_DUCK;
                            }
                        }
                    }
                }
            }
        }
       return  endPointsArray;
    }
}
