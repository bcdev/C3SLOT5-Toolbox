package com.bc.c3slot5.sandbankridge;


public class MafiozoDeLaProspetto {


    public void mafiozoDeLaProspettoOfSourceBand(double[][] SuppressedGData,
                                                 double[][] ImprovedGData,
                                                 double[] endPointsFoundData,
                                                 int sourceWidth,
                                                 int sourceHeight,
                                                 int[] flagArray) {

        double pointOfInterestDirection = 0.0;
        double pointOfInterestMagnitude = 0.0;

        for (int j = 3; j < sourceHeight - 3; j++) {
            for (int i = 3; i < sourceWidth - 3; i++) {

                if (endPointsFoundData[j * (sourceWidth) + i] == EndPointsFound.RED_DUCK) {

                    for (int jj = -2; jj < 3; jj++) {
                        for (int ii = -2; ii < 3; ii++) {
                            if (ii == -2 || jj == -2 || ii == 2 || jj == 2) {
                                if (endPointsFoundData[(j + jj) * (sourceWidth) + (i + ii)] == EndPointsFound.RED_DUCK) {
                                    double ii2 = ii / 2.0;
                                    double jj2 = jj / 2.0;
                                    int var1ii2;
                                    int var2ii2;
                                    int var1jj2;
                                    int var2jj2;
                                    if (ii2 < -0.8 && ii2 > -1.2 && jj2 < -0.8 && jj2 > -1.2) {  //ii=-2; jj=-2
                                        SuppressedGData[0][(j - 1) * (sourceWidth) + (i - 1)] = ImprovedGData[0][(j - 1) * (sourceWidth) + (i - 1)];
                                        SuppressedGData[1][(j - 1) * (sourceWidth) + (i - 1)] = ImprovedGData[1][(j - 1) * (sourceWidth) + (i - 1)];
                                    } else if (ii2 > -0.8 && ii2 < -0.2 && jj2 < -0.8 && jj2 > -1.2) {  //ii=-1; jj=-2
                                        var1ii2 = -1;
                                        var2ii2 = 0;
                                        var1jj2 = -1;
                                        var2jj2 = -1;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 > -0.2 && ii2 < 0.2 && jj2 < -0.8 && jj2 > -1.2) {  //ii=0; jj=-2
                                        SuppressedGData[0][(j - 1) * (sourceWidth) + (i)] = ImprovedGData[0][(j - 1) * (sourceWidth) + (i)];
                                        SuppressedGData[1][(j - 1) * (sourceWidth) + (i)] = ImprovedGData[1][(j - 1) * (sourceWidth) + (i)];
                                    } else if (ii2 < 0.8 && ii2 > 0.2 && jj2 < -0.8 && jj2 > -1.2) { //ii=1; jj=-2
                                        var1ii2 = 0;
                                        var2ii2 = 1;
                                        var1jj2 = -1;
                                        var2jj2 = -1;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 > 0.8 && ii2 < 1.2 && jj2 < -0.8 && jj2 > -1.2) {  //ii=2; jj=-2
                                        SuppressedGData[0][(j - 1) * (sourceWidth) + (i+1)] = ImprovedGData[0][(j - 1) * (sourceWidth) + (i+1)];
                                        SuppressedGData[1][(j - 1) * (sourceWidth) + (i+1)] = ImprovedGData[1][(j - 1) * (sourceWidth) + (i+1)];
                                    } else if (ii2 < -0.8 && ii2 > -1.2 && jj2 > -0.8 && jj2 < -0.2) { //ii=-2; jj=-1
                                        var1ii2 = -1;
                                        var2ii2 = -1;
                                        var1jj2 = -1;
                                        var2jj2 = 0;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 < 1.2 && ii2 > 0.8 && jj2 > -0.8 && jj2 < -0.2) { //ii=2; jj=-1
                                        var1ii2 = 1;
                                        var2ii2 = 1;
                                        var1jj2 = -1;
                                        var2jj2 = 0;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 < -0.8 && ii2 > -1.2 && jj2 > -0.2 && jj2 < 0.2) {  //ii=-2; jj=0
                                        SuppressedGData[0][(j) * (sourceWidth) + (i-1)] = ImprovedGData[0][(j) * (sourceWidth) + (i-1)];
                                        SuppressedGData[1][(j) * (sourceWidth) + (i-1)] = ImprovedGData[1][(j) * (sourceWidth) + (i-1)];
                                    } else if (ii2 > 0.8 && ii2 < 1.2 && jj2 > -0.2 && jj2 < 0.2) {  //ii=2; jj=0
                                        SuppressedGData[0][(j) * (sourceWidth) + (i+1)] = ImprovedGData[0][(j) * (sourceWidth) + (i+1)];
                                        SuppressedGData[1][(j) * (sourceWidth) + (i+1)] = ImprovedGData[1][(j) * (sourceWidth) + (i+1)];
                                    } else if (ii2 > -1.2 && ii2 < -0.8 && jj2 < 0.8 && jj2 > 0.2) { //ii=-2; jj=1
                                        var1ii2 = -1;
                                        var2ii2 = -1;
                                        var1jj2 = 0;
                                        var2jj2 = 1;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 < 1.2 && ii2 > 0.8 && jj2 < 0.8 && jj2 > 0.2) { //ii=2; jj=1
                                        var1ii2 = 1;
                                        var2ii2 = 1;
                                        var1jj2 = 0;
                                        var2jj2 = 1;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 < -0.8 && ii2 > -1.2 && jj2 > 0.8 && jj2 < 1.2) {  //ii=-2; jj=2
                                        SuppressedGData[0][(j+1) * (sourceWidth) + (i-1)] = ImprovedGData[0][(j+1) * (sourceWidth) + (i-1)];
                                        SuppressedGData[1][(j+1) * (sourceWidth) + (i-1)] = ImprovedGData[1][(j+1) * (sourceWidth) + (i-1)];
                                    } else if (ii2 > -0.8 && ii2 < -0.2 && jj2 < 1.2 && jj2 > 0.8) { //ii=-1; jj=2
                                        var1ii2 = -1;
                                        var2ii2 = 0;
                                        var1jj2 = 1;
                                        var2jj2 = 1;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 > -0.2 && ii2 < 0.2 && jj2 > 0.8 && jj2 < 1.2) {  //ii=0; jj=2
                                        SuppressedGData[0][(j+1) * (sourceWidth) + (i)] = ImprovedGData[0][(j+1) * (sourceWidth) + (i)];
                                        SuppressedGData[1][(j+1) * (sourceWidth) + (i)] = ImprovedGData[1][(j+1) * (sourceWidth) + (i)];
                                    } else if (ii2 < 0.8 && ii2 > 0.2 && jj2 < 1.2 && jj2 > 0.8) { //ii=1; jj=2
                                        var1ii2 = 0;
                                        var2ii2 = 1;
                                        var1jj2 = 1;
                                        var2jj2 = 1;
                                        mafiosiProssimoFondare(SuppressedGData, ImprovedGData, sourceWidth, j, i, var1ii2, var2ii2, var1jj2, var2jj2);
                                    } else if (ii2 > 0.8 && ii2 < 1.2 && jj2 > 0.8 && jj2 < 1.2) {  //ii=2; jj=2
                                        SuppressedGData[0][(j+1) * (sourceWidth) + (i+1)] = ImprovedGData[0][(j+1) * (sourceWidth) + (i+1)];
                                        SuppressedGData[1][(j+1) * (sourceWidth) + (i+1)] = ImprovedGData[1][(j+1) * (sourceWidth) + (i+1)];
                                    }
                                }
                            }
                        }
                    }


                }
            }  /* endfor i*/
        } /* endfor j*/
    }

    // searching of maximal gradient magnitude from two neighbour pixel
    private void mafiosiProssimoFondare(double[][] SuppressedGData,
                                        double[][] ImprovedGData,
                                        int sourceWidth,
                                        int j,
                                        int i,
                                        int var1ii2,
                                        int var2ii2,
                                        int var1jj2,
                                        int var2jj2) {
        int loc1 = (j + var1jj2) * (sourceWidth) + (i + var1ii2);
        int loc2 = (j + var2jj2) * (sourceWidth) + (i + var2ii2);
        if (Double.isNaN(SuppressedGData[0][loc1]) && Double.isNaN(SuppressedGData[0][loc2])) {
            if (!Double.isNaN(ImprovedGData[0][loc1]) && !Double.isNaN(ImprovedGData[0][loc2])) {
                if (ImprovedGData[0][loc1] > ImprovedGData[0][loc2]) {
                    SuppressedGData[0][loc1] = ImprovedGData[0][loc1];
                    SuppressedGData[1][loc1] = ImprovedGData[1][loc1];
                } else {
                    SuppressedGData[0][loc2] = ImprovedGData[0][loc2];
                    SuppressedGData[1][loc2] = ImprovedGData[1][loc2];
                }
            } else {
                if (!Double.isNaN(ImprovedGData[0][loc1]) && Double.isNaN(ImprovedGData[0][loc2])) {
                    SuppressedGData[0][loc1] = ImprovedGData[0][loc1];
                    SuppressedGData[1][loc1] = ImprovedGData[1][loc1];
                } else {
                    if (Double.isNaN(ImprovedGData[0][loc1]) && !Double.isNaN(ImprovedGData[0][loc2])) {
                        SuppressedGData[0][loc2] = ImprovedGData[0][loc2];
                        SuppressedGData[1][loc2] = ImprovedGData[1][loc2];

                    }
                }

            }
        }
    }
}


