package com.sg.base.conf;

/**
 * DeployMode
 *
 * @author Dai Wenqing
 * @date 2016/1/26
 */
public enum DeployMode {
    Web("web"), Singleton("singleton"), Zk("zk");

    private String type = "";

    public String getType() {
        return type;
    }

    private DeployMode(String type) {
        this.type = type;
    }
}
