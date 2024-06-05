package cn.bugstack.middleware.db.router;

public class DBRouterConfig {
    private Integer dbCount;
    private Integer tbCount;
    private String routerKey;

    public DBRouterConfig(Integer dbCount, Integer tbCount,String routerKey) {
        this.dbCount=dbCount;
        this.tbCount=tbCount;
        this.routerKey=routerKey;
    }

    public Integer getDbCount() {
        return dbCount;
    }

    public void setDbCount(Integer dbCount) {
        this.dbCount = dbCount;
    }

    public Integer getTbCount() {
        return tbCount;
    }

    public void setTbCount(Integer tbCount) {
        this.tbCount = tbCount;
    }

    public String getRouterKey() {
        return routerKey;
    }

    public void setRouterKey(String routerKey) {
        this.routerKey = routerKey;
    }
}
