package net.sf.redisbook.ch7.recentview;

import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;

import java.util.List;

public class RecentViewList {
    private Jedis jedis;
    private static final String KEY_VIEW_LIST = "recent:view:";
    private static final int listMaxSize = 30;
    private String userNo;

    public RecentViewList(JedisHelper helper, String userNo) {
        this.jedis = helper.getConnection();
        this.userNo = userNo;
    }

    /**
     * 최근 조회 상품 목록에 상품을 추가한다.
     * @param productNo 상품번호
     * @return 저장된 상품 목록의 개수
     */
    public Long add(String productNo) {
        Long result = this.jedis.lpush(KEY_VIEW_LIST + this.userNo, productNo);
        this.jedis.ltrim(KEY_VIEW_LIST + this.userNo, 0, listMaxSize - 1);

        return result;
    }

    /**
     * 주어진 사용자의 저장된 최근 조회 상품 목록을 조회한다.
     * @return 조회된 상품 목록
     */
    public List<String> getRecentViewList() {
        return this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, -1);
    }

    /**
     * 주어진 개수에 해당하는 최근 조회 상품 목록을 조회한다.
     * @param cnt 조회할 상품 개수
     * @return 조회된 상품 목록
     */
    public List<String> getRecentViewList(int cnt) {
        return this.jedis.lrange(KEY_VIEW_LIST + this.userNo, 0, cnt - 1);
    }

    /**
     * 저장 가능한 상품 목록의 최대 개수를 조회한다.
     * @return 상품 목록의 최대 개수
     */
    public int getListMaxSize() {
        return listMaxSize;
    }

}
