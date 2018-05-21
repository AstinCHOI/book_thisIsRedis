package net.sf.redisbook.ch7.recentview;

import net.sf.redisbook.JedisHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RecentViewListTest {

    static JedisHelper helper;
    private RecentViewList recentViewList;
    private static final String TEST_USER = "123";
    private int listMaxSize;


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
        this.recentViewList = new RecentViewList(helper, TEST_USER);
        assertNotNull(this.recentViewList);
        this.listMaxSize = this.recentViewList.getListMaxSize();
    }

    @Test
    public void testAdd() {
        for (int i = 1; i <= 50; i++) {
            this.recentViewList.add(String.valueOf(i));
        }
    }

    @Test
    public void checkMaxSize() {
        int storedSize = this.recentViewList.getRecentViewList().size();
        assertEquals(this.listMaxSize, storedSize);
    }

    @Test
    public void checkRecentSize() {
        int checkSize = 4;
        int redisSize = this.recentViewList.getRecentViewList(checkSize).size();
        assertEquals(redisSize, checkSize);
    }

    @Test
    public void checkProductNo() {
        this.recentViewList.add("50");
        assertEquals(this.recentViewList.getRecentViewList().size(), this.listMaxSize);

        List<String> itemList = this.recentViewList.getRecentViewList(5);
        assertEquals("50", itemList.get(0));
        for (String item: itemList) {
            System.out.println(item);
        }
    }
}
