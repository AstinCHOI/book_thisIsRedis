package net.sf.redisbook.ch7.redislogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;


public class LogWriter {
    private static final String KEY_WAS_LOG = "was:log";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS ");
    JedisHelper helper;

    /**
     * 레디스에 로그를 기록하기 위한 Logger의 생성자
     * @param helper 제디스 헬퍼 객체
     */
    public LogWriter(JedisHelper helper) {
        this.helper = helper;
    }

    /**
     * 레디스에 로그를 기록하는 메서드
     * @Param log 저장할 로그 문자열
     * @return 저장된 후의 레디스에 저장된 로그 문자열의 길이
     */
    public Long log(String log) {
        Jedis jedis = this.helper.getConnection();
        Long rtn = jedis.append(KEY_WAS_LOG, sdf.format(new Date()) + log + "\n");

        jedis.close();
        return rtn;
    }
}
