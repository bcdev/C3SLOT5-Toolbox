package com.bc.c3slot5.sandbankridge;

import java.util.Arrays;

public class EdgeLinkingHysteresis {

    public int[] edgeLinkingOfSourceBand(
            int[][] sourceData,
            int sourceWidth,
            int sourceHeight) {


        int[][] preparedData = new int[2][sourceWidth * sourceHeight];

        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                preparedData[0][j * (sourceWidth) + i] = sourceData[0][j * (sourceWidth) + i];
                preparedData[1][j * (sourceWidth) + i] = sourceData[1][j * (sourceWidth) + i];
            }
        }

        int[] edgeLinkedData = new int[sourceWidth * sourceHeight];
        Arrays.fill(edgeLinkedData, 0);
        int centralValue = 0;

        for (int j = 1; j < sourceHeight; j++) {
            for (int i = 1; i < sourceWidth; i++) {
                if (sourceData[1][j * (sourceWidth) + i] >= SandbankRidgeOp.thresholdRidgeDetectionMax) {
                    edgeLinkedData[j * (sourceWidth) + i] = 1;
                    centralValue = sourceData[1][j * (sourceWidth) + i];
                    makeEdgeLinking(i, j, sourceWidth, sourceHeight, sourceData, preparedData, edgeLinkedData, centralValue);
                }
            }
        }

        return edgeLinkedData;
    }

    private void makeEdgeLinking(int i_width,
                                 int j_height,
                                 int sourceWidth,
                                 int sourceHeight,
                                 int[][] sourceData,
                                 int[][] preparedData,
                                 int[] edgeLinkedData,
                                 int centralValue) {


        if (j_height < sourceHeight - 1 && i_width < sourceWidth - 1 && j_height > 1 && i_width > 1) {

            //System.out.printf("Second EDGE: heightValue %d , widthValue %d) \n", j_height, i_width);
            int heightValue;
            int widthValue;

            heightValue = j_height - 1;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);

            heightValue = j_height - 1;
            widthValue = i_width;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);

            heightValue = j_height - 1;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);

            heightValue = j_height;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);

            heightValue = j_height;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);

            heightValue = j_height + 1;
            widthValue = i_width - 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);

            heightValue = j_height + 1;
            widthValue = i_width;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);

            heightValue = j_height + 1;
            widthValue = i_width + 1;
            makeEdgeLinkingPartTwo(heightValue, widthValue, sourceWidth, sourceHeight,
                    sourceData, preparedData, edgeLinkedData, centralValue);


        }
    }

    private void makeEdgeLinkingPartTwo(int heightValue, int widthValue,
                                        int sourceWidth, int sourceHeight,
                                        int[][] sourceData,
                                        int[][] preparedData,
                                        int[] edgeLinkedData,
                                        int centralValue) {
        int centralValueMax;
        if (sourceData[1][(heightValue) * (sourceWidth) + (widthValue)] >= SandbankRidgeOp.thresholdRidgeDetectionMax
                && edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] == 0) {
            edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] = 1;
            centralValueMax = Math.max(sourceData[1][(heightValue) * (sourceWidth) + (widthValue)], centralValue);
            makeEdgeLinking(widthValue, heightValue, sourceWidth, sourceHeight, sourceData,
                    preparedData, edgeLinkedData, centralValueMax);
        } else {
            if (sourceData[1][(heightValue) * (sourceWidth) + (widthValue)] >= SandbankRidgeOp.thresholdRidgeDetectionMin
                    && edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] == 0) {
                //System.out.printf("EDGE: heightValue %d , widthValue %d) \n", heightValue, widthValue);
                edgeLinkedData[(heightValue) * (sourceWidth) + (widthValue)] = 1;
                centralValueMax = Math.max(sourceData[1][(heightValue) * (sourceWidth) + (widthValue)], centralValue);
                preparedData[1][(heightValue) * (sourceWidth) + (widthValue)] = centralValueMax;
                makeEdgeLinking(widthValue, heightValue, sourceWidth, sourceHeight, sourceData,
                        preparedData, edgeLinkedData, centralValueMax);
            }
        }
    }


}
