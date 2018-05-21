package net.sf.redisbook;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Hashing;

import java.util.*;

public class ShardedJedisHelper {
    protected static final String SHARD1_HOST = "127.0.0.1";
    protected static final int SHARD1_PORT = 6380;
    protected static final String SHARD2_HOST = "127.0.0.1";
    protected static final int SHARD2_PORT = 6381;

    private final Set<ShardedJedis> connectionList = new HashSet<ShardedJedis>();
    private ShardedJedisPool shardedPool;

    /**
     * 싱글톤 처리를 위한 홀더 클래스
     * 제디스 연결풀이 포함된 도우미 객체를 반환한다.
     */
    private static class LazyHolder {
        @SuppressWarnings("synthetic-access")
        private static final ShardedJedisHelper INSTANCE = new ShardedJedisHelper();
    }

    /**
     * 샤딩된 제디스 연결품 생성을 위한 헬퍼 클래스 내부 생성자
     * 싱글톤 패턴이므로 외부에서 호출할 수 없다.
     */
    private ShardedJedisHelper() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(20);
        config.setBlockWhenExhausted(true);


        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo(SHARD1_HOST, SHARD1_PORT));
        shards.add(new JedisShardInfo(SHARD2_HOST, SHARD2_PORT));

        this.shardedPool = new ShardedJedisPool(config, shards, Hashing.MURMUR_HASH);
    }

    /**
     * 싱글톤 객체를 가져온다.
     * @return 제디스 도우미 객체
     */
    @SuppressWarnings("synthetic-access")
    public static ShardedJedisHelper getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 제디스 클라이언트 연결을 가져온다.
     * @return 제디스 객체
     */
    final public ShardedJedis getConnection() {
        ShardedJedis jedis = this.shardedPool.getResource();
        this.connectionList.add(jedis);

        return jedis;
    }

//    final public void returnResource(ShardedJedis jedis) {
//        this.shardedPool.close(); // this.shardedPool.returnResource(jedis);
//    }

    /**
     * 제디스 연결 풀을 제거한다.
     */
    final public void destoryPool() {
        Iterator<ShardedJedis> jedisList = this.connectionList.iterator();
        while (jedisList.hasNext()) {
            ShardedJedis jedis = jedisList.next();

            try {
                jedis.close(); // this.pool.returnResource(jedis);
            } catch (JedisException e) {
                // do nothing
            }
        }
        this.shardedPool.destroy();
    }
}
