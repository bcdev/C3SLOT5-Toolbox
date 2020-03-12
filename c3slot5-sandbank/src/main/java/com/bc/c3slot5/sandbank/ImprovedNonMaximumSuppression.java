package com.bc.c3slot5.sandbank;

public class ImprovedNonMaximumSuppression {


    public double[][] improvedNonMaxSuppressionOfSourceBand(double[][] gradientSourceData,
                                                            int sourceWidth,
                                                            int sourceHeight) {

        double[][] ImpNonMaxSuppressedGradientData = new double[2][sourceWidth * sourceHeight];
        double pointOfInterestDirection = 0.0;
        double pointOfInterestMagnitude = 0.0;

        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                ImpNonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = gradientSourceData[0][j * (sourceWidth) + i];
                ImpNonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = gradientSourceData[1][j * (sourceWidth) + i];

            }
        }


        for (int j = SandbankOp.nonMaxSuppressionKernelRadius;
             j < sourceHeight - SandbankOp.nonMaxSuppressionKernelRadius; j++) {
            for (int i = SandbankOp.nonMaxSuppressionKernelRadius;
                 i < sourceWidth - SandbankOp.nonMaxSuppressionKernelRadius; i++) {

                pointOfInterestDirection = gradientSourceData[1][j * (sourceWidth) + i];
                pointOfInterestMagnitude = gradientSourceData[0][j * (sourceWidth) + i];

                if (!Double.isNaN(gradientSourceData[1][j * (sourceWidth) + i])) {


                    if ((pointOfInterestDirection >= 0.0 &&
                            pointOfInterestDirection < 22.5)
                            || (pointOfInterestDirection >= 157.5 &&
                            pointOfInterestDirection < 202.5)
                            || (pointOfInterestDirection >= 337.5 &&
                            pointOfInterestDirection <= 360.0)) {

                        ImpNonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 0.0;
                    }

                    if ((pointOfInterestDirection >= 22.5 &&
                            pointOfInterestDirection < 67.5)
                            || (pointOfInterestDirection >= 202.5 &&
                            pointOfInterestDirection < 247.5)) {

                        ImpNonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 45.0;
                    }


                    if ((pointOfInterestDirection >= 67.5 &&
                            pointOfInterestDirection < 112.5)
                            || (pointOfInterestDirection >= 247.5 &&
                            pointOfInterestDirection < 292.5)) {

                        ImpNonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 90.0;
                    }

                    if ((pointOfInterestDirection >= 112.5 &&
                            pointOfInterestDirection < 157.5)
                            || (pointOfInterestDirection >= 292.5 &&
                            pointOfInterestDirection < 337.5)) {

                        ImpNonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = 135;
                    }

                    if (!Double.isNaN(gradientSourceData[1][(j - 1) * (sourceWidth) + i])
                            && !Double.isNaN(gradientSourceData[1][(j + 1) * (sourceWidth) + i])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][(j - 1) * (sourceWidth) + i])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][(j + 1) * (sourceWidth) + i])) {

                        ImpNonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = pointOfInterestMagnitude;
                        continue;
                    }

                    if (!Double.isNaN(gradientSourceData[1][j * (sourceWidth) + i + 1])
                            && !Double.isNaN(gradientSourceData[1][j * (sourceWidth) + i - 1])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][j * (sourceWidth) + i + 1])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][j * (sourceWidth) + i - 1])) {

                        ImpNonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = pointOfInterestMagnitude;
                        continue;
                    }

                    if (!Double.isNaN(gradientSourceData[1][(j - 1) * (sourceWidth) + i - 1])
                            && !Double.isNaN(gradientSourceData[1][(j + 1) * (sourceWidth) + i + 1])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][(j - 1) * (sourceWidth) + i - 1])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][(j + 1) * (sourceWidth) + i + 1])) {

                        ImpNonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = pointOfInterestMagnitude;
                        continue;
                    }

                    if (!Double.isNaN(gradientSourceData[1][(j - 1) * (sourceWidth) + i + 1])
                            && !Double.isNaN(gradientSourceData[1][(j + 1) * (sourceWidth) + i - 1])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][(j - 1) * (sourceWidth) + i + 1])
                            && (pointOfInterestMagnitude) > (gradientSourceData[0][(j + 1) * (sourceWidth) + i - 1])) {

                        ImpNonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = pointOfInterestMagnitude;
                    }

                } else {
                    ImpNonMaxSuppressedGradientData[0][j * (sourceWidth) + i] = Double.NaN;
                    ImpNonMaxSuppressedGradientData[1][j * (sourceWidth) + i] = Double.NaN;
                }

            }  /* endfor i*/
        } /* endfor j*/


        return ImpNonMaxSuppressedGradientData;
    }

}




