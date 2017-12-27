package fr.genin.geocoding;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * BigDecimals utils class.
 */
public final class BigDecimals {

    public static final BigDecimal PI = BigDecimal.valueOf(Math.PI);

    public static BigDecimal of(Number val){
        Objects.requireNonNull(val);
        return BigDecimal.valueOf(val.doubleValue());
    }

    public static BigDecimal sin(BigDecimal val) {
        return BigDecimal.valueOf(Math.sin(val.doubleValue()));
    }

    public static BigDecimal cos(BigDecimal val) {
        return BigDecimal.valueOf(Math.cos(val.doubleValue()));
    }

    public static BigDecimal acos(BigDecimal val) {
        return BigDecimal.valueOf(Math.acos(val.doubleValue()));
    }

    public static BigDecimal rad2deg(BigDecimal rad) {
        return rad.multiply(BigDecimal.valueOf(180)).divide(PI, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal deg2rad(BigDecimal deg) {

        return deg.multiply(PI).divide(BigDecimal.valueOf(180.0), BigDecimal.ROUND_HALF_EVEN);
    }
}
