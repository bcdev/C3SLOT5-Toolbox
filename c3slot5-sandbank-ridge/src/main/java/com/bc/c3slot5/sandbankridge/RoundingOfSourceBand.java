package com.bc.c3slot5.sandbankridge;

public class RoundingOfSourceBand {

    public void roundedOfSourceBand(double[] sourceData,
                                    int sourceWidth,
                                    int sourceHeight,
                                    double roundingInputData) {
        
        double[] preparedData = new double[sourceData.length];
        System.arraycopy(sourceData, 0, preparedData, 0, sourceData.length);

        double delta = Math.floor(roundingInputData * 100.0 + 0.5);
        double halfDelta = Math.floor(roundingInputData * 50.0 + 0.5);

        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                if (!Double.isNaN(sourceData[j * (sourceWidth) + i])) {

                    preparedData[j * (sourceWidth) + i]
                            = (Math.floor((sourceData[j * (sourceWidth) + i]*100.0 + halfDelta)/(delta))*delta)/100.0 ;
                    //System.out.printf("!!!!!!!!!!!!!!!!!  Round:  %f  %f   \n",sourceData[j * (sourceWidth) + i], preparedData[j * (sourceWidth) + i]);
                }
            }
        }

        System.arraycopy(preparedData, 0, sourceData, 0, sourceData.length);

    }
}



