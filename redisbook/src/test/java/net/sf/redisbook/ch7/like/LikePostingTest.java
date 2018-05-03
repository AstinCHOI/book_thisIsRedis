package net.sf.redisbook.ch7.like;

import static org.junit.Assert.*;
import net.sf.redisbook.JedisHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;

public class LikePostingTest {
    static JedisHelper helper;
    private LikePosting likePosting;
    private static Random rand = new Random();
    private static final int POSTING_COUNT = 20;
    private static final int TESTUSER = rand.nextInt(10000000);
    private static String[] POSTLIST = new String[POSTING_COUNT];

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        helper = JedisHelper.getInstance();
        for (int i = 0; i < POSTLIST.length; i++) {
            POSTLIST[i] = String.valueOf(i + 1);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        helper.destoryPool();
    }

    @Before
    public void SetUp() throws Exception {
        this.likePosting = new LikePosting(helper);
        assertNotNull(this.likePosting);
    }

    @Test
    public void testLike() {
        String postingNo = String.valueOf(this.rand.nextInt(POSTING_COUNT));
        if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
            this.likePosting.unLike(postingNo, String.valueOf(TESTUSER));
        }
        assertTrue(this.likePosting.like(postingNo, String.valueOf(TESTUSER)));
    }

    @Test
    public void testUnLike() {
        String postingNo = String.valueOf(this.rand.nextInt(POSTING_COUNT));
        if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
            assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
        } else {
            assertTrue(this.likePosting.like(postingNo, String.valueOf(TESTUSER)));
            assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
        }
    }

    @Test
    public void testGetLikeCount() {
        String postingNo = String.valueOf(this.rand.nextInt(POSTING_COUNT));
        if (this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER))) {
            assertTrue(this.likePosting.unLike(postingNo, String.valueOf(TESTUSER)));
        }

        Long prevCount = this.likePosting.getLikeCount(postingNo);
        this.likePosting.like(postingNo, String.valueOf(TESTUSER));
        assertEquals(this.likePosting.getLikeCount(postingNo), new Long(prevCount + 1));
    }

    @Test
    public void testGetLikeCountList() {
        List<Long> countList = this.likePosting.getLikeCountList(POSTLIST);
        assertEquals(countList.size(), POSTING_COUNT);

    }

    @Test
    public void testIsLiked() {
        String postingNo = String.valueOf(this.rand.nextInt(POSTING_COUNT));
        this.likePosting.like(postingNo, String.valueOf(TESTUSER));
        assertTrue(this.likePosting.isLiked(postingNo, String.valueOf(TESTUSER)));
    }

    @Test
    public void testDeleteLikeInfo() {
        String postingNo = "A1234567890";
        this.likePosting.like(postingNo, String.valueOf(TESTUSER));
        assertTrue(this.likePosting.deleteLikeInfo(postingNo));
    }

    @Test
    public void testRandomLike() {
        for (int i = 0; i < POSTING_COUNT; i++) {
            String sudoRandomUser = String.valueOf(rand.nextInt(1000000));
            this.likePosting.like(String.valueOf(i), sudoRandomUser);
            assertTrue(this.likePosting.isLiked(String.valueOf(i), sudoRandomUser));
        }
    }
}
