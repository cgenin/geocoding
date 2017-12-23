package fr.genin.geocoding;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Geocoding utility class.
 */
public final class Distances {

    private static final Double DEG2MILES = 60.0 * 1.1515;
    static final Double MILES2NAUTIC = 0.8684;
    static final Double MILES2KM = 1.609344;

    /**
     * create an sorter instance to sort point from reference point.
     *
     * @param reference The reference point
     * @param <T>       the target object
     * @return the sorter instance.
     */
    private static <T> Sorter<T> sorter(Point<T> reference) {
        return new Sorter<>(reference);
    }

    /**
     * create an sorter instance to sort point from lattitude and longitude
     *
     * @param refLat lattitude
     * @param refLon longitude
     * @param <T>    the type
     * @return An Sorter instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> Sorter<T> sorter(Number refLat, Number refLon) {
        return sorter(Point.<T>builderRef().lat(refLat).lon(refLon).build());
    }

    /**
     * Get the distance between Two Points.
     *
     * @param pt1 first point.
     * @param pt2 second points
     * @param <T> the object
     * @return the distance.
     */
    public static <T> Distance between(Point<T> pt1, Point<T> pt2) {
        if (pt1.isEmpty() || pt2.isEmpty()) {
            return new Distance();
        }
        return between(pt1.getLat().get(), pt1.getLon().get(), pt2.getLat().get(), pt2.getLon().get());
    }

    /**
     * Get the distance between Two Points.
     *
     * @param lat1 lattitude for the first point.
     * @param lon1 longitude for the first point
     * @param lat2 lattitude for the second point
     * @param lon2 longitude for the second point
     * @return The distance.
     */
    public static Distance between(Number lat1, Number lon1, Number lat2, Number lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return new Distance();
        }
        return between(lat1.doubleValue(), lon1.doubleValue(), lat2.doubleValue(), lon2.doubleValue());
    }

    private static Distance between(Double lat1, Double lon1, Double lat2, Double lon2) {
        Objects.requireNonNull(lat1);
        Objects.requireNonNull(lon1);
        Objects.requireNonNull(lat2);
        Objects.requireNonNull(lon2);
        final Double theta = lon1 - lon2;
        final Double dist = Math.sin(Doubles.deg2rad(lat1)) * Math.sin(Doubles.deg2rad(lat2))
                + (Math.cos(Doubles.deg2rad(lat1)) * (Math.cos(Doubles.deg2rad(lat2)) * (Math.cos(Doubles.deg2rad(theta)))));
        final Double distAcos = Math.acos(dist);
        final Double distDeg = Doubles.rad2deg(distAcos);
        final Double miles = distDeg * DEG2MILES;
        return new Distance(miles);
    }

    /**
     * Implementation class.
     *
     * @param <T>
     */
    public static class PointImpl<T> implements Point<T> {
        private final T data;
        private final Double lat;
        private final Double lon;


        private PointImpl(T data, Double lat, Double lon) {
            this.data = data;
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public T getData() {
            return data;
        }

        @Override
        public Optional<Double> getLat() {
            return Optional.ofNullable(lat);
        }

        @Override
        public Optional<Double> getLon() {
            return Optional.ofNullable(lon);
        }

        @Override
        public boolean isPresent() {
            return !Objects.isNull(lat) && !Objects.isNull(lon);
        }

        @Override
        public boolean isEmpty() {
            return !isPresent();
        }


    }

    public static class PointBuilder<T> implements Point.Builder {
        private final T data;
        private Double lat;
        private Double lon;


        public PointBuilder(T data) {

            this.data = data;
        }

        @Override
        public PointBuilder lat(Number lat) {
            if (lat == null) {
                return this;
            }
            return lat(lat.doubleValue());
        }

        @Override
        public PointBuilder lat(Double lat) {
            if (lat == null) {
                return this;
            }
            this.lat = lat;
            return this;
        }


        @Override
        public PointBuilder lon(Number lon) {
            if (lon == null) {
                return this;
            }
            return lon(lon.doubleValue());
        }

        @Override
        public PointBuilder lon(Double lon) {
            if (lon == null) {
                return this;
            }
            this.lon = lon;
            return this;
        }

        @Override
        public Point<T> build() {
            return new PointImpl<>(data, lat, lon);
        }

    }

    public static class Distance implements Comparable<Distance> {

        private final Double miles;

        private Distance(Double miles) {
            this.miles = miles;
        }

        Distance() {
            this(null);
        }


        public Optional<Double> miles() {
            return Optional.ofNullable(miles);
        }

        public Optional<Double> kilometer() {
            return miles()
                    .map(m -> m * MILES2KM);
        }

        public Optional<Double> nautic() {
            return miles().map(m -> m * MILES2NAUTIC);
        }

        @Override
        public int compareTo(Distance o) {
            if (Objects.isNull(this.miles)) {
                if (!o.miles().isPresent()) {
                    return 0;
                }
                return -1;
            } else {
                if (Objects.isNull(o.miles)) {
                    return 1;
                }

            }
            return this.miles.compareTo(o.miles);
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

    /**
     * Sorter
     * @param <T> the object which contains geo datas.
     */
    public static class Sorter<T> {

        private final Point<T> ref;
        private boolean desc = false;
        private Comparator<Point<T>> subComparatorOfPoints = (o1, o2) -> 0;
        private NullSorting nullSorting = NullSorting.NULLS_LAST;

        private Sorter(Point<T> ref) {
            Objects.requireNonNull(ref);
            this.ref = ref;
        }

        /**
         * Sorting ascendant way the point
         *
         * @return the build instance.
         */
        public Sorter<T> asc() {
            desc = false;
            return this;
        }

        /**
         * Sorting descendant way the point
         *
         * @return the build instance.
         */
        public Sorter<T> desc() {
            desc = true;
            return this;
        }

        /**
         * The null values must be at last.
         * @return the instance
         */
        public Sorter<T> nullsLast() {
            nullSorting = NullSorting.NULLS_LAST;
            return this;
        }

        /**
         * the null values must be first.
         * @return the instance.
         */
        public Sorter<T> nullsFirst() {
            nullSorting = NullSorting.NULLS_FIRST;
            return this;
        }

        /**
         * For adding subcmparaison if point has the same coordinates.
         * @param comparator the comparator.
         * @return the instance.
         */
        public Sorter<T> withSubComparatorOfPoints(Comparator<T> comparator) {
            Objects.requireNonNull(comparator);
            subComparatorOfPoints = (pt1, pt2) -> comparator.compare(pt1.getData(), pt2.getData());
            return this;
        }

        /**
         * Sort an collection of objects.
         * @param collection the collection.
         * @param function An function to tranform object to Point
         * @return the sorting list.
         */
        public List<T> sort( Collection<T> collection, Function<T, Point<T>> function) {
            Objects.requireNonNull(collection);
            Objects.requireNonNull(function);
            return sort(collection.parallelStream().map(function).collect(Collectors.toSet()));
        }

        /**
         * Sort an collection of Points
         * @param collection the collection.
         * @return the sorting list.
         */
        public List<T> sort(Collection<Point<T>> collection) {
            return sortPoints(collection).parallelStream().map(Point::getData).collect(Collectors.toList());
        }

        private List<Point<T>> sortPoints(Collection<Point<T>> collection) {
            if (collection == null || collection.isEmpty()) {
                return Collections.emptyList();
            }

            return collection.stream().sorted(getComparatorPoints()).collect(Collectors.toList());
        }

        /**
         * The list of distance for an List of points.
         * @param collection the list.
         * @return the list of Distance.
         */
        public List<Distance> distances(List<Point<T>> collection) {
            return MoreObjects.firstNonNull(collection, Collections.<Point<T>>emptyList()).stream()
                    .map(p -> between(ref, p)).collect(Collectors.toList());
        }

        Comparator<Point<T>> getComparatorPoints() {
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