package net.sf.redisbook.ch7.visitcount;

import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitCountOfDay {
    private JedisHelper jedisHelper;
    private Jedis jedis;
    private static final String KEY_EVENT_CLICK_DAILY_TOTAL="event:click:daily:total";
    private static final String KEY_EVENT_CLICK_DAILY="event:click:daily:";

    /**
     * 날짜별 방문 횟수 처리를 위한 클래스 생성자
     * @param jedisHelper 객체
     */
    public VisitCountOfDay(JedisHelper jedisHelper) {
        this.jedisHelper = jedisHelper;
        this.jedis = this.jedisHelper.getConnection();
    }

    /**
     * 이벤트 아이디에 해당하는 날짜의 방문 횟수와 날짜별 전체 방문 횟수를 증가시킨다.
     * @param eventId
     * @return 이벤트 페이지 방문 횟수
     */
    public Long addVisit(String eventId) {
        this.jedis.incr(KEY_EVENT_CLICK_DAILY_TOTAL + getToday());
        return this.jedis.incr(KEY_EVENT_CLICK_DAILY + getToday() + ":" + eventId);
    }

    /**
     * 요청된 날짜에 해당하는 전체 이벤트 페이지 방문 횟수를 조회한다.
     * @return 전체 이벤트 페이지 방문 횟수
     */
    public String getVisitTotalCount(String date) {
        return this.jedis.get(KEY_EVENT_CLICK_DAILY_TOTAL + date);
    }

    /**
     * 이벤트 아이디에 해당하는 요청된 날짜들의 방문 횟수를 조회한다.
     * @param eventId
     * @param dateList
     * @return 날짜 목록에 대한 방문 횟수 목록
     */
    public List<String> getVisitCountByDate(String eventId, String[] dateList) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < dateList.length; i++) {
            String one = jedis.get(KEY_EVENT_CLICK_DAILY + dateList[i] + ":" + eventId);
            if (one != null) {
                result.add(one);
            }
        }
        return result;
    }

    private String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }
}
