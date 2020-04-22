package com.bc.c3slot5.sandbankridge;

import org.esa.snap.core.gpf.Tile;

import java.util.Arrays;

public class EdgeLinkingHysteresisHessian {

    public int[] edgeLinkingOfSourceBand(
            double[][] sourceData,
            double[][] gradientSourceData,
            int sourceWidth,
            int sourceHeight,
            Tile targetTileSandBanksBeltMagHessian,
            Tile targetTileSandBanksBeltDirHessian) {


//        double[] preparedData = new double[sourceWidth * sourceHeight];
//        System.arraycopy(sourceData, 0, preparedData, 0, sourceData.length);

//        static double thresholdRidgeDetectionHessian = 1.0;
//        static double thresholdRidgeDetectionHessianMax = 1.0;
//        static double thresholdRidgeDetectionHessianMin = 0.5;

        double[][] gradientLinesData= new double[2][sourceWidth * sourceHeight];

        int[] edgeLinkedData = new int[sourceWidth * sourceHeight];
        Arrays.fill(edgeLinkedData, 0);

        for (int j = 1; j < sourceHeight-1; j++) {
            for (int i = 1; i < sourceWidth-1; i++) {
                if (sourceData[1][j * (sourceWidth) + i] >= SandbankRidgeOp.nonMaxSuppressionThresholdHessian &&
                        !Double.isNaN(sourceData[1][j * (sourceWidth) + i])) {
                    edgeLinkedData[j * (sourceWidth) + i] = 1;
                    makeEdgeLinking(i, j, sourceWidth, sourceHeight, sourceData, edgeLinkedData);
                }
            }
        }
        for (int j = 1; j < sourceHeight-1; j++) {
            for (int i = 1; i < sourceWidth-1; i++) {
                gradientLinesData[0][j * (sourceWidth) + i] = 0.0;
                gradientLinesData[1][j * (sourceWidth) + i] = 0.0;
                if (edgeLinkedData[j * (sourceWidth) + i] >= 1) {
                    gradientLinesData[0][j * (sourceWidth) + i] = gradientSourceData[0][j * (sourceWidth) + i];
                    gradientLinesData[1][j * (sourceWidth) + i] = gradientSourceData[1][j * (sourceWidth) + i];

                }
            }
        }


        SandbankRidgeOp.makeFilledBand(gradientLinesData, sourceWidth, sourceHeight,
                targetTileSandBanksBeltMagHessian, targetTileSandBanksBeltDirHessian, SandbankRidgeOp.maxKernelRadius);



        return edgeLinkedData;

    }

    private void makeEdgeLinking(int i_width,
                                 int j_height,
                                 int sourceWidth,
                                 int sourceHeight,
                                 double[][] sourceData,
                                 int[] edgeLinkedData) {


        if (j_height < sourceHeight - 1 && i_width < sourceWidth - 1 && j_height > 1 && i_width > 1) {

            //System.out.printf("Second EDGE: heightValue %d , widthValue %d) \n", j_height, i_width);
            int heightValue;
            int widthValue;

            heightValue = j_height - 1;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);

            heightValue = j_height - 1;
            widthValue = i_width;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);

            heightValue = j_height - 1;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);

            heightValue = j_height;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);

            heightValue = j_height;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);

            heightValue = j_height + 1;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);

            heightValue = j_height + 1;
            widthValue = i_width;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);

            heightValue = j_height + 1;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, edgeLinkedData);


        }
    }

    private void makeEdgeLinkingPartTwo(int heightValue, int widthValue,
                                        int sourceWidth, int sourceHeight,
                                        double[][] sourceData,
                                        int[] edgeLinkedData) {

        if (sourceData[0][(heightValue) * (sourceWidth) + (widthValue)] > SandbankRidgeOp.thresholdRidgeDetectionHessian
                && edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] == 0
                && !Double.isNaN(sourceData[0][(heightValue) * (sourceWidth) + (widthValue)])) {
            edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] = 1;
            makeEdgeLinking(widthValue, heightValue, sourceWidth, sourceHeight, sourceData, edgeLinkedData);

        }
    }


}