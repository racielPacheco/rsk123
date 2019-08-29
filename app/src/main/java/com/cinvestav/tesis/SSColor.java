package com.cinvestav.tesis;
import java.util.Random;

/**
 * Created by
 */
public class SSColor {

    private static final float[] HSV_TO_COLOR = new float[3];
    private static final int HSV_TO_COLOR_HUE_INDEX = 0;
    private static final int HSV_TO_COLOR_SATURATION_INDEX = 1;
    private static final int HSV_TO_COLOR_VALUE_INDEX = 2;

    /**
     * @param pHue [0 .. 360)
     * @param pSaturation [0...1]
     * @param pValue [0...1]
     */
    public static float[] hsb(final float pHue, final float pSaturation, final float pValue) {
        HSV_TO_COLOR[HSV_TO_COLOR_HUE_INDEX] = pHue;
        HSV_TO_COLOR[HSV_TO_COLOR_SATURATION_INDEX] = pSaturation;
        HSV_TO_COLOR[HSV_TO_COLOR_VALUE_INDEX] = pValue;
        return  HSV_TO_COLOR;//Color.HSVToColor(HSV_TO_COLOR);
    }

    private static float[] flatBlackColor() {
        return hsb(0, 0, 17);
    }

    private static float[] flatBlueColor() {
        return hsb(224, 50, 63);
    }

    private static float[] flatBrownColor() {
        return hsb(24, 45, 37);
    }

    private static float[] flatCoffeeColor() {
        return hsb(25, 31, 64);
    }

    private static float[] flatForestGreenColor() {
        return hsb(138, 45, 37);
    }

    private static float[] flatGrayColor() {
        return hsb(184, 10, 65);
    }

    private static float[] flatGreenColor() {
        return hsb(145, 77, 80);
    }

    private static float[] flatLimeColor() {
        return hsb(74, 70, 78);
    }

    private static float[] flatMagentaColor() {
        return hsb(283, 51, 71);
    }

    private static float[] flatMaroonColor() {
        return hsb(5, 65, 47);
    }

    private static float[] flatMintColor() {
        return hsb(168, 86, 74);
    }

    private static float[] flatNavyBlueColor() {
        return hsb(210, 45, 37);
    }

    private static float[] flatOrangeColor() {
        return hsb(28, 85, 90);
    }

    private static float[] flatPinkColor() {
        return hsb(324, 49, 96);
    }

    private static float[] flatPlumColor() {
        return hsb(300, 45, 37);
    }

    private static float[] flatPowderBlueColor() {
        return hsb(222, 24, 95);
    }

    private static float[] flatPurpleColor() {
        return hsb(253, 52, 77);
    }

    private static float[] flatRedColor() {
        return hsb(6, 74, 91);
    }

    private static float[] flatSandColor() {
        return hsb(42, 25, 94);
    }

    private static float[] flatSkyBlueColor() {
        return hsb(204, 76, 86);
    }

    private static float[] flatTealColor() {
        return hsb(195, 55, 51);
    }

    private static float[] flatWatermelonColor() {
        return hsb(356, 53, 94);
    }

    private static float[] flatWhiteColor() {
        return hsb(192, 2, 95);
    }

    private static float[] flatYellowColor() {
        return hsb(48, 99, 100);
    }

    private  float[] flatColors(int randomColorChosen){
        float[] cflat = new float[3];
        switch (randomColorChosen) {
            case 0:cflat = flatBlackColor();break;
            case 1:cflat = flatBlueColor();break;
            case 2:cflat = flatBrownColor();break;
            case 3:cflat = flatCoffeeColor();break;
            case 4:cflat = flatForestGreenColor();break;
            case 5:cflat = flatGrayColor();break;
            case 6:cflat = flatGreenColor();break;
            case 7:cflat = flatLimeColor();break;
            case 8:cflat = flatMagentaColor();break;
            case 9:cflat = flatMaroonColor();break;
            case 10:cflat = flatMintColor();break;
            case 11:cflat = flatNavyBlueColor();break;
            case 12:cflat = flatOrangeColor();break;
            case 13:cflat = flatPinkColor();break;
            case 14:cflat = flatPlumColor();break;
            case 15:cflat = flatPowderBlueColor();break;
            case 16:cflat = flatPurpleColor();break;
            case 17:cflat = flatRedColor();break;
            case 18:cflat = flatSandColor();break;
            case 19:cflat = flatSkyBlueColor();break;
            case 20:cflat = flatTealColor();break;
            case 21:cflat = flatWatermelonColor();break;
            case 22:cflat = flatWhiteColor();break;
            case 23:cflat = flatYellowColor();break;
        }
        return cflat;
    }

    public float[] randomFlatColor(){

        Random r = new Random();
        int Low = 0;
        int High = 24;
        int Result = r.nextInt(High-Low) + Low;
    return flatColors(Result);
    }
}
