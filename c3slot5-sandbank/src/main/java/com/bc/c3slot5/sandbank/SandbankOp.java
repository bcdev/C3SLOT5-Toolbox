package com.bc.c3slot5.sandbank;

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


@OperatorMetadata(alias = "Sandbank",
        label = "C3S LOT5 S2 SandBank",
        authors = "GK",
        copyright = "Brockmann Consult",
        version = "0.1")

public class SandbankOp extends Operator {

    private static final String TYPE_SUFFIX = "_sand";
    public static final int INT_NAN_VALUE = -9999;

    @SourceProduct()
    private Product sourceProduct;

    @Parameter(rasterDataNodeType = Band.class)
    private String sourceBandName;

    @Parameter(rasterDataNodeType = Band.class)
    private String flagBandName;

    @TargetProduct
    private Product targetProduct;

    private String targetBandNameGradientMagnitude = "GradientMagnitude";
    private String targetBandNameGradientDirection = "GradientDirection";
    private String targetBandNameFlag = "Flag_Cloud_Land_Ocean_NoData";
    private String targetBandNameFinalSandBankssMag = "Final Fronts - Magnitude";
    private String targetBandNameFinalSandBankssDir = "Final Fronts - Direction";
    private String targetBandNameSandBanksID = "SandBankID";
    private String targetBandNameSandBanksBeltMag = "SandBankBeltMag";
    private String targetBandNameSandBanksBeltDir = "SandBankBeltDir";
    private String targetBandNameSandBanksBeltID = "SandBankBeltID";

    private Band targetBandCopySourceBand;
    private Band targetBandGradientMagnitude;
    private Band targetBandGradientDirection;
    private Band targetBandFlag;
    private Band targetBandFinalSandBanksMag;
    private Band targetBandFinalSandBanksDir;
    private Band targetBandSandBanksBeltMag;
    private Band targetBandSandBanksBeltDir;
    private Band targetBandSandBanksBeltID;
    private Band targetBandSandBanksID;


    private String operator;
    private String filterGradient;
    private String algorithm;
    private String filterHistogram;
    private int growCloud;
    private String rounding;
    private double roundingInputData;
    private String hysteresis;

    private static final String SOBEL_OPERATOR = "Sobel Operator";
    private static final String SCHARR_OPERATOR = "Scharr Operator";
    private static final String CONTEXTUAL_MEDIAN_FILTER = "Contextualer Median Filter";
    private static final String GAUSS_FILTER = "Gaussian Filter";
    private static final String MEDIAN_FILTER = "Median Filter";
    private static final String LAPLACE_FILTER = "Laplace Filter";
    private static final String SMOS_FILTER = "SMOS Filter";
    private static final String ADAPTIVER_MEAN_FILTER = "adaptiver Mean Filter";

    private static final String CANNY_HYSTERESIS = "Hysteresis according to Canny Algorithm";
    private static final String SIMPLE_HYSTERESIS = "Simple Hysteresis Algorithm";
    private static final String SIED_ALGORITHM = "SIED Algorithm";
    private static final String ENTROPY_ALGORITHM = "Entropy Algorithm";

    private static final String YES_ROUNDING = "YES";
    private static final String NO_ROUNDING = "NO";

    private double kernelEdgeValue;
    private double kernelCentreValue;
    private double weightingFactor;
    private double maxThresholdHysteresis = 0.1;
    private double minThresholdHysteresis = 0.09;
    private static final int filterBoarder = 11;

    static final int fillKernelRadius = 1;
    static final int medianFilterKernelRadius = 2;
    static final int conMedianFilterKernelRadius = 2;
    static final int gaussFilterKernelRadius = 2;
    static final int laplaceFilterKernelRadius = 2;
    static final int smosFilterKernelRadius = 12;
    static final int convolutionFilterKernelRadius = 1;
    static final int nonMaxSuppressionKernelRadius = 1;
    //static final int maxKernelRadius = fillKernelRadius + filterKernelRadius + convolutionFilterKernelRadius + nonMaxSuppressionKernelRadius; // 5!
    static final int maxKernelRadius = 30; //20;

    static final int frontValue = 1;
    static final int windowOverlap = 50;
    static final int standardHistogramBins = 32;
    static final int minKernelRadius = 0;

    static double beltThreshold = 0.15;
    static double froggy = 2.5;//1.5;
    static final int beltRepeatValue = 3;


    private int totalParameterNumber = 0; //Parameter , e.g. SST
    private int totalFrontBeltPixelNumber = 0;
    private double maxFrontBeltMagnitude = 0.;
    private int thresholdFrontBeltPixelNumber = 0;
    private double acceptableFrontBeltPixel = 0.025;


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
        targetBandFinalSandBanksMag = targetProduct.addBand(targetBandNameFinalSandBankssMag, ProductData.TYPE_FLOAT64);
        targetBandFinalSandBanksDir = targetProduct.addBand(targetBandNameFinalSandBankssDir, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBeltMag = targetProduct.addBand(targetBandNameSandBanksBeltMag, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBeltDir = targetProduct.addBand(targetBandNameSandBanksBeltDir, ProductData.TYPE_FLOAT64);
        targetBandSandBanksBeltID = targetProduct.addBand(targetBandNameSandBanksBeltID, ProductData.TYPE_INT16);
        targetBandSandBanksID = targetProduct.addBand(targetBandNameSandBanksID, ProductData.TYPE_INT16);

        operator = SCHARR_OPERATOR;
        filterGradient = CONTEXTUAL_MEDIAN_FILTER;
        algorithm = SIED_ALGORITHM;
        filterHistogram = MEDIAN_FILTER;
        growCloud = 1;
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
        Tile targetTileSandBanksID = targetTiles.get(targetBandSandBanksID);
        Tile targetTileSandBanksBeltID = targetTiles.get(targetBandSandBanksBeltID);
        Tile targetTileSandBanksBeltMag = targetTiles.get(targetBandSandBanksBeltMag);
        Tile targetTileSandBanksBeltDir = targetTiles.get(targetBandSandBanksBeltDir);
        Tile targetTileFinalSandBanksMag = targetTiles.get(targetBandFinalSandBanksMag);
        Tile targetTileFinalSandBanksDir = targetTiles.get(targetBandFinalSandBanksDir);


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
        makeFilledBand(flagArray, sourceWidth, sourceHeight, targetTileBandFlag, SandbankOp.maxKernelRadius);
        // copy source data for histogram method

        double[] histogramSourceData = new double[sourceArray.length];
        System.arraycopy(sourceArray, 0, histogramSourceData, 0, sourceArray.length);


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

        double[][] nonMaxSuppressedGradientData = new double[2][sourceWidth * sourceHeight];
        double[][] nonMaxImprovedSuppressedGradientData = new double[2][sourceWidth * sourceHeight];

        double[][] mafiozoDeLaProspettoGradientData = new double[2][sourceWidth * sourceHeight];
        double[][] edgeLinkedGradientData = new double[2][sourceWidth * sourceHeight];

        // standard thinning method according to Canny - disadvantage: lags
        NonMaximumSuppression nonMaximumSuppressedSourceBand = new NonMaximumSuppression();
        nonMaxSuppressedGradientData = nonMaximumSuppressedSourceBand.nonMaxSuppressionOfSourceBand(
                gradientSourceData,
                sourceWidth,
                sourceHeight);

        // Endpoints
        EndPointsFound endPointsFound = new EndPointsFound();
        double[] endPointsFoundData = endPointsFound.compute(nonMaxSuppressedGradientData,
                sourceWidth,
                sourceHeight);

        // standard edge linking method according to Canny - disadvantage: lags
        if (CANNY_HYSTERESIS.equals(hysteresis)) {
            EdgeLinkingHysteresis edgeLinkingOfSourceBand = new EdgeLinkingHysteresis();
            edgeLinkingOfSourceBand.edgeLinkingOfSourceBand(
                    gradientSourceData,
                    nonMaxSuppressedGradientData,
                    sourceWidth,
                    sourceHeight,
                    maxThresholdHysteresis, // FrontsOperator.weightingFactor,
                    minThresholdHysteresis, // FrontsOperator.weightingFactor,
                    edgeLinkedGradientData);
        }

        // improved thinning method according to Canny - advantage: no lags, but frayed fronts
        if (SIMPLE_HYSTERESIS.equals(hysteresis)) {

            ImprovedNonMaximumSuppression improvedNonMaximumSuppressedSourceBand = new ImprovedNonMaximumSuppression();
            nonMaxImprovedSuppressedGradientData = improvedNonMaximumSuppressedSourceBand.improvedNonMaxSuppressionOfSourceBand(
                    gradientSourceData,
                    sourceWidth,
                    sourceHeight);

            // filling of gaps
            MafiozoDeLaProspetto mafiozoDeLaProspettoSourceBand = new MafiozoDeLaProspetto();
            mafiozoDeLaProspettoSourceBand.mafiozoDeLaProspettoOfSourceBand(
                    nonMaxSuppressedGradientData,
                    nonMaxImprovedSuppressedGradientData,
                    endPointsFoundData,
                    sourceWidth,
                    sourceHeight,
                    flagArray);

            //makeFilledBand(nonMaxSuppressedGradientData, sourceWidth, sourceHeight, testTile, 0, maxKernelRadius);

            SimpleEdgeLinkingHysteresis simpleEdgeLinkingOfSourceBand = new SimpleEdgeLinkingHysteresis();
            simpleEdgeLinkingOfSourceBand.simpleEdgeLinkingOfSourceBand(
                    gradientSourceData,
                    nonMaxSuppressedGradientData,
                    sourceWidth,
                    sourceHeight,
                    maxThresholdHysteresis, // FrontsOperator.weightingFactor,
                    minThresholdHysteresis, // FrontsOperator.weightingFactor,
                    edgeLinkedGradientData);
        }


        /**************************************************************************/
        /************************** Histogram Method  *****************************/
        /**************************************************************************/
        /* Rounding: parameter: decimal */

        if (YES_ROUNDING.equals(rounding)) {
            RoundingOfSourceBand roundedSourceBand = new RoundingOfSourceBand();
            roundedSourceBand.roundedOfSourceBand(histogramSourceData,
                    sourceWidth,
                    sourceHeight,
                    roundingInputData);
        }

        if (CONTEXTUAL_MEDIAN_FILTER.equals(filterHistogram)) {
            Filter filter = new ContextualMedianFilter();
            filter.compute(histogramSourceData,
                    sourceWidth,
                    sourceHeight,
                    flagArray,
                    conMedianFilterKernelRadius);

        } else if (MEDIAN_FILTER.equals(filterHistogram)) {
            Filter filter = new MedianFilter();
            filter.compute(histogramSourceData,
                    sourceWidth,
                    sourceHeight,
                    flagArray,
                    medianFilterKernelRadius);

        } else if (GAUSS_FILTER.equals(filterHistogram)) {
            Filter filter = new GaussFilter();
            filter.compute(histogramSourceData,
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

        ArrayNaNisator preparedArrayNaN = new ArrayNaNisator();
        preparedArrayNaN.preparedArrayNaNisator(histogramSourceData,
                sourceWidth,
                sourceHeight,
                flagArray);

        int windowSize = 48;

        // TODO importante error grit

        //       double[] frontsCannyArrayDir  = targetTileHysteresisDirection.getSamplesDouble();
        //       double[] frontsCannyArrayMag  = targetTileHysteresisGradient.getSamplesDouble();


        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;

        double[] nonMaxSuppressionArray = new double[targetWidth * targetHeight];
        double[] frontsCannyArrayDir = new double[targetWidth * targetHeight];
        double[] frontsCannyArrayMag = new double[targetWidth * targetHeight];

        extractSubArray(sourceWidth, sourceHeight, targetWidth, 0, edgeLinkedGradientData, frontsCannyArrayMag);
        extractSubArray(sourceWidth, sourceHeight, targetWidth, 1, edgeLinkedGradientData, frontsCannyArrayDir);
        extractSubArray(sourceWidth, sourceHeight, targetWidth, 0, nonMaxSuppressedGradientData, nonMaxSuppressionArray);

        double[] frontsSumCayulaArray = new double[targetWidth * targetHeight];
        double[] frontsCayulaArray = new double[targetWidth * targetHeight];

        Arrays.fill(frontsSumCayulaArray, 0);

        for (int wi = 16; wi < 60; wi += 8) {
            /* SIED OPerator */
            double[] frontsData = new double[sourceWidth * sourceHeight];

            if (SIED_ALGORITHM.equals(algorithm)) {
                Arrays.fill(frontsCayulaArray, 0);
                SplitWindow splitWindow = new SplitWindow();
                splitWindow.compute(histogramSourceData,
                        sourceWidth,
                        sourceHeight,
                        targetWidth,
                        flagArray,
                        wi,
                        frontsData,
                        frontsCayulaArray /*,
                        targetTileFronts*/);
            }
            // double[] frontsCayulaArray = targetTileFronts.getSamplesDouble();
            // todo  old from 1 and new from 0
            for (int j = 1; j < targetHeight; j++) {
                for (int i = 1; i < targetWidth; i++) {
                    int k = (j) * (targetWidth) + (i);
                    frontsSumCayulaArray[k] = frontsSumCayulaArray[k] + frontsCayulaArray[k];
                    if (frontsSumCayulaArray[k] > 1) frontsSumCayulaArray[k] = 1;
                }
            }
        }

        ProjectionCayulasCanny projectionCayulasCanny = new ProjectionCayulasCanny();
        double[][] generalGradientArray = projectionCayulasCanny.compute(targetRectangle,
                flagTile,
                frontsCannyArrayMag,
                frontsCannyArrayDir,
                frontsSumCayulaArray,
                nonMaxSuppressionArray,
                targetTileGradientMagnitude,
                targetTileGradientDirection);

        /*FrontsOperator.makeFilledBand(generalGradientArray, targetRectangle.width, targetRectangle.height,
     targetTileFinalFrontsMag, targetTileFinalFrontsDir, FrontsOperator.minKernelRadius); */

        // Endpoints
        EndPointsFound endFrontPointsFound = new EndPointsFound();
        double[] endFrontPointsFoundData = endFrontPointsFound.compute(generalGradientArray,
                targetWidth,
                targetHeight);

        // todo testband
        // makeFilledBand(endFrontPointsFoundData, targetWidth, targetHeight, testTile, FrontsOperator.minKernelRadius);

        ShortFrontSuppression shortFrontSuppression = new ShortFrontSuppression();
        shortFrontSuppression.compute(endFrontPointsFoundData,
                generalGradientArray,
                targetRectangle);

        SandbankOp.makeFilledBand(generalGradientArray, targetRectangle.width, targetRectangle.height,
                targetTileFinalSandBanksMag, targetTileFinalSandBanksDir, SandbankOp.minKernelRadius);

        CreateFrontID createFrontIDArray = new CreateFrontID();
        int[] frontIDArray = createFrontIDArray.compute(generalGradientArray, targetRectangle);
        SandbankOp.makeFilledBand(frontIDArray, targetRectangle.width, targetRectangle.height,
                targetTileSandBanksID, SandbankOp.minKernelRadius);

        double[] frontsArrayMag = targetTileGradientMagnitude.getSamplesDouble();
        double[] frontsArrayDir = targetTileGradientDirection.getSamplesDouble();
        int[] finalFlagArray = flagTile.getSamplesInt();

        CreateFrontBelt createFrontBeltArray = new CreateFrontBelt();
        double[][] frontBeltArray = createFrontBeltArray.compute(frontsArrayMag, frontsArrayDir, generalGradientArray, frontIDArray, targetRectangle);
        SandbankOp.makeFilledBand(frontBeltArray, targetRectangle.width, targetRectangle.height,
                targetTileSandBanksBeltMag, targetTileSandBanksBeltDir, SandbankOp.minKernelRadius);

        // Number of e.g. FrontBelt data
        totalFrontBeltPixelNumber = 0;
        maxFrontBeltMagnitude = 0.;
        thresholdFrontBeltPixelNumber = 0;
        computeValuesCount(targetRectangle.width, targetRectangle.height, 0, frontBeltArray);

        CreateFrontBeltID createFrontBeltIDArray = new CreateFrontBeltID();
        int[] frontBeltIDArray = createFrontBeltIDArray.compute(frontBeltArray, frontIDArray, targetRectangle);
        SandbankOp.makeFilledBand(frontBeltIDArray, targetRectangle.width, targetRectangle.height,
                targetTileSandBanksBeltID, SandbankOp.minKernelRadius);




        /*
        PlayFronts playFronts = new PlayFronts();
        playFronts.compute(generalGradientArray,
                frontIDArray,
                frontBeltArray,
                frontBeltIDArray,
                finalFlagArray,
                targetRectangle);
        */

    }

    // todo sourceHeight - FrontsOperator.maxKernelRadius - 1 to sourceHeight - FrontsOperator.maxKernelRadius


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
        for (int j = SandbankOp.maxKernelRadius; j < sourceHeight - SandbankOp.maxKernelRadius; j++) {
            for (int i = SandbankOp.maxKernelRadius; i < sourceWidth - SandbankOp.maxKernelRadius; i++) {
                int k = j * (sourceWidth) + i;
                int l = (j - SandbankOp.maxKernelRadius) * targetWidth + (i - SandbankOp.maxKernelRadius);
                targetData[l] = sourceData[index][k];
            }
        }
    }

    static void extractSubArray(int sourceWidth,
                                int sourceHeight,
                                int targetWidth,
                                double[] sourceData,
                                double[] targetData) {
        for (int j = SandbankOp.maxKernelRadius; j < sourceHeight - SandbankOp.maxKernelRadius; j++) {
            for (int i = SandbankOp.maxKernelRadius; i < sourceWidth - SandbankOp.maxKernelRadius; i++) {
                int k = j * (sourceWidth) + i;
                int l = (j - SandbankOp.maxKernelRadius) * targetWidth + (i - SandbankOp.maxKernelRadius);
                targetData[l] = sourceData[k];
            }
        }
    }

    static int computeValuesCount(int countWidth,
                                  int countHeight,
                                  double[] countData) {
        int counter = 0;
        for (int j = SandbankOp.maxKernelRadius; j < countHeight - SandbankOp.maxKernelRadius; j++) {
            for (int i = SandbankOp.maxKernelRadius; i < countWidth - SandbankOp.maxKernelRadius; i++) {
                int k = j * (countWidth) + i;
                if (!Double.isNaN(countData[k])) {
                    counter++;
                }
            }
        }
        return counter;
    }

    void computeValuesCount(int countWidth,
                            int countHeight,
                            int index,
                            double[][] countData) {
        for (int j = minKernelRadius; j < countHeight - minKernelRadius; j++) {
            for (int i = minKernelRadius; i < countWidth - minKernelRadius; i++) {
                int k = j * (countWidth) + i;
                if (!Double.isNaN(countData[index][k])) {
                    totalFrontBeltPixelNumber++;
                    if (countData[index][k] > acceptableFrontBeltPixel) {
                        thresholdFrontBeltPixelNumber++;
                    }
                    if (countData[index][k] > maxFrontBeltMagnitude) {
                        maxFrontBeltMagnitude = countData[index][k];
                    }
                }
            }
        }
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
            super(SandbankOp.class);
        }
    }
}
