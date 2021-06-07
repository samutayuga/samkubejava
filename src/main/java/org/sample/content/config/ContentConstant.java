package org.sample.content.config;

public enum ContentConstant {

    CONTENT_TYPE("Content-Type", "application/json"),
    SERVER_CONFIG("server_config", "server_config");

    final public String configVal;

    final public String cofigKey;

    ContentConstant(String configKey, String configValue) {
        this.cofigKey = configKey;
        this.configVal = configValue;
    }


}
