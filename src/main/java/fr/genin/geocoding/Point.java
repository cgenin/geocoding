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

    Optional<BigDecimal> getLat();

    Optional<BigDecimal> getLon();

    boolean isPresent();

    boolean isEmpty();


    interface Builder<T> {

        GeoCod.PointBuilder lat(Number lat);

        GeoCod.PointBuilder lat(BigDecimal lat);

        GeoCod.PointBuilder lon(Number lon);

        GeoCod.PointBuilder lon(BigDecimal lon);

        Point<T> build();
    }
}
