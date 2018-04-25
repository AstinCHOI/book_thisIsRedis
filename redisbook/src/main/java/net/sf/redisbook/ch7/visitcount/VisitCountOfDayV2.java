package net.sf.redisbook.ch7.visitcount;

import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

public class VisitCountOfDayV2 {
    private JedisHelper jedisHelper;
    private Jedis jedis;
    private static final String KEY_EVENT_CLICK_DAILY_TOTAL="event:click:daily:total:hash";
    private static final String KEY_EVENT_CLICK_DAILY="event:click:daily:hash:";

    /**
     * 날짜별 방문 횟수 처리를 위한 클래스 생성자
     * @param jedisHelper 객체
     */
    public VisitCountOfDayV2(JedisHelper jedisHelper) {
        this.jedisHelper = jedisHelper;
        this.jedis = this.jedisHelper.getConnection();
    }

    /**
     * 이벤트 아이디에 해당하는 날짜의 방문 횟수와 날짜별 전체 방문 횟수를 증가시킨다.
     * @param eventId
     * @return 이벤트 페이지 방문 횟수
     */
    public Long addVisit(String eventId) {
        this.jedis.hincrBy(KEY_EVENT_CLICK_DAILY_TOTAL, getToday(), 1);
        return this.jedis.hincrBy(KEY_EVENT_CLICK_DAILY + eventId, getToday(), 1);
    }

    /**
     * 요청된 날짜에 해당하는 전체 이벤트 페이지 방문 횟수를 조회한다.
     * @return 전체 이벤트 페이지 방문 횟수
     */
    public SortedMap<String, String> getVisitCountByDailyTotal() {
        SortedMap<String, String> result = new TreeMap<>();
        result.putAll(this.jedis.hgetAll(KEY_EVENT_CLICK_DAILY_TOTAL));
        return result;
    }

    /**
     * 이벤트 아이디에 해당하는 요청된 날짜들의 방문 횟수를 조회한다.
     * @param eventId
     * @return 날짜 목록에 대한 방문 횟수 목록
     */
    public SortedMap<String, String> getVisitCountByDaily(String eventId) {
        SortedMap<String, String> result = new TreeMap<>();
        result.putAll(this.jedis.hgetAll(KEY_EVENT_CLICK_DAILY + eventId));
        return result;
    }

    private String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }
}
