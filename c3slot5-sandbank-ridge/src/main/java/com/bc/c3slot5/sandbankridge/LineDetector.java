/*
 * #%L
 * Ridge Detection plugin for ImageJ
 * %%
 * Copyright (C) 2014 - 2015 Thorsten Wagner (ImageJ java plugin), 1996-1998 Carsten Steger (original C code), 1999 R. Balasubramanian (detect lines code to incorporate within GRASP)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package com.bc.c3slot5.sandbankridge;


import org.esa.snap.core.gpf.Tile;

import java.util.Arrays;

/**
 * The Class LineDetector.
 */
public class LineDetector {

    static final int RED_DUCK = 255;

    /**
     * Detect lines.
     *
     * @return An array with lines
     */

    public int[][] detectLines(double[] sourceArray,
                             double[][] gradientSourceData,
                             int sourceHeight,
                             int sourceWidth,
                             Tile targetTileSandBanksBelt,
                             Tile targetTileSandBanksBeltMag,
                             Tile targetTileSandBanksBeltDir) {

        int sourceLength = sourceWidth * sourceHeight;

        double[] preparedData = new double[sourceArray.length];
        System.arraycopy(sourceArray, 0, preparedData, 0, sourceArray.length);

        int[][] linesData = new int[2][sourceArray.length];
        double[][] gradientLinesData= new double[2][sourceArray.length];
        int[] countsData = new int[sourceArray.length];
        Arrays.fill(countsData, 0);


        for (int j = 0; j < sourceHeight - 1; j++) {
            for (int i = 0; i < sourceWidth - 1; i++) {
                linesData[0][(j) * (sourceWidth) + (i)] = 0;
                linesData[1][(j) * (sourceWidth) + (i)] = 0;
                preparedData[(j) * (sourceWidth) + (i)]= Math.pow(preparedData[(j) * (sourceWidth) + (i)], 0.25);
            }
        }

        int oldI;
        int oldJ;
        int newI;
        int newJ;
        int[] values = new int[2];
        int[] valuesXrun = new int[2];


        // TODO more than one Maximum and NaN
        for (int j = 1; j < sourceHeight - 1; j++) {
            for (int i = 1; i < sourceWidth - 1; i++) {
                oldI = i;
                oldJ = j;
                values = identifyRidges(sourceHeight, sourceWidth, preparedData, j, i);
                newI = values[0];
                newJ = values[1];
//                System.out.printf("!!!!!!!!!!!!!!!LineDetector   \n");
//                System.out.printf("!!!!!!!!!!!!!!!!!  eins entdeckt:  %d  %d  %d  %d \n", i, newI, j, newJ);

                for (int k = 0; k < Math.max(sourceHeight,sourceWidth) - 2; k++) {
                    if (preparedData[(newJ) * (sourceWidth) + (newI)] > preparedData[(oldJ) * (sourceWidth) + (oldI)]) {
                        countsData[(newJ) * (sourceWidth) + (newI)] = countsData[(newJ) * (sourceWidth) + (newI)] + 1;
                        valuesXrun = identifyRidges(sourceHeight, sourceWidth, preparedData, newJ, newI);
                        oldI = newI;
                        oldJ = newJ;
                        newI = valuesXrun[0];
                        newJ = valuesXrun[1];

                    } else {
                        break;
                    }
                }
            }
        }

        for (int j = 1; j < sourceHeight - 1; j++) {
            for (int i = 1; i < sourceWidth - 1; i++) {

                gradientLinesData[0][j * (sourceWidth) + i] = 0.0;
                gradientLinesData[1][j * (sourceWidth) + i] = 0.0;
                linesData[1][(j) * (sourceWidth) + (i)] = countsData[(j) * (sourceWidth) + (i)];
                if (countsData[(j) * (sourceWidth) + (i)] > SandbankRidgeOp.thresholdRidgeDetection) {
                    linesData[0][(j) * (sourceWidth) + (i)] = 1;
                    gradientLinesData[0][j * (sourceWidth) + i] = gradientSourceData[0][j * (sourceWidth) + i];
                    gradientLinesData[1][j * (sourceWidth) + i] = gradientSourceData[1][j * (sourceWidth) + i];
                }
            }
        }

        SandbankRidgeOp.makeFilledBand(gradientLinesData, sourceWidth, sourceHeight,
                targetTileSandBanksBeltMag, targetTileSandBanksBeltDir, SandbankRidgeOp.maxKernelRadius);

        SandbankRidgeOp.makeFilledBand(countsData, sourceWidth, sourceHeight,
                targetTileSandBanksBelt, SandbankRidgeOp.maxKernelRadius);

        return linesData;
    }

    private int[] identifyRidges(int sourceHeight, int sourceWidth, double[] preparedData, int j, int i) {
        int[] values = new int[2];
        int newI = i;
        int newJ = j;
        double maxValue = Double.MIN_VALUE;
        for (int jj = -1; jj <= 1; jj++) {
            for (int ii = -1; ii <= 1; ii++) {
                int kk = j + jj;
                int pp = i + ii;
                if (kk >= 0 && kk < sourceHeight - 1 && pp >= 0 && pp < sourceWidth) {
                    if ((preparedData[(kk) * (sourceWidth) + (pp)]- preparedData[(j) * (sourceWidth) + (i)]) > maxValue) {
                        maxValue = preparedData[(kk) * (sourceWidth) + (pp)];
                        newI = pp;
                        newJ = kk;
                    }
                }
            }
        }
        values[0] = newI;
        values[1] = newJ;

        return values;
    }
}