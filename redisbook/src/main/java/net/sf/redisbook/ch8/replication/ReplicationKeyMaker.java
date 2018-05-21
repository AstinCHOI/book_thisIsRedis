package net.sf.redisbook.ch8.replication;

import net.sf.redisbook.ch7.redislogger.KeyMaker;

public class ReplicationKeyMaker implements KeyMaker {
    private static final String keyPrefix = "Replication-";
    private int index;

    /**
     * 키 메이커 클래스를 위한 생성자
     * @param index 키 생성을 위한 인덱스 값
     */
    public ReplicationKeyMaker(int index) {
        this.index = index;
    }

    @Override
    public String getKey() {
        return keyPrefix + this.index;
    }
}
