package fr.genin.geocoding;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an point with data.
 */
public interface Point<T> {

    static <T> Builder<T> builder(T data) {
        Objects.requireNonNull(data);
        return new GeoCod.PointBuilder<>(data);
    }

    static <T> Builder<T> builderRef() {
        return new GeoCod.PointBuilder<>(null);
    }

    T getData();

    Optional<Double> getLat();

    Optional<Double> getLon();

    boolean isPresent();

    boolean isEmpty();


    interface Builder<T> {

        GeoCod.PointBuilder lat(Number lat);

        GeoCod.PointBuilder lat(Double  lat);

        GeoCod.PointBuilder lon(Number lon);

        GeoCod.PointBuilder lon(Double lon);

        Point<T> build();
    }
}
