package com.bc.c3slot5.sandbankridge;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.util.math.Histogram;
import org.esa.snap.core.util.math.IndexValidator;

import java.util.Arrays;

public class SimpleEdgeLinkingHysteresis {

    public void simpleEdgeLinkingOfSourceBand(
            double[][] gradientSourceData,
            double[][] nonMaxSuppressedGradientData,
            int sourceWidth,
            int sourceHeight,
            double maxThresholdHysteresis,
            double minThresholdHysteresis,
            double[][] edgeLinkedGradientData) {

        //double[][] edgeLinkedGradientData = new double[2][sourceWidth * sourceHeight];
        int[] edgeLinkedData = new int[sourceWidth * sourceHeight];
        int countNotNaN = 0;

        double[] thinGradients = new double[sourceWidth * sourceHeight];

        for (int j = SandbankRidgeOp.maxKernelRadius; j < sourceHeight - SandbankRidgeOp.maxKernelRadius; j++) {
            for (int i = SandbankRidgeOp.maxKernelRadius; i < sourceWidth - SandbankRidgeOp.maxKernelRadius; i++) {
                int k = j * (sourceWidth) + i;
                edgeLinkedGradientData[0][k] = Double.NaN;
                edgeLinkedGradientData[1][k] = Double.NaN;
                // statistics
                if (!Double.isNaN(nonMaxSuppressedGradientData[0][k])) {
                    thinGradients[countNotNaN] = nonMaxSuppressedGradientData[0][k];
                    countNotNaN++;
                }
            }
        }
        //  System.out.printf("************    FRONTS STATISTIC:    %d  %f  %d  (%f)  %f  %d (%f) \n", countNotNaN, minThresholdHysteresis, countltMinHyst, 100.0*countltMinHyst/countNotNaN, maxThresholdHysteresis, countgtMaxHyst, 100.0*countgtMaxHyst/countNotNaN);

        double[] fitForHistogram = new double[countNotNaN];
        for (int i = 0; i < countNotNaN; i++) {
            fitForHistogram[i] = thinGradients[i];
        }
        int BinsNumber = 256;
        double topThresholdValue = 0.75;
        int tempMax = -1;
        Histogram histogram = Histogram.computeHistogramDouble(fitForHistogram, IndexValidator.TRUE, BinsNumber, null, null, ProgressMonitor.NULL);
        int summ = 0;
        int tempMinAccount = -1;
        int tempMaxAccount = -1;
        for (int i = 0; i < BinsNumber; i++) {
            summ = summ + histogram.getBinCounts()[i];
            if (histogram.getBinCounts()[i] > tempMax) {
                tempMax = histogram.getBinCounts()[i];
                tempMinAccount = i;
            }
            if ((double) summ / (double) countNotNaN <= topThresholdValue) {
                tempMaxAccount = i;
            }
            //System.out.printf("*->*->*->*->*>*>*>*>*>*>*>*>  HISTOGRAM  SUM:    %d  %d  %f %d\n", i, histogram.getBinCounts()[i], histogram.getRange(i).getMax(), summ);
        }
        //System.out.printf("=============>>>>>>>>  HISTOGRAM  Thresholds:    tempMin = %f  tempMax = %f  \n", (histogram.getRange(tempMinAccount).getMax()) * 2.0, histogram.getRange(tempMaxAccount).getMax());

        //minThresholdHysteresis = (histogram.getRange(tempMinAccount).getMax())*2.0;
        //maxThresholdHysteresis= histogram.getRange(tempMaxAccount).getMax();

        Arrays.fill(edgeLinkedData, 0);

        for (int j = 1; j < sourceHeight; j++) {
            for (int i = 1; i < sourceWidth; i++) {
                if (nonMaxSuppressedGradientData[0][j * (sourceWidth) + i]
                        >= maxThresholdHysteresis
                        && !Double.isNaN(nonMaxSuppressedGradientData[0][j * (sourceWidth) + i])) {
                    edgeLinkedData[j * (sourceWidth) + i] = SandbankRidgeOp.frontValue;
                    edgeLinkedGradientData[0][j * (sourceWidth) + i]
                            = nonMaxSuppressedGradientData[0][j * (sourceWidth) + i];
                    edgeLinkedGradientData[1][j * (sourceWidth) + i]
                            = gradientSourceData[1][j * (sourceWidth) + i];

                    makeEdgeLinking(i, j, sourceWidth, sourceHeight, maxThresholdHysteresis, minThresholdHysteresis,
                            nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);
                }
            }
        }


/*        FrontsOperator.makeFilledBand(edgeLinkedGradientData, sourceWidth, sourceHeight,
                targetTileHysteresisGradient, targetTileHysteresisDirection, FrontsOperator.maxKernelRadius);   */

    }

    private void makeEdgeLinking(int i_width,
                                 int j_height,
                                 int sourceWidth,
                                 int sourceHeight,
                                 double maxThresholdHysteresis,
                                 double minThresholdHysteresis,
                                 double[][] nonMaxSuppressedGradientData,
                                 int[] edgeLinkedData,
                                 double[][] gradientSourceData,
                                 double[][] edgeLinkedGradientData) {


        if (j_height < sourceHeight - 1 && i_width < sourceWidth - 1 && j_height > 1 && i_width > 1) {

            int heightValue;
            int widthValue;

            heightValue = j_height - 1;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);

            heightValue = j_height - 1;
            widthValue = i_width;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);

            heightValue = j_height - 1;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);

            heightValue = j_height;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);

            heightValue = j_height;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);

            heightValue = j_height + 1;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);

            heightValue = j_height + 1;
            widthValue = i_width;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);

            heightValue = j_height + 1;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    maxThresholdHysteresis, minThresholdHysteresis,
                    nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);


        }
    }

    private void makeEdgeLinkingPartTwo(int heightValue, int widthValue,
                                        int sourceWidth, int sourceHeight, double maxThresholdHysteresis,
                                        double minThresholdHysteresis, double[][] nonMaxSuppressedGradientData,
                                        int[] edgeLinkedData, double[][] gradientSourceData, double[][] edgeLinkedGradientData) {

        if (nonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                > maxThresholdHysteresis) {
            edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] = SandbankRidgeOp.frontValue;
            edgeLinkedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                    = nonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)];
            edgeLinkedGradientData[1][(heightValue) * (sourceWidth) + (widthValue)]
                    = gradientSourceData[1][(heightValue) * (sourceWidth) + (widthValue)];
        } else {
            if (nonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                    > minThresholdHysteresis
                    && edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] != SandbankRidgeOp.frontValue) {
                edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] = SandbankRidgeOp.frontValue;
                edgeLinkedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                        = nonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)];
                edgeLinkedGradientData[1][(heightValue) * (sourceWidth) + (widthValue)]
                        = gradientSourceData[1][(heightValue) * (sourceWidth) + (widthValue)];

                makeEdgeLinking(widthValue, heightValue, sourceWidth, sourceHeight, maxThresholdHysteresis,
                        minThresholdHysteresis, nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);
            } /*else {
                if (edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] != FrontsOperator.frontValue
                        && !Double.isNaN(improvedNonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)])
                        && improvedNonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                        > minThresholdHysteresis) {

                    edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] = FrontsOperator.frontValue;
                    edgeLinkedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                            = improvedNonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)];
                    edgeLinkedGradientData[1][(heightValue) * (sourceWidth) + (widthValue)]
                            = improvedNonMaxSuppressedGradientData[1][(heightValue) * (sourceWidth) + (widthValue)];

                    makeEdgeLinking(widthValue, heightValue, sourceWidth, sourceHeight, maxThresholdHysteresis,
                            minThresholdHysteresis, nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);
                }
            }*/
        }
    }
}