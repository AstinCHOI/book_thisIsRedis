package net.sf.redisbook.ch8;

import net.sf.redisbook.ch7.redislogger.KeyMaker;
import net.sf.redisbook.ch8.replication.DataReader;
import net.sf.redisbook.ch8.replication.DataWriter;
import net.sf.redisbook.ch8.replication.ReplicationKeyMaker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.fail;

public class ReplicationTest {
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
        DataWriter writer = new DataWriter(master);
        DataReader reader = new DataReader(slave);

        for (int i = 0; i < TEST_COUNT; i++) {
            KeyMaker keyMaker = new ReplicationKeyMaker(i);
            String value = "test value" + i;
            writer.set(keyMaker.getKey(), value);
            String result = reader.get(keyMaker.getKey());

            if (value.equals(result)) {
                // success case
            } else {
                fail("The value Not match with a result. [" + value + "][" + result + "]");
            }
        }
    }
}
