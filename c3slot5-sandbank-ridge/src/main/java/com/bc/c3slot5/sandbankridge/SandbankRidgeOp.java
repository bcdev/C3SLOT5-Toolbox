package com.bc.c3slot5.sandbankridge;

import com.bc.ceres.core.ProgressMonitor;
import org.apache.commons.lang.ObjectUtils;
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
import java.util.Arrays;
import java.util.Map;


@OperatorMetadata(alias = "Sandbankridge",
        label = "C3S LOT5 S2 SandBank ridge",
        authors = "GK",
        copyright = "Brockmann Consult GmbH",
        version = "0.7-SNAPSHOT")

public class SandbankRidgeOp extends Operator {

    private static final String TYPE_SUFFIX = "_sand_ridge";
    public static final int INT_NAN_VALUE = -9999;

    @SourceProduct()
    private Product sourceProduct;

    @Parameter(rasterDataNodeType = Band.class)
    private String sourceBandName;

    @Parameter(rasterDataNodeType = Band.class, description = "optional")
    private String flagBandName;


    @Parameter(defaultValue = "0.5",
            label = " Ridge Magnitude Threshold - Hessian approach",
            description = " Ridge Magnitude Threshold - Hessian approach ")
    private double nonMaxSuppressionThresholdHessian;

    @Parameter(defaultValue = "9",
            label = " Ridge Magnitude Threshold - Steepness approach ",
            description = "  Ridge Magnitude Threshold - Steepness approach ")
    private int thresholdRidgeDetection;


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
    private String targetBandNameSandBanksBeltLinkedCombined = "SandBankBeltLinkedCombined";

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

    private Band targetBandSandBanksBeltLinkedCombined;


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

    private static final String SIMPLE_HYSTERESIS = "Simple Hysteresis Algorithm";
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
    static final int nonMaxSuppressionKernelRadius = 1;
    static final int maxKernelRadius = 10; //30;
    static final int minKernelRadius = 0;

    private double maxFrontBeltMagnitude = 0.;
    private double acceptableFrontBeltPixel = 0.025;

    private int thresholdRidgeDetectionMax;
    private int thresholdRidgeDetectionMin;


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
        targetBandSandBanksBeltLinkedCombined = targetProduct.addBand(targetBandNameSandBanksBeltLinkedCombined, ProductData.TYPE_INT16);

        operator = SCHARR_OPERATOR;
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
        Tile flagTile = null;
        if (flagBandName != null) {
            flagTile = getSourceTile(sourceProduct.getBand(flagBandName), sourceRectangle, borderExtenderNaN);
        }
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
        Tile targetTileSandBanksBeltLinkedCombined = targetTiles.get(targetBandSandBanksBeltLinkedCombined);


        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceRectangle.width * sourceRectangle.height;

        thresholdRidgeDetectionMax = thresholdRidgeDetection;
        thresholdRidgeDetectionMin = thresholdRidgeDetection - (int) Math.floor(0.25 * thresholdRidgeDetection);


        final double[] sourceArray = sourceTile.getSamplesDouble();
        final int[] flagArray;
        if (flagTile != null) {
            flagArray = flagTile.getSamplesInt();
        } else {
            flagArray = new int[sourceLength];
            Arrays.fill(flagArray, 0);
        }
        PreparingOfSourceBand preparedSourceBand = new PreparingOfSourceBand();
        preparedSourceBand.preparedOfSourceBand(sourceArray,
                sourceWidth,
                sourceHeight,
                flagArray);

        makeFilledBand(sourceArray,targetRectangle, sourceWidth, sourceHeight, targetTileCopySourceBand, maxKernelRadius);

        // Pepraring Source Band - Land/Cloud detection
        // Filling
        makeFilledBand(flagArray, targetRectangle, sourceWidth, sourceHeight, targetTileBandFlag, SandbankRidgeOp.maxKernelRadius);
        // copy source data for histogram method


        /**************************************************************************/
        /************************** Gradient Calculation   *****************************/
        /**************************************************************************/

        /* Convolution with Gradient-Operator */
        GradientOperator gradient = new GradientOperator();
        double[][] gradientSourceData = gradient.computeGradient(targetRectangle,sourceArray,
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

        double[] ridgeDetectorSourceData = new double[sourceArray.length];
        System.arraycopy(sourceArray, 0, ridgeDetectorSourceData, 0, sourceArray.length);

        double[] ridgeDetectorSourceDataHessian = new double[sourceArray.length];
        for (int k = 0; k < sourceArray.length; k++) {
            ridgeDetectorSourceDataHessian[k] = sourceArray[k] * -1.0;
        }
        //System.arraycopy(sourceArray, 0, ridgeDetectorSourceDataHessian, 0, sourceArray.length);


        Filter filter = new ContextualMedianFilter();
        filter.compute(ridgeDetectorSourceData,
                sourceWidth,
                sourceHeight,
                flagArray,
                conMedianFilterKernelRadius);
        String filterType;
        filterType = CONTEXTUAL_MEDIAN_FILTER;

        /* LineDetector - linesData[1][..] = countsData; linesData[0][..] = 0 or 1*/
        LineDetector lineDetector = new LineDetector();
        int[][] linesSourceData = lineDetector.detectLines(targetRectangle,
                ridgeDetectorSourceData,
                sourceHeight,
                sourceWidth,
                thresholdRidgeDetection,
                targetTileSandBanksBelt);

        EdgeLinkingHysteresis edgeLinkingOfSourceBand = new EdgeLinkingHysteresis();
        int[] edgeLinkedData = edgeLinkingOfSourceBand.edgeLinkingOfSourceBand(targetRectangle,
                linesSourceData,
                gradientSourceData,
                sourceWidth,
                sourceHeight,
                thresholdRidgeDetection,
                thresholdRidgeDetectionMax,
                thresholdRidgeDetectionMin,
                targetTileSandBanksBeltMag,
                targetTileSandBanksBeltDir);

        makeFilledBand(edgeLinkedData,targetRectangle, sourceWidth, sourceHeight, targetTileSandBanksBeltLinked, maxKernelRadius);


        /**************************************************************************/
        /************************** Ridge Method 2  *****************************/
        /**************************************************************************/

        Filter filterHessian = new GaussFilter();
        filterHessian.compute(ridgeDetectorSourceDataHessian,
                sourceWidth,
                sourceHeight,
                flagArray,
                gaussFilterKernelRadius);
        String filterTypeHessian;
        filterTypeHessian = GAUSS_FILTER;
        String filterTypeHessianUsed;
        filterTypeHessianUsed = GAUSS_FILTER;
        /* LineDetector */
        LineDetectorHessian lineDetectorHessian = new LineDetectorHessian();
        double[][] linesSourceDataHessian = lineDetectorHessian.detectLines(targetRectangle,
                ridgeDetectorSourceDataHessian,
                flagArray,
                sourceHeight,
                sourceWidth,
                nonMaxSuppressionThresholdHessian,
                kernelEdgeValue,
                kernelCentreValue,
                targetTileSandBanksBeltHessian,
                filterTypeHessian,
                filterTypeHessianUsed);

        EdgeLinkingHysteresisHessian edgeLinkingOfSourceBandHessian = new EdgeLinkingHysteresisHessian();
        int[] edgeLinkedDataHessian = edgeLinkingOfSourceBandHessian.edgeLinkingOfSourceBand(targetRectangle,
                linesSourceDataHessian,
                gradientSourceData,
                sourceWidth,
                sourceHeight,
                targetTileSandBanksBeltMagHessian,
                targetTileSandBanksBeltDirHessian);

        makeFilledBand(edgeLinkedDataHessian,targetRectangle, sourceWidth, sourceHeight, targetTileSandBanksBeltLinkedHessian, maxKernelRadius);


        CombineSteepnessHessianRidge combinedSteepnessHessian = new CombineSteepnessHessianRidge();
        combinedSteepnessHessian.combineResults(targetRectangle,
                edgeLinkedData,
                edgeLinkedDataHessian,
                sourceWidth,
                sourceHeight,
                targetTileSandBanksBeltLinkedCombined,
                maxKernelRadius);
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
                               Rectangle targetRectangle,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int mkr) {
        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x + xLocation - mkr, y + yLocation - mkr, inputData[y * (inputDataWidth) + x]);
            }
        }
    }


    static void makeFilledBand(double[][] inputData,
                               Rectangle targetRectangle,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand1,
                               Tile targetTileOutputBand2,
                               int mkr) {
        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand1.setSample(x + xLocation - mkr, y + yLocation - mkr, inputData[0][y * (inputDataWidth) + x]);
                targetTileOutputBand2.setSample(x + xLocation - mkr, y + yLocation - mkr, inputData[1][y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(double[][] inputData,
                               Rectangle targetRectangle,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int index,
                               int mkr) {
        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x + xLocation - mkr, y + yLocation - mkr, inputData[index][y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(int[][] inputData,
                               Rectangle targetRectangle,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int index,
                               int mkr) {
        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x + xLocation - mkr, y + yLocation - mkr, inputData[index][y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(int[] inputData,
                               Rectangle targetRectangle,
                               int inputDataWidth,
                               int inputDataHeight,
                               Tile targetTileOutputBand,
                               int mkr) {
        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x + xLocation - mkr, y + yLocation - mkr, inputData[y * (inputDataWidth) + x]);
            }
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(SandbankRidgeOp.class);
        }
    }
}
