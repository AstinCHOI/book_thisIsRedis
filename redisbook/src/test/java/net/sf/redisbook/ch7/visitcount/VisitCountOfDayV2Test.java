package net.sf.redisbook.ch7.visitcount;

import net.sf.redisbook.JedisHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VisitCountOfDayV2Test {
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

        VisitCountOfDayV2 visitCountOfDay = new VisitCountOfDayV2(helper);
        assertTrue(visitCountOfDay.addVisit("52") > 0);
        assertTrue(visitCountOfDay.addVisit("180") > 0);
        assertTrue(visitCountOfDay.addVisit("554") > 0);
    }

    @Test
    public void testGetVisitCountByDate() {
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        VisitCountOfDayV2 visitCountOfDay = new VisitCountOfDayV2(helper);

        SortedMap<String, String> visitCount = visitCountOfDay.getVisitCountByDaily("554");

        assertTrue(visitCount.size() > 0);
        assertNotNull(visitCount);
        assertNotNull(visitCount.firstKey());
        assertNotNull(visitCount.lastKey());

        System.out.println(visitCount);

        SortedMap<String, String> totalVisit = visitCountOfDay.getVisitCountByDailyTotal();
        assertTrue(totalVisit.size() > 0);
        assertNotNull(totalVisit);
        assertNotNull(totalVisit.firstKey());
        assertNotNull(totalVisit.lastKey());

        System.out.println(totalVisit);
    }
}