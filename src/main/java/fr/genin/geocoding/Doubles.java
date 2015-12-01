package fr.genin.geocoding;

import java.math.BigDecimal;

/**
 * Doubles Utils.
 */
public class Doubles {

    public static Double rad2deg(Double rad) {
        return rad * 180.0 / Math.PI;
    }
    public static Double deg2rad(Double deg) {

        return deg * Math.PI / 180.0;
    }
}
