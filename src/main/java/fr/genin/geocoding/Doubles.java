package fr.genin.geocoding;


/**
 * Doubles Utils.
 */
public class Doubles {
    /**
     * Transform double radian value tu degree.
      * @param rad the radian value
     * @return the degree value.
     */
    public static Double rad2deg(Double rad) {
        return rad * 180.0 / Math.PI;
    }

    /**
     * Transform double degreevalue the radian.
     * @param deg the  value
     * @return the degree value.
     */
    public static Double deg2rad(Double deg) {

        return deg * Math.PI / 180.0;
    }
}
