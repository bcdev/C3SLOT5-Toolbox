package com.bc.c3slot5.sandbank;


public class LaplaceFilter implements Filter {

    @Override
    public void compute(double[] sourceData,
                        int sourceWidth,
                        int sourceHeight,
                        int[] flagArray,
                        /*Tile targetBandFilter,*/
                        int kernelRadius) {

        int kernelSize =  kernelRadius * 2 + 1;
        int sourceLength = sourceWidth * sourceHeight;

        double[] preparedData = new double[sourceData.length];
        System.arraycopy(sourceData, 0, preparedData, 0, sourceData.length);


        double[][] kernelLaplace = makeConvolutionKernel(kernelSize);
        //int kernelRadius = FrontsOperator.laplaceFilterKernelRadius;

        Convolution Convolution = new Convolution(kernelLaplace, kernelRadius);
        double[][] laplaceData = Convolution.makeConvolution(sourceData, sourceWidth, sourceHeight, flagArray);

        for (int y = 0; y < sourceHeight; y++) {
            for (int x = 0; x < sourceWidth; x++) {
                preparedData[y * (sourceWidth) + x] = laplaceData[x][y];
            }
        }

    /*    FrontsOperator.makeFilledBand(preparedData, sourceWidth, sourceHeight,
                targetBandFilter, FrontsOperator.maxKernelRadius); */

        System.arraycopy(preparedData, 0, sourceData, 0, sourceData.length);
    }


    private double[][] makeConvolutionKernel(int kernelSize) {
        double[][] kernel = new double[kernelSize][kernelSize];


        if (kernelSize == 3) {
            double[][] laplaceMatrix3x3 = new double[][]{
                    new double[]{1., 1., 1.},
                    new double[]{1., -8., 1.},
                    new double[]{1., 1., 1.},
            };
            for (int j = 0; j < kernelSize; j++) {
                for (int i = 0; i < kernelSize; i++) {
                    kernel[j][i] = laplaceMatrix3x3[j][i];
                }
            }

        }  else if (kernelSize == 5) {
            double[][] laplaceMatrix5x5 = new double[][]{
                    new double[]{1., 1., 1., 1., 1.},
                    new double[]{1., 1., 1., 1., 1.},
                    new double[]{1., 1., -24., 1., 1.},
                    new double[]{1., 1., 1., 1., 1.},
                    new double[]{1., 1., 1., 1., 1.},
            };
            for (int j = 0; j < kernelSize; j++) {
                for (int i = 0; i < kernelSize; i++) {
                    kernel[j][i] = laplaceMatrix5x5[j][i];
                }
            }
        }  else {
            makeConvolutionKernel(5);
        }

/*
        Laplace 3x3:
            1  2  1
            2  4  2     * 1/16
            1  2  1

         Laplace 5x5:
            2   7   12    7   2
            7  31   52   31   7
           15  52  127   52  15   * 1/423
            7  31   52   31   7
            2   7   12    7   2

         Laplace 5x5:
            1	4	7	4	1
            4	20	33	20	4
            7	33	55	33	7    * 1/331
            4	20	33	20	4
            1	4	7	4	1

        Laplace 7x7:
            1	    12	    55	    90	    55	    12	    1
            12	    148	    665	    1097	665	    148	    12
            55	    665	    2981	4915	2981	665	    55
            90	    1097	4915	8103	4915	1097	90   * 1/50887
            55	    665	    2981	4915	2981	665	    55
            12	    148	    665	    1097	665	    148	    12
            1	    12	    55	    90	    55	    12	    1

        Laplace 9x9:
            1   2   4   8   16   8    4    2    1
            2   4   8   16  32   16   8    4    2
            4   8   16  32  64   32   16   8    4
            8   16  32  64  128  64   32   16   8
            16  32  64  128 256  128  64   32   16   * 1/2116
            8   16  32  64  128  64   32   16   8
            4   8   16  32  64   32   16   8    4
            2   4   8   16  32   16   8    4    2
            1   2   4   8   16   8    4    2    1
*/

        double grit = 0.;
        for (int j = 0; j < kernelSize; j++) {
            for (int i = 0; i < kernelSize; i++) {
                grit = grit + kernel[j][i];
            }
        }
        for (int j = 0; j < kernelSize; j++) {
            for (int i = 0; i < kernelSize; i++) {
                kernel[j][i] = kernel[j][i] / grit;
            }
        }

        return kernel;
    }
}

