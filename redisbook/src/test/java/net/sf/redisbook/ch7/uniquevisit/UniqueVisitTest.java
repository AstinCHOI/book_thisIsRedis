package net.sf.redisbook.ch7.uniquevisit;

import static org.junit.Assert.*;
import net.sf.redisbook.JedisHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

public class UniqueVisitTest {

    static JedisHelper helper;
    private UniqueVisit uniqueVisit;
    private static final int VISIT_COUNT = 1000;
    private static final int TOTAL_USER = 10000000;
    private static final String TEST_DATE = "19500101";
    static Random rand = new Random();


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        helper = JedisHelper.getInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        helper.destoryPool();
    }

    @Before
    public void setUp() throws Exception {
        uniqueVisit = new UniqueVisit(helper);
        assertNotNull(uniqueVisit);
    }

    @Test
    public void testRandomPV() {
        int pv = uniqueVisit.getPVCount(uniqueVisit.getToday());
        for (int i = 0; i < VISIT_COUNT; i++) {
            uniqueVisit.visit(rand.nextInt(TOTAL_USER));
        }
        assertEquals(pv + VISIT_COUNT, uniqueVisit.getPVCount(uniqueVisit.getToday()));
    }

    @Test
    public void testInvalidPV() {
        assertEquals(0, uniqueVisit.getPVCount(TEST_DATE));
        assertEquals(new Long(0), uniqueVisit.getUVCount(TEST_DATE));
    }

    @Test
    public void testPV() {
        int result = uniqueVisit.getPVCount(uniqueVisit.getToday());
        uniqueVisit.visit(65487);

        assertEquals(result + 1, uniqueVisit.getPVCount(uniqueVisit.getToday()));
    }

    @Test
    public void testUV() {
        uniqueVisit.visit(65487);
        Long result = uniqueVisit.getUVCount(uniqueVisit.getToday());
        uniqueVisit.visit(65487);

        assertEquals(result, uniqueVisit.getUVCount(uniqueVisit.getToday()));
    }
}
