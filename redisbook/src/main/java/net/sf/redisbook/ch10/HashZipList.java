package net.sf.redisbook.ch10;

import net.sf.redisbook.JedisHelper;
import redis.clients.jedis.Jedis;

import java.util.Random;

public class HashZipList {

    private Jedis jedis;
    private static final int INIT_HASH_ENTRY = 512;
    private static final int INIT_HASH_DATA_LENGTH = 64;
    private static final String KEY_HASH_ENTRY_TEST = "ziplist:hash:entry:test";
    private static final String KEY_HASH_LENGTH_TEST = "ziplist:hash:length:test";
    String chars[] = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");

    public HashZipList(JedisHelper helper) {
        this.jedis = helper.getConnection();
    }

    /**
     * 512개의 데이터가 저장된 해시 데이터의 인코딩 정보를 가져온다.
     * @return 인코딩 상태 문자열
     */
    public String getBeforeEncoding1() {
        initHashForEntryTest();
        return this.jedis.objectEncoding(KEY_HASH_ENTRY_TEST);
    }

    /**
     * 513개의 데이터가 저장된 해시 데이터의 인코딩 정보를 가져온다.
     * @return 인코딩 상태 문자열
     */
    public String getAfterEncoding1() {
        this.jedis.hset(KEY_HASH_ENTRY_TEST, "lastfield", "test value");
        return this.jedis.objectEncoding(KEY_HASH_ENTRY_TEST);
    }

    /**
     * 한 개의 64바이트의 데이터가 저장된 해시 데이터의 인코딩 정보를 가져온다.
     * @return 인코딩 상태 문자열
     */
    public String getBeforeEncoding2() {
        initHashForLengthTest();
        return this.jedis.objectEncoding(KEY_HASH_LENGTH_TEST);
    }

    /**
     * 한 개의 65바이트의 데이터가 추가된 해시 데이터의 인코딩 정보를 가져온다.
     * @return 인코딩 상태 문자열
     */
    public String getAfterEncoding2() {
        String testValue = this.makeRandomString(INIT_HASH_DATA_LENGTH + 1);
        this.jedis.hset(KEY_HASH_LENGTH_TEST, "field2", testValue);

        return this.jedis.objectEncoding(KEY_HASH_LENGTH_TEST);
    }

    /**
     * 저장된 길이의 랜덤 문자열을 생성한다.
     * @param size
     */
    private String makeRandomString(int size) {
        Random rand = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < INIT_HASH_ENTRY; i++) {
            builder.append(this.chars[rand.nextInt(this.chars.length)]);
        }

        return builder.toString();
    }

    private void initHashForEntryTest() {
        this.jedis.del(KEY_HASH_ENTRY_TEST);

        for (int i = 0; i < INIT_HASH_ENTRY; i++) {
            this.jedis.hset(KEY_HASH_ENTRY_TEST, "field1" + (i+1), "test value" + i);
        }
    }

    private void initHashForLengthTest() {
        this.jedis.del(KEY_HASH_LENGTH_TEST);
        String testValue = this.makeRandomString(INIT_HASH_DATA_LENGTH);
        this.jedis.hset(KEY_HASH_LENGTH_TEST, "field", testValue);
    }
}
