package com.bc.c3slot5.sandbankridge;

import com.bc.ceres.core.ProgressMonitor;
import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.util.ProductUtils;


import javax.media.jai.BorderExtenderConstant;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@OperatorMetadata(alias = "Sandbankridge",
        label = "C3S LOT5 S2 SandBank ridge",
        authors = "GK",
        copyright = "Brockmann Consult",
        version = "0.1")

public class SandbankRidgeOp extends Operator {

    private static final String TYPE_SUFFIX = "_sand_ridge";
    public static final int INT_NAN_VALUE = -9999;

    @SourceProduct()
    private Product sourceProduct;

    @Parameter(rasterDataNodeType = Band.class)
    private String sourceBandName;

    @Parameter(rasterDataNodeType = Band.class)
    private String flagBandName;


    //Detect lines parameters

//     @param sigma
//     //            A value which depends on the line width: sigma greater or equal to
//    //            width/(2*sqrt(3))
//     @param upperThresh
//     //           Upper hysteresis thresholds used in the linking algorithm (Depends
//     //            on the maximum contour brightness (greyvalue))
//     @param lowerThresh
//    //            Lower hysteresis thresholds used in the linking algorithm (Depends
//    //            on the minimum contour brightness (greyvalue)).
//     @param minLength
//     //           the min length
//     @param maxLength
//     //            the max length
//     @param isDarkLine
//     //            True if the line darker than the background
//     @param doCorrectPosition
//     //            Determines whether the line width and position correction should
//     //            be applied
//     @param doEstimateWidth
//     //           Determines whether the line width should be extracted
//     @param doExtendLine
//    //            Extends the detect lines to find more junction points




    @TargetProduct
    private Product targetProduct;


    private String targetBandNameGradientMagnitude = "GradientMagnitude";
    private String targetBandNameGradientDirection = "GradientDirection";
    private String targetBandNameFlag = "Flag_Cloud_Land_Ocean_NoData";
    private String targetBandNameSandBanksID = "SandBankID";
    private String targetBandNameSandBanksBeltMag = "SandBankBeltMag";
    private String targetBandNameSandBanksBeltDir = "SandBankBeltDir";
    private String targetBandNameSandBanksBelt = "SandBankBelt";
    private String targetBandNameSandBanksBeltLinked = "SandBankBeltLinked";
    private String targetBandNameSandBanksBeltMagHessian = "SandBankBeltMagHessian";
    private String targetBandNameSandBanksBeltDirHessian = "SandBankBeltDirHessian";
    private String targetBandNameSandBanksBeltHessian = "SandBankBeltHessian";
    private String targetBandNameSandBanksBeltLinkedHessian = "SandBankBeltLinkedHessian";

    private Band targetBandCopySourceBand;
    private Band targetBandGradientMagnitude;
    private Band targetBandGradientDirection;
    private Band targetBandFlag;
    private Band targetBandSandBanksBeltMag;
    private Band targetBandSandBanksBeltDir;
    private Band targetBandSandBanksBelt;
    private Band targetBandSandBanksBeltLinked;
    private Band targetBandSandBanksBeltMagHessian;
    private Band targetBandSandBanksBeltDirHessian;
    private Band targetBandSandBanksBeltHessian;
    private Band targetBandSandBanksBeltLinkedHessian;




    private String operator;
    private String filterGradient;
    private String algorithm;
    private String rounding;
    private double roundingInputData;
    private String hysteresis;

    private static final String SOBEL_OPERATOR = "Sobel Operator";
    private static final String SCHARR_OPERATOR = "Scharr Operator";
    private static final String CONTEXTUAL_MEDIAN_FILTER = "Contextualer Median Filter";
    private static final String GAUSS_FILTER = "Gaussian Filter";
    private static final String MEDIAN_FILTER = "Median Filter";
    private static final String LAPLACE_FILTER = "Laplace Filter";


    private static final String CANNY_HYSTERESIS = "Hysteresis according to Canny Algorithm";
    private static final String SIMPLE_HYSTERESIS = "Simple Hysteresis Algorithm";
    private static final String SIED_ALGORITHM = "SIED Algorithm";
    private static final String ENTROPY_ALGORITHM = "Entropy Algorithm";

    private static final String YES_ROUNDING = "YES";
    private static final String NO_ROUNDING = "NO";

    private double kernelEdgeValue;
    private double kernelCentreValue;
    private double weightingFactor;

    static final int fillKernelRadius = 1;
    static final int medianFilterKernelRadius = 2;
    static final int conMedianFilterKernelRadius = 2;
    static final int gaussFilterKernelRadius = 2;
    static final int laplaceFilterKernelRadius = 2;
    static final int convolutionFilterKernelRadius = 1;
    static final int maxKernelRadius = 30; //30;
    static final int minKernelRadius = 0;

    private double maxFrontBeltMagnitude = 0.;
    private double acceptableFrontBeltPixel = 0.025;
    static double thresholdRidgeDetection = 6.0;
    static int thresholdRidgeDetectionMax = 6;
    static int thresholdRidgeDetectionMin = 2;
    static double thresholdRidgeDetectionHessian = 3.0;
    static double thresholdRidgeDetectionHessianMax = 3.0;
    static double thresholdRidgeDetectionHessianMin = 1.0;



    /**********************************************************************************************/
    /************ Threshold Biomodality  (Unimpeachable Cayulas Thing = 0.7 !!!!!!!) **************/
    /*** ******************************************************************************************/
    static final double thresholdSegmentationGoodness = 0.7;


    @Override
    public void initialize() throws OperatorException {
        targetProduct = createTargetProduct();


        targetBandCopySourceBand = targetProduct.addBand(sourceBandName, ProductData.TYPE_FLOAT64);
        targetBandGradientMagnitude = targetProduct.addBand(targetBandNameGradientMagnitude, ProductData.TYPE_FLOAT64);
        targetBandGradientDirection = targetProduct.addBand(targetBandNameGradientDirection, ProductData.TYPE_FLOAT64);
        targetBandFlag = targetProduct.addBand(targetBandNameFlag, ProductData.TYPE_INT16);
        targetBandSandBanksBeltMag = targetProduct.addBand(targetBandNameSandBanksBeltMag, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBeltDir = targetProduct.addBand(targetBandNameSandBanksBeltDir, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBelt = targetProduct.addBand(targetBandNameSandBanksBelt, ProductData.TYPE_INT16);
        targetBandSandBanksBeltLinked = targetProduct.addBand(targetBandNameSandBanksBeltLinked, ProductData.TYPE_INT16);
        targetBandSandBanksBeltMagHessian = targetProduct.addBand(targetBandNameSandBanksBeltMagHessian, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBeltDirHessian = targetProduct.addBand(targetBandNameSandBanksBeltDirHessian, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBeltHessian = targetProduct.addBand(targetBandNameSandBanksBeltHessian, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBeltLinkedHessian = targetProduct.addBand(targetBandNameSandBanksBeltLinkedHessian, ProductData.TYPE_INT16);

        operator = SCHARR_OPERATOR;
        filterGradient = CONTEXTUAL_MEDIAN_FILTER;
        rounding = NO_ROUNDING;
        roundingInputData = 0.025;
        hysteresis = SIMPLE_HYSTERESIS;

        if (SOBEL_OPERATOR.equals(operator)) {
            kernelEdgeValue = 1.0;
            kernelCentreValue = 2.0;
        }
        if (SCHARR_OPERATOR.equals(operator)) {
            kernelEdgeValue = 3.0;
            kernelCentreValue = 10.0;
        }

        weightingFactor = 2 * (2 * kernelEdgeValue + kernelCentreValue);

    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {

        Rectangle sourceRectangle = new Rectangle(targetRectangle);
        sourceRectangle.grow(maxKernelRadius, maxKernelRadius);

        BorderExtenderConstant borderExtenderNaN = new BorderExtenderConstant(new double[]{Double.NaN});
        Tile sourceTile = getSourceTile(sourceProduct.getBand(sourceBandName), sourceRectangle, borderExtenderNaN);
        Tile flagTile = getSourceTile(sourceProduct.getBand(flagBandName), sourceRectangle, borderExtenderNaN);

        Tile targetTileCopySourceBand = targetTiles.get(targetBandCopySourceBand);
        Tile targetTileGradientMagnitude = targetTiles.get(targetBandGradientMagnitude);
        Tile targetTileGradientDirection = targetTiles.get(targetBandGradientDirection);
        Tile targetTileBandFlag = targetTiles.get(targetBandFlag);
        Tile targetTileSandBanksBelt = targetTiles.get(targetBandSandBanksBelt);
        Tile targetTileSandBanksBeltMag = targetTiles.get(targetBandSandBanksBeltMag);
        Tile targetTileSandBanksBeltDir = targetTiles.get(targetBandSandBanksBeltDir);
        Tile targetTileSandBanksBeltLinked = targetTiles.get(targetBandSandBanksBeltLinked);
        Tile targetTileSandBanksBeltHessian = targetTiles.get(targetBandSandBanksBeltHessian);
        Tile targetTileSandBanksBeltMagHessian = targetTiles.get(targetBandSandBanksBeltMagHessian);
        Tile targetTileSandBanksBeltDirHessian = targetTiles.get(targetBandSandBanksBeltDirHessian);
        Tile targetTileSandBanksBeltLinkedHessian = targetTiles.get(targetBandSandBanksBeltLinkedHessian);



        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        final double[] sourceArray = sourceTile.getSamplesDouble();
        final int[] flagArray = flagTile.getSamplesInt();

        PreparingOfSourceBand preparedSourceBand = new PreparingOfSourceBand();
        preparedSourceBand.preparedOfSourceBand(sourceArray,
                sourceWidth,
                sourceHeight,
                flagArray);

        makeFilledBand(sourceArray, sourceWidth, sourceHeight, targetTileCopySourceBand, maxKernelRadius);

        // Pepraring Source Band - Land/Cloud detection
        // Filling
        makeFilledBand(flagArray, sourceWidth, sourceHeight, targetTileBandFlag, SandbankRidgeOp.maxKernelRadius);
        // copy source data for histogram method

        double[] ridgeDetectorSourceData = new double[sourceArray.length];
        System.arraycopy(sourceArray, 0, ridgeDetectorSourceData, 0, sourceArray.length);

        double[] ridgeDetectorSourceDataHessian = new double[sourceArray.length];
        System.arraycopy(sourceArray, 0, ridgeDetectorSourceDataHessian, 0, sourceArray.length);


        /**************************************************************************/
        /************************** Gradient Method   *****************************/
        /**************************************************************************/

        // Filtering Source Band for Gradient Method
        // Belkin _ Contextual Median Filtering
        if (CONTEXTUAL_MEDIAN_FILTER.equals(filterGradient)) {
            Filter filter = new ContextualMedianFilter();
            filter.compute(sourceArray,
                    sourceWidth,
                    sourceHeight,
                    flagArray,
                    conMedianFilterKernelRadius);
        } else if (MEDIAN_FILTER.equals(filterGradient)) {
            Filter filter = new MedianFilter();
            filter.compute(sourceArray,
                    sourceWidth,
                    sourceHeight,
                    flagArray,
                    medianFilterKernelRadius);
        } else if (GAUSS_FILTER.equals(filterGradient)) {
            Filter filter = new GaussFilter();
            filter.compute(sourceArray,
                    sourceWidth,
                    sourceHeight,
                    flagArray,
                    gaussFilterKernelRadius);
        } else if (LAPLACE_FILTER.equals(filterGradient)) {
            Filter filter = new LaplaceFilter();
            filter.compute(sourceArray,
                    sourceWidth,
                    sourceHeight,
                    flagArray,
                    laplaceFilterKernelRadius);
        }

        /* Convolution with Gradient-Operator */
        GradientOperator gradient = new GradientOperator();
        double[][] gradientSourceData = gradient.computeGradient(sourceArray,
                sourceWidth,
                sourceHeight,
                flagArray,
                targetTileGradientMagnitude,
                targetTileGradientDirection,
                kernelEdgeValue,
                kernelCentreValue,
                weightingFactor);

        /**************************************************************************/
        /************************** Ridge Method 1  *****************************/
        /**************************************************************************/

//        Filter filter = new ContextualMedianFilter();
//        filter.compute(ridgeDetectorSourceData,
//                sourceWidth,
//                sourceHeight,
//                flagArray,
//                conMedianFilterKernelRadius);

        /* LineDetector */
        LineDetector lineDetector = new LineDetector();
        int[][] linesSourceData = lineDetector.detectLines(ridgeDetectorSourceData, gradientSourceData,
               sourceHeight,
               sourceWidth,
               targetTileSandBanksBelt,
               targetTileSandBanksBeltMag,
               targetTileSandBanksBeltDir);

        EdgeLinkingHysteresis edgeLinkingOfSourceBand = new EdgeLinkingHysteresis();
        int[] edgeLinkedData = edgeLinkingOfSourceBand.edgeLinkingOfSourceBand(
                linesSourceData,
                sourceWidth,
                sourceHeight);

        makeFilledBand(edgeLinkedData, sourceWidth, sourceHeight, targetTileSandBanksBeltLinked, maxKernelRadius);


        /**************************************************************************/
        /************************** Ridge Method 1  *****************************/
        /**************************************************************************/

//        Filter filter = new ContextualMedianFilter();
//        filter.compute(ridgeDetectorSourceData,
//                sourceWidth,
//                sourceHeight,
//                flagArray,
//                conMedianFilterKernelRadius);

        /* LineDetector */
        LineDetectorHessian lineDetectorHessian = new LineDetectorHessian();
        double[] linesSourceDataHessian = lineDetectorHessian.detectLines(ridgeDetectorSourceDataHessian,
                flagArray,
                sourceHeight,
                sourceWidth,
                kernelEdgeValue,
                kernelCentreValue,
                targetTileSandBanksBeltHessian);

        EdgeLinkingHysteresisHessian edgeLinkingOfSourceBandHessian = new EdgeLinkingHysteresisHessian();
        int[] edgeLinkedDataHessian = edgeLinkingOfSourceBandHessian.edgeLinkingOfSourceBand(
                linesSourceDataHessian,
                gradientSourceData,
                sourceWidth,
                sourceHeight,
                targetTileSandBanksBeltMagHessian,
                targetTileSandBanksBeltDirHessian);

        makeFilledBand(edgeLinkedDataHessian, sourceWidth, sourceHeight, targetTileSandBanksBeltLinked, maxKernelRadius);

    }



    private Product createTargetProduct() {
        String productType = sourceProduct.getProductType();
        String productName = sourceProduct.getName();
        if (!productType.endsWith(TYPE_SUFFIX)) {
            productType = productType + TYPE_SUFFIX;
            productName = productName + TYPE_SUFFIX;
        }
        Product product = new Product(productName, productType, sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        if (sourceProduct.getSceneRasterWidth() > 1000 && sourceProduct.getSceneRasterHeight() > 1000) {
            product.setPreferredTileSize(500, 500);
        } else {
            product.setPreferredTileSize(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        }
        /*Copies all properties from source product to the target product.*/
        ProductUtils.copyProductNodes(sourceProduct, product);

        return product;
    }


    static void extractSubArray(int sourceWidth,
                                int sourceHeight,
                                int targetWidth,
                                int index,
                                double[][] sourceData,
                                double[] targetData) {
        for (int j = SandbankRidgeOp.maxKernelRadius; j < sourceHeight - SandbankRidgeOp.maxKernelRadius; j++) {
            for (int i = SandbankRidgeOp.maxKernelRadius; i < sourceWidth - SandbankRidgeOp.maxKernelRadius; i++) {
                int k = j * (sourceWidth) + i;
                int l = (j - SandbankRidgeOp.maxKernelRadius) * targetWidth + (i - SandbankRidgeOp.maxKernelRadius);
                targetData[l] = sourceData[index][k];
            }
        }
    }

    static void extractSubArray(int sourceWidth,
                                int sourceHeight,
                                int targetWidth,
                                double[] sourceData,
                                double[] targetData) {
        for (int j = SandbankRidgeOp.maxKernelRadius; j < sourceHeight - SandbankRidgeOp.maxKernelRadius; j++) {
            for (int i = SandbankRidgeOp.maxKernelRadius; i < sourceWidth - SandbankRidgeOp.maxKernelRadius; i++) {
                int k = j * (sourceWidth) + i;
                int l = (j - SandbankRidgeOp.maxKernelRadius) * targetWidth + (i - SandbankRidgeOp.maxKernelRadius);
                targetData[l] = sourceData[k];
            }
        }
    }

    static int computeValuesCount(int countWidth,
                                  int countHeight,
                                  double[] countData) {
        int counter = 0;
        for (int j = SandbankRidgeOp.maxKernelRadius; j < countHeight - SandbankRidgeOp.maxKernelRadius; j++) {
            for (int i = SandbankRidgeOp.maxKernelRadius; i < countWidth - SandbankRidgeOp.maxKernelRadius; i++) {
                int k = j * (countWidth) + i;
                if (!Double.isNaN(countData[k])) {
                    counter++;
                }
            }
        }
        return counter;
    }

    static void makeFilledBand(double[] inputData,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int mkr) {
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x - mkr, y - mkr, inputData[y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(double[][] inputData,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand1,
                               Tile targetTileOutputBand2,
                               int mkr) {
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand1.setSample(x - mkr, y - mkr, inputData[0][y * (inputDataWidth) + x]);
                targetTileOutputBand2.setSample(x - mkr, y - mkr, inputData[1][y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(double[][] inputData,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int index,
                               int mkr) {
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x - mkr, y - mkr, inputData[index][y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(int[][] inputData,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int index,
                               int mkr) {
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x - mkr, y - mkr, inputData[index][y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(int[] inputData,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int mkr) {
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x - mkr, y - mkr, inputData[y * (inputDataWidth) + x]);
            }
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(SandbankRidgeOp.class);
        }
    }
}
