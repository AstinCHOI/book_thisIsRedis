package net.sf.redisbook.ch7.redislogger;

import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogReceiverV2 {
    private static final JedisHelper helper = JedisHelper.getInstance();
    private static final String KEY_WAS_LOG = "was:log:list";
    private static final String LOG_FILE_NAME_PREFIX = "./waslog";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH'.log'");

    /**
     * 레디스 서버에서 로그를 읽어서 파일로 저장한다.
     * 리스트에 저장된 모든 요소를 파일로 저장한다. 요소가 없으면 메서드가 종료된다.
     */
    public void start() {
        Jedis jedis = helper.getConnection();
        while (true) {
            String log = jedis.rpop(KEY_WAS_LOG);
            if (log == null) break;
            writeFile(log);

        }
    }

    /**
     * 메서드가 호출된 시간에 해당하는 로그 파일명을 생성한다.
     * @return 현재 시간에 해당하는 로그 파일명
     */
    public String getCurrentFileName() {
        return LOG_FILE_NAME_PREFIX + sdf.format(new Date());
    }

    private void writeFile(String log) {
        try {
            if (log == null) return;
            FileWriter writer = new FileWriter(getCurrentFileName(), true);
            writer.write(log);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
