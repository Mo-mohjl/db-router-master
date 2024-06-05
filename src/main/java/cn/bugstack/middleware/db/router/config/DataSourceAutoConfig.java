package cn.bugstack.middleware.db.router.config;

import cn.bugstack.middleware.db.router.DBRouterConfig;
import cn.bugstack.middleware.db.router.DBRouterJoinPoint;
import cn.bugstack.middleware.db.router.dynamic.DynamicDataSource;
import cn.bugstack.middleware.db.router.dynamic.DynamicMybatisPlugin;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.middleware.db.router.strategy.impl.DBRouterStrategyHashCode;
import cn.bugstack.middleware.db.router.util.PropertyUtil;
import com.alibaba.fastjson.JSON;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {
    private Logger logger= LoggerFactory.getLogger(DataSourceAutoConfig.class);
    /**
     * 数据源配置组
     */
    private Map<String,Map<String,Object>> dataSourceMap=new HashMap<>();
    /**
     * 默认数据源配置
     */
    private Map<String,Object> defaultDataSourceConfig=new HashMap<>();
    private Integer dbCount;
    private Integer tbCount;
    private String routerKey;
    @Bean(name = "db-router-point")
    @ConditionalOnMissingBean
    public DBRouterJoinPoint point(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy){
        return new DBRouterJoinPoint(dbRouterConfig,dbRouterStrategy);
    }
    @Bean
    public DBRouterConfig dbRouterConfig(){
        return new DBRouterConfig(dbCount,tbCount,routerKey);
    }
    @Bean
    public Interceptor plugin(){
        return new DynamicMybatisPlugin();
    }
    @Bean
    public DataSource dataSource(){
        Map<Object,Object> targetDataSources=new HashMap<>();
        logger.info("读取配置信息，共{}个数据源", targetDataSources.size());
        for(String dbInfo:dataSourceMap.keySet()){
            Map<String,Object> objectMap=dataSourceMap.get(dbInfo);
            targetDataSources.put(dbInfo,new DriverManagerDataSource(objectMap.get("url").toString(),objectMap.get("username").toString(),objectMap.get("password").toString()));
            logger.info("读取配置信息");
            logger.info("数据源 {} 的URL是 {}", dbInfo, ((DriverManagerDataSource)targetDataSources.get(dbInfo)).getUrl());
        }
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;
    }
    @Bean
    public IDBRouterStrategy dbRouterStrategy(DBRouterConfig dbRouterConfig){
        return new DBRouterStrategyHashCode(dbRouterConfig);
    }

    /**
     *自动配置事务管理
     */
    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }

    /**
     * 读取resource配置
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        String prefix = "router.jdbc.datasource.";
        dbCount= Integer.valueOf(environment.getProperty(prefix+"dbCount"));
        tbCount=Integer.valueOf(environment.getProperty(prefix+"tbCount"));
        String dataSources = environment.getProperty(prefix + "list");
        for (String dbInfo:dataSources.split(",")){
            Map<String,Object> dataSourceProps  = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
            dataSourceMap.put(dbInfo,dataSourceProps);
        }
    }
}
