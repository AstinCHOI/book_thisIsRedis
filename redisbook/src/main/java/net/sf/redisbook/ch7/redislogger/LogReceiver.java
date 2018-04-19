package net.sf.redisbook.ch7.redislogger;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;

public class LogReceiver {
    private static final JedisHelper helper = JedisHelper.getInstance();
    private static final String KEY_WAS_LOG = "was:log";
    private static final String LOG_FILE_NAME_PREFIX = "./waslog";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH'.log'");
    private static final int WAITING_TERM = 5000;

    /**
     * 레디스 서버에서 로그를 읽어서 파일로 저장한다.
     * 프로그램이 종료되기 전까지 무한 실행한다.
     */
    public void start() {
        Random random = new Random();
        Jedis jedis = helper.getConnection();
        while (true) {
            writeFile(jedis.getSet(KEY_WAS_LOG, ""));

            try {
                Thread.sleep(random.nextInt(WAITING_TERM));
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    /**
     * 메서드가 호출된 시간에 해당하는 로그 파일명을 생성한다.
     * @return 현재 시간에 해당하는 로그 파일명
     */
    public String getCurrentFileName() {
        return LOG_FILE_NAME_PREFIX + sdf.format(new Date());
    }

    public void writeFile(String log) {
        try {
            FileWriter writer = new FileWriter(getCurrentFileName(), true);

            writer.write(log);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
