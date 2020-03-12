package com.bc.c3slot5.sandbankridge;

public class NonMaximumSuppression {


    public double[][] nonMaxSuppressionOfSourceBand(double[][] gradientSourceData,
                                                    int sourceWidth,
                                                    int sourceHeight) {

        double[][] nonMaxSuppressedGradientData = new double[2][sourceWidth * sourceHeight];
        double neighbourValue1;
        double neighbourValue2;

        for (int j = SandbankRidgeOp.nonMaxSuppressionKernelRadius;
             j < sourceHeight - SandbankRidgeOp.nonMaxSuppressionKernelRadius; j++) {
            for (int i = SandbankRidgeOp.nonMaxSuppressionKernelRadius;
                 i < sourceWidth - SandbankRidgeOp.nonMaxSuppressionKernelRadius; i++) {
                if (!Double.isNaN(gradientSourceData[1][j * (sourceWidth) + i])) {


                    if ((gradientSourceData[1][j * (sourceWidth) + i] >= 0.0 &&
                            gradientSourceData[1][j * (sourceWidth) + i] < 22.5)
                            || (gradientSourceData[1][j * (sourceWidth) + i] >= 157.5 &&
                            gradientSourceData[1][j * (sourceWidth) + i] < 202.5)
                            || (gradientSourceData[1][j * (sourceWidth) + i] >= 337.5 &&
                            gradientSourceData[1][j * (sourceWidth) + i] <= 360.0)) {

                        nonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 0.0;


                        neighbourValue1 = gradientSourceData[0][(j) * (sourceWidth) + (i - 1)];
                        neighbourValue2 = gradientSourceData[0][(j) * (sourceWidth) + (i + 1)];
                        if (Double.isNaN(gradientSourceData[0][(j) * (sourceWidth) + (i - 1)])) {
                            neighbourValue1
                                    = gradientSourceData[0][j * (sourceWidth) + i] - 1.;
                        }
                        if (Double.isNaN(gradientSourceData[0][(j) * (sourceWidth) + (i + 1)])) {
                            neighbourValue2
                                    = gradientSourceData[0][j * (sourceWidth) + i] - 1.;
                        }
                        if (gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue1
                                && gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue2) {
                            nonMaxSuppressedGradientData[0][j * (sourceWidth) + i]
                                    = gradientSourceData[0][j * (sourceWidth) + i];


                        } else nonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = Double.NaN;

                    } else {


                        if ((gradientSourceData[1][j * (sourceWidth) + i] >= 22.5 &&
                                gradientSourceData[1][j * (sourceWidth) + i] < 67.5)
                                || (gradientSourceData[1][j * (sourceWidth) + i] >= 202.5 &&
                                gradientSourceData[1][j * (sourceWidth) + i] < 247.5)) {

                            nonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 45.0;

                            neighbourValue1 = gradientSourceData[0][(j - 1) * (sourceWidth) + (i - 1)];
                            neighbourValue2 = gradientSourceData[0][(j + 1) * (sourceWidth) + (i + 1)];

                            if (Double.isNaN(gradientSourceData[0][(j - 1) * (sourceWidth) + (i - 1)])) {
                                neighbourValue1
                                        = gradientSourceData[0][j * (sourceWidth) + i] - 1.;
                            }
                            if (Double.isNaN(gradientSourceData[0][(j + 1) * (sourceWidth) + (i + 1)])) {
                                neighbourValue2
                                        = gradientSourceData[0][j * (sourceWidth) + i] - 1.;
                            }
                            if (gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue1
                                    && gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue2) {
                                nonMaxSuppressedGradientData[0][j * (sourceWidth) + i]
                                        = gradientSourceData[0][j * (sourceWidth) + i];


                            } else nonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = Double.NaN;

                        } else {
                            if ((gradientSourceData[1][j * (sourceWidth) + i] >= 67.5 &&
                                    gradientSourceData[1][j * (sourceWidth) + i] < 112.5)
                                    || (gradientSourceData[1][j * (sourceWidth) + i] >= 247.5 &&
                                    gradientSourceData[1][j * (sourceWidth) + i] < 292.5)) {

                                nonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 90.0;
                                neighbourValue1 = gradientSourceData[0][(j - 1) * (sourceWidth) + (i)];
                                neighbourValue2 = gradientSourceData[0][(j + 1) * (sourceWidth) + (i)];
                                if (Double.isNaN(gradientSourceData[0][(j - 1) * (sourceWidth) + (i)])) {

                                    neighbourValue1
                                            = gradientSourceData[0][j * (sourceWidth) + i] - 1.;
                                }
                                if (Double.isNaN(gradientSourceData[0][(j + 1) * (sourceWidth) + (i)])) {
                                    neighbourValue2
                                            = gradientSourceData[0][j * (sourceWidth) + i] - 1.;

                                }
                                if (gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue1
                                        && gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue2) {
                                    nonMaxSuppressedGradientData[0][j * (sourceWidth) + i]
                                            = gradientSourceData[0][j * (sourceWidth) + i];


                                } else nonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = Double.NaN;

                            } else {
                                if ((gradientSourceData[1][j * (sourceWidth) + i] >= 112.5 &&
                                        gradientSourceData[1][j * (sourceWidth) + i] < 157.5)
                                        || (gradientSourceData[1][j * (sourceWidth) + i] >= 292.5 &&
                                        gradientSourceData[1][j * (sourceWidth) + i] < 337.5)) {

                                    nonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 135;

                                    neighbourValue1 = gradientSourceData[0][(j - 1) * (sourceWidth) + (i + 1)];
                                    neighbourValue2 = gradientSourceData[0][(j + 1) * (sourceWidth) + (i - 1)];

                                    if (Double.isNaN(gradientSourceData[0][(j - 1) * (sourceWidth) + (i + 1)])) {
                                        neighbourValue1
                                                = gradientSourceData[0][j * (sourceWidth) + i] - 1.;

                                    }
                                    if (Double.isNaN(gradientSourceData[0][(j + 1) * (sourceWidth) + (i - 1)])) {
                                        neighbourValue2
                                                = gradientSourceData[0][j * (sourceWidth) + i] - 1.;
                                    }
                                    if (gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue1
                                            && gradientSourceData[0][j * (sourceWidth) + i] > neighbourValue2) {
                                        nonMaxSuppressedGradientData[0][j * (sourceWidth) + i]
                                                = gradientSourceData[0][j * (sourceWidth) + i];


                                    } else nonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = Double.NaN;

                                }
                            }
                        }
                    }
                } else {
                    nonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = Double.NaN;
                    nonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = Double.NaN;
                }

            }  /* endfor i*/
        } /* endfor j*/


        return nonMaxSuppressedGradientData;
    }

}




