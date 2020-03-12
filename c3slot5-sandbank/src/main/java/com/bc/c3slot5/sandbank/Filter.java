package com.bc.c3slot5.sandbank;

public interface Filter {

    void compute(double[] sourceData,
                 int sourceWidth,
                 int sourceHeight,
                 int[] flagArray,
                 int filterKernelRadius);
}

