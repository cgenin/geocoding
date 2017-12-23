package fr.genin.geocoding;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Class Test of Distance.
 */
public class DistanceTest {

    @Test
    public void betweenNumberWithNull() {
        assertFalse(Distances.between(null, -96.80322, 29.46786, -98.53506).miles().isPresent());
        assertFalse(Distances.between(32.9697, null, 29.46786, -98.53506).miles().isPresent());
        assertFalse(Distances.between(32.9697, -96.80322, null, -98.53506).miles().isPresent());
        assertFalse(Distances.between(32.9697, -96.80322, 29.46786, null).miles().isPresent());
    }

    @Test
    public void betweenPoint() {
        final Point pt1 = Point.builder("Pt1").lat(32.9697).lon(-96.80322).build();
        final Point pt2 = Point.builder("Pt2").lat(29.46786).lon(-98.53506).build();
        final Distances.Distance result = Distances.between(pt1, pt2);
        assertNotNull(result);
        assertTrue(result.miles().isPresent());
        assertEquals(262.6777938054349, result.miles().get().doubleValue(), 0.00000000001);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void betweenPointWithNull() {
        assertFalse(Distances.between(
                Point.builder("Pt1")
                        .lat(32.9697)
                        .<String>build(),
                Point.builder("Pt2")
                        .lat(29.46786)
                        .lon(-98.53506)
                        .<String>build())
                .miles()
                .isPresent()
        );
    }

    @Test
    public void betweenNumber() {
        final Distances.Distance result = Distances.between(32.9697, -96.80322, 29.46786, -98.53506);
        assertNotNull(result);
        assertTrue(result.miles().isPresent());
        assertEquals(262.6777938054349, result.miles().get().doubleValue(), 0.00000000001);
        assertEquals(422.73893139401383, result.kilometer().get().doubleValue(), 0.000000001);
        assertEquals(228.10939614063963, result.nautic().get().doubleValue(), 0.00000000001);
    }
}
