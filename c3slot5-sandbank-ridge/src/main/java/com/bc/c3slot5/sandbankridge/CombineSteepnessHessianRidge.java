package com.bc.c3slot5.sandbankridge;

import org.esa.snap.core.gpf.Tile;

import java.awt.*;
import java.util.Arrays;

public class CombineSteepnessHessianRidge {


    public void combineResults(Rectangle targetRectangle,
                               int[] sourceDataSteep,
                               int[] sourceDataHessian,
                               int sourceWidth,
                               int sourceHeight,
                               Tile targetTileSandBanksBeltLinkedCombined,
                               int maxKernelRadius) {

        int[] sourceDataFinal = new int[sourceWidth * sourceHeight];
        Arrays.fill(sourceDataFinal, 0);

        for (int k = 0; k < sourceHeight * sourceWidth; k++) {
                sourceDataFinal[k] = sourceDataSteep[k] + sourceDataHessian[k];
        }
        SandbankRidgeOp.makeFilledBand(sourceDataFinal, targetRectangle,sourceWidth,sourceHeight,targetTileSandBanksBeltLinkedCombined,maxKernelRadius);
    }
}

