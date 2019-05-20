package com.bc.c3slot5.comparison;

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


@OperatorMetadata(alias = "ComparisonComposite",
        label = "C3S LOT5 Comparison Seasonal Composite",
        authors = "Marco Peters",
        copyright = "Brockmann Consult",
        version = "0.6")

public class ComparisonCompositeOp extends Operator {

    private static final String TYPE_SUFFIX = "_comp";
    public static final int INT_NAN_VALUE = -9999;

    @SourceProduct()
    private Product sourceCompositeObservationCountA;
    @SourceProduct()
    private Product sourceCompositeObservationCountB;
    @SourceProduct()
    private Product sourceCompositeStatusCountA;
    @SourceProduct()
    private Product sourceCompositeStatusCountB;
    @SourceProduct()
    private Product sourceCompositeStatusA;
    @SourceProduct()
    private Product sourceCompositeStatusB;
    @SourceProduct()
    private Product sourceCompositeSr1A;
    @SourceProduct()
    private Product sourceCompositeSr1B;
    @SourceProduct()
    private Product sourceCompositeSr2A;
    @SourceProduct()
    private Product sourceCompositeSr2B;
    @SourceProduct()
    private Product sourceCompositeSr3A;
    @SourceProduct()
    private Product sourceCompositeSr3B;
    @SourceProduct()
    private Product sourceCompositeSr4A;
    @SourceProduct()
    private Product sourceCompositeSr4B;


    @TargetProduct
    private Product targetProduct;

    private Band targetDiffernceStausCountBand;
    static String targetDiffernceStausCountBandName = "difference_status_count";
    private Band targetDiffernceObservationCountBand;
    static String targetDiffernceObservationCountBandName = "difference_obs_count";
    private Band targetStatusBand;
    static String targetStatusBandName = "status";
    private Band targetMaskBand;
    static String targetMaskBandName = "mask";
    private Band targetDifferenceSr1Band;
    static String targetDifferenceSr1BandName = "delta_sr1";
    private Band targetDifferenceSr2Band;
    static String targetDifferenceSr2BandName = "delta_sr2";
    private Band targetDifferenceSr3Band;
    static String targetDifferenceSr3BandName = "delta_sr3";
    private Band targetDifferenceSr4Band;
    static String targetDifferenceSr4BandName = "delta_sr4";
    @Parameter(defaultValue = "statisticFile.txt")
    private String statisticFileName;

    private File statisticFile;

    @Override
    public void initialize() throws OperatorException {
        targetProduct = createTargetProduct();

        targetDiffernceObservationCountBand = targetProduct.addBand(targetDiffernceObservationCountBandName, ProductData.TYPE_INT16);
        targetDiffernceStausCountBand = targetProduct.addBand(targetDiffernceStausCountBandName, ProductData.TYPE_INT16);
        targetStatusBand = targetProduct.addBand(targetStatusBandName, ProductData.TYPE_INT16);
        targetMaskBand = targetProduct.addBand(targetMaskBandName, ProductData.TYPE_INT16);
        targetDifferenceSr1Band = targetProduct.addBand(targetDifferenceSr1BandName, ProductData.TYPE_FLOAT32);
        targetDifferenceSr2Band = targetProduct.addBand(targetDifferenceSr2BandName, ProductData.TYPE_FLOAT32);
        targetDifferenceSr3Band = targetProduct.addBand(targetDifferenceSr3BandName, ProductData.TYPE_FLOAT32);
        targetDifferenceSr4Band = targetProduct.addBand(targetDifferenceSr4BandName, ProductData.TYPE_FLOAT32);

        statisticFile = new File("D:/C3SLOT5/QA/" + statisticFileName);
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {
        Rectangle sourceRectangle = new Rectangle(targetRectangle);

        Tile sourceObservationCountTileA = getSourceTile(sourceCompositeObservationCountA.getBand("obs_count"), sourceRectangle);
        Tile sourceObservationCountTileB = getSourceTile(sourceCompositeObservationCountB.getBand("obs_count"), sourceRectangle);
        Tile sourceStatusCountTileA = getSourceTile(sourceCompositeStatusCountA.getBand("status_count"), sourceRectangle);
        Tile sourceStatusCountTileB = getSourceTile(sourceCompositeStatusCountB.getBand("status_count"), sourceRectangle);
        Tile sourceStatusTileA = getSourceTile(sourceCompositeStatusA.getBand("status"), sourceRectangle);
        Tile sourceStatusTileB = getSourceTile(sourceCompositeStatusB.getBand("status"), sourceRectangle);
        Tile sourceSr1TileA = getSourceTile(sourceCompositeSr1A.getBand("sr_1_mean"), sourceRectangle);
        Tile sourceSr1TileB = getSourceTile(sourceCompositeSr1B.getBand("sr_1_mean"), sourceRectangle);
        Tile sourceSr2TileA = getSourceTile(sourceCompositeSr2A.getBand("sr_2_mean"), sourceRectangle);
        Tile sourceSr2TileB = getSourceTile(sourceCompositeSr2B.getBand("sr_2_mean"), sourceRectangle);
        Tile sourceSr3TileA = getSourceTile(sourceCompositeSr3A.getBand("sr_3_mean"), sourceRectangle);
        Tile sourceSr3TileB = getSourceTile(sourceCompositeSr3B.getBand("sr_3_mean"), sourceRectangle);
        Tile sourceSr4TileA = getSourceTile(sourceCompositeSr4A.getBand("sr_4_mean"), sourceRectangle);
        Tile sourceSr4TileB = getSourceTile(sourceCompositeSr4B.getBand("sr_4_mean"), sourceRectangle);

        Tile targetDifferenceObservationCountTile = targetTiles.get(targetDiffernceObservationCountBand);
        Tile targetDifferenceStatusCountTile = targetTiles.get(targetDiffernceStausCountBand);
        Tile targetStatusTile = targetTiles.get(targetStatusBand);
        Tile targetMaskTile = targetTiles.get(targetMaskBand);
        Tile targetDifferenceSr1Tile = targetTiles.get(targetDifferenceSr1Band);
        Tile targetDifferenceSr2Tile = targetTiles.get(targetDifferenceSr2Band);
        Tile targetDifferenceSr3Tile = targetTiles.get(targetDifferenceSr3Band);
        Tile targetDifferenceSr4Tile = targetTiles.get(targetDifferenceSr4Band);


        int sourceLength = sourceRectangle.height * sourceRectangle.width;

        int[] sourceObservationCountArrayA = sourceObservationCountTileA.getSamplesInt();
        int[] sourceObservationCountArrayB = sourceObservationCountTileB.getSamplesInt();
        int[] sourceStatusCountArrayA = sourceStatusCountTileA.getSamplesInt();
        int[] sourceStatusCountArrayB = sourceStatusCountTileB.getSamplesInt();
        int[] sourceStatusArrayA = sourceStatusTileA.getSamplesInt();
        int[] sourceStatusArrayB = sourceStatusTileB.getSamplesInt();

        double[] sourceSr1ArrayA = sourceSr1TileA.getSamplesDouble();
        double[] sourceSr1ArrayB = sourceSr1TileB.getSamplesDouble();
        double[] sourceSr2ArrayA = sourceSr2TileA.getSamplesDouble();
        double[] sourceSr2ArrayB = sourceSr2TileB.getSamplesDouble();
        double[] sourceSr3ArrayA = sourceSr3TileA.getSamplesDouble();
        double[] sourceSr3ArrayB = sourceSr3TileB.getSamplesDouble();
        double[] sourceSr4ArrayA = sourceSr4TileA.getSamplesDouble();
        double[] sourceSr4ArrayB = sourceSr4TileB.getSamplesDouble();

        int[] targetDifferenceObservationCountArray = new int[sourceLength];
        int[] targetDifferenceStatusCountArray = new int[sourceLength];
        int[] targetStatusArray = new int[sourceLength];
        int[] targetMaskArray = new int[sourceLength];
        double[] targetDifferenceSr1Array = new double[sourceLength];
        double[] targetDifferenceSr2Array = new double[sourceLength];
        double[] targetDifferenceSr3Array = new double[sourceLength];
        double[] targetDifferenceSr4Array = new double[sourceLength];


        String content;

        int counterInvalid = 0;
        int counterValid = 0;
        int counterValidLandSnowWater = 0;

        double doubleK = 0.5;
        int intK = 1;
        int SumDifferenceObservationCountArray = 0;
        int SumDifferenceObservationCountArraySquared = 0;
        int SumDifferenceStatusCountArray = 0;
        int SumDifferenceStatusCountArraySquared = 0;
        int SumFilteredDifferenceObservationCountArray = 0;
        int SumFilteredDifferenceObservationCountArraySquared = 0;
        int SumFilteredDifferenceStatusCountArray = 0;
        int SumFilteredDifferenceStatusCountArraySquared = 0;
        double SumDifferenceSr1Array = 0.0;
        double SumDifferenceSr1ArraySquared = 0.0;
        double SumDifferenceSr2Array = 0.0;
        double SumDifferenceSr2ArraySquared = 0.0;
        double SumDifferenceSr3Array = 0.0;
        double SumDifferenceSr3ArraySquared = 0.0;
        double SumDifferenceSr4Array = 0.0;
        double SumDifferenceSr4ArraySquared = 0.0;

        double SumSr1ArrayA = 0.0;
        double SumSr1ArraySquaredA = 0.0;
        double SumSr2ArrayA = 0.0;
        double SumSr2ArraySquaredA = 0.0;
        double SumSr3ArrayA = 0.0;
        double SumSr3ArraySquaredA = 0.0;
        double SumSr4ArrayA = 0.0;
        double SumSr4ArraySquaredA = 0.0;

        double SumSr1ArrayB = 0.0;
        double SumSr1ArraySquaredB = 0.0;
        double SumSr2ArrayB = 0.0;
        double SumSr2ArraySquaredB = 0.0;
        double SumSr3ArrayB = 0.0;
        double SumSr3ArraySquaredB = 0.0;
        double SumSr4ArrayB = 0.0;
        double SumSr4ArraySquaredB = 0.0;

        for (int i = 0; i < sourceLength; i++) {
            if ((sourceStatusArrayA[i] == 0) || (sourceStatusArrayB[i] == 0) ||
                    Double.isNaN(sourceSr1ArrayA[i]) || Double.isNaN(sourceSr1ArrayB[i]) ||
                    Double.isNaN(sourceSr2ArrayA[i]) || Double.isNaN(sourceSr2ArrayB[i]) ||
                    Double.isNaN(sourceSr3ArrayA[i]) || Double.isNaN(sourceSr3ArrayB[i]) ||
                    Double.isNaN(sourceSr4ArrayA[i]) || Double.isNaN(sourceSr4ArrayB[i])) {
                targetMaskArray[i] = 0;
                targetStatusArray[i] = -1;
                targetDifferenceStatusCountArray[i] = INT_NAN_VALUE;
                targetDifferenceObservationCountArray[i] = INT_NAN_VALUE;
            } else {
                if ((sourceStatusArrayA[i] == sourceStatusArrayB[i]) && (sourceStatusArrayA[i] !=0)) {
                    targetStatusArray[i] = sourceStatusArrayA[i];
                    targetDifferenceObservationCountArray[i] = sourceObservationCountArrayA[i] - sourceObservationCountArrayB[i];
                    targetDifferenceStatusCountArray[i] = sourceStatusCountArrayA[i] - sourceStatusCountArrayB[i];
                    SumDifferenceObservationCountArray += targetDifferenceObservationCountArray[i] - intK;
                    SumDifferenceObservationCountArraySquared += (targetDifferenceObservationCountArray[i] - intK) * (targetDifferenceObservationCountArray[i] - intK);
                    SumDifferenceStatusCountArray += targetDifferenceStatusCountArray[i] - intK;
                    SumDifferenceStatusCountArraySquared += (targetDifferenceStatusCountArray[i] - intK) * (targetDifferenceStatusCountArray[i] - intK);
                    targetMaskArray[i] = 1;
                    counterValid++;
                    if ((sourceStatusArrayA[i] > 0) && (sourceStatusArrayA[i] < 4)) {
                        targetDifferenceSr1Array[i] = sourceSr1ArrayA[i] - sourceSr1ArrayB[i];
                        targetDifferenceSr2Array[i] = sourceSr2ArrayA[i] - sourceSr2ArrayB[i];
                        targetDifferenceSr3Array[i] = sourceSr3ArrayA[i] - sourceSr3ArrayB[i];
                        targetDifferenceSr4Array[i] = sourceSr4ArrayA[i] - sourceSr4ArrayB[i];
                        SumFilteredDifferenceObservationCountArray += targetDifferenceObservationCountArray[i] - intK;
                        SumFilteredDifferenceObservationCountArraySquared += (targetDifferenceObservationCountArray[i] - intK) * (targetDifferenceObservationCountArray[i] - intK);
                        SumFilteredDifferenceStatusCountArray += targetDifferenceStatusCountArray[i] - intK;
                        SumFilteredDifferenceStatusCountArraySquared += (targetDifferenceStatusCountArray[i] - intK) * (targetDifferenceStatusCountArray[i] - intK);
                        SumDifferenceSr1Array += targetDifferenceSr1Array[i] - doubleK;
                        SumDifferenceSr1ArraySquared += (targetDifferenceSr1Array[i] - doubleK) * (targetDifferenceSr1Array[i] - doubleK);
                        SumDifferenceSr2Array += targetDifferenceSr2Array[i] - doubleK;
                        SumDifferenceSr2ArraySquared += (targetDifferenceSr2Array[i] - doubleK) * (targetDifferenceSr2Array[i] - doubleK);
                        SumDifferenceSr3Array += targetDifferenceSr3Array[i] - doubleK;
                        SumDifferenceSr3ArraySquared += (targetDifferenceSr3Array[i] - doubleK) * (targetDifferenceSr3Array[i] - doubleK);
                        SumDifferenceSr4Array += targetDifferenceSr4Array[i] - doubleK;
                        SumDifferenceSr4ArraySquared += (targetDifferenceSr4Array[i] - doubleK) * (targetDifferenceSr4Array[i] - doubleK);
                        SumSr1ArrayA += sourceSr1ArrayA[i] - doubleK;
                        SumSr1ArraySquaredA += (sourceSr1ArrayA[i] - doubleK) * (sourceSr1ArrayA[i] - doubleK);
                        SumSr2ArrayA += sourceSr2ArrayA[i] - doubleK;
                        SumSr2ArraySquaredA += (sourceSr2ArrayA[i] - doubleK) * (sourceSr2ArrayA[i] - doubleK);
                        SumSr3ArrayA += sourceSr3ArrayA[i] - doubleK;
                        SumSr3ArraySquaredA += (sourceSr3ArrayA[i] - doubleK) * (sourceSr3ArrayA[i] - doubleK);
                        SumSr4ArrayA += sourceSr4ArrayA[i] - doubleK;
                        SumSr4ArraySquaredA += (sourceSr4ArrayA[i] - doubleK) * (sourceSr4ArrayA[i] - doubleK);
                        SumSr1ArrayB += sourceSr1ArrayB[i] - doubleK;
                        SumSr1ArraySquaredB += (sourceSr1ArrayB[i] - doubleK) * (sourceSr1ArrayB[i] - doubleK);
                        SumSr2ArrayB += sourceSr2ArrayB[i] - doubleK;
                        SumSr2ArraySquaredB += (sourceSr2ArrayB[i] - doubleK) * (sourceSr2ArrayB[i] - doubleK);
                        SumSr3ArrayB += sourceSr3ArrayB[i] - doubleK;
                        SumSr3ArraySquaredB += (sourceSr3ArrayB[i] - doubleK) * (sourceSr3ArrayB[i] - doubleK);
                        SumSr4ArrayB += sourceSr4ArrayB[i] - doubleK;
                        SumSr4ArraySquaredB += (sourceSr4ArrayB[i] - doubleK) * (sourceSr4ArrayB[i] - doubleK);
                        counterValidLandSnowWater++;
                    }
                } else {
                    targetStatusArray[i] = 0;
                    targetDifferenceObservationCountArray[i] = INT_NAN_VALUE;
                    targetDifferenceStatusCountArray[i] = INT_NAN_VALUE;
                    targetMaskArray[i] = 1;
                    targetDifferenceSr1Array[i] = Double.NaN;
                    targetDifferenceSr2Array[i] = Double.NaN;
                    targetDifferenceSr3Array[i] = Double.NaN;
                    targetDifferenceSr4Array[i] = Double.NaN;
                    counterInvalid++;
                }
            }
        }

        if (counterValid != 0 || counterInvalid != 0) {
            //System.out.printf("counterValid counterInvalid  %d %d %f %f\n", counterValid, counterValidLandSnowWater, SumDifferenceSr1Array, SumDifferenceSr1ArraySquared);
            content = "X Y counterValid counterValidLandSnowWater counterInvalid intK  doubleK " +
                    "SumDifferenceObsevationCountArray SumDifferenceObservationCountArraySquared " +
                    "SumDifferenceStatusCountArray SumDifferenceStatusCountArraySquared " +
                    "SumFilteredDifferenceObsevationCountArray SumFilteredDifferenceObservationCountArraySquared " +
                    "SumFilteredDifferenceStatusCountArray SumFilteredDifferenceStatusCountArraySquared " +
                    "SumDifferenceSr1Array SumDifferenceSr1ArraySquared SumDifferenceSr2Array " +
                    "SumDifferenceSr2ArraySquared SumDifferenceSr3Array SumDifferenceSr3ArraySquared " +
                    "SumDifferenceSr4Array SumDifferenceSr4ArraySquared " +
                    "SumSr1ArrayA SumSr1ArraySquaredA " +
                    "SumSr2ArrayA SumSr2ArraySquaredA " +
                    "SumSr3ArrayA SumSr3ArraySquaredA " +
                    "SumSr4ArrayA SumSr4ArraySquaredA " +
                    "SumSr1ArrayB SumSr1ArraySquaredB " +
                    "SumSr2ArrayB SumSr2ArraySquaredB " +
                    "SumSr3ArrayB SumSr3ArraySquaredB " +
                    "SumSr4ArrayB SumSr4ArraySquaredB " +
                    (targetRectangle.x) +" " + (targetRectangle.y)+ " " +
                    (counterValid) + " " + (counterValidLandSnowWater) + " " + (counterInvalid) + " " +
                    (intK) + " " + (doubleK) + " " +
                    (SumDifferenceObservationCountArray) + " " + (SumDifferenceObservationCountArraySquared) + " " +
                    (SumDifferenceStatusCountArray) + " " + (SumDifferenceStatusCountArraySquared) + " " +
                    (SumFilteredDifferenceObservationCountArray) + " " + (SumFilteredDifferenceObservationCountArraySquared) + " " +
                    (SumFilteredDifferenceStatusCountArray) + " " + (SumFilteredDifferenceStatusCountArraySquared) + " " +
                    (SumDifferenceSr1Array) + " " + (SumDifferenceSr1ArraySquared) + " " +
                    (SumDifferenceSr2Array) + " " + (SumDifferenceSr2ArraySquared) + " " +
                    (SumDifferenceSr3Array) + " " + (SumDifferenceSr3ArraySquared) + " " +
                    (SumDifferenceSr4Array) + " " + (SumDifferenceSr4ArraySquared)+ " " +
                    (SumSr1ArrayA) + " " + (SumSr1ArraySquaredA) + " " +
                    (SumSr2ArrayA) + " " + (SumSr2ArraySquaredA) + " " +
                    (SumSr3ArrayA) + " " + (SumSr3ArraySquaredA) + " " +
                    (SumSr4ArrayA) + " " + (SumSr4ArraySquaredA) + " " +
                    (SumSr1ArrayB) + " " + (SumSr1ArraySquaredB) + " " +
                    (SumSr2ArrayB) + " " + (SumSr2ArraySquaredB) + " " +
                    (SumSr3ArrayB) + " " + (SumSr3ArraySquaredB) + " " +
                    (SumSr4ArrayB) + " " + (SumSr4ArraySquaredB);
            writeToFileExample(statisticFile, content);
        }

        makeFilledBand(targetDifferenceObservationCountArray, targetRectangle, targetDifferenceObservationCountTile);
        makeFilledBand(targetDifferenceStatusCountArray, targetRectangle, targetDifferenceStatusCountTile);
        makeFilledBand(targetStatusArray, targetRectangle, targetStatusTile);
        makeFilledBand(targetMaskArray, targetRectangle, targetMaskTile);
        makeFilledBand(targetDifferenceSr1Array, targetRectangle, targetDifferenceSr1Tile);
        makeFilledBand(targetDifferenceSr2Array, targetRectangle, targetDifferenceSr2Tile);
        makeFilledBand(targetDifferenceSr3Array, targetRectangle, targetDifferenceSr3Tile);
        makeFilledBand(targetDifferenceSr4Array, targetRectangle, targetDifferenceSr4Tile);

    }


    private Product createTargetProduct() {
        String productType = sourceCompositeStatusCountA.getProductType();
        String productName = sourceCompositeStatusCountA.getName();
        if (!productType.endsWith(TYPE_SUFFIX)) {
            productType = productType + TYPE_SUFFIX;
            productName = productName + TYPE_SUFFIX;
        }
        Product product = new Product(productName, productType, sourceCompositeStatusCountA.getSceneRasterWidth(), sourceCompositeStatusCountA.getSceneRasterHeight());
        product.setPreferredTileSize(500, 500);
        /*Copies all properties from source product to the target product.*/
        ProductUtils.copyProductNodes(sourceCompositeStatusCountA, product);

        return product;
    }

    static void makeFilledBand(int[] inputData,
                               Rectangle targetRectangle,
                               Tile targetTileOutputBand) {

        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        int inputDataWidth = targetRectangle.width;
        int inputDataHeight = targetRectangle.height;

        for (int y = 0; y < inputDataHeight; y++) {
            for (int x = 0; x < inputDataWidth; x++) {
                targetTileOutputBand.setSample(x + xLocation, y + yLocation, inputData[y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand(double[] inputData,
                               Rectangle targetRectangle,
                               Tile targetTileOutputBand) {

        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        int inputDataWidth = targetRectangle.width;
        int inputDataHeight = targetRectangle.height;

        for (int y = 0; y < inputDataHeight; y++) {
            for (int x = 0; x < inputDataWidth; x++) {
                targetTileOutputBand.setSample(x + xLocation, y + yLocation, inputData[y * (inputDataWidth) + x]);
            }
        }
    }


    static void writeToFileExample(File file, String content) {
        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // standard deviation and mean in one pass
    static void addVariable(long n, double K, double x, double Ex, double Ex2) {
        n = n + 1;
        Ex += x - K;
        Ex2 += (x - K) * (x - K);
    }

    static void removeVariable(long n, double K, double x, double Ex, double Ex2) {
        n = n - 1;
        Ex -= (x - K);
        Ex2 -= (x - K) * (x - K);
    }

    static double getMeanValue(long n, double K, double x, double Ex) {
        return K + Ex / n;
    }

    static double getVariance(long n, double K, double x, double Ex, double Ex2) {
        return (Ex2 - (Ex * Ex) / n) / (n - 1);
    }


    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ComparisonCompositeOp.class);
        }
    }
}
