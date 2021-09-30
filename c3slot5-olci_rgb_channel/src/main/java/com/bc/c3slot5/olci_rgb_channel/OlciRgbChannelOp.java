package com.bc.c3slot5.olci_rgb_channel;

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


@OperatorMetadata(alias = "OlciRgbChannel",
        label = "Algorithm for C3SLOT5 OLCI AC RGB Channel of OLCI SR composite products C3S LOT5",
        authors = "Marco Peters",
        copyright = "Brockmann Consult",
        version = "0.6")

public class OlciRgbChannelOp extends Operator {

    private static final String TYPE_SUFFIX = "_rgb";
    public static final int INT_NAN_VALUE = -9999;


    @SourceProduct()
    private Product sourceCompositeSr1;
    @SourceProduct()
    private Product sourceCompositeSr5;
    @SourceProduct()
    private Product sourceCompositeSr7;
    @SourceProduct()
    private Product sourceCompositeSr14;

    @SourceProduct()
    private Product sourceCompositeSr2;
    @SourceProduct()
    private Product sourceCompositeSr3;
    @SourceProduct()
    private Product sourceCompositeSr4;
    @SourceProduct()
    private Product sourceCompositeSr6;
    @SourceProduct()
    private Product sourceCompositeSr8;


    @TargetProduct
    private Product targetProduct;

    //      //Original TIFF
    private Band sourceBandSr1;
    static String sourceBandNameSr1 = "sr_1_mean";
    private Band sourceBandSr2;
    static String sourceBandNameSr2 = "sr_2_mean";
    private Band sourceBandSr3;
    static String sourceBandNameSr3 = "sr_3_mean";
    private Band sourceBandSr4;
    static String sourceBandNameSr4 = "sr_4_mean";
    private Band sourceBandSr5;
    static String sourceBandNameSr5 = "sr_5_mean";
    private Band sourceBandSr6;
    static String sourceBandNameSr6 = "sr_6_mean";
    private Band sourceBandSr7;
    static String sourceBandNameSr7 = "sr_7_mean";
    private Band sourceBandSr8;
    static String sourceBandNameSr8 = "sr_8_mean";

    // Original TIFF - RGB and FalseRGB
    private Band sourceBandSr14;
    static String sourceBandNameSr14 = "sr_14_mean";

//    // TEST TIFF
//    private Band sourceBandSr1;
//    static String sourceBandNameSr1 = "band_1";
//    private Band sourceBandSr2;
//    static String sourceBandNameSr2 = "band_1";
//    private Band sourceBandSr3;
//    static String sourceBandNameSr3 = "band_1";
//    private Band sourceBandSr4;
//    static String sourceBandNameSr4 = "band_1";
//    private Band sourceBandSr5;
//    static String sourceBandNameSr5 = "band_1";
//    private Band sourceBandSr6;
//    static String sourceBandNameSr6 = "band_1";
//    private Band sourceBandSr7;
//    static String sourceBandNameSr7 = "band_1";
//    private Band sourceBandSr8;
//    static String sourceBandNameSr8 = "band_1";
//    private Band sourceBandSr14;
//    static String sourceBandNameSr14 = "band_1";

    private Band targetBlueBand;
    static String targetBlueBandName = "BLUE";
    private Band targetGreenBand;
    static String targetGreenBandName = "GREEN";
    private Band targetRedBand;
    static String targetRedBandName = "RED";
    private Band targetNirBand;
    static String targetNirBandName = "NIR";

    @Override
    public void initialize() throws OperatorException {
        targetProduct = createTargetProduct();


        sourceBandSr1 = sourceCompositeSr1.getBand(sourceBandNameSr1);
        sourceBandSr5 = sourceCompositeSr5.getBand(sourceBandNameSr5);
        sourceBandSr7 = sourceCompositeSr7.getBand(sourceBandNameSr7);
        sourceBandSr14 = sourceCompositeSr14.getBand(sourceBandNameSr14);

        sourceBandSr2 = sourceCompositeSr2.getBand(sourceBandNameSr2);
        sourceBandSr3 = sourceCompositeSr3.getBand(sourceBandNameSr3);
        sourceBandSr4 = sourceCompositeSr4.getBand(sourceBandNameSr4);
        sourceBandSr6 = sourceCompositeSr6.getBand(sourceBandNameSr6);
        sourceBandSr8 = sourceCompositeSr8.getBand(sourceBandNameSr8);


        targetBlueBand = targetProduct.addBand(targetBlueBandName, ProductData.TYPE_INT32);
        targetGreenBand = targetProduct.addBand(targetGreenBandName, ProductData.TYPE_INT32);
        targetRedBand = targetProduct.addBand(targetRedBandName, ProductData.TYPE_INT32);
        targetNirBand = targetProduct.addBand(targetNirBandName, ProductData.TYPE_INT32);

        targetProduct.setPreferredTileSize(100, 100); //(366,366) (915, 915); //1500
    }

    private Product createTargetProduct() {
        String productType = sourceCompositeSr1.getProductType();
        String productName = sourceCompositeSr1.getName();
        if (!productType.endsWith(TYPE_SUFFIX)) {
            productType = productType + TYPE_SUFFIX;
            productName = productName + TYPE_SUFFIX;
        }
        Product product = new Product(productName, productType, sourceCompositeSr1.getSceneRasterWidth(), sourceCompositeSr1.getSceneRasterHeight());
        product.setPreferredTileSize(100, 100);
        /*Copies all properties from source product to the target product.*/
        ProductUtils.copyProductNodes(sourceCompositeSr1, product);
        return product;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws
            OperatorException {


        Tile sourceTileSr1 = getSourceTile(sourceBandSr1, targetRectangle);
        Tile sourceTileSr5 = getSourceTile(sourceBandSr5, targetRectangle);
        Tile sourceTileSr7 = getSourceTile(sourceBandSr7, targetRectangle);
        Tile sourceTileSr14 = getSourceTile(sourceBandSr14, targetRectangle);

        Tile sourceTileSr2 = getSourceTile(sourceBandSr2, targetRectangle);
        Tile sourceTileSr3 = getSourceTile(sourceBandSr3, targetRectangle);
        Tile sourceTileSr4 = getSourceTile(sourceBandSr4, targetRectangle);
        Tile sourceTileSr6 = getSourceTile(sourceBandSr6, targetRectangle);
        Tile sourceTileSr8 = getSourceTile(sourceBandSr8, targetRectangle);


        Tile targetTileBlueBand = targetTiles.get(targetBlueBand);
        Tile targetTileGreenBand = targetTiles.get(targetGreenBand);
        Tile targetTileRedBand = targetTiles.get(targetRedBand);
        Tile targetTileNirBand = targetTiles.get(targetNirBand);

        final float[] sourceDataSr1 = sourceTileSr1.getSamplesFloat();
        final float[] sourceDataSr5 = sourceTileSr5.getSamplesFloat();
        final float[] sourceDataSr7 = sourceTileSr7.getSamplesFloat();
        final float[] sourceDataSr14 = sourceTileSr14.getSamplesFloat();

        final float[] sourceDataSr2 = sourceTileSr2.getSamplesFloat();
        final float[] sourceDataSr3 = sourceTileSr3.getSamplesFloat();
        final float[] sourceDataSr4 = sourceTileSr4.getSamplesFloat();
        final float[] sourceDataSr6 = sourceTileSr6.getSamplesFloat();
        final float[] sourceDataSr8 = sourceTileSr8.getSamplesFloat();


        int targetWidth = targetRectangle.width;
        int targetHeight = targetRectangle.height;
        int targetLength = targetWidth * targetHeight;
        //System.out.printf("targetWidth: %d targetHeight: %d\n", targetRectangle.x + targetWidth, targetRectangle.y + targetHeight);


        final int[] blueData = new int[targetLength];
        final int[] greenData = new int[targetLength];
        final int[] redData = new int[targetLength];
        final int[] nirData = new int[targetLength];

        Arrays.fill(blueData, 0);
        Arrays.fill(greenData, 0);
        Arrays.fill(redData, 0);


        double blue;
        double green;
        double red;

        for (int i = 0; i < targetLength; i++) {

//            if (!Float.isNaN(sourceDataSr1[i]) &&   !Float.isNaN(sourceDataSr5[i]) &&
//                    !Float.isNaN(sourceDataSr7[i]) &&   !Float.isNaN(sourceDataSr14[i])) {

            if (!Float.isNaN(sourceDataSr1[i]) && !Float.isNaN(sourceDataSr2[i]) &&
                    !Float.isNaN(sourceDataSr3[i]) && !Float.isNaN(sourceDataSr4[i]) &&
                    !Float.isNaN(sourceDataSr5[i]) && !Float.isNaN(sourceDataSr6[i]) &&
                    !Float.isNaN(sourceDataSr7[i]) && !Float.isNaN(sourceDataSr8[i]) &&
                    !Float.isNaN(sourceDataSr14[i])) {

                //TODO Tristimulus RGB
                blue = Math.log(1.0 + 0.28 * sourceDataSr1[i] + 1.77 * sourceDataSr2[i] +
                        0.47 * sourceDataSr3[i] + 0.16 * sourceDataSr4[i]);
                green = Math.log(1.0 + 0.26 * sourceDataSr2[i] + 0.21 * sourceDataSr3[i] +
                        0.50 * sourceDataSr4[i] + sourceDataSr5[i] + 0.38 * sourceDataSr6[i] + 0.04 * sourceDataSr7[i] +
                        0.02 * sourceDataSr8[i]);
                red = Math.log(1.0 + 0.09 * sourceDataSr1[i] + 0.35 * sourceDataSr2[i] +
                        0.04 * sourceDataSr3[i] + 0.01 * sourceDataSr4[i] + 0.59 * sourceDataSr5[i] +
                        0.85 * sourceDataSr6[i] + 0.12 * sourceDataSr7[i] + 0.04 * sourceDataSr8[i]);
                blueData[i] = (int) Math.floor(blue * 10000);
                greenData[i] = (int) Math.floor(green * 10000);
                redData[i] = (int) Math.floor(red * 10000);
                nirData[i] = (int) Math.floor(sourceDataSr14[i] * 10000);
                //TODO RGB and FalseRGB
//                blueData[i] = (int) Math.floor(sourceDataSr1[i] * 1000);
//                greenData[i] = (int) Math.floor(sourceDataSr5[i] * 1000);
//                redData[i] = (int) Math.floor(sourceDataSr7[i] * 1000);
//                nirData[i] = (int) Math.floor(sourceDataSr14[i] * 1000);
            }
        }


        makeFilledBand(blueData, targetRectangle, targetTileBlueBand);
        makeFilledBand(greenData, targetRectangle, targetTileGreenBand);
        makeFilledBand(redData, targetRectangle, targetTileRedBand);
        makeFilledBand(nirData, targetRectangle, targetTileNirBand);
//        System.out.printf("target product xPos yPos:  %d  %d   \n",targetRectangle.x, targetRectangle.y );

    }


    private static void makeFilledBand
            (
                    int[] inputData,
                    Rectangle targetRectangle,
                    Tile targetTileOutputBand) {

        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        //System.out.printf("rectangle:  %d  %d _______________ rectangle_target_input_data:  %d  %d  \n", targetRectangle.width, targetRectangle.height,inputDataWidth, inputDataHeight);
        for (int y = 0; y < targetRectangle.height; y++) {
            for (int x = 0; x < targetRectangle.width; x++) {
                targetTileOutputBand.setSample(x + xLocation, y + yLocation, inputData[y * (targetRectangle.width) + x]);
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
            super(OlciRgbChannelOp.class);
        }
    }
}
