package com.bc.c3slot5.sandbank;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: michael
 * Date: 16.03.11
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class PlayFronts {

    public void compute(double[][] generalGradientArray,
                int [] frontIDArray,
                double[][]frontBeltArray,
                int []frontBeltIDArray,
                int[] flagArray,
                Rectangle targetRectangle) {

        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;
        int targetLength = targetHeight * targetWidth;
        //double[] frontsData = new double[sourceWidth * sourceHeight];
       // Arrays.fill(frontsArray, 0);
         int ID_counter = 0;
        double AbThreshold = 0.2;
        double temp;
        double gr_rad = Math.PI / 180.0;

        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                if (frontIDArray[k] > ID_counter) {
                    ID_counter = frontIDArray[k];
                }
            }
        }
        System.out.printf("The Number of the Fronts is: %d. \n", ID_counter);

        int[] neighbourNumber = new int[ID_counter + 1];
        for (int j = 1; j < targetHeight - 1; j++) {
            for (int i = 1; i < targetWidth - 1; i++) {
                int k = j * (targetWidth) + i;
                if (frontBeltIDArray[k] > 0) {
                    neighbourNumber = searchNeighbour(i, j, targetWidth, flagArray, frontBeltIDArray, neighbourNumber);
                }
            }
        }


        int[] frontLineSquare = new int[ID_counter+1];
        int[] frontBeltSquare = new int[ID_counter+1];
        int[] frontBeltTop = new int[ID_counter+1];     Arrays.fill(frontBeltTop, 10000);
        int[] frontBeltBottom = new int[ID_counter+1];  Arrays.fill(frontBeltBottom, -1);
        int[] frontBeltLeft = new int[ID_counter+1];    Arrays.fill(frontBeltLeft, 10000);
        int[] frontBeltRight = new int[ID_counter+1];   Arrays.fill(frontBeltRight, -1);
        double[] frontsLineTotalPower = new double[ID_counter+1];
        double[] frontsBeltTotalPower = new double[ID_counter+1];
        double[] frontsLineTotalDirection = new double[ID_counter+1];
        double[] frontsBeltTotalDirection = new double[ID_counter+1];
        double CloudValue = 0.0;
        double Threshold1 = 0.25;  //relevant area: (0.25 - 0.3) {max environment: (0.2 - 0.35)}
        //double[] frontsLineThreshold1Power = new double[ID_counter+1];
        double[] frontsBeltThreshold1Power = new double[ID_counter+1];
        int[] frontBeltThreshold1Square = new int[ID_counter+1];

        for (int j = 0; j < targetHeight; j++) {
            for (int i = 0; i < targetWidth; i++) {
                int k = j * (targetWidth) + i;
                if (j<frontBeltTop[frontBeltIDArray[k]]) {frontBeltTop[frontBeltIDArray[k]]=j;}
                if (i<frontBeltLeft[frontBeltIDArray[k]]) {frontBeltLeft[frontBeltIDArray[k]]=i;}
                if (j>frontBeltBottom[frontBeltIDArray[k]]) {frontBeltBottom[frontBeltIDArray[k]]=j;}
                if (i>frontBeltRight[frontBeltIDArray[k]]) {frontBeltRight[frontBeltIDArray[k]]=i;}
                frontLineSquare[frontIDArray[k]] ++;
                frontBeltSquare[frontBeltIDArray[k]] ++;
                frontsLineTotalPower[frontIDArray[k]]=frontsLineTotalPower[frontIDArray[k]]+generalGradientArray[0][k];
                frontsBeltTotalPower[frontBeltIDArray[k]]=frontsBeltTotalPower[frontBeltIDArray[k]]+frontBeltArray[0][k];
                frontsLineTotalDirection[frontIDArray[k]] = frontsLineTotalDirection[frontIDArray[k]] + Math.cos(gr_rad*generalGradientArray[1][k]);
                //System.out.printf("COS  %d, %d,   %f, %f \n", k, frontIDArray[k], Math.cos(gr_rad*generalGradientArray[1][k]), frontsLineTotalDirection[frontIDArray[k]]);
                frontsBeltTotalDirection[frontBeltIDArray[k]] = frontsBeltTotalDirection[frontBeltIDArray[k]] + Math.cos(gr_rad*frontBeltArray[1][k]);
                if (frontBeltArray[0][k] > Threshold1){
                   frontsBeltThreshold1Power[frontBeltIDArray[k]] = frontsBeltThreshold1Power[frontBeltIDArray[k]] + frontBeltArray[0][k];
                   frontBeltThreshold1Square[frontBeltIDArray[k]] ++;
                }
            }
        }
        for (int c = 0; c < ID_counter; c++) {
            temp = frontsLineTotalPower[c] / frontLineSquare[c];
            if (temp >= 0.1) {
                double dX = (double)(frontBeltRight[c] - frontBeltLeft[c]);
                double dY = (double)(frontBeltBottom[c] - frontBeltTop[c]);
                // ratio: (square under the front belt) through (the square of the environmental rectangle)
                double compactness = (double)frontBeltSquare[c]/(double)(dX*dY);
                double middleWidth = (double) frontLineSquare[c] / (double) frontBeltSquare[c];
                double beltPowerDensity = frontsBeltTotalPower[c] / frontBeltSquare[c];
                // diagonal of the environmental rectangle
                double expansion = Math.sqrt(dX*dX+dY*dY);

                double beltThreshold1PowerDensity = frontsBeltThreshold1Power[c] / frontBeltThreshold1Square[c];
                // Flachheit
                double evenness = (double)frontBeltThreshold1Square[c] / (double)frontBeltSquare[c];

                System.out.printf("ID %d, " +
                        "LineSqu %d, " +
                        "BeltSqu %d, " +
                        "MiddleWidth %f, " +
                        /*"LinePowDens %f, " + */
                        "BeltPowDens %f, " +
                        /*"cosLineMidDir %f, " +*/
                        /*"cosBeltMidDir %f, " +*/
                        /*"Border:  %d %d %d %d," +*/
                        /*"BeltThresh1PowDens %f, " +*/
                        "Evenness: %f, " +
                        "Expansion: %f, " +
                        "Compactness: %f, " +
                        "Neighbours: %d" +
                        " \n",
                        c,
                        frontLineSquare[c],
                        frontBeltSquare[c],
                        middleWidth,
                        /*temp,*/
                        beltPowerDensity,
                        /*frontsLineTotalDirection[c] / frontLineSquare[c],*/
                        /*frontsBeltTotalDirection[c] / frontBeltSquare[c],*/
                        /*frontLineTop[c], frontLineLeft[c], frontLineBottom[c], frontLineRight[c],*/
                        /*beltThreshold1PowerDensity,*/
                        evenness,
                        expansion,
                        compactness ,
                        neighbourNumber[c]
                );

                if(middleWidth < 0.4 && evenness > 0.3 && beltPowerDensity > 0.2 && expansion < 18 && compactness > 0.5){
                   CloudValue = 0.92;
                   System.out.printf("FrontID: %d   "+"CLOUD is very probably (probability >= %f) \n", c, CloudValue);
                } else if(middleWidth < 0.55 && evenness > 0.2 && beltPowerDensity > 0.15 && expansion < 22 && compactness > 0.4){
                   CloudValue = 0.75;
                   System.out.printf("FrontID: %d   "+"CLOUD is very probably (probability >= %f) \n", c, CloudValue);
                }  else if(middleWidth < 0.6 && evenness > 0.1 && beltPowerDensity > 0.18 && expansion < 40 && compactness > 0.25){
                   CloudValue = 0.65;
                   System.out.printf("FrontID: %d   "+"CLOUD is probably (probability >= %f) \n", c, CloudValue);
                }  else if(middleWidth < 0.7 && evenness > 0.1 && beltPowerDensity > 0.14 && expansion < 40 && compactness > 0.25){
                   CloudValue = 0.55;
                   System.out.printf("FrontID: %d   "+"CLOUD is probably (probability ≈ %f) \n", c, CloudValue);
                }  else if(middleWidth < 0.7 && evenness > 0.1 && beltPowerDensity > 0.2 && expansion < 40 && compactness > 0.7){
                   CloudValue = 0.5;
                   System.out.printf("FrontID: %d   "+"CLOUD is probably (probability >= %f) \n", c, CloudValue);
                }  else if(middleWidth < 0.7 && evenness > 0.1 && evenness > 0.1 && beltPowerDensity > 0.14 && expansion < 50 && compactness > 0.15){
                   CloudValue = 0.5;
                   System.out.printf("FrontID: %d   "+"CLOUD is probably (probability ≈ %f) \n", c, CloudValue);
                }  else if(middleWidth < 0.7 && evenness > 0.1 && beltPowerDensity > 0.1 && expansion < 18 && compactness > 0.25){
                   CloudValue = 0.5;
                   System.out.printf("ID: %d   "+"CLOUD is probably (probability ≈ %f) \n", c, CloudValue);
                }  else if(middleWidth < 0.65 && evenness > 0.05 && beltPowerDensity > 0.1 && expansion < 55 && compactness > 0.15){
                   CloudValue = 0.5;
                   System.out.printf("ID: %d   "+"CLOUD is probably (probability ≈ %f) \n", c, CloudValue);
                }
            }
        }
    }

    private int[] searchNeighbour(int i,
                                  int j,
                                  int targetWidth,
                                  int[] flagArray,
                                  int[] frontBeltIDArray,
                                  int[] neighbourNumber) {
        for (int jk = -1; jk <= 1; jk++) {
            for (int ik = -1; ik <= 1; ik++) {
                int kk = (j + jk) * (targetWidth) + (i + ik);
                if (flagArray[kk] == 11) {
                    neighbourNumber[frontBeltIDArray[j*targetWidth+i]]++;
                    if(frontBeltIDArray[j*targetWidth+i]==323 || frontBeltIDArray[j*targetWidth+i]==330){
                     System.out.printf("ID: %d   %d   %d   %d     %d   %d\n", frontBeltIDArray[j*targetWidth+i], neighbourNumber[frontBeltIDArray[j*targetWidth+i]], i, j, i+ik, j+jk);
                    }
                    return neighbourNumber;
                }
            }
        }
        return neighbourNumber;
    }

}

/*
MiddleWidth:  >0.3,   BeltPowDens:  >0.2,    Distance X,Y: <14,    Compactness:  >0.5   =>   Probability> 0.9   (if neighbour, then 0.95)
MiddleWidth:  >0.5,   BeltPowDens:  >0.15,   Distance X,Y: <20,    Compactness:  >0.4   =>   Probability> 0.7   (if neighbour, then 0.8)
MiddleWidth:  >0.4,   BeltPowDens:  >0.18,   Distance X,Y: <40,    Compactness:  >0.3   =>   Probability> 0.5   (if neighbour, then 0.7)
MiddleWidth:  >0.4,   BeltPowDens:  >0.14,   Distance X,Y: <40,    Compactness:  >0.25  =>   Probability> 0.3   (if neighbour, then 0.55)
MiddleWidth 0.543147, BeltPowDens 0.153854, Distance X,Y:  46.957428, Compactness: 0.194664
MiddleWidth 0.742857, BeltPowDens 0.113131, Distance X,Y:  13.038405, Compactness: 0.299145
*/