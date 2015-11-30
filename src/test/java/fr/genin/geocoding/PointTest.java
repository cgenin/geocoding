package fr.genin.geocoding;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * TEst class for Point.
 */
public class PointTest {
    @Test
    public void isEmpty() {
        assertTrue(Point.builder("T1").build().isEmpty());
        assertTrue(Point.builder("T1").lat(0.1).build().isEmpty());
        assertTrue(Point.builder("T1").lon(0.1).build().isEmpty());
        assertFalse(Point.builder("T1").lat(0.1).lon(0.1).build().isEmpty());
    }

    @Test
    public void isPresent() {
        assertFalse(Point.builder("T1").build().isPresent());
        assertFalse(Point.builder("T1").lat(0.1).build().isPresent());
        assertFalse(Point.builder("T1").lon(0.1).build().isPresent());
        assertTrue(Point.builder("T1").lat(0.1).lon(0.1).build().isPresent());
    }
}
