package fr.genin.geocoding;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Test Class for Distances.Sorter.
 */
public class SorterTest {

    @SuppressWarnings("unchecked")
    private static final List<Point<String>> LIST = Arrays.asList(new Point[]{
            Point.builder("POITIERS").lat(46.580119).lon(0.340751).build(),
            Point.builder("NIORT").lat(46.323810).lon(-0.464679).build(),
            Point.builder("LA ROCHELLE").lat(46.159765).lon(-1.151780).build(),
    });

    @SuppressWarnings("unchecked")
    private static final List<Point<String>> LIST_WITH_NULL = Arrays.asList(
            Point.builder("POITIERS").lat(46.580119).lon(0.340751).build(),
            Point.builder("NIORT").lat(46.323810).lon(-0.464679).build(),
            Point.builder("LA ROCHELLE").lat(46.159765).lon(-1.151780).build(),
            Point.builder("2").lat(46.159765).build(),
            Point.builder("3").lon(-1.151780).build(),
            Point.builder("1").build()
    );


    private static final List<Data> DATAS = Arrays.asList(
            new Data("POITIERS", 46.580119, 0.340751),
            new Data("NIORT", 46.323810, -0.464679),
            new Data("LA ROCHELLE", 46.159765, -1.151780)

    );


    @Test
    public void nulL() {
        assertThat(Distances.<String>sorter(46.348164, -0.387781).sort(null)).isEmpty();
        assertThat(Distances.<String>sorter(46.348164, -0.387781).sort(Collections.EMPTY_LIST)).isEmpty();
        assertThat(Distances.<String>sorter(46.348164, -0.387781).distances(null)).isEmpty();
        assertThat(Distances.<String>sorter(46.348164, -0.387781).distances(Collections.EMPTY_LIST)).isEmpty();
    }

    @Test
    public void distances() {
        final List<Distances.Distance> distances = Distances.<String>sorter(46.348164, -0.387781).desc().distances(LIST);
        final List<String> bigDecimals = distances.stream()
                .flatMap(d -> d.kilometer().map(Stream::of).orElse(Stream.empty()))
                .map(bg -> bg.toString())
                .collect(Collectors.toList());
        assertThat(bigDecimals).containsExactly(
                "61.46904691380353",
                "6.494780510980947",
                "62.36215286811597");
    }

    @Test
    public void desc() {
        final List<String> sortedInverse = Distances.<String>sorter(46.348164, -0.387781).desc().sort(LIST);
        assertThat(sortedInverse).isNotNull().isNotEmpty().containsExactly("LA ROCHELLE", "POITIERS", "NIORT");
    }

    @Test
    public void asc() {
        final List<String> sortedByChauray = Distances.<String>sorter(46.348164, -0.387781).sort(LIST);
        assertThat(sortedByChauray).isNotNull().isNotEmpty().containsExactly("NIORT", "POITIERS", "LA ROCHELLE");
        final List<String> sortedWithAsc = Distances.<String>sorter(46.348164, -0.387781).asc().sort(LIST);
        assertThat(sortedWithAsc).isNotNull().isNotEmpty().containsExactly("NIORT", "POITIERS", "LA ROCHELLE");
    }

    @Test
    public void ascWithNull() {
        final List<String> sorted = Distances.<String>sorter(46.348164, -0.387781).sort(LIST_WITH_NULL);
        assertThat(sorted).isNotNull().isNotEmpty().containsExactly("NIORT", "POITIERS", "LA ROCHELLE", "2", "3", "1");

    }

    @Test
    public void ascWithNullAndSorting() {
        final List<String> sorted = Distances.<String>sorter(46.348164, -0.387781)
                .withSubComparatorOfPoints(String::compareTo).sort(LIST_WITH_NULL);
        assertThat(sorted).isNotNull().isNotEmpty().containsExactly("NIORT", "POITIERS", "LA ROCHELLE", "1", "2", "3");
    }

    @Test
    public void ascWithNullFirstAndSorting() {
        final List<String> sorted = Distances.<String>sorter(46.348164, -0.387781)
                .withSubComparatorOfPoints(String::compareTo).nullsFirst().sort(LIST_WITH_NULL);
        assertThat(sorted).isNotNull().isNotEmpty().containsExactly("1", "2", "3", "NIORT", "POITIERS", "LA ROCHELLE");
    }

    @Test
    public void descWithNull() {
        final List<String> sorted = Distances.<String>sorter(46.348164, -0.387781).desc().sort(LIST_WITH_NULL);
        assertThat(sorted).isNotNull().isNotEmpty().containsExactly("LA ROCHELLE", "POITIERS", "NIORT", "2", "3", "1");
    }

    @Test
    public void ascWithNullLAST() {
        final List<String> sorted = Distances.<String>sorter(46.348164, -0.387781).nullsLast().sort(LIST_WITH_NULL);
        assertThat(sorted).isNotNull().isNotEmpty().containsExactly("NIORT", "POITIERS", "LA ROCHELLE", "2", "3", "1");

    }

    @Test
    public void ascWithNullFirst() {
        final List<String> sorted = Distances.<String>sorter(46.348164, -0.387781).nullsFirst().sort(LIST_WITH_NULL);
        assertThat(sorted).isNotNull().isNotEmpty().containsExactly("2", "3", "1", "NIORT", "POITIERS", "LA ROCHELLE");
    }

    @Test
    public void ascWithDataMapper() {
        final List<Data> sortedByChauray = Distances.<Data>sorter(46.348164, -0.387781)
                .sort(DATAS, (d) -> Point.builder(d).lat(d.lat).lon(d.lon).build());
        final List<String> collect = sortedByChauray.stream().map(d -> d.test).collect(Collectors.toList());
        assertThat(collect)
                .isNotNull().isNotEmpty().containsExactly("NIORT", "POITIERS", "LA ROCHELLE");
    }
}
