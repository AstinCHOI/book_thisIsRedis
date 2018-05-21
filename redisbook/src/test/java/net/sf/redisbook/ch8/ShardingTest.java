package net.sf.redisbook.ch8;

import net.sf.redisbook.ShardedJedisHelper;
import net.sf.redisbook.ch8.replication.ShardTestKeyMaker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.ShardedJedis;

import static org.junit.Assert.*;

public class ShardingTest {
    private static final int TEST_COUNT = 500;
    static ShardedJedisHelper helper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        helper = ShardedJedisHelper.getInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        helper.destoryPool();
    }

    @Test
    public void replicationTest() {
        ShardedJedis jedis = helper.getConnection();

        for (int i = 0; i < TEST_COUNT; i++) {
            String testValue = "Test Value " + i;
            ShardTestKeyMaker keyMaker = new ShardTestKeyMaker(i);
            jedis.set(keyMaker.getKey(), testValue);
            assertEquals(testValue, jedis.get(keyMaker.getKey()));
        }
    }
}
