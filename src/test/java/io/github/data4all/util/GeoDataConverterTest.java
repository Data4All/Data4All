/**
 * 
 */
package io.github.data4all.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for io.github.data4all.util.GeoDataConverter.
 * 
 * @author sbollen
 */
public class GeoDataConverterTest {

    GeoDataConverter geoDataConverter;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        geoDataConverter = new GeoDataConverter();

    }

    /**
     * Test method for
     * {@link io.github.data4all.util.GeoDataConverter#convertToDMS(double)}.
     */
    @Test
    public void testConvertToDMS() {
        // normal positive input
        double degree = 20.00;
        Object value = geoDataConverter.convertToDMS(degree);
        String result = (String) value;
        assertEquals("20/1,0/1,0/1000", result);

        // normal negative input
        double degree0 = -20.00;
        Object value0 = geoDataConverter.convertToDMS(degree0);
        String result0 = (String) value0;
        assertEquals("20/1,0/1,0/1000", result0);

        // 0.00 as input
        double degree1 = 0;
        Object value1 = geoDataConverter.convertToDMS(degree1);
        String result1 = (String) value1;
        assertEquals("0/1,0/1,0/1000", result1);

        // more complex input
        double degree2 = -56.2341;
        Object value2 = geoDataConverter.convertToDMS(degree2);
        String result2 = (String) value2;
        assertEquals("56/1,14/1,2759/1000", result2);

        // another complex input
        double degree3 = 37.715183;
        Object value3 = geoDataConverter.convertToDMS(degree3);
        String result3 = (String) value3;
        assertEquals("37/1,42/1,54658/1000", result3);

    }

    /**
     * Test method for
     * {@link io.github.data4all.util.GeoDataConverter#convertToDegree(java.lang.String)}
     * .
     */
    @Test
    public void testConvertToDegree() {
        // normal positive input
        String dms = "20/1,0/1,0/1000";
        Object value = geoDataConverter.convertToDegree(dms);
        double result = (Double) value;
        assertEquals(20, result, 0.001);

        // 0.00 as input
        String dms1 = "0/1,0/1,0/1000";
        Object value1 = geoDataConverter.convertToDegree(dms1);
        double result1 = (Double) value1;
        assertEquals(0, result1, 0.001);

        // more complex input
        String dms2 = "56/1,14/1,2759/1000";
        Object value2 = geoDataConverter.convertToDegree(dms2);
        double result2 = (Double) value2;
        assertEquals(56.2341, result2, 0.001);

        // another complex input
        String dms3 = "37/1,42/1,54658/1000";
        Object value3 = geoDataConverter.convertToDegree(dms3);
        double result3 = (Double) value3;
        assertEquals(37.715183, result3, 0.001);

        // input in another format or with negative numbers is not possible
    }

}
