package com.bc.c3slot5.sandbank;

import java.util.Arrays;

public class EdgeLinkingHysteresis {

    public void edgeLinkingOfSourceBand(
            double[][] gradientSourceData,
            double[][] nonMaxSuppressedGradientData,
            int sourceWidth,
            int sourceHeight,
            double maxThresholdHysteresis,
            double minThresholdHysteresis,
            double[][] edgeLinkedGradientData/*,
            Tile targetTileHysteresisGradient,
            Tile targetTileHysteresisDirection*/) {


        // double[][] edgeLinkedGradientData = new double[2][sourceWidth * sourceHeight];
        int[] edgeLinkedData = new int[sourceWidth * sourceHeight];

        for (int j = 1; j < sourceHeight; j++) {
            for (int i = 1; i < sourceWidth; i++) {
                edgeLinkedGradientData[0][j * (sourceWidth) + i] = Double.NaN;
                edgeLinkedGradientData[1][j * (sourceWidth) + i] = Double.NaN;
            }
        }
        Arrays.fill(edgeLinkedData, 0);

        for (int j = 1; j < sourceHeight; j++) {
            for (int i = 1; i < sourceWidth; i++) {
                if (nonMaxSuppressedGradientData[0][j * (sourceWidth) + i]
                        >= maxThresholdHysteresis
                        && !Double.isNaN(nonMaxSuppressedGradientData[0][j * (sourceWidth) + i])) {
                    edgeLinkedData[j * (sourceWidth) + i] = SandbankOp.frontValue;
                    edgeLinkedGradientData[0][j * (sourceWidth) + i]
                            = nonMaxSuppressedGradientData[0][j * (sourceWidth) + i];
                    edgeLinkedGradientData[1][j * (sourceWidth) + i]
                            = gradientSourceData[1][j * (sourceWidth) + i];


                    makeEdgeLinking(i, j, sourceWidth, sourceHeight, maxThresholdHysteresis, minThresholdHysteresis,
                            nonMaxSuppressedGradientData, edgeLinkedData, gradientSourceData, edgeLinkedGradientData);
                }
            }
        }

        /*FrontsOperator.makeFilledBand(edgeLinkedGradientData, sourceWidth, sourceHeight,
                targetTileHysteresisGradient, targetTileHysteresisDirection, FrontsOperator.maxKernelRadius); */

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
            edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] = SandbankOp.frontValue;
        } else {
            if (nonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                    > minThresholdHysteresis
                    && edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] != SandbankOp.frontValue) {
                edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)]
                        = SandbankOp.frontValue;
                edgeLinkedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)]
                        = nonMaxSuppressedGradientData[0][(heightValue) * (sourceWidth) + (widthValue)];
                edgeLinkedGradientData[1][(heightValue) * (sourceWidth) + (widthValue)]
                        = gradientSourceData[1][(heightValue) * (sourceWidth) + (widthValue)];


                makeEdgeLinking(widthValue, heightValue, sourceWidth, sourceHeight, maxThresholdHysteresis,
                        minThresholdHysteresis, nonMaxSuppressedGradientData,
                        edgeLinkedData, gradientSourceData, edgeLinkedGradientData);
            } 
        }
    }

}