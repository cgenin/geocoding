package fr.genin.geocoding;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Limitroph.
 */
public class LimitrophTest {

    @Test(expected = Exception.class)
    public void withNullDepts() {
        Depts.limitroph(null);
    }

    @Test
    public void list() {
        assertThat(Depts.limitroph("86").list()).hasSize(7).contains("16", "36", "37", "49", "79", "87","86");
        assertThat(Depts.limitroph("20").list()).hasSize(1).contains("20");
        assertThat(Depts.limitroph("66").list()).hasSize(3).contains("09", "11","66");
        assertThat(Depts.limitroph("").list()).isEmpty();
        assertThat(Depts.limitroph("sdsq dsqdsqdsq").list()).isEmpty();
    }

    @Test
    public void matchPostalCode() {
        assertThat(Depts.limitroph("86").matchPostalCode("86430")).isTrue();
        assertThat(Depts.limitroph("86").matchPostalCode("87000")).isTrue();
        assertThat(Depts.limitroph("86").matchPostalCode("")).isFalse();
        assertThat(Depts.limitroph("86").matchPostalCode("17")).isFalse();
        assertThat(Depts.limitroph("86").matchPostalCode(null)).isFalse();

    }
}
