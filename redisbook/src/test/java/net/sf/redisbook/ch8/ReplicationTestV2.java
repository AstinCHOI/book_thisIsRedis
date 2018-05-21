package net.sf.redisbook.ch8;

import net.sf.redisbook.ch7.redislogger.KeyMaker;
import net.sf.redisbook.ch8.replication.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;

import static org.junit.Assert.fail;

public class ReplicationTestV2 {
    private static final int TEST_COUNT = 100000;
    static Jedis master;
    static Jedis slave;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        master = new Jedis("127.0.0.1", 6300);
        slave = new Jedis("127.0.0.1", 6301);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        master.disconnect();
        slave.disconnect();
    }

    @Test
    public void replicationTest() {
        DataWriterV2 writer = new DataWriterV2(master);
        DataReaderV2 reader = new DataReaderV2(slave);
        long current = 0;

        for (int i = 0; i < TEST_COUNT; i++) {
            KeyMaker keyMaker = new ReplicationKeyMakerV2(i);
            String value = "test value" + i;
            writer.set(keyMaker.getKey(), value);

            current = System.currentTimeMillis();
            List<String> result = reader.get(keyMaker.getKey());
            long elapsed = System.currentTimeMillis() - current;

            if (value.equals(result.get(1))) { // success case
                if (elapsed > 1) {
                    System.out.println(elapsed);
                }
            } else {
                fail("The value Not match with a result. [" + value + "][" + result + "]");
            }
        }
    }
}
