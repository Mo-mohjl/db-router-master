package cn.bugstack.middleware.db.router.strategy.impl;

import cn.bugstack.middleware.db.router.DBContextHolder;
import cn.bugstack.middleware.db.router.DBRouterConfig;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBRouterStrategyHashCode implements IDBRouterStrategy {
    private Logger logger= LoggerFactory.getLogger(DBRouterStrategyHashCode.class);
    private DBRouterConfig dbRouterConfig;
    public DBRouterStrategyHashCode(DBRouterConfig dbRouterConfig){
        this.dbRouterConfig=dbRouterConfig;
    }
    @Override
    public void doRouter(String dbKeyAttr) {
        int size=dbRouterConfig.getDbCount()*dbRouterConfig.getTbCount();
        int idx = (size - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));
        int dbIdx = idx / dbRouterConfig.getTbCount() + 1;
        int tbIdx = idx - dbRouterConfig.getTbCount() * (dbIdx - 1);
        DBContextHolder.setDBKey(String.format("%02d",dbIdx));
        DBContextHolder.setTBKey(String.format("%02d",tbIdx));
        logger.info("数据库路由dbKey:{},tbKey:{}",dbIdx,tbIdx);
    }

    @Override
    public void setDBKey(int dbIdx) {
        DBContextHolder.setDBKey(String.format("%02d",dbIdx));
    }

    @Override
    public void setTBKey(int tbIdx) {
        DBContextHolder.setTBKey(String.format("%02d",tbIdx));
    }

    @Override
    public int dbCount() {
        return dbRouterConfig.getDbCount();
    }

    @Override
    public int tbCount() {
        return dbRouterConfig.getTbCount();
    }

    @Override
    public void clear() {
        DBContextHolder.clearDBKey();
        DBContextHolder.clearTBKey();
    }
}
