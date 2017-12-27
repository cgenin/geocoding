package fr.genin.geocoding;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an point with data.
 */
public interface Point<T> {

    /**
     * Create an builder for creating one Point with attached object.
     *
     * @param data the attached object in the point.
     * @param <T>  the Type of the attached object
     * @return the Builder
     */
    @SuppressWarnings("unchecked")
    static <T> Builder<T> builder(T data) {
        Objects.requireNonNull(data);
        return new Distances.PointBuilder<>(data);
    }

    /**
     * Create an builder for creating one Point without attached object.
     *
     * @param <T> the Type of the attached object
     * @return the Builder
     */
    @SuppressWarnings("unchecked")
    static <T> Builder<T> builderRef() {
        return new Distances.PointBuilder(null);
    }

    /**
     * Getter on the attached object.
     *
     * @return the Attchaed object.
     */
    T getData();

    /**
     * Optional for the lattitude.
     */
    Optional<Double> getLat();

    /**
     * Optional for the longitude.
     */
    Optional<Double> getLon();

    /**
     * True if all the geopoint data are present.
     *
     * @return true or false otherwise.
     */
    boolean isPresent();

    /**
     * The function inverse of {@link isPresent}.
     *
     * @return true if no geodatas is present. False otherwise.
     */
    default boolean isEmpty() {
        return !isPresent();
    }

    /**
     * The Builder of Point.
     *
     * @param <T> The attached type
     */
    interface Builder<T> {

        /**
         * Set the lattitude.
         * @param lat the lattitude.
         * @return the instance.
         */
        Builder<T> lat(Number lat);

        /**
         * Set the longitude
          * @param lon longitude
         * @return instance.
         */
        Builder<T> lon(Number lon);

        /**
         * Create an point.
         * @return an point.
         */
        Point<T> build();
    }
}
