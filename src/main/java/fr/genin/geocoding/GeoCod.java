package fr.genin.geocoding;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.genin.geocoding.BigDecimals.of;

public final class GeoCod {

    public static final BigDecimal DEG2MILES = of(60).multiply(of(1.1515));
    public static final BigDecimal MILES2NAUTIC = of(0.8684);
    public static final BigDecimal MILES2KM = of(1.609344);

    public static <T> Sorter<T> sorter(Point<T> reference) {
        return new Sorter<>(reference);
    }

    @SuppressWarnings("unchecked")
    public static <T> Sorter<T> sorter(Number refLat, Number refLon) {
        return sorter(Point.<T>builderRef().lat(refLat).lon(refLon).build());
    }

    public static <T> Distance between(Point<T> pt1, Point<T> pt2) {
        if (pt1.isEmpty() || pt2.isEmpty()) {
            return new Distance();
        }
        return between(pt1.getLat().get(), pt1.getLon().get(), pt2.getLat().get(), pt2.getLon().get());
    }

    public static Distance between(Number lat1, Number lon1, Number lat2, Number lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return new Distance();
        }
        return between(of(lat1), of(lon1), of(lat2), of(lon2));
    }

    public static Distance between(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        Objects.requireNonNull(lat1);
        Objects.requireNonNull(lon1);
        Objects.requireNonNull(lat2);
        Objects.requireNonNull(lon2);
        final BigDecimal theta = lon1.subtract(lon2);
        final BigDecimal dist = BigDecimals.sin(BigDecimals.deg2rad(lat1)).multiply(BigDecimals.sin(BigDecimals.deg2rad(lat2))).add(BigDecimals.cos(BigDecimals.deg2rad(lat1)).multiply(BigDecimals.cos(BigDecimals.deg2rad(lat2)).multiply(BigDecimals.cos(BigDecimals.deg2rad(theta)))));
        final BigDecimal distAcos = BigDecimals.acos(dist);
        final BigDecimal distDeg = BigDecimals.rad2deg(distAcos);
        final BigDecimal miles = distDeg.multiply(DEG2MILES);
        return new Distance(Optional.of(miles));
    }

    public static class PointImpl<T> implements Point<T> {
        private final T data;
        private final Optional<BigDecimal> lat;
        private final Optional<BigDecimal> lon;


        private PointImpl(T data, Optional<BigDecimal> lat, Optional<BigDecimal> lon) {
            this.data = data;
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public T getData() {
            return data;
        }

        @Override
        public Optional<BigDecimal> getLat() {
            return lat;
        }

        @Override
        public Optional<BigDecimal> getLon() {
            return lon;
        }

        @Override
        public boolean isPresent() {
            return lat.isPresent() && lon.isPresent();
        }

        @Override
        public boolean isEmpty() {
            return !isPresent();
        }


    }

    public static class PointBuilder<T> implements Point.Builder {
        private final T data;
        private BigDecimal lat;
        private BigDecimal lon;


        PointBuilder(T data) {

            this.data = data;
        }

        @Override
        public PointBuilder lat(Number lat) {
            if (lat == null) {
                return this;
            }
            final BigDecimal latBd = of(lat);
            return lat(latBd);
        }

        @Override
        public PointBuilder lat(BigDecimal lat) {
            Objects.requireNonNull(lat);
            this.lat = lat;
            return this;
        }


        @Override
        public PointBuilder lon(Number lon) {
            if (lon == null) {
                return this;
            }
            final BigDecimal lonBd = of(lon);
            return lon(lonBd);
        }

        @Override
        public PointBuilder lon(BigDecimal lon) {
            Objects.requireNonNull(lon);
            this.lon = lon;
            return this;
        }

        @Override
        public Point<T> build() {
            return new PointImpl<>(data, Optional.ofNullable(lat), Optional.ofNullable(lon));
        }

    }

    public static class Distance implements Comparable<Distance> {

        private final Optional<BigDecimal> miles;

        private Distance(Optional<BigDecimal> miles) {
            this.miles = miles;
        }

        public Distance() {
            this(Optional.<BigDecimal>empty());
        }


        public Optional<BigDecimal> miles() {
            return miles;
        }

        public Optional<BigDecimal> kilometer() {
            return miles.map(m -> m.multiply(MILES2KM));
        }

        public Optional<BigDecimal> nautic() {
            return miles.map(m -> m.multiply(MILES2NAUTIC));
        }

        @Override
        public int compareTo(Distance o) {
            if (!this.miles.isPresent()) {
                if (!this.miles.isPresent()) {
                    return 0;
                }
                return -1;
            } else {
                if (!this.miles.isPresent()) {
                    return 1;
                }

            }

            return this.miles.get().compareTo(o.miles.get());
        }
    }

    private enum NullSorting {
        NULLS_LAST(1, -1),
        NULLS_FIRST(-1, 1);
        private final int first;
        private final int second;

        NullSorting(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class Sorter<T> {

        private final Point<T> ref;
        private boolean desc = false;
        private Comparator<Point<T>> subComparatorOfPoints = (o1, o2) -> 0;
        private NullSorting nullSorting = NullSorting.NULLS_LAST;

        private Sorter(Point<T> ref) {
            Objects.requireNonNull(ref);
            this.ref = ref;
        }

        public Sorter<T> asc() {
            desc = false;
            return this;
        }

        public Sorter<T> desc() {
            desc = true;
            return this;
        }

        public Sorter<T> nullsLast() {
            nullSorting = NullSorting.NULLS_LAST;
            return this;
        }

        public Sorter<T> nullsFirst() {
            nullSorting = NullSorting.NULLS_FIRST;
            return this;
        }

        public Sorter<T> withSubComparatorOfPoints(Comparator<T> comparator) {
            Objects.requireNonNull(comparator);
            subComparatorOfPoints = (pt1, pt2) -> comparator.compare(pt1.getData(), pt2.getData());
            return this;
        }

        public List<T> sort(Collection<T> collection, Function<T, Point<T>> function) {
            Objects.requireNonNull(collection);
            Objects.requireNonNull(function);
            return sort(collection.parallelStream().map(function).collect(Collectors.toSet()));
        }

        public List<T> sort(Collection<Point<T>> collection) {
            return sortPoints(collection).parallelStream().map(Point::getData).collect(Collectors.toList());
        }

        public List<Point<T>> sortPoints(Collection<Point<T>> collection) {
            if (collection == null || collection.isEmpty()) {
                return Collections.emptyList();
            }

            return collection.stream().sorted(getComparatorPoints()).collect(Collectors.toList());
        }

        public List<Distance> distances(List<Point<T>> collection) {
            return MoreObjects.firstNonNull(collection, Collections.<Point<T>>emptyList()).stream()
                    .map(p -> between(ref, p)).collect(Collectors.toList());
        }

        public Comparator<Point<T>> getComparatorPoints() {
            if (ref.isEmpty()) {
                return subComparatorOfPoints;
            }
            final LoadingCache<Point<T>, Distance> cache = CacheBuilder.newBuilder()
                    .maximumSize(1000).build(
                            new CacheLoader<Point<T>, Distance>() {
                                public Distance load(Point<T> pt) {
                                    return between(ref, pt);
                                }
                            });
            return (pt1, pt2) -> {

                try {
                    if (pt1.isEmpty() && pt2.isEmpty()) {
                        return subComparatorOfPoints.compare(pt1, pt2);
                    }
                    if (pt1.isEmpty()) {
                        return nullSorting.first;
                    }
                    if (pt2.isEmpty()) {
                        return nullSorting.second;
                    }
                    final Distance d1 = cache.get(pt1);
                    final Distance d2 = cache.get(pt2);
                    final int compare = d1.compareTo(d2);
                    if (compare == 0) {
                        return subComparatorOfPoints.compare(pt1, pt2);
                    }
                    if (desc) {
                        return compare * -1;
                    }
                    return compare;
                } catch (ExecutionException e) {
                    throw Throwables.propagate(e);
                }
            };
        }


    }
}