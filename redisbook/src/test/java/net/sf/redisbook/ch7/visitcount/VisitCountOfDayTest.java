package net.sf.redisbook.ch7.visitcount;

import static org.junit.Assert.*;
import java.util.List;
import net.sf.redisbook.JedisHelper;
import org.junit.*;

public class VisitCountOfDayTest {
    static JedisHelper helper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        helper = JedisHelper.getInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        helper.destoryPool();
    }

    @Test
    public void testAddVisit() {
        VisitCount visitCount = new VisitCount(helper);
        assertTrue(visitCount.addVisit("52") > 0);
        assertTrue(visitCount.addVisit("180") > 0);
        assertTrue(visitCount.addVisit("554") > 0);

        VisitCountOfDay visitCountOfDay = new VisitCountOfDay(helper);
//        assertTrue(visitCountOfDay.addVisit("52") > 0);
        assertTrue(visitCountOfDay.addVisit("180") > 0);
        assertTrue(visitCountOfDay.addVisit("554") > 0);
    }

    @Test
    public void testGetVisitCountByDate() {
        String[] dateList = {"20180424", "20180424", "20180424", "20180424"};
        VisitCountOfDay visitCountOfDay = new VisitCountOfDay(helper);
        List<String> result = visitCountOfDay.getVisitCountByDate("52", dateList);
        assertNotNull(result);
        assertTrue(result.size() == 4);
    }
}