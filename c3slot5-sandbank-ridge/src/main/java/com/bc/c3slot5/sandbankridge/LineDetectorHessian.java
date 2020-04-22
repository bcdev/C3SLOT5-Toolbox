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
public class LineDetectorHessian {

    static final int RED_DUCK = 255;

    /**
     * Detect lines.
     *
     * @return An array with lines
     */

    public double[][] detectLines(double[] sourceArray,
                                int[] flagArray,
                                int sourceHeight,
                                int sourceWidth,
                                double kernelEdgeValue,
                                double kernelCentreValue,
                                Tile targetTileSandBanksBeltHessian) {

        int sourceLength = sourceWidth * sourceHeight;

        double[][] kernelGradient3x3_Y = make3x3ConvolutionKernel(kernelEdgeValue, kernelCentreValue);
        double[][] kernelGradient3x3_X = make3x3TransposeConvolutionKernel(kernelGradient3x3_Y);

        double[][] gradientData = new double[2][sourceLength];
        int kernelRadius = SandbankRidgeOp.convolutionFilterKernelRadius;

        Convolution xConvolution = new Convolution(kernelGradient3x3_X, kernelRadius);
        Convolution yConvolution = new Convolution(kernelGradient3x3_Y, kernelRadius);

        double[][] xData = xConvolution.makeConvolution(sourceArray, sourceWidth, sourceHeight, flagArray);
        double[][] yData = yConvolution.makeConvolution(sourceArray, sourceWidth, sourceHeight, flagArray);

        double[] hxData = new double[sourceLength];
        double[] hyData = new double[sourceLength];
        double[] majorEigenValueHessian = new double[sourceLength];
        Arrays.fill(majorEigenValueHessian,Double.NaN);

        for (int y = 0; y < sourceHeight; y++) {
            for (int x = 0; x < sourceWidth; x++) {
                hxData[y * (sourceWidth) + x] = xData[x][y];
                hyData[y * (sourceWidth) + x] = yData[x][y];
            }
        }

        double[][] xxData = xConvolution.makeConvolution(hxData, sourceWidth, sourceHeight, flagArray);
        double[][] yyData = yConvolution.makeConvolution(hyData, sourceWidth, sourceHeight, flagArray);
        double[][] xyData = yConvolution.makeConvolution(hxData, sourceWidth, sourceHeight, flagArray);

        double valueIntermediate;
        for (int y = 0; y < sourceHeight; y++) {
            for (int x = 0; x < sourceWidth; x++) {
                valueIntermediate = (Math.pow(xxData[x][y], 2) + 4. * Math.pow(xyData[x][y], 2) -
                                2 * xxData[x][y] * yyData[x][y] + Math.pow(yyData[x][y], 2));
                if (valueIntermediate>=0) {
                    majorEigenValueHessian[y * (sourceWidth) + x] =
                            0.5 * (xxData[x][y] + yyData[x][y] + Math.sqrt(valueIntermediate));
                }
            }
        }

        // standard thinning method according to Canny - disadvantage: lags
        NonMaximumSuppressionHessian majorEigenValueHessianNonMax = new NonMaximumSuppressionHessian();
        double[][] majorEigenValueHessianData = majorEigenValueHessianNonMax.nonMaxSuppressionOfSourceBand(
                majorEigenValueHessian,
                sourceWidth,
                sourceHeight);


        SandbankRidgeOp.makeFilledBand(majorEigenValueHessianData, sourceWidth, sourceHeight,
                targetTileSandBanksBeltHessian, 1,SandbankRidgeOp.maxKernelRadius);

        return majorEigenValueHessianData;
    }


    private double[][] make3x3TransposeConvolutionKernel
            (
                    double[][] kernel3x3_Y) {
        double[][] kernel3x3_X = new double[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                kernel3x3_X[j][i] = kernel3x3_Y[i][j];
            }
        }
        return kernel3x3_X;
    }

    private double[][] make3x3ConvolutionKernel
            (
                    double kernelEdgeValue,
                    double kernelCentreValue) {
        double[][] kernel3x3_Y = new double[3][3];


        kernel3x3_Y[0][0] = kernelEdgeValue;
        kernel3x3_Y[1][0] = kernelCentreValue;
        kernel3x3_Y[2][0] = kernelEdgeValue;
        kernel3x3_Y[0][1] = 0.0;
        kernel3x3_Y[1][1] = 0.0;
        kernel3x3_Y[2][1] = 0.0;
        kernel3x3_Y[0][2] = -kernelEdgeValue;
        kernel3x3_Y[1][2] = -kernelCentreValue;
        kernel3x3_Y[2][2] = -kernelEdgeValue;

        return kernel3x3_Y;

    }


}