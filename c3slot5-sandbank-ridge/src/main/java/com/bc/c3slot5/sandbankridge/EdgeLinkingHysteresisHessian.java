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

        double[][] gradientLinesData = new double[2][sourceWidth * sourceHeight];

        int[] edgeLinkedData = new int[sourceWidth * sourceHeight];
        Arrays.fill(edgeLinkedData, 0);

        for (int j = 1; j < sourceHeight - 1; j++) {
            for (int i = 1; i < sourceWidth - 1; i++) {
                if (Double.isNaN(sourceData[1][j * (sourceWidth) + i])) {
                    makeEdgeLinking(i, j, sourceWidth, sourceHeight, sourceData, edgeLinkedData);
                } else {
                    edgeLinkedData[j * (sourceWidth) + i] = 1;
                }
            }
        }

        for (int j = 1; j < sourceHeight - 1; j++) {
            for (int i = 1; i < sourceWidth - 1; i++) {
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


        boolean checkThreeValidA = true;
        boolean checkThreeValidB = true;
        boolean checkThreeValidC = true;
        boolean checkThreeValidD = true;

        if (j_height < sourceHeight - 1 && i_width < sourceWidth - 1 && j_height > 1 && i_width > 1) {

            //System.out.printf("Second EDGE: heightValue %d , widthValue %d) \n", j_height, i_width);
            int heightValueOne;
            int heightValueTwo;
            int widthValueOne;
            int widthValueTwo;


            heightValueOne = j_height - 1;
            widthValueOne = i_width;
            heightValueTwo = j_height + 1;
            widthValueTwo = i_width;
            makeEdgeLinkingPartTwo(j_height, i_width,
                    heightValueOne, widthValueOne,
                    heightValueTwo, widthValueTwo,
                    sourceWidth, sourceHeight,
                    sourceData,
                    edgeLinkedData);

            heightValueOne = j_height;
            widthValueOne = i_width - 1;
            heightValueTwo = j_height;
            widthValueTwo = i_width + 1;
            makeEdgeLinkingPartTwo(j_height, i_width,
                    heightValueOne, widthValueOne,
                    heightValueTwo, widthValueTwo,
                    sourceWidth, sourceHeight,
                    sourceData,
                    edgeLinkedData);


            checkThreeValidA =!Double.isNaN(sourceData[1][(j_height-1) * (sourceWidth) + (i_width -1)]) &&
                    !Double.isNaN(sourceData[1][(j_height+1) * (sourceWidth) + (i_width -1)]) &&
                    !Double.isNaN(sourceData[1][(j_height+1) * (sourceWidth) + (i_width +1)]);
            checkThreeValidB =!Double.isNaN(sourceData[1][(j_height-1) * (sourceWidth) + (i_width +1)]) &&
                    !Double.isNaN(sourceData[1][(j_height+1) * (sourceWidth) + (i_width -1)]) &&
                    !Double.isNaN(sourceData[1][(j_height+1) * (sourceWidth) + (i_width +1)]);
            checkThreeValidC =!Double.isNaN(sourceData[1][(j_height-1) * (sourceWidth) + (i_width -1)]) &&
                    !Double.isNaN(sourceData[1][(j_height-1) * (sourceWidth) + (i_width +1)]) &&
                    !Double.isNaN(sourceData[1][(j_height+1) * (sourceWidth) + (i_width +1)]);
            checkThreeValidD =!Double.isNaN(sourceData[1][(j_height-1) * (sourceWidth) + (i_width -1)]) &&
                    !Double.isNaN(sourceData[1][(j_height-1) * (sourceWidth) + (i_width +1)]) &&
                    !Double.isNaN(sourceData[1][(j_height+1) * (sourceWidth) + (i_width -11)]);

            if (!(checkThreeValidA || checkThreeValidB || checkThreeValidC || checkThreeValidD)) {
                if (edgeLinkedData[(j_height) * (sourceWidth) + (i_width)] == 0) {
                    //System.out.printf("linking diagonal_1: heightValue %d , widthValue %d) \n", j_height, i_width);

                    heightValueOne = j_height - 1;
                    widthValueOne = i_width - 1;
                    heightValueTwo = j_height + 1;
                    widthValueTwo = i_width + 1;
                    makeEdgeLinkingPartTwo(j_height, i_width,
                            heightValueOne, widthValueOne,
                            heightValueTwo, widthValueTwo,
                            sourceWidth, sourceHeight,
                            sourceData,
                            edgeLinkedData);
                }
                if (edgeLinkedData[(j_height) * (sourceWidth) + (i_width)] == 0) {
                    //System.out.printf("linking diagonal_2: heightValue %d , widthValue %d) \n", j_height, i_width);

                    heightValueOne = j_height + 1;
                    widthValueOne = i_width - 1;
                    heightValueTwo = j_height - 1;
                    widthValueTwo = i_width + 1;
                    makeEdgeLinkingPartTwo(j_height, i_width,
                            heightValueOne, widthValueOne,
                            heightValueTwo, widthValueTwo,
                            sourceWidth, sourceHeight,
                            sourceData,
                            edgeLinkedData);
                }
            }
        }
    }

    private void makeEdgeLinkingPartTwo(int j_height, int i_width,
                                        int heightValueOne, int widthValueOne,
                                        int heightValueTwo, int widthValueTwo,
                                        int sourceWidth, int sourceHeight,
                                        double[][] sourceData,
                                        int[] edgeLinkedData) {

        if (!Double.isNaN(sourceData[1][(heightValueOne) * (sourceWidth) + (widthValueOne)]) &&
                !Double.isNaN(sourceData[1][(heightValueTwo) * (sourceWidth) + (widthValueTwo)])) {
            edgeLinkedData[(j_height) * (sourceWidth) + (i_width)] = 1;
        }
    }
}
