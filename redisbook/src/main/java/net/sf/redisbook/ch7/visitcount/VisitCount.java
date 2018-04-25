package net.sf.redisbook.ch7.visitcount;

import java.util.*;
import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;

public class VisitCount {
    private JedisHelper jedisHelper;
    private Jedis jedis;
    private static final String KEY_EVENT_CLICK_TOTAL = "event:click:total";
    private static final String KEY_EVENT_CLICK = "event:click:";

    /**
     * 방문 횟수 처리를 위한 클래스 생성자
     * @param jedisHelper 객체
     */
    public VisitCount(JedisHelper jedisHelper) {
        this.jedisHelper = jedisHelper;
        this.jedis = this.jedisHelper.getConnection();
    }

    /**
     * 요청된 이벤트 페이지의 방문 횟수와 전체 이벤트 페이지의 방문 횟수를 증가시킨다.
     * @param eventId 이벤트 아이디
     * @return 요청된 이벤트 페이지의 총 방문 횟수
     */
    public Long addVisit(String eventId) {
        this.jedis.incr(KEY_EVENT_CLICK_TOTAL);
        return this.jedis.incr(KEY_EVENT_CLICK + eventId);
    }

    /**
     * 전체 이벤트 페이지 방문 횟수를 조회한다.
     * @return 전체 이벤트 페이지 방문 횟수
     */
    public String getVisitTotalCount() {
        return this.jedis.get(KEY_EVENT_CLICK_TOTAL);
    }

    /**
     * 요청된 이벤트 아이디들에 대한 방문 횟수를 조회힌다.
     * @param eventList 이벤트 아이디 목록
     * @return 이벤트 아이디 목록에 대한 방문 횟수
     */
    public List<String> getVisitCount(String... eventList) {
        List<String> result = new ArrayList<>();
        for (int i=0; i<eventList.length; i++) {
            result.add(this.jedis.get(KEY_EVENT_CLICK + eventList[i]));
        }
        return result;
    }
}


