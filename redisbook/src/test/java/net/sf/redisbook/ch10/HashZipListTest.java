package net.sf.redisbook.ch10;

import net.sf.redisbook.JedisHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class HashZipListTest {

    static JedisHelper helper;
    private HashZipList hashZipList;

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
        this.hashZipList = new HashZipList(helper);
    }

    @Test
    public void testEncodingTestForEntrySize() {
        assertEquals("ziplist", this.hashZipList.getBeforeEncoding1());
        assertEquals("hashtable", this.hashZipList.getAfterEncoding1());
    }

    @Test
    public void testEncodingTestForDataSize() {
        assertEquals("hashtable", this.hashZipList.getBeforeEncoding2()); // ziplist?
        assertEquals("hashtable", this.hashZipList.getAfterEncoding2());
    }

}
