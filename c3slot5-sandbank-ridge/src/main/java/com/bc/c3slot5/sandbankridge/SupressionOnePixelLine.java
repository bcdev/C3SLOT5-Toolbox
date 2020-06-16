package com.bc.c3slot5.sandbankridge;

import java.util.Arrays;

public class SupressionOnePixelLine {


    public void suppressOnePixelLine(int[] sourceData,
                                     int sourceWidth,
                                     int sourceHeight) {

        int[] sourceDataCopy = new int[sourceWidth * sourceHeight];
        System.arraycopy(sourceData, 0, sourceDataCopy, 0, sourceData.length);

        int k;
        int heightValue;
        int widthValue;
        int arrayWidth = 3;
        int arrayHeigth = 3;
        int arrayLength = arrayWidth * arrayHeigth;
        int sourceDataValue = 0;

        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                sourceDataValue = 0;
                for (int jj = 0; jj < arrayHeigth; jj++) {
                    for (int ii = 0; ii < arrayWidth; ii++) {
                        k = jj * 3 + ii;
                        heightValue = j + (jj - 1);
                        widthValue = i + (ii - 1);
                        if (heightValue < sourceHeight - 1 && widthValue < sourceWidth - 1 &&
                                heightValue >= 0 && widthValue >= 0) {
                            if (sourceData[heightValue * (sourceWidth) + widthValue] == 1) {
                                sourceDataValue++;
                            }
                        }
                    }
                }
                if (sourceDataValue <= 1) {
                    sourceDataCopy[j * (sourceWidth) + i] = 0;
                }
            }
        }
        System.arraycopy(sourceDataCopy, 0, sourceData, 0, sourceDataCopy.length);
    }
}

