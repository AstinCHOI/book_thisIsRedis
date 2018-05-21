package net.sf.redisbook.ch8.replication;

import redis.clients.jedis.Jedis;

public class DataReader {
    private Jedis jedis;

    /**
     * 데이터 조회을 위한 Reader 클래스 생성자
     * @param jedis 데이터를 저장할 노드에 대한 제디스 연결
     */
    public DataReader(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 주어진 키에 데이터를 조회한다.
     * @param key 데이터 조회를 위한 레디스의 키
     * return value 조회된 데이터, 키가 존재하지 않으면 null
     */
    public String get(String key) {
        return this.jedis.get(key);
    }
}
