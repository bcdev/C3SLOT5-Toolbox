package com.bc.c3slot5.geotoolSYN;

import com.bc.ceres.core.ProgressMonitor;
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
import java.util.Arrays;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


@OperatorMetadata(alias = "GeoToolS3Syn",
        label = "Algorithm for geolocation test of SYN C3S LOT5",
        authors = "Marco Peters",
        copyright = "Brockmann Consult",
        version = "0.6")

public class GeoToolS3SynOp extends Operator {

    private static final String TYPE_SUFFIX = "_comp";
    public static final int INT_NAN_VALUE = -9999;

    @SourceProduct
    private Product sourceProductSyn;

    @Parameter(rasterDataNodeType = Band.class, description = "OLCI band")
    private String sourceBandNameSynOlci;

    @Parameter(rasterDataNodeType = Band.class, description = "SLSTR band")
    private String sourceBandNameSynSlstr;

    @TargetProduct
    private Product targetProduct;

    private Band sourceBandSynSlstr;
    private Band sourceBandSynOlci;

    

    private Band targetCopySourceBandSynOlci;
    static String targetCopySourceBandNameSynOlci;
    private Band targetCopySourceBandSynSlstr;
    static String targetCopySourceBandNameSynSlstr;
    private Band targetMaxCorr;
    static String targetMaxCorrBandName;
    private Band targetMaxCorrDir;
    static String targetMaxCorrDirBandName;


    static int maxKernelRadius = 1;
    static int minKernelRadius = 0;
    static int corrKernelRadius = 1;

    @Override
    public void initialize() throws OperatorException {
        targetProduct = createTargetProduct();

        targetProduct = copyGeocodingForSmallerTarget(maxKernelRadius, sourceProductSyn);

        targetCopySourceBandNameSynOlci = sourceBandNameSynOlci;
        targetCopySourceBandNameSynSlstr = sourceBandNameSynSlstr;

        targetMaxCorrBandName = "maximum_of _correlation_coefficient";
        targetMaxCorrDirBandName = "maximum_of _correlation_coefficient_drift";

        sourceBandSynOlci = sourceProductSyn.getBand(sourceBandNameSynOlci);
        sourceBandSynSlstr = sourceProductSyn.getBand(sourceBandNameSynSlstr);
        
        targetCopySourceBandSynOlci = targetProduct.addBand(targetCopySourceBandNameSynOlci, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSynOlci.setUnit(sourceBandSynOlci.getUnit());
        targetCopySourceBandSynSlstr = targetProduct.addBand(targetCopySourceBandNameSynSlstr, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSynSlstr.setUnit(sourceBandSynOlci.getUnit());
        targetMaxCorr = targetProduct.addBand(targetMaxCorrBandName, ProductData.TYPE_FLOAT32);
        targetMaxCorrDir = targetProduct.addBand(targetMaxCorrDirBandName, ProductData.TYPE_FLOAT32);

        targetProduct.setPreferredTileSize(122, 122); //(366,366) (915, 915); //1500
        // targetProduct.setPreferredTileSize(new Dimension(targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight()));
    }

    private Product copyGeocodingForSmallerTarget(int maxKernelRadius, Product sourceProduct) {
        targetProduct = new Product("snap_geotool_BC",
                "org.esa.snap",
                sourceProduct.getSceneRasterWidth() - 2 * maxKernelRadius,
                sourceProduct.getSceneRasterHeight() - 2 * maxKernelRadius);
        ProductSubsetDef def = new ProductSubsetDef();
        Product sourceSubsetProduct = null;
        def.setRegion(new Rectangle(maxKernelRadius,
                maxKernelRadius,
                sourceProduct.getSceneRasterWidth() - maxKernelRadius,
                sourceProduct.getSceneRasterHeight() - maxKernelRadius));
        try {
            sourceSubsetProduct = sourceProduct.createSubset(def, "SourceSubsetProduct", "desc");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // Some target products may require more aid from ProductUtils methods...
        ProductUtils.copyGeoCoding(sourceSubsetProduct, targetProduct);
        return targetProduct;
    }

    private Product createTargetProduct() {
        String productType = sourceProductSyn.getProductType();
        String productName = sourceProductSyn.getName();
        if (!productType.endsWith(TYPE_SUFFIX)) {
            productType = productType + TYPE_SUFFIX;
            productName = productName + TYPE_SUFFIX;
        }
        Product product = new Product(productName, productType, sourceProductSyn.getSceneRasterWidth(), sourceProductSyn.getSceneRasterHeight());
        product.setPreferredTileSize(700, 700);
        /*Copies all properties from source product to the target product.*/
        ProductUtils.copyProductNodes(sourceProductSyn, product);

        return product;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws
            OperatorException {


        Rectangle sourceRectangle = new Rectangle(targetRectangle);
        sourceRectangle.grow(maxKernelRadius, maxKernelRadius);
        // todo check translate requested?
        //sourceRectangle.translate(maxKernelRadius, maxKernelRadius);


        Tile sourceTileSynOlci = getSourceTile(sourceBandSynOlci, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));
        Tile sourceTileSynSlstr = getSourceTile(sourceBandSynSlstr, sourceRectangle, new BorderExtenderConstant(new double[]{Double.NaN}));

        Tile targetTileCopySourceBandSynOlci = targetTiles.get(targetCopySourceBandSynOlci);
        Tile targetTileCopySourceBandSynSlstr = targetTiles.get(targetCopySourceBandSynSlstr);
        Tile targetTileMaxCorr = targetTiles.get(targetMaxCorr);
        Tile targetTileMaxCorrDir = targetTiles.get(targetMaxCorrDir);


        final float[] sourceDataSynOlci = sourceTileSynOlci.getSamplesFloat();
        final float[] sourceDataSynSlstr = sourceTileSynSlstr.getSamplesFloat();



        int sourceWidth = sourceRectangle.width;
        int sourceHeight = sourceRectangle.height;
        int sourceLength = sourceWidth * sourceHeight;
        System.out.printf("sourceWidth: %d sourceHeight: %d\n", sourceWidth, sourceHeight);

        final float[][] sourceDataMoveSynOlci = new float[sourceWidth][sourceHeight];
        final float[][] sourceDataMoveSynSlstr = new float[sourceWidth][sourceHeight];
        final float[][] correlationARRAY = new float[sourceWidth][sourceHeight];
        final float[] correlationMaxARRAY = new float[sourceLength];
        final float[] correlationDirARRAY = new float[sourceLength];
        final int[] statusData = new int[sourceLength];

        Arrays.fill(correlationMaxARRAY, Float.MIN_VALUE);
        Arrays.fill(correlationDirARRAY, Float.MIN_VALUE);

        float[] kernelSizeArraySynOlci = new float[(2 * corrKernelRadius + 1) * (2 * corrKernelRadius + 1)];
        float[] kernelSizeArraySynSlstr = new float[(2 * corrKernelRadius + 1) * (2 * corrKernelRadius + 1)];

        float direction = 0.0f;

        makeFilledBand(sourceDataSynOlci, targetRectangle, targetTileCopySourceBandSynOlci, GeoToolS3SynOp.maxKernelRadius);
        makeFilledBand(sourceDataSynSlstr, targetRectangle, targetTileCopySourceBandSynSlstr, GeoToolS3SynOp.maxKernelRadius);

        int indexSynOlci;
        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                indexSynOlci = j * (sourceWidth) + i;
                // no data value 65535.0 -32768.0
                if (Float.isNaN(sourceDataSynOlci[indexSynOlci])) {
                    sourceDataMoveSynOlci[i][j] = Float.NaN;
                    statusData[indexSynOlci] = 1;
                } else {
                    sourceDataMoveSynOlci[i][j] = sourceDataSynOlci[indexSynOlci];
                    statusData[indexSynOlci] = 0;

                }
            }
        }

        // direction
        //  NW N NE    1 4 7
        //   W - E     2 5 8
        //  SW S SE    3 6 9
        //  0 noDataValue


        int indexSynSlstr;
        for (int k = -1; k < 2; k++) {
            for (int ll = -1; ll < 2; ll++) {

                direction += 1;

                for (int j = 0; j < sourceHeight; j++) {
                    for (int i = 0; i < sourceWidth; i++) {
                        sourceDataMoveSynSlstr[i][j] = Float.NaN;
                        correlationARRAY[i][j] = Float.NaN;
                    }
                }

                for (int j = 1; j < sourceHeight - 1; j++) {
                    for (int i = 1; i < sourceWidth - 1; i++) {
                        indexSynSlstr = (j - ll) * (sourceWidth) + (i - k);
                        if (!Float.isNaN(sourceDataSynSlstr[indexSynSlstr])) {
                            sourceDataMoveSynSlstr[i][j] = sourceDataSynSlstr[indexSynSlstr];
                        }


                    }
                }

//                // Todo for test of shifting helps - systematic bias
//                for (int j = 1; j < sourceHeight - 2; j++) {
//                    for (int i = 1; i < sourceWidth - 1; i++) {
//                        indexSynSlstr = (j - ll + 1) * (sourceWidth) + (i - k);
//                        if (sourceDataStatusSynOlci[indexSynSlstr] == 0 && sourceDataStatusSynSlstr[indexSynSlstr] == 0) {
//                            sourceDataMoveSynSlstr[i][j] = sourceDataSynSlstr[indexSynSlstr];
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
                                //kernelSizeArraySynOlci[(jj + corrKernelRadius) * (2 * corrKernelRadius + 1) + (ii + corrKernelRadius)] = sourceDataMoveSynOlci[i + ii][j + jj];
                                //kernelSizeArraySynSlstr[(jj + corrKernelRadius) * (2 * corrKernelRadius + 1) + (ii + corrKernelRadius)] = sourceDataMoveSynSlstr[i + ii][j + jj];
                                //System.out.printf("2. width height 3x3matrix width height:  %d  %d  %d   \n", i + ii, j + jj, (jj + corrKernelRadius) * (2 * corrKernelRadius + 1) + (ii + corrKernelRadius));
                                if (!Float.isNaN(sourceDataMoveSynOlci[i + ii][j + jj]) && !Float.isNaN(sourceDataMoveSynSlstr[i + ii][j + jj])) {
                                    kernelSizeArraySynOlci[indexValid] = sourceDataMoveSynOlci[i + ii][j + jj];
                                    kernelSizeArraySynSlstr[indexValid] = sourceDataMoveSynSlstr[i + ii][j + jj];
                                    indexValid++;
                                }
                            }
                        }
                        if (indexValid > 1) {
                            correlationARRAY[i][j] = GeoToolCorrelation.getPearsonCorrelation1(kernelSizeArraySynOlci, kernelSizeArraySynSlstr, indexValid);
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

        makeFilledBand(correlationMaxARRAY, targetRectangle, targetTileMaxCorr, GeoToolS3SynOp.maxKernelRadius);
        makeFilledBand(correlationDirARRAY, targetRectangle, targetTileMaxCorrDir, GeoToolS3SynOp.maxKernelRadius);

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
            super(GeoToolS3SynOp.class);
        }
    }
}
