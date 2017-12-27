package fr.genin.geocoding;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

final class Utils {

    static <T> T firstNonNull(T t1, T t2) {
        return (Objects.isNull(t1)) ? t2 : t1;
    }


    /**
     * Doubles Utils.
     */
    public static class Doubles {
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


    static class CacheBuilder<Key, Values> {
        private final Map<Key, Values> map = new ConcurrentHashMap<>();
        private final Function<Key, Values> populate;

        CacheBuilder(Function<Key, Values> populate) {
            this.populate = populate;
        }

        Values get(Key key) {
            return Optional.ofNullable(map.get(key))
                    .orElseGet(() -> {
                        Values apply = populate.apply(key);
                        map.put(key, apply);
                        return apply;
                    });
        }

    }


    public static class Splitter {
        private final String split;
        private boolean omitEmptyString = false;

        private Splitter(String c) {
            this.split = c;
        }


        public static Splitter on(char c) {
            return on("" + c);
        }



        private static Splitter on(String c) {
            return new Splitter(c);
        }

        public Splitter omitEmptyString(){
            this.omitEmptyString = true;
            return this;
        }

        public List<String> splitToList(String source) {
            if (Objects.isNull(source))
                return Collections.emptyList();
            return Arrays.stream(source.split(split))
                    .map(String::trim)
                    .filter(str -> !omitEmptyString || !"".equals(str))
                    .collect(Collectors.toList());
        }
    }
}
