package net.sf.redisbook.ch7.redislogger;

public class KeyMakerForLogger implements KeyMaker {
    private static final String KEY_WAS_LOG = "was:log:list";
    private final String serverId;

    public KeyMakerForLogger(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String getKey() {
        return KeyMakerForLogger.KEY_WAS_LOG + this.serverId;
    }

    public String getServerId() {
        return this.serverId;
    }
}
