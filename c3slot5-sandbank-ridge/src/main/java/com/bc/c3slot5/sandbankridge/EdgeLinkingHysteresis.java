package com.bc.c3slot5.sandbankridge;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.esa.snap.core.gpf.Tile;

import java.util.Arrays;
import java.util.Collections;

public class EdgeLinkingHysteresis {

    static final int BLUE_DUCK = -255;

    public int[] edgeLinkingOfSourceBand(
            int[][] sourceData,
            double[][] gradientSourceData,
            int sourceWidth,
            int sourceHeight,
            int thresholdRidgeDetection,
            int thresholdRidgeDetectionMax,
            int thresholdRidgeDetectionMin,
            Tile targetTileSandBanksBeltMag,
            Tile targetTileSandBanksBeltDir) {


//        System.out.printf("threshold %d threshold %d threshold %d) \n",
//                thresholdRidgeDetection, thresholdRidgeDetectionMax,  thresholdRidgeDetectionMin);


        double[][] gradientLinesData = new double[2][sourceWidth * sourceHeight];
        int[] edgeLinkedData = new int[sourceWidth * sourceHeight];
        Arrays.fill(edgeLinkedData, 0);
        int[] edgeLinkedOriginalData = new int[sourceWidth * sourceHeight];
        Arrays.fill(edgeLinkedOriginalData, 0);
        int centralValue;
        int thresholdRidgeDetectionMinIterate;
        /*- sourceData[1][..] = countsData; sourceData[0][..] = 0 or 1 */
        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                if (sourceData[1][j * (sourceWidth) + i] >= thresholdRidgeDetectionMax) {
                    edgeLinkedData[j * (sourceWidth) + i] = 1;
                    edgeLinkedOriginalData[j * (sourceWidth) + i] = 1;
                }
            }
        }
        for (int iterate = 0; iterate < 3; iterate++) {
            centralValue = BLUE_DUCK;
            for (int j = 1; j < sourceHeight; j++) {
                for (int i = 1; i < sourceWidth; i++) {
                    centralValue = BLUE_DUCK;
                    thresholdRidgeDetectionMinIterate=Math.max(1,thresholdRidgeDetectionMin-iterate);
                    if (edgeLinkedData[j * (sourceWidth) + i] == 0 &&
                            sourceData[1][j * (sourceWidth) + i] >= thresholdRidgeDetectionMinIterate) {
//                        System.out.printf("edgelinking_starts: heightValue %d , widthValue %d  count %d threshold %d) \n", j, i, sourceData[1][j * (sourceWidth) + i], thresholdRidgeDetectionMin);

                        centralValue = sourceData[1][j * (sourceWidth) + i];
                        makeEdgeLinking(i, j, sourceWidth, sourceHeight, sourceData, edgeLinkedOriginalData, edgeLinkedData, centralValue);
                    }
                }
            }
//            for (int j = 1; j < sourceHeight; j++) {
//                for (int i = 1; i < sourceWidth; i++) {
//                    if (edgeLinkedData[j * (sourceWidth) + i] != edgeLinkedOriginalData[j * (sourceWidth) + i]) {
//                        System.out.printf("edgelinking iterate %d: heightValue %d , widthValue %d  edgeLinkedData %d  edgeLinkedOriginalData %d) \n", iterate, j, i, edgeLinkedData[j * (sourceWidth) + i], edgeLinkedOriginalData[j * (sourceWidth) + i]);
//                    }
//                }
//            }
            System.arraycopy(edgeLinkedData, 0, edgeLinkedOriginalData, 0, edgeLinkedData.length);

//            for (int j = 1; j < sourceHeight; j++) {
//                for (int i = 1; i < sourceWidth; i++) {
//                    if (edgeLinkedData[j * (sourceWidth) + i]!=edgeLinkedOriginalData[j * (sourceWidth) + i]) {
//                        System.out.printf("________________-edgelinking iterate %d: heightValue %d , widthValue %d  edgeLinkedData %d  edgeLinkedOriginalData %d) \n", iterate, j, i, edgeLinkedData[j * (sourceWidth) + i], edgeLinkedOriginalData[j * (sourceWidth) + i]);
//                    }
//                }
//            }
        }

        SupressionOnePixelLine applySuppressOnePixelLine = new SupressionOnePixelLine();
        applySuppressOnePixelLine.suppressOnePixelLine(edgeLinkedData,sourceWidth, sourceHeight);

        for (int j = 1; j < sourceHeight; j++) {
            for (int i = 1; i < sourceWidth; i++) {
                gradientLinesData[0][j * (sourceWidth) + i] = 0.0;
                gradientLinesData[1][j * (sourceWidth) + i] = 0.0;
                if (edgeLinkedData[(j) * (sourceWidth) + (i)] == 1) {
                    gradientLinesData[0][j * (sourceWidth) + i] = gradientSourceData[0][j * (sourceWidth) + i];
                    gradientLinesData[1][j * (sourceWidth) + i] = gradientSourceData[1][j * (sourceWidth) + i];
                }
            }
        }

        SandbankRidgeOp.makeFilledBand(gradientLinesData, sourceWidth, sourceHeight,
                targetTileSandBanksBeltMag, targetTileSandBanksBeltDir, SandbankRidgeOp.maxKernelRadius);


        return edgeLinkedData;
    }

    private void makeEdgeLinking(int i_width,
                                 int j_height,
                                 int sourceWidth,
                                 int sourceHeight,
                                 int[][] sourceData,
                                 int[] edgeLinkedOriginalData,
                                 int[] edgeLinkedData,
                                 int centralValue) {


        if (j_height < sourceHeight - 1 && i_width < sourceWidth - 1 && j_height >= 1 && i_width >= 1) {
            int arrayWidth = 3;
            int arrayHeigth = 3;
            int arrayLength = arrayWidth * arrayHeigth;
            int switchValue = 0;
            boolean linkingSwitch = false;
            int[] heightValue = new int[arrayLength];
            int[] widthValue = new int[arrayLength];
            int[] edgeLinkedOriginalValue = new int[arrayLength];
            int[] edgeLinkedValue = new int[arrayLength];
            int[] sourceDataValue = new int[arrayLength];

            int k;
            for (int jj = 0; jj < arrayHeigth; jj++) {
                for (int ii = 0; ii < arrayWidth; ii++) {
                    k = jj * 3 + ii;
                    heightValue[k] = j_height + (jj - 1);
                    widthValue[k] = i_width + (ii - 1);
                    edgeLinkedOriginalValue[k] = edgeLinkedOriginalData[heightValue[k] * (sourceWidth) + widthValue[k]];
                    edgeLinkedValue[k] = edgeLinkedData[heightValue[k] * (sourceWidth) + widthValue[k]];
                    sourceDataValue[k] = sourceData[1][heightValue[k] * (sourceWidth) + widthValue[k]];
                    if (edgeLinkedOriginalValue[k] == 1) {
                        switchValue = 1;
                    } else {
                        if (edgeLinkedValue[k] == 1 && switchValue != 1) {
                            switchValue = 2;
                        }
                    }
                }
            }

            if (switchValue == 1 || switchValue == 2) {
                linkingSwitch = makeEdgeLinkingPartTwo(heightValue, widthValue, arrayWidth, arrayHeigth,
                        sourceDataValue, edgeLinkedOriginalValue, edgeLinkedValue,
                        edgeLinkedData, centralValue);
            }

            if (linkingSwitch == true) {
                edgeLinkedData[j_height * (sourceWidth) + i_width] = 1;
            }
        }
    }

    private boolean makeEdgeLinkingPartTwo(int[] heightValue, int[] widthValue,
                                           int arrayWidth, int arrayHeigth,
                                           int[] sourceDataValue,
                                           int[] edgeLinkedOriginalValue,
                                           int[] edgeLinkedValue,
                                           int[] edgeLinkedData,
                                           int centralValue) {

        boolean linkingSwitch = false;
        int k;
        int maxValue = BLUE_DUCK;
        for (int jj = 0; jj < arrayHeigth; jj++) {
            for (int ii = 1; ii < arrayWidth; ii++) {
                k = jj * 3 + ii;
                if (edgeLinkedOriginalValue[k] == 0) {
                    maxValue = Math.max(sourceDataValue[k], centralValue);
                }
            }

        }
        if (maxValue == centralValue && maxValue != BLUE_DUCK) {
            linkingSwitch = true;
        }
        return linkingSwitch;
    }

}
