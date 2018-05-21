package net.sf.redisbook.ch7.uniquevisit;

import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UniqueVisit {
    private Jedis jedis;
    private static final String KEY_PAGE_VIEW = "page:view:";
    private static final String KEY_UNIQUE_VISITOR = "unique:visitors:";

    public UniqueVisit(JedisHelper helper) {
        this.jedis = helper.getConnection();
    }

    /**
     * 특정 사용자의 순 방문 횟수와 누적 방문 횟수를 증가시킨다.
     * @param userNo 사용자번호
     */
    public void visit(int userNo) {
        Pipeline p = this.jedis.pipelined();
        p.incr(KEY_PAGE_VIEW + getToday());
        p.setbit(KEY_UNIQUE_VISITOR + getToday(), userNo, true);
        p.sync();
    }

    /**
     * 요청된 날짜의 누적 방문자 수를 조회한다.
     * @param date 조회 대상 날짜
     * @return 누적 방문자 수
     */
    public int getPVCount(String date) {
        int result = 0;
        try {
            result = Integer.parseInt(jedis.get(KEY_PAGE_VIEW + date));
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    /**
     * 요청된 날짜의 순 방문자 수를 조회한다.
     * @param date 조회 대상 날짜
     * @return 순 방문자 수
     */
    public Long getUVCount(String date) {
        return jedis.bitcount(KEY_UNIQUE_VISITOR + date);
    }

    public String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }
}