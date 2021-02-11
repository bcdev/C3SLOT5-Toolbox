package com.bc.c3slot5.geotool;

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
import org.esa.snap.core.dataio.ProductSubsetDef;


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


@OperatorMetadata(alias = "GeoToolOlci1dSR",
        label = "Algorithm for geolocation test of OLCI SR composit products C3S LOT5",
        authors = "Marco Peters",
        copyright = "Brockmann Consult",
        version = "0.6")

public class GeoToolOlci1dSROp extends Operator {

    private static final String TYPE_SUFFIX = "_comp";
    public static final int INT_NAN_VALUE = -9999;

    @SourceProduct
    private Product sourceProductOlciA;

    @SourceProduct
    private Product sourceProductOlciB;


    @TargetProduct
    private Product targetProduct;

    private Band sourceBandOlciA;
    private Band sourceBandOlciB;
    static String sourceBandNameOlci = "sdr_8";
    private Band sourceBandStatusOlciA;
    private Band sourceBandStatusOlciB;
    static String sourceBandNameOlciStatus = "current_pixel_state";

    static String sourceBandOlciStatusName;

    private Band targetCopySourceBandOlciA;
    static String targetCopySourceBandNameOlciA;
    private Band targetCopySourceBandOlciB;
    static String targetCopySourceBandNameOlciB;
    private Band targetMaxCorr;
    static String targetMaxCorrBandName;
    private Band targetMaxCorrDir;
    static String targetMaxCorrDirBandName;
    private Band targetStatusBand;
    static String targetStatusBandName;

    static int maxKernelRadius = 1;
    static int minKernelRadius = 0;
    static int corrKernelRadius = 1;

    @Parameter(defaultValue = "statisticFile.txt")
    private String statisticFileName;

    private File statisticFile;

    @Override
    public void initialize() throws OperatorException {
        //targetProduct = createTargetProduct();
        targetProduct = copyGeocodingForSmallerTarget(maxKernelRadius);

        targetCopySourceBandNameOlciA = sourceBandNameOlci + "_OlciA";
        targetCopySourceBandNameOlciB = sourceBandNameOlci + "_OlciB";
        targetStatusBandName = "flag_not_processed";
        targetMaxCorrBandName = "maximum_of _correlation_coefficient";
        targetMaxCorrDirBandName = "maximum_of _correlation_coefficient_drift";

        statisticFile = new File("D:/C3SLOT5/QA/" + statisticFileName);

        sourceBandOlciA = sourceProductOlciA.getBand(sourceBandNameOlci);
        sourceBandOlciB = sourceProductOlciB.getBand(sourceBandNameOlci);
        sourceBandStatusOlciA = sourceProductOlciA.getBand(sourceBandNameOlciStatus);
        sourceBandStatusOlciB = sourceProductOlciB.getBand(sourceBandNameOlciStatus);

        targetCopySourceBandOlciA = targetProduct.addBand(targetCopySourceBandNameOlciA, ProductData.TYPE_FLOAT32);
        targetCopySourceBandOlciA.setUnit(sourceBandOlciA.getUnit());
        targetCopySourceBandOlciB = targetProduct.addBand(targetCopySourceBandNameOlciB, ProductData.TYPE_FLOAT32);
        targetCopySourceBandOlciB.setUnit(sourceBandOlciB.getUnit());
        targetMaxCorr = targetProduct.addBand(targetMaxCorrBandName, ProductData.TYPE_FLOAT32);
        targetMaxCorrDir = targetProduct.addBand(targetMaxCorrDirBandName, ProductData.TYPE_FLOAT32);
        targetStatusBand = targetProduct.addBand(targetStatusBandName, ProductData.TYPE_INT16);

        targetProduct.setPreferredTileSize(122, 122); //(366,366) (915, 915); //1500
        // targetProduct.setPreferredTileSize(new Dimension(targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight()));
    }

    private Product copyGeocodingForSmallerTarget(int maxKernelRadius) {
        targetProduct = new Product("snap_geotool_BC",
                "org.esa.snap",
                sourceProductOlciA.getSceneRasterWidth() - 2 * maxKernelRadius,
                sourceProductOlciA.getSceneRasterHeight() - 2 * maxKernelRadius);
        ProductSubsetDef def = new ProductSubsetDef();
        Product sourceSubsetProduct = null;
        def.setRegion(new Rectangle(maxKernelRadius,
                maxKernelRadius,
                sourceProductOlciA.getSceneRasterWidth() - maxKernelRadius,
                sourceProductOlciA.getSceneRasterHeight() - maxKernelRadius));
        try {
            sourceSubsetProduct = sourceProductOlciA.createSubset(def, "SourceSubsetProduct", "desc");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // Some target products may require more aid from ProductUtils methods...
        ProductUtils.copyGeoCoding(sourceSubsetProduct, targetProduct);
        return targetProduct;
    }

    private Product createTargetProduct() {
        String productType = sourceProductOlciA.getProductType();
        String productName = sourceProductOlciA.getName();
        if (!productType.endsWith(TYPE_SUFFIX)) {
            productType = productType + TYPE_SUFFIX;
            productName = productName + TYPE_SUFFIX;
        }
        Product product = new Product(productName, productType, sourceProductOlciA.getSceneRasterWidth(), sourceProductOlciA.getSceneRasterHeight());
        product.setPreferredTileSize(700, 700);
        /*Copies all properties from source product to the target product.*/
        ProductUtils.copyProductNodes(sourceProductOlciA, product);

        return product;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws
            OperatorException {


        Rectangle sourceRectangle = new Rectangle(targetRectangle);
        sourceRectangle.grow(maxKernelRadius, maxKernelRadius);
        // todo check translate requested?
        //sourceRectangle.translate(maxKernelRadius, maxKernelRadius);


        Tile sourceTileOlciA = getSourceTile(sourceBandOlciA, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));
        Tile sourceTileOlciB = getSourceTile(sourceBandOlciB, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));

        Tile sourceTileStatusOlciA = getSourceTile(sourceBandStatusOlciA, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));
        Tile sourceTileStatusOlciB = getSourceTile(sourceBandStatusOlciB, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));


        Tile targetTileCopySourceBandOlciA = targetTiles.get(targetCopySourceBandOlciA);
        Tile targetTileCopySourceBandOlciB = targetTiles.get(targetCopySourceBandOlciB);
        Tile targetTileMaxCorr = targetTiles.get(targetMaxCorr);
        Tile targetTileMaxCorrDir = targetTiles.get(targetMaxCorrDir);
        Tile targetTileStatus = targetTiles.get(targetStatusBand);


        final float[] sourceDataOlciA = sourceTileOlciA.getSamplesFloat();
        final float[] sourceDataOlciB = sourceTileOlciB.getSamplesFloat();

        final float[] sourceDataStatusOlciA = sourceTileStatusOlciA.getSamplesFloat();
        final float[] sourceDataStatusOlciB = sourceTileStatusOlciB.getSamplesFloat();


        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceWidth * sourceHeight;
        System.out.printf("sourceWidth: %d sourceHeight: %d\n", sourceWidth, sourceHeight);

        final float[][] sourceDataMoveOlciA = new float[sourceWidth][sourceHeight];
        final float[][] sourceDataMoveOlciB = new float[sourceWidth][sourceHeight];
        final float[][] correlationARRAY = new float[sourceWidth][sourceHeight];
        final float[] correlationMaxARRAY = new float[sourceLength];
        final float[] correlationDirARRAY = new float[sourceLength];
        final int[] statusData = new int[sourceLength];

        Arrays.fill(correlationMaxARRAY, Float.MIN_VALUE);
        Arrays.fill(correlationDirARRAY, Float.MIN_VALUE);

        float[] kernelSizeArrayOlciA = new float[(2 * corrKernelRadius + 1) * (2 * corrKernelRadius + 1)];
        float[] kernelSizeArrayOlciB = new float[(2 * corrKernelRadius + 1) * (2 * corrKernelRadius + 1)];

        float direction = 0.0f;

        makeFilledBand(sourceDataOlciA, targetRectangle, targetTileCopySourceBandOlciA, GeoToolOlci1dSROp.maxKernelRadius);
        makeFilledBand(sourceDataOlciB, targetRectangle, targetTileCopySourceBandOlciB, GeoToolOlci1dSROp.maxKernelRadius);
        // makeFilledBand(sourceDataOlciB, sourceWidth, sourceHeight, targetTileCopySourceBandOlciB, Sentinel2GeoToolOperator.maxKernelRadius);

        int indexOlciA;
        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                indexOlciA = j * (sourceWidth) + i;
                if (sourceDataStatusOlciA[indexOlciA] > 0 && sourceDataStatusOlciA[indexOlciA] < 4 &&
                        sourceDataStatusOlciB[indexOlciA] > 0 && sourceDataStatusOlciB[indexOlciA] < 4) {
                    sourceDataMoveOlciA[i][j] = sourceDataOlciA[indexOlciA];
                    statusData[indexOlciA] = 0;
                } else {
                    sourceDataMoveOlciA[i][j] = Float.NaN;
                    statusData[indexOlciA] = 1;
                }
            }
        }

        // direction
        //  NW N NE    1 4 7
        //   W - E     2 5 8
        //  SW S SE    3 6 9
        //  0 noDataValue


        int indexOlciB;
        for (int k = -1; k < 2; k++) {
            for (int ll = -1; ll < 2; ll++) {

                direction += 1;

                for (int j = 0; j < sourceHeight; j++) {
                    for (int i = 0; i < sourceWidth; i++) {
                        sourceDataMoveOlciB[i][j] = Float.NaN;
                        correlationARRAY[i][j] = Float.NaN;
                    }
                }

                for (int j = 1; j < sourceHeight - 1; j++) {
                    for (int i = 1; i < sourceWidth - 1; i++) {
                        indexOlciB = (j - ll) * (sourceWidth) + (i - k);
                        if(sourceDataStatusOlciA[indexOlciB] > 0 && sourceDataStatusOlciA[indexOlciB] < 4 &&
                                sourceDataStatusOlciB[indexOlciB] > 0 && sourceDataStatusOlciB[indexOlciB] < 4){
                            sourceDataMoveOlciB[i][j] = sourceDataOlciB[indexOlciB];
                        }


                    }
                }

//                // Todo for test of shifting helps - systematic bias
//                for (int j = 1; j < sourceHeight - 2; j++) {
//                    for (int i = 1; i < sourceWidth - 1; i++) {
//                        indexOlciB = (j - ll + 1) * (sourceWidth) + (i - k);
//                        if (sourceDataStatusOlciA[indexOlciB] == 0 && sourceDataStatusOlciB[indexOlciB] == 0) {
//                            sourceDataMoveOlciB[i][j] = sourceDataOlciB[indexOlciB];
//
//                        }
//
//
//                    }
//                }

                int indexValid;
                for (int j = 1; j < sourceHeight - 1; j++) {
                    for (int i = 1; i < sourceWidth - 1; i++) {

                        indexValid = 0;
                        for (int jj = -corrKernelRadius; jj < corrKernelRadius + 1; jj++) {
                            for (int ii = -corrKernelRadius; ii < corrKernelRadius + 1; ii++) {
                                // System.out.printf("1. width height 3x3matrix width height:  %d  %d  %d   \n", i + ii, j + jj, (jj + corrKernelRadius) * (2 * corrKernelRadius + 1) + (ii + corrKernelRadius));
                                //kernelSizeArrayOlciA[(jj + corrKernelRadius) * (2 * corrKernelRadius + 1) + (ii + corrKernelRadius)] = sourceDataMoveOlciA[i + ii][j + jj];
                                //kernelSizeArrayOlciB[(jj + corrKernelRadius) * (2 * corrKernelRadius + 1) + (ii + corrKernelRadius)] = sourceDataMoveOlciB[i + ii][j + jj];
                                //System.out.printf("2. width height 3x3matrix width height:  %d  %d  %d   \n", i + ii, j + jj, (jj + corrKernelRadius) * (2 * corrKernelRadius + 1) + (ii + corrKernelRadius));
                                if (!Float.isNaN(sourceDataMoveOlciA[i + ii][j + jj]) && !Float.isNaN(sourceDataMoveOlciB[i + ii][j + jj])) {
                                    kernelSizeArrayOlciA[indexValid] = sourceDataMoveOlciA[i + ii][j + jj];
                                    kernelSizeArrayOlciB[indexValid] = sourceDataMoveOlciB[i + ii][j + jj];
                                    indexValid++;
                                }
                            }
                        }
                        if (indexValid > 1) {
                            correlationARRAY[i][j] = GeoToolCorrelation.getPearsonCorrelation1(kernelSizeArrayOlciA, kernelSizeArrayOlciB, indexValid);
                        } else {
                            correlationARRAY[i][j] = -0.1f;
                        }
                    }
                }

                for (int j = 0; j < sourceHeight; j++) {
                    for (int i = 0; i < sourceWidth; i++) {

                        if (correlationARRAY[i][j] >= correlationMaxARRAY[j * sourceWidth + i] &&
                                correlationARRAY[i][j] > -0.05f) {

                            correlationMaxARRAY[j * sourceWidth + i] = correlationARRAY[i][j];
                            correlationDirARRAY[j * sourceWidth + i] = direction;
                        }
                    }
                }
            }
        }

        makeFilledBand(correlationMaxARRAY, targetRectangle, targetTileMaxCorr, GeoToolOlci1dSROp.maxKernelRadius);
        makeFilledBand(correlationDirARRAY, targetRectangle, targetTileMaxCorrDir, GeoToolOlci1dSROp.maxKernelRadius);
        makeFilledBand(statusData, targetRectangle, targetTileStatus, GeoToolOlci1dSROp.maxKernelRadius);

//        System.out.printf("source rectangle width height:  %d  %d   \n",sourceRectangle.width, sourceRectangle.height);
//        System.out.printf("source rectangle xPos yPos:  %d  %d   \n",sourceRectangle.x, sourceRectangle.y);
//        System.out.printf("target product width height:  %d  %d   \n",targetRectangle.width, targetRectangle.height );
//        System.out.printf("target product xPos yPos:  %d  %d   \n",targetRectangle.x, targetRectangle.y );

    }


    private static void makeFilledBand
            (
                    float[] inputData,
                    Rectangle targetRectangle,
                    Tile targetTileOutputBand,
                    int mkr) {

        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        int inputDataWidth = targetRectangle.width + 2 * mkr;
        int inputDataHeight = targetRectangle.height + 2 * mkr;

        //System.out.printf("rectangle:  %d  %d _______________ rectangle_target_input_data:  %d  %d  \n", targetRectangle.width, targetRectangle.height,inputDataWidth, inputDataHeight);

        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x - mkr + xLocation, y - mkr + yLocation, inputData[y * (inputDataWidth) + x]);
            }
        }
    }

    private static void makeFilledBand
            (
                    int[] inputData,
                    Rectangle targetRectangle,
                    Tile targetTileOutputBand,
                    int mkr) {

        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        int inputDataWidth = targetRectangle.width + 2 * mkr;
        int inputDataHeight = targetRectangle.height + 2 * mkr;

        //System.out.printf("rectangle:  %d  %d _______________ rectangle_target_input_data:  %d  %d  \n", targetRectangle.width, targetRectangle.height,inputDataWidth, inputDataHeight);

        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x - mkr + xLocation, y - mkr + yLocation, inputData[y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand
            (
                    double[] inputData,
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

    static void makeFilledBand
            (
                    double[][] inputData,
                    int inputDataWidth,
                    int inputDataHeight,
                    Tile targetTileOutputBand1, Tile
                            targetTileOutputBand2, int mkr) {

        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand1.setSample(x - mkr, y - mkr, inputData[0][y * (inputDataWidth) + x]);
                targetTileOutputBand2.setSample(x - mkr, y - mkr, inputData[1][y * (inputDataWidth) + x]);
            }
        }
    }

    static void makeFilledBand
            (
                    double[][] inputData,
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

    static void makeFilledBand
            (
                    int[] inputData,
                    int inputDataWidth,
                    int inputDataHeight
                    , Tile targetTileOutputBand,
                    int mkr) {

        for (int y = mkr; y < inputDataHeight - mkr; y++) {
            for (int x = mkr; x < inputDataWidth - mkr; x++) {
                targetTileOutputBand.setSample(x - mkr, y - mkr, inputData[y * (inputDataWidth) + x]);
            }
        }
    }


    /**
     * The SPI is used to register this operator in the graph processing framework
     * via the SPI configuration file
     * {@code META-INF/services/org.esa.beam.framework.gpf.OperatorSpi}.
     * This class may also serve as a factory for new operator instances.
     *
     * @see OperatorSpi#createOperator()
     * @see OperatorSpi#createOperator(java.util.Map, java.util.Map)
     */


    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GeoToolOlci1dSROp.class);
        }
    }
}
