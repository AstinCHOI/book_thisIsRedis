package net.sf.redisbook;

import java.util.*;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;


public class JedisHelper {
    protected static final String REDIS_HOST = "localhost";
    protected static final int REDIS_PORT = 6379;
    private final Set<Jedis> connectionList = new HashSet<>();
    private JedisPool pool;

    private JedisHelper() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(20);
        config.setBlockWhenExhausted(true);

        this.pool = new JedisPool(config, REDIS_HOST, REDIS_PORT, 5000);
    }

    // 싱글톤 3가지 구현 방법: http://blog.daum.net/smufu/3
    private static class LazyHolder {
        private static final JedisHelper INSTANCE = new JedisHelper();
    }

    public static JedisHelper getInstance() {
        return LazyHolder.INSTANCE;
    }

    final public Jedis getConnection() {
        Jedis jedis = this.pool.getResource();
        this.connectionList.add(jedis);
        return jedis;
    }

//    final public void returnResource(Jedis jedis) {
//        jedis.close(); // this.pool.returnResource(jedis);
//    }

    final public void destoryPool() {
        Iterator<Jedis> jedisList = this.connectionList.iterator();
        while (jedisList.hasNext()) {
            Jedis jedis = jedisList.next();

            try {
                jedis.close(); // this.pool.returnResource(jedis);
            } catch (JedisException e) {
                // do nothing
            }
        }
        this.pool.destroy();
    }
}