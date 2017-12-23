package fr.genin.geocoding;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Classe d'utilitaire sur les départements français.
 */
public final class Depts {

    private static LimitrophBuilder builder;

    /**
     * builder for creating the instance
     * @return the instance.
     */
    private static synchronized LimitrophBuilder builder() {
        return Optional.ofNullable(builder).orElseGet(() -> {
            try {
                final Properties properties = new Properties();
                properties.load(Depts.class.getResourceAsStream("/limitroph.properties"));
                builder = new LimitrophBuilder(properties);
                return builder;
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        });
    }

    /**
     * Permet de récupérer les départements limitrophes d'un autre à partir de son N°.
     * @param noDept le nuémro.
     * @return les limitrophes.
     */
    public static Limitroph limitroph(String noDept) {
        Objects.requireNonNull(noDept);
        return new Limitroph(builder(), noDept);
    }

    /**
     * class
     */
    private static class LimitrophBuilder {
        private static Splitter SPLIT_DEPT = Splitter.on(',').omitEmptyStrings().trimResults();

        private final LoadingCache<String, List<String>> cache = CacheBuilder.newBuilder()
                .weakKeys().weakValues().build(
                        new CacheLoader<String, List<String>>() {
                            public List<String> load(String dept) {
                                final String property = limitroph.getProperty(dept, "");

                                return SPLIT_DEPT.splitToList(property);
                            }
                        });

        private final Properties limitroph;

        private LimitrophBuilder(Properties limitroph) {
            this.limitroph = limitroph;
        }
    }

    public static class Limitroph {

        private final LimitrophBuilder builder;
        private final String dept;

        private Limitroph(LimitrophBuilder builder, String dept) {
            this.builder = builder;
            this.dept = dept;
        }

        /**
         * La liste des départements limitrophe.
         * @return la liste
         */
        public List<String> list() {
            try {
                return builder.cache.get(dept);
            } catch (ExecutionException e) {
                throw Throwables.propagate(e);
            }
        }

        /**
         * Permet de savoir si le code postal apprtient au départements ou à un département limitrophe.
         * @param postalCode le code postal
         * @return true si limitrophe ou false dans les autres cas.
         */
        public boolean matchPostalCode(String postalCode) {
            return postalCode != null && list().stream().anyMatch(postalCode::startsWith);
        }

        /**
         * Permet de créer un prédicat pour les départements limitrophes.
         * Exemple :
         * <code>
         *  List<String> list = MonStream
         *          .filter(Depts.limitroph("86")
         *                          .predicate(obj -> input.codepostal()))
         *          .collect(Collectors.toList());
         * </code>
         * @param postalCodeFunction Un fonction de transformation
         * @param <T> le type d'objet attendu.
         * @return le stream filtré.
         */
        public <T> Predicate<T> predicate(Function<T, String> postalCodeFunction) {
            Objects.requireNonNull(postalCodeFunction);
            return (t) -> matchPostalCode(postalCodeFunction.apply(t));
        }
    }
}
