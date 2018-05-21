package net.sf.redisbook.ch7.uniquevisit;

import net.sf.redisbook.JedisHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UniqueVisitTestV2 {

    static JedisHelper helper;
    private UniqueVisitV2 uniqueVisit;
    private static final int TOTAL_USER = 100000000;


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
        this.uniqueVisit = new UniqueVisitV2(helper);
        assertNotNull(this.uniqueVisit);

        this.uniqueVisit.visit(7, "20130510");
        this.uniqueVisit.visit(11, "20130510");
        this.uniqueVisit.visit(15, "20130510");
        this.uniqueVisit.visit(TOTAL_USER, "20130510");

        this.uniqueVisit.visit(3, "20130511");
        this.uniqueVisit.visit(7, "20130511");
        this.uniqueVisit.visit(9, "20130511");
        this.uniqueVisit.visit(11, "20130511");
        this.uniqueVisit.visit(15, "20130511");
        this.uniqueVisit.visit(TOTAL_USER, "20130511");

        this.uniqueVisit.visit(7, "20130512");
        this.uniqueVisit.visit(12, "20130512");
        this.uniqueVisit.visit(13, "20130512");
        this.uniqueVisit.visit(15, "20130512");
        this.uniqueVisit.visit(TOTAL_USER, "20130512");
    }

    @Test
    public void testUVSum() {
        String[] dateList1 = {"20130510", "20130511", "20130512"};
        assertEquals(new Long(3), this.uniqueVisit.getUVSum(dateList1));

        String[] dateList2 = {"20130510", "20130511", "20130512", "20110512"};
        assertEquals(new Long(0), this.uniqueVisit.getUVSum(dateList2));
    }
}
