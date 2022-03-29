package com.bc.c3slot5.olciP1Drules;

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
import org.esa.snap.core.util.ProductUtils;


import java.awt.*;
import java.util.Map;


@OperatorMetadata(alias = "OlciP1DRules",
        label = "Algorithm for P1D composite C3S LOT5",
        authors = "Marco Peters",
        copyright = "Brockmann Consult",
        version = "0.6")

public class OlciP1DRulesOp extends Operator {

    private static final String TYPE_SUFFIX = "_comp";
    public static final int INT_NAN_VALUE = -9999;

    @SourceProduct
    private Product sourceProduct;

//    @Parameter(rasterDataNodeType = Band.class, description = "OLCI band")
//    private String sourceBandNameSynOlci;
//
//    @Parameter(rasterDataNodeType = Band.class, description = "SLSTR band")
//    private String sourceBandNameSynSlstr;

    @TargetProduct
    private Product targetProduct;

    private Band sourceBandSr1;
    private Band sourceBandSr2;
    private Band sourceBandSr3;
    private Band sourceBandSr4;
    private Band sourceBandSr5;
    private Band sourceBandSr6;
    private Band sourceBandSr7;
    private Band sourceBandSr8;
    private Band sourceBandSr9;
    private Band sourceBandSr10;
    private Band sourceBandSr11;
    private Band sourceBandSr12;
    private Band sourceBandSr13;
    private Band sourceBandSr14;
    private Band sourceBandSr15;
    private Band sourceBandOAA;
    private Band sourceBandOZA;
    private Band sourceBandSAA;
    private Band sourceBandSZA;
    private Band sourceBandFlag;
    private Band sourceBandDetectorIndex;

    private Band sourceBandSr1_B;
    private Band sourceBandSr2_B;
    private Band sourceBandSr3_B;
    private Band sourceBandSr4_B;
    private Band sourceBandSr5_B;
    private Band sourceBandSr6_B;
    private Band sourceBandSr7_B;
    private Band sourceBandSr8_B;
    private Band sourceBandSr9_B;
    private Band sourceBandSr10_B;
    private Band sourceBandSr11_B;
    private Band sourceBandSr12_B;
    private Band sourceBandSr13_B;
    private Band sourceBandSr14_B;
    private Band sourceBandSr15_B;
    private Band sourceBandOAA_B;
    private Band sourceBandOZA_B;
    private Band sourceBandSAA_B;
    private Band sourceBandSZA_B;
    private Band sourceBandFlag_B;
    private Band sourceBandDetectorIndex_B;

    static String sourceBandNameSr1 = "sdr_1";
    static String sourceBandNameSr2 = "sdr_2";
    static String sourceBandNameSr3 = "sdr_3";
    static String sourceBandNameSr4 = "sdr_4";
    static String sourceBandNameSr5 = "sdr_5";
    static String sourceBandNameSr6 = "sdr_6";
    static String sourceBandNameSr7 = "sdr_7";
    static String sourceBandNameSr8 = "sdr_8";
    static String sourceBandNameSr9 = "sdr_9";
    static String sourceBandNameSr10 = "sdr_10";
    static String sourceBandNameSr11 = "sdr_11";
    static String sourceBandNameSr12 = "sdr_12";
    static String sourceBandNameSr13 = "sdr_13";
    static String sourceBandNameSr14 = "sdr_14";
    static String sourceBandNameSr15 = "sdr_15";
    static String sourceBandNameOAA = "OAA";
    static String sourceBandNameOZA = "OZA";
    static String sourceBandNameSAA = "SAA";
    static String sourceBandNameSZA = "SZA";
    static String sourceBandNameFlag = "pixel_classif_flags";
    static String sourceBandNameDetectorIndex = "detector_index";

    private Band targetCopySourceBandSr1;
    private Band targetCopySourceBandSr2;
    private Band targetCopySourceBandSr3;
    private Band targetCopySourceBandSr4;
    private Band targetCopySourceBandSr5;
    private Band targetCopySourceBandSr6;
    private Band targetCopySourceBandSr7;
    private Band targetCopySourceBandSr8;
    private Band targetCopySourceBandSr9;
    private Band targetCopySourceBandSr10;
    private Band targetCopySourceBandSr11;
    private Band targetCopySourceBandSr12;
    private Band targetCopySourceBandSr13;
    private Band targetCopySourceBandSr14;
    private Band targetCopySourceBandSr15;
    private Band targetBandProduct;
    private Band targetBandStatus;


    static String targetCopySourceBandNameSr1 = sourceBandNameSr1;
    static String targetCopySourceBandNameSr2 = sourceBandNameSr2;
    static String targetCopySourceBandNameSr3 = sourceBandNameSr3;
    static String targetCopySourceBandNameSr4 = sourceBandNameSr4;
    static String targetCopySourceBandNameSr5 = sourceBandNameSr5;
    static String targetCopySourceBandNameSr6 = sourceBandNameSr6;
    static String targetCopySourceBandNameSr7 = sourceBandNameSr7;
    static String targetCopySourceBandNameSr8 = sourceBandNameSr8;
    static String targetCopySourceBandNameSr9 = sourceBandNameSr9;
    static String targetCopySourceBandNameSr10 = sourceBandNameSr10;
    static String targetCopySourceBandNameSr11 = sourceBandNameSr11;
    static String targetCopySourceBandNameSr12 = sourceBandNameSr12;
    static String targetCopySourceBandNameSr13 = sourceBandNameSr13;
    static String targetCopySourceBandNameSr14 = sourceBandNameSr14;
    static String targetCopySourceBandNameSr15 = sourceBandNameSr15;
    static String targetBandNameProduct = "Product_Master_Slave";
    static String targetBandNameStatus = "Status";

    @Override
    public void initialize() throws OperatorException {
        targetProduct = createTargetProduct();

        sourceBandSr1 = sourceProduct.getBand(sourceBandNameSr1 + "_M");
        sourceBandSr2 = sourceProduct.getBand(sourceBandNameSr2 + "_M");
        sourceBandSr3 = sourceProduct.getBand(sourceBandNameSr3 + "_M");
        sourceBandSr4 = sourceProduct.getBand(sourceBandNameSr4 + "_M");
        sourceBandSr5 = sourceProduct.getBand(sourceBandNameSr5 + "_M");
        sourceBandSr6 = sourceProduct.getBand(sourceBandNameSr6 + "_M");
        sourceBandSr7 = sourceProduct.getBand(sourceBandNameSr7 + "_M");
        sourceBandSr8 = sourceProduct.getBand(sourceBandNameSr8 + "_M");
        sourceBandSr9 = sourceProduct.getBand(sourceBandNameSr9 + "_M");
        sourceBandSr10 = sourceProduct.getBand(sourceBandNameSr10 + "_M");
        sourceBandSr11 = sourceProduct.getBand(sourceBandNameSr11 + "_M");
        sourceBandSr12 = sourceProduct.getBand(sourceBandNameSr12 + "_M");
        sourceBandSr13 = sourceProduct.getBand(sourceBandNameSr13 + "_M");
        sourceBandSr14 = sourceProduct.getBand(sourceBandNameSr14 + "_M");
        sourceBandSr15 = sourceProduct.getBand(sourceBandNameSr15 + "_M");
        sourceBandOAA = sourceProduct.getBand(sourceBandNameOAA + "_M");
        sourceBandOZA = sourceProduct.getBand(sourceBandNameOZA + "_M");
        sourceBandSAA = sourceProduct.getBand(sourceBandNameSAA + "_M");
        sourceBandSZA = sourceProduct.getBand(sourceBandNameSZA + "_M");
        sourceBandFlag = sourceProduct.getBand(sourceBandNameFlag + "_M");
        sourceBandDetectorIndex = sourceProduct.getBand(sourceBandNameDetectorIndex + "_M");

        sourceBandSr1_B = sourceProduct.getBand(sourceBandNameSr1 + "_S");
        sourceBandSr2_B = sourceProduct.getBand(sourceBandNameSr2 + "_S");
        sourceBandSr3_B = sourceProduct.getBand(sourceBandNameSr3 + "_S");
        sourceBandSr4_B = sourceProduct.getBand(sourceBandNameSr4 + "_S");
        sourceBandSr5_B = sourceProduct.getBand(sourceBandNameSr5 + "_S");
        sourceBandSr6_B = sourceProduct.getBand(sourceBandNameSr6 + "_S");
        sourceBandSr7_B = sourceProduct.getBand(sourceBandNameSr7 + "_S");
        sourceBandSr8_B = sourceProduct.getBand(sourceBandNameSr8 + "_S");
        sourceBandSr9_B = sourceProduct.getBand(sourceBandNameSr9 + "_S");
        sourceBandSr10_B = sourceProduct.getBand(sourceBandNameSr10 + "_S");
        sourceBandSr11_B = sourceProduct.getBand(sourceBandNameSr11 + "_S");
        sourceBandSr12_B = sourceProduct.getBand(sourceBandNameSr12 + "_S");
        sourceBandSr13_B = sourceProduct.getBand(sourceBandNameSr13 + "_S");
        sourceBandSr14_B = sourceProduct.getBand(sourceBandNameSr14 + "_S");
        sourceBandSr15_B = sourceProduct.getBand(sourceBandNameSr15 + "_S");
        sourceBandOAA_B = sourceProduct.getBand(sourceBandNameOAA + "_S");
        sourceBandOZA_B = sourceProduct.getBand(sourceBandNameOZA + "_S");
        sourceBandSAA_B = sourceProduct.getBand(sourceBandNameSAA + "_S");
        sourceBandSZA_B = sourceProduct.getBand(sourceBandNameSZA + "_S");
        sourceBandFlag_B = sourceProduct.getBand(sourceBandNameFlag + "_S");
        sourceBandDetectorIndex_B = sourceProduct.getBand(sourceBandNameDetectorIndex + "_S");

        targetCopySourceBandSr1 = targetProduct.addBand(targetCopySourceBandNameSr1, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr2 = targetProduct.addBand(targetCopySourceBandNameSr2, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr3 = targetProduct.addBand(targetCopySourceBandNameSr3, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr4 = targetProduct.addBand(targetCopySourceBandNameSr4, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr5 = targetProduct.addBand(targetCopySourceBandNameSr5, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr6 = targetProduct.addBand(targetCopySourceBandNameSr6, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr7 = targetProduct.addBand(targetCopySourceBandNameSr7, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr8 = targetProduct.addBand(targetCopySourceBandNameSr8, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr9 = targetProduct.addBand(targetCopySourceBandNameSr9, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr10 = targetProduct.addBand(targetCopySourceBandNameSr10, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr11 = targetProduct.addBand(targetCopySourceBandNameSr11, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr12 = targetProduct.addBand(targetCopySourceBandNameSr12, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr13 = targetProduct.addBand(targetCopySourceBandNameSr13, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr14 = targetProduct.addBand(targetCopySourceBandNameSr14, ProductData.TYPE_FLOAT32);
        targetCopySourceBandSr15 = targetProduct.addBand(targetCopySourceBandNameSr15, ProductData.TYPE_FLOAT32);
        targetBandProduct = targetProduct.addBand(targetBandNameProduct, ProductData.TYPE_INT8);
        targetBandStatus = targetProduct.addBand(targetBandNameStatus, ProductData.TYPE_INT16);

        targetCopySourceBandSr1.setUnit(sourceBandSr1.getUnit());
        targetCopySourceBandSr2.setUnit(sourceBandSr2.getUnit());
        targetCopySourceBandSr3.setUnit(sourceBandSr3.getUnit());
        targetCopySourceBandSr4.setUnit(sourceBandSr4.getUnit());
        targetCopySourceBandSr5.setUnit(sourceBandSr5.getUnit());
        targetCopySourceBandSr6.setUnit(sourceBandSr6.getUnit());
        targetCopySourceBandSr7.setUnit(sourceBandSr7.getUnit());
        targetCopySourceBandSr8.setUnit(sourceBandSr8.getUnit());
        targetCopySourceBandSr9.setUnit(sourceBandSr9.getUnit());
        targetCopySourceBandSr10.setUnit(sourceBandSr10.getUnit());
        targetCopySourceBandSr11.setUnit(sourceBandSr11.getUnit());
        targetCopySourceBandSr12.setUnit(sourceBandSr12.getUnit());
        targetCopySourceBandSr13.setUnit(sourceBandSr13.getUnit());
        targetCopySourceBandSr14.setUnit(sourceBandSr14.getUnit());
        targetCopySourceBandSr15.setUnit(sourceBandSr15.getUnit());

        targetProduct.setPreferredTileSize(122, 122); //(366,366) (915, 915); //1500
        // targetProduct.setPreferredTileSize(new Dimension(targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight()));
    }


    private Product createTargetProduct() {
        String productType = sourceProduct.getProductType();
        String productName = sourceProduct.getName();
        if (!productType.endsWith(TYPE_SUFFIX)) {
            productType = productType + TYPE_SUFFIX;
            productName = productName + TYPE_SUFFIX;
        }
        Product product = new Product(productName, productType, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        product.setPreferredTileSize(700, 700);
        /*Copies all properties from source product to the target product.*/
        ProductUtils.copyProductNodes(sourceProduct, product);

        return product;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws
            OperatorException {

        Tile sourceTileSr1 = getSourceTile(sourceBandSr1, targetRectangle);
        Tile sourceTileSr2 = getSourceTile(sourceBandSr2, targetRectangle);
        Tile sourceTileSr3 = getSourceTile(sourceBandSr3, targetRectangle);
        Tile sourceTileSr4 = getSourceTile(sourceBandSr4, targetRectangle);
        Tile sourceTileSr5 = getSourceTile(sourceBandSr5, targetRectangle);
        Tile sourceTileSr6 = getSourceTile(sourceBandSr6, targetRectangle);
        Tile sourceTileSr7 = getSourceTile(sourceBandSr7, targetRectangle);
        Tile sourceTileSr8 = getSourceTile(sourceBandSr8, targetRectangle);
        Tile sourceTileSr9 = getSourceTile(sourceBandSr9, targetRectangle);
        Tile sourceTileSr10 = getSourceTile(sourceBandSr10, targetRectangle);
        Tile sourceTileSr11 = getSourceTile(sourceBandSr11, targetRectangle);
        Tile sourceTileSr12 = getSourceTile(sourceBandSr12, targetRectangle);
        Tile sourceTileSr13 = getSourceTile(sourceBandSr13, targetRectangle);
        Tile sourceTileSr14 = getSourceTile(sourceBandSr14, targetRectangle);
        Tile sourceTileSr15 = getSourceTile(sourceBandSr15, targetRectangle);
        Tile sourceTileOAA = getSourceTile(sourceBandOAA, targetRectangle);
        Tile sourceTileOZA = getSourceTile(sourceBandOZA, targetRectangle);
        Tile sourceTileSAA = getSourceTile(sourceBandSAA, targetRectangle);
        Tile sourceTileSZA = getSourceTile(sourceBandSZA, targetRectangle);
        Tile sourceTileFlag = getSourceTile(sourceBandFlag, targetRectangle);
        Tile sourceTileDetectorIndex = getSourceTile(sourceBandDetectorIndex, targetRectangle);

        Tile sourceTileSr1_B = getSourceTile(sourceBandSr1_B, targetRectangle);
        Tile sourceTileSr2_B = getSourceTile(sourceBandSr2_B, targetRectangle);
        Tile sourceTileSr3_B = getSourceTile(sourceBandSr3_B, targetRectangle);
        Tile sourceTileSr4_B = getSourceTile(sourceBandSr4_B, targetRectangle);
        Tile sourceTileSr5_B = getSourceTile(sourceBandSr5_B, targetRectangle);
        Tile sourceTileSr6_B = getSourceTile(sourceBandSr6_B, targetRectangle);
        Tile sourceTileSr7_B = getSourceTile(sourceBandSr7_B, targetRectangle);
        Tile sourceTileSr8_B = getSourceTile(sourceBandSr8_B, targetRectangle);
        Tile sourceTileSr9_B = getSourceTile(sourceBandSr9_B, targetRectangle);
        Tile sourceTileSr10_B = getSourceTile(sourceBandSr10_B, targetRectangle);
        Tile sourceTileSr11_B = getSourceTile(sourceBandSr11_B, targetRectangle);
        Tile sourceTileSr12_B = getSourceTile(sourceBandSr12_B, targetRectangle);
        Tile sourceTileSr13_B = getSourceTile(sourceBandSr13_B, targetRectangle);
        Tile sourceTileSr14_B = getSourceTile(sourceBandSr14_B, targetRectangle);
        Tile sourceTileSr15_B = getSourceTile(sourceBandSr15_B, targetRectangle);
        Tile sourceTileOAA_B = getSourceTile(sourceBandOAA_B, targetRectangle);
        Tile sourceTileOZA_B = getSourceTile(sourceBandOZA_B, targetRectangle);
        Tile sourceTileSAA_B = getSourceTile(sourceBandSAA_B, targetRectangle);
        Tile sourceTileSZA_B = getSourceTile(sourceBandSZA_B, targetRectangle);
        Tile sourceTileFlag_B = getSourceTile(sourceBandFlag_B, targetRectangle);
        Tile sourceTileDetectorIndex_B = getSourceTile(sourceBandDetectorIndex_B, targetRectangle);

        Tile targetTileCopySourceBandSr1 = targetTiles.get(targetCopySourceBandSr1);
        Tile targetTileCopySourceBandSr2 = targetTiles.get(targetCopySourceBandSr2);
        Tile targetTileCopySourceBandSr3 = targetTiles.get(targetCopySourceBandSr3);
        Tile targetTileCopySourceBandSr4 = targetTiles.get(targetCopySourceBandSr4);
        Tile targetTileCopySourceBandSr5 = targetTiles.get(targetCopySourceBandSr5);
        Tile targetTileCopySourceBandSr6 = targetTiles.get(targetCopySourceBandSr6);
        Tile targetTileCopySourceBandSr7 = targetTiles.get(targetCopySourceBandSr7);
        Tile targetTileCopySourceBandSr8 = targetTiles.get(targetCopySourceBandSr8);
        Tile targetTileCopySourceBandSr9 = targetTiles.get(targetCopySourceBandSr9);
        Tile targetTileCopySourceBandSr10 = targetTiles.get(targetCopySourceBandSr10);
        Tile targetTileCopySourceBandSr11 = targetTiles.get(targetCopySourceBandSr11);
        Tile targetTileCopySourceBandSr12 = targetTiles.get(targetCopySourceBandSr12);
        Tile targetTileCopySourceBandSr13 = targetTiles.get(targetCopySourceBandSr13);
        Tile targetTileCopySourceBandSr14 = targetTiles.get(targetCopySourceBandSr14);
        Tile targetTileCopySourceBandSr15 = targetTiles.get(targetCopySourceBandSr15);
        Tile targetTileProduct = targetTiles.get(targetBandProduct);
        Tile targetTileStatus = targetTiles.get(targetBandStatus);

        final float[] sourceDataSr1 = sourceTileSr1.getSamplesFloat();
        final float[] sourceDataSr2 = sourceTileSr2.getSamplesFloat();
        final float[] sourceDataSr3 = sourceTileSr3.getSamplesFloat();
        final float[] sourceDataSr4 = sourceTileSr4.getSamplesFloat();
        final float[] sourceDataSr5 = sourceTileSr5.getSamplesFloat();
        final float[] sourceDataSr6 = sourceTileSr6.getSamplesFloat();
        final float[] sourceDataSr7 = sourceTileSr7.getSamplesFloat();
        final float[] sourceDataSr8 = sourceTileSr8.getSamplesFloat();
        final float[] sourceDataSr9 = sourceTileSr9.getSamplesFloat();
        final float[] sourceDataSr10 = sourceTileSr10.getSamplesFloat();
        final float[] sourceDataSr11 = sourceTileSr11.getSamplesFloat();
        final float[] sourceDataSr12 = sourceTileSr12.getSamplesFloat();
        final float[] sourceDataSr13 = sourceTileSr13.getSamplesFloat();
        final float[] sourceDataSr14 = sourceTileSr14.getSamplesFloat();
        final float[] sourceDataSr15 = sourceTileSr15.getSamplesFloat();
        final float[] sourceDataOAA = sourceTileOAA.getSamplesFloat();
        final float[] sourceDataOZA = sourceTileOZA.getSamplesFloat();
        final float[] sourceDataSAA = sourceTileSAA.getSamplesFloat();
        final float[] sourceDataSZA = sourceTileSZA.getSamplesFloat();
        final int[] sourceDataDetectorIndex = sourceTileDetectorIndex.getSamplesInt();

        final float[] sourceDataSr1_B = sourceTileSr1_B.getSamplesFloat();
        final float[] sourceDataSr2_B = sourceTileSr2_B.getSamplesFloat();
        final float[] sourceDataSr3_B = sourceTileSr3_B.getSamplesFloat();
        final float[] sourceDataSr4_B = sourceTileSr4_B.getSamplesFloat();
        final float[] sourceDataSr5_B = sourceTileSr5_B.getSamplesFloat();
        final float[] sourceDataSr6_B = sourceTileSr6_B.getSamplesFloat();
        final float[] sourceDataSr7_B = sourceTileSr7_B.getSamplesFloat();
        final float[] sourceDataSr8_B = sourceTileSr8_B.getSamplesFloat();
        final float[] sourceDataSr9_B = sourceTileSr9_B.getSamplesFloat();
        final float[] sourceDataSr10_B = sourceTileSr10_B.getSamplesFloat();
        final float[] sourceDataSr11_B = sourceTileSr11_B.getSamplesFloat();
        final float[] sourceDataSr12_B = sourceTileSr12_B.getSamplesFloat();
        final float[] sourceDataSr13_B = sourceTileSr13_B.getSamplesFloat();
        final float[] sourceDataSr14_B = sourceTileSr14_B.getSamplesFloat();
        final float[] sourceDataSr15_B = sourceTileSr15_B.getSamplesFloat();
        final float[] sourceDataOAA_B = sourceTileOAA_B.getSamplesFloat();
        final float[] sourceDataOZA_B = sourceTileOZA_B.getSamplesFloat();
        final float[] sourceDataSAA_B = sourceTileSAA_B.getSamplesFloat();
        final float[] sourceDataSZA_B = sourceTileSZA_B.getSamplesFloat();
        final int[] sourceDataDetectorIndex_B = sourceTileDetectorIndex_B.getSamplesInt();
        final int[] sourceDataFlag = sourceTileFlag.getSamplesInt();
        final int[] sourceDataFlag_B = sourceTileFlag_B.getSamplesInt();


        int sourceWidth = targetRectangle.width;
        int sourceHeight = targetRectangle.height;
        int sourceLength = sourceWidth * sourceHeight;

        System.out.printf("sourceWidth: %d sourceHeight: %d\n", sourceWidth, sourceHeight);

        final float[] dataSr1 = new float[sourceLength];
        final float[] dataSr2 = new float[sourceLength];
        final float[] dataSr3 = new float[sourceLength];
        final float[] dataSr4 = new float[sourceLength];
        final float[] dataSr5 = new float[sourceLength];
        final float[] dataSr6 = new float[sourceLength];
        final float[] dataSr7 = new float[sourceLength];
        final float[] dataSr8 = new float[sourceLength];
        final float[] dataSr9 = new float[sourceLength];
        final float[] dataSr10 = new float[sourceLength];
        final float[] dataSr11 = new float[sourceLength];
        final float[] dataSr12 = new float[sourceLength];
        final float[] dataSr13 = new float[sourceLength];
        final float[] dataSr14 = new float[sourceLength];
        final float[] dataSr15 = new float[sourceLength];
        final int[] productNumber = new int[sourceLength];
        final int[] statusNumber = new int[sourceLength];
//        invalid= 0, clear_land= 1; clear water=2, clear_snow_ice=3, cloud=4, cloud shadow=5


        int indexOlci;
        for (int j = 0; j < sourceHeight; j++) {
            for (int i = 0; i < sourceWidth; i++) {
                indexOlci = j * (sourceWidth) + i;
                int status = analyseSatus(sourceDataFlag[indexOlci], sourceDataDetectorIndex[indexOlci]);
                int status_B = analyseSatus(sourceDataFlag_B[indexOlci], sourceDataDetectorIndex_B[indexOlci]);
                int s = status * 100 + status_B;
                statusNumber[indexOlci] = s;
                float ndvi = (sourceDataSr14[indexOlci] + sourceDataSr7[indexOlci]) / (sourceDataSr14[indexOlci] + sourceDataSr7[indexOlci]);
                float ndvi_B = (sourceDataSr14_B[indexOlci] + sourceDataSr7_B[indexOlci]) / (sourceDataSr14_B[indexOlci] + sourceDataSr7_B[indexOlci]);
                switch (s) {
                    case 0:
                        dataSr1[indexOlci] = Float.NaN;
                        dataSr2[indexOlci] = Float.NaN;
                        dataSr3[indexOlci] = Float.NaN;
                        dataSr4[indexOlci] = Float.NaN;
                        dataSr5[indexOlci] = Float.NaN;
                        dataSr6[indexOlci] = Float.NaN;
                        dataSr7[indexOlci] = Float.NaN;
                        dataSr8[indexOlci] = Float.NaN;
                        dataSr9[indexOlci] = Float.NaN;
                        dataSr10[indexOlci] = Float.NaN;
                        dataSr11[indexOlci] = Float.NaN;
                        dataSr12[indexOlci] = Float.NaN;
                        dataSr13[indexOlci] = Float.NaN;
                        dataSr14[indexOlci] = Float.NaN;
                        dataSr15[indexOlci] = Float.NaN;
                        productNumber[indexOlci] = 0;
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 201:
                    case 203:
                    case 301:
                    case 401:
                    case 402:
                    case 403:
                    case 405:
                    case 501:
                    case 502:
                    case 503:
                        dataSr1[indexOlci] = sourceDataSr1_B[indexOlci];
                        dataSr2[indexOlci] = sourceDataSr2_B[indexOlci];
                        dataSr3[indexOlci] = sourceDataSr3_B[indexOlci];
                        dataSr4[indexOlci] = sourceDataSr4_B[indexOlci];
                        dataSr5[indexOlci] = sourceDataSr5_B[indexOlci];
                        dataSr6[indexOlci] = sourceDataSr6_B[indexOlci];
                        dataSr7[indexOlci] = sourceDataSr7_B[indexOlci];
                        dataSr8[indexOlci] = sourceDataSr8_B[indexOlci];
                        dataSr9[indexOlci] = sourceDataSr9_B[indexOlci];
                        dataSr10[indexOlci] = sourceDataSr10_B[indexOlci];
                        dataSr11[indexOlci] = sourceDataSr11_B[indexOlci];
                        dataSr12[indexOlci] = sourceDataSr12_B[indexOlci];
                        dataSr13[indexOlci] = sourceDataSr13_B[indexOlci];
                        dataSr14[indexOlci] = sourceDataSr14_B[indexOlci];
                        dataSr15[indexOlci] = sourceDataSr15_B[indexOlci];
                        productNumber[indexOlci] = 2;
                        break;
                    case 100:
                    case 200:
                    case 300:
                    case 400:
                    case 500:
                    case 102:
                    case 103:
                    case 104:
                    case 105:
                    case 204:
                    case 205:
                    case 302:
                    case 304:
                    case 305:
                    case 504:
                        dataSr1[indexOlci] = sourceDataSr1[indexOlci];
                        dataSr2[indexOlci] = sourceDataSr2[indexOlci];
                        dataSr3[indexOlci] = sourceDataSr3[indexOlci];
                        dataSr4[indexOlci] = sourceDataSr4[indexOlci];
                        dataSr5[indexOlci] = sourceDataSr5[indexOlci];
                        dataSr6[indexOlci] = sourceDataSr6[indexOlci];
                        dataSr7[indexOlci] = sourceDataSr7[indexOlci];
                        dataSr8[indexOlci] = sourceDataSr8[indexOlci];
                        dataSr9[indexOlci] = sourceDataSr9[indexOlci];
                        dataSr10[indexOlci] = sourceDataSr10[indexOlci];
                        dataSr11[indexOlci] = sourceDataSr11[indexOlci];
                        dataSr12[indexOlci] = sourceDataSr12[indexOlci];
                        dataSr13[indexOlci] = sourceDataSr13[indexOlci];
                        dataSr14[indexOlci] = sourceDataSr14[indexOlci];
                        dataSr15[indexOlci] = sourceDataSr15[indexOlci];
                        productNumber[indexOlci] = 1;
                        break;
                    case 404:
                        if (sourceDataOZA[indexOlci] <= sourceDataOZA_B[indexOlci]) {
                            dataSr1[indexOlci] = sourceDataSr1[indexOlci];
                            dataSr2[indexOlci] = sourceDataSr2[indexOlci];
                            dataSr3[indexOlci] = sourceDataSr3[indexOlci];
                            dataSr4[indexOlci] = sourceDataSr4[indexOlci];
                            dataSr5[indexOlci] = sourceDataSr5[indexOlci];
                            dataSr6[indexOlci] = sourceDataSr6[indexOlci];
                            dataSr7[indexOlci] = sourceDataSr7[indexOlci];
                            dataSr8[indexOlci] = sourceDataSr8[indexOlci];
                            dataSr9[indexOlci] = sourceDataSr9[indexOlci];
                            dataSr10[indexOlci] = sourceDataSr10[indexOlci];
                            dataSr11[indexOlci] = sourceDataSr11[indexOlci];
                            dataSr12[indexOlci] = sourceDataSr12[indexOlci];
                            dataSr13[indexOlci] = sourceDataSr13[indexOlci];
                            dataSr14[indexOlci] = sourceDataSr14[indexOlci];
                            dataSr15[indexOlci] = sourceDataSr15[indexOlci];
                            productNumber[indexOlci] = 1;
                        } else {
                            dataSr1[indexOlci] = sourceDataSr1_B[indexOlci];
                            dataSr2[indexOlci] = sourceDataSr2_B[indexOlci];
                            dataSr3[indexOlci] = sourceDataSr3_B[indexOlci];
                            dataSr4[indexOlci] = sourceDataSr4_B[indexOlci];
                            dataSr5[indexOlci] = sourceDataSr5_B[indexOlci];
                            dataSr6[indexOlci] = sourceDataSr6_B[indexOlci];
                            dataSr7[indexOlci] = sourceDataSr7_B[indexOlci];
                            dataSr8[indexOlci] = sourceDataSr8_B[indexOlci];
                            dataSr9[indexOlci] = sourceDataSr9_B[indexOlci];
                            dataSr10[indexOlci] = sourceDataSr10_B[indexOlci];
                            dataSr11[indexOlci] = sourceDataSr11_B[indexOlci];
                            dataSr12[indexOlci] = sourceDataSr12_B[indexOlci];
                            dataSr13[indexOlci] = sourceDataSr13_B[indexOlci];
                            dataSr14[indexOlci] = sourceDataSr14_B[indexOlci];
                            dataSr15[indexOlci] = sourceDataSr15_B[indexOlci];
                            productNumber[indexOlci] = 2;
                        }
                    case 202:
                    case 303:
                    case 505:
                        if (sourceDataSr1[indexOlci] <= sourceDataSr1_B[indexOlci]) {
                            dataSr1[indexOlci] = sourceDataSr1[indexOlci];
                            dataSr2[indexOlci] = sourceDataSr2[indexOlci];
                            dataSr3[indexOlci] = sourceDataSr3[indexOlci];
                            dataSr4[indexOlci] = sourceDataSr4[indexOlci];
                            dataSr5[indexOlci] = sourceDataSr5[indexOlci];
                            dataSr6[indexOlci] = sourceDataSr6[indexOlci];
                            dataSr7[indexOlci] = sourceDataSr7[indexOlci];
                            dataSr8[indexOlci] = sourceDataSr8[indexOlci];
                            dataSr9[indexOlci] = sourceDataSr9[indexOlci];
                            dataSr10[indexOlci] = sourceDataSr10[indexOlci];
                            dataSr11[indexOlci] = sourceDataSr11[indexOlci];
                            dataSr12[indexOlci] = sourceDataSr12[indexOlci];
                            dataSr13[indexOlci] = sourceDataSr13[indexOlci];
                            dataSr14[indexOlci] = sourceDataSr14[indexOlci];
                            dataSr15[indexOlci] = sourceDataSr15[indexOlci];
                            productNumber[indexOlci] = 1;
                        } else {
                            dataSr1[indexOlci] = sourceDataSr1_B[indexOlci];
                            dataSr2[indexOlci] = sourceDataSr2_B[indexOlci];
                            dataSr3[indexOlci] = sourceDataSr3_B[indexOlci];
                            dataSr4[indexOlci] = sourceDataSr4_B[indexOlci];
                            dataSr5[indexOlci] = sourceDataSr5_B[indexOlci];
                            dataSr6[indexOlci] = sourceDataSr6_B[indexOlci];
                            dataSr7[indexOlci] = sourceDataSr7_B[indexOlci];
                            dataSr8[indexOlci] = sourceDataSr8_B[indexOlci];
                            dataSr9[indexOlci] = sourceDataSr9_B[indexOlci];
                            dataSr10[indexOlci] = sourceDataSr10_B[indexOlci];
                            dataSr11[indexOlci] = sourceDataSr11_B[indexOlci];
                            dataSr12[indexOlci] = sourceDataSr12_B[indexOlci];
                            dataSr13[indexOlci] = sourceDataSr13_B[indexOlci];
                            dataSr14[indexOlci] = sourceDataSr14_B[indexOlci];
                            dataSr15[indexOlci] = sourceDataSr15_B[indexOlci];
                            productNumber[indexOlci] = 2;
                        }
                        break;
                    case 101:
                        System.out.printf("ndvi_master 1.5xndvi_slave ndvi_slave 1.5xndvi_master: %f  %f  %f  %f _______________ \n", ndvi, 1.5 * ndvi_B, ndvi_B, 1.5*ndvi );
                        if (ndvi >= 1.5 * ndvi_B) {
                            dataSr1[indexOlci] = sourceDataSr1[indexOlci];
                            dataSr2[indexOlci] = sourceDataSr2[indexOlci];
                            dataSr3[indexOlci] = sourceDataSr3[indexOlci];
                            dataSr4[indexOlci] = sourceDataSr4[indexOlci];
                            dataSr5[indexOlci] = sourceDataSr5[indexOlci];
                            dataSr6[indexOlci] = sourceDataSr6[indexOlci];
                            dataSr7[indexOlci] = sourceDataSr7[indexOlci];
                            dataSr8[indexOlci] = sourceDataSr8[indexOlci];
                            dataSr9[indexOlci] = sourceDataSr9[indexOlci];
                            dataSr10[indexOlci] = sourceDataSr10[indexOlci];
                            dataSr11[indexOlci] = sourceDataSr11[indexOlci];
                            dataSr12[indexOlci] = sourceDataSr12[indexOlci];
                            dataSr13[indexOlci] = sourceDataSr13[indexOlci];
                            dataSr14[indexOlci] = sourceDataSr14[indexOlci];
                            dataSr15[indexOlci] = sourceDataSr15[indexOlci];
                            productNumber[indexOlci] = 1;
                        } else {
                            if (ndvi_B >= 1.5 * ndvi) {
                                dataSr1[indexOlci] = sourceDataSr1_B[indexOlci];
                                dataSr2[indexOlci] = sourceDataSr2_B[indexOlci];
                                dataSr3[indexOlci] = sourceDataSr3_B[indexOlci];
                                dataSr4[indexOlci] = sourceDataSr4_B[indexOlci];
                                dataSr5[indexOlci] = sourceDataSr5_B[indexOlci];
                                dataSr6[indexOlci] = sourceDataSr6_B[indexOlci];
                                dataSr7[indexOlci] = sourceDataSr7_B[indexOlci];
                                dataSr8[indexOlci] = sourceDataSr8_B[indexOlci];
                                dataSr9[indexOlci] = sourceDataSr9_B[indexOlci];
                                dataSr10[indexOlci] = sourceDataSr10_B[indexOlci];
                                dataSr11[indexOlci] = sourceDataSr11_B[indexOlci];
                                dataSr12[indexOlci] = sourceDataSr12_B[indexOlci];
                                dataSr13[indexOlci] = sourceDataSr13_B[indexOlci];
                                dataSr14[indexOlci] = sourceDataSr14_B[indexOlci];
                                dataSr15[indexOlci] = sourceDataSr15_B[indexOlci];
                                productNumber[indexOlci] = 2;
                            } else {
                                if (sourceDataSr1[indexOlci] <= sourceDataSr1_B[indexOlci]) {
                                    dataSr1[indexOlci] = sourceDataSr1[indexOlci];
                                    dataSr2[indexOlci] = sourceDataSr2[indexOlci];
                                    dataSr3[indexOlci] = sourceDataSr3[indexOlci];
                                    dataSr4[indexOlci] = sourceDataSr4[indexOlci];
                                    dataSr5[indexOlci] = sourceDataSr5[indexOlci];
                                    dataSr6[indexOlci] = sourceDataSr6[indexOlci];
                                    dataSr7[indexOlci] = sourceDataSr7[indexOlci];
                                    dataSr8[indexOlci] = sourceDataSr8[indexOlci];
                                    dataSr9[indexOlci] = sourceDataSr9[indexOlci];
                                    dataSr10[indexOlci] = sourceDataSr10[indexOlci];
                                    dataSr11[indexOlci] = sourceDataSr11[indexOlci];
                                    dataSr12[indexOlci] = sourceDataSr12[indexOlci];
                                    dataSr13[indexOlci] = sourceDataSr13[indexOlci];
                                    dataSr14[indexOlci] = sourceDataSr14[indexOlci];
                                    dataSr15[indexOlci] = sourceDataSr15[indexOlci];
                                    productNumber[indexOlci] = 1;
                                } else {
                                    dataSr1[indexOlci] = sourceDataSr1_B[indexOlci];
                                    dataSr2[indexOlci] = sourceDataSr2_B[indexOlci];
                                    dataSr3[indexOlci] = sourceDataSr3_B[indexOlci];
                                    dataSr4[indexOlci] = sourceDataSr4_B[indexOlci];
                                    dataSr5[indexOlci] = sourceDataSr5_B[indexOlci];
                                    dataSr6[indexOlci] = sourceDataSr6_B[indexOlci];
                                    dataSr7[indexOlci] = sourceDataSr7_B[indexOlci];
                                    dataSr8[indexOlci] = sourceDataSr8_B[indexOlci];
                                    dataSr9[indexOlci] = sourceDataSr9_B[indexOlci];
                                    dataSr10[indexOlci] = sourceDataSr10_B[indexOlci];
                                    dataSr11[indexOlci] = sourceDataSr11_B[indexOlci];
                                    dataSr12[indexOlci] = sourceDataSr12_B[indexOlci];
                                    dataSr13[indexOlci] = sourceDataSr13_B[indexOlci];
                                    dataSr14[indexOlci] = sourceDataSr14_B[indexOlci];
                                    dataSr15[indexOlci] = sourceDataSr15_B[indexOlci];
                                    productNumber[indexOlci] = 2;
                                }
                            }
                        }
                        break;

                    default:
                        System.out.println("Unknown case");
                        break;
                }
            }
        }


        makeFilledBand(dataSr1, targetRectangle, targetTileCopySourceBandSr1);
        makeFilledBand(dataSr2, targetRectangle, targetTileCopySourceBandSr2);
        makeFilledBand(dataSr3, targetRectangle, targetTileCopySourceBandSr3);
        makeFilledBand(dataSr4, targetRectangle, targetTileCopySourceBandSr4);
        makeFilledBand(dataSr5, targetRectangle, targetTileCopySourceBandSr5);
        makeFilledBand(dataSr6, targetRectangle, targetTileCopySourceBandSr6);
        makeFilledBand(dataSr7, targetRectangle, targetTileCopySourceBandSr7);
        makeFilledBand(dataSr8, targetRectangle, targetTileCopySourceBandSr8);
        makeFilledBand(dataSr9, targetRectangle, targetTileCopySourceBandSr9);
        makeFilledBand(dataSr10, targetRectangle, targetTileCopySourceBandSr10);
        makeFilledBand(dataSr11, targetRectangle, targetTileCopySourceBandSr11);
        makeFilledBand(dataSr12, targetRectangle, targetTileCopySourceBandSr12);
        makeFilledBand(dataSr13, targetRectangle, targetTileCopySourceBandSr13);
        makeFilledBand(dataSr14, targetRectangle, targetTileCopySourceBandSr14);
        makeFilledBand(dataSr15, targetRectangle, targetTileCopySourceBandSr15);
        makeFilledBand(productNumber, targetRectangle, targetTileProduct);
        makeFilledBand(statusNumber, targetRectangle, targetTileStatus);
    }

    private static int analyseSatus
            (int flagValue,
             int detectorIndexValue) {

        //        invalid= 0, clear_land= 1; clear water=2, clear_snow_ice=3, cloud=4, cloud shadow=5

        int status;
        if ((flagValue & 1) == 1 || detectorIndexValue <= 0 || flagValue ==0) {
            status = 0;
        } else if ((flagValue & 2) == 2 || (flagValue & 16) == 16) {
            status = 4;
        } else if ((flagValue & 32) == 32) {
            status = 5;
        } else if ((flagValue & 64) == 64) {
            status = 3;
        } else if ((flagValue & 1024) == 1024) {
            status = 1;
        } else {
            status = 2;
        }
//        System.out.printf("flag_value detector_index status: %d  %d   %d_______________ \n", flagValue, detectorIndexValue, status);
        return status;
    }

    private static void makeFilledBand
            (
                    float[] inputData,
                    Rectangle targetRectangle,
                    Tile targetTileOutputBand) {

        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        int inputDataWidth = targetRectangle.width;
        int inputDataHeight = targetRectangle.height;

        //System.out.printf("rectangle:  %d  %d _______________ rectangle_target_input_data:  %d  %d  \n", targetRectangle.width, targetRectangle.height,inputDataWidth, inputDataHeight);

        for (int y = 0; y < inputDataHeight; y++) {
            for (int x = 0; x < inputDataWidth; x++) {
                targetTileOutputBand.setSample(x + xLocation, y + yLocation, inputData[y * (inputDataWidth) + x]);
            }
        }
    }

    private static void makeFilledBand
            (
                    int[] inputData,
                    Rectangle targetRectangle,
                    Tile targetTileOutputBand) {

        int xLocation = targetRectangle.x;
        int yLocation = targetRectangle.y;
        int inputDataWidth = targetRectangle.width;
        int inputDataHeight = targetRectangle.height;

        //System.out.printf("rectangle:  %d  %d _______________ rectangle_target_input_data:  %d  %d  \n", targetRectangle.width, targetRectangle.height,inputDataWidth, inputDataHeight);

        for (int y = 0; y < inputDataHeight; y++) {
            for (int x = 0; x < inputDataWidth; x++) {
                targetTileOutputBand.setSample(x + xLocation, y + yLocation, inputData[y * (inputDataWidth) + x]);
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
            super(OlciP1DRulesOp.class);
        }
    }
}
