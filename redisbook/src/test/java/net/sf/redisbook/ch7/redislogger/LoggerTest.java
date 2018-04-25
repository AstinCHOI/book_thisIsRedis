package net.sf.redisbook.ch7.redislogger;

import java.util.Random;
import net.sf.redisbook.JedisHelper;
import static org.junit.Assert.*;

import org.junit.*;


public class LoggerTest {
    static JedisHelper helper;
    private static final int WAITING_TERM = 5000;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        helper = JedisHelper.getInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        helper.destoryPool();
    }

    @Test
    public void testWrite() {
        Random random = new Random(System.currentTimeMillis());
        LogWriterV2 logWriter = new LogWriterV2(helper);
        for (int i = 0; i < 100; i++) {
            assertTrue(logWriter.log(i + ", This is new test log message") > 0);

            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    @Test
    public void testReceiver() {
        LogReceiverV2 logReceiver = new LogReceiverV2();

        for (int i = 0; i < 5; i++) {
            logReceiver.start();
            try {
                Thread.sleep(WAITING_TERM);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }
}
