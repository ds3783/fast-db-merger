package com.tgbus.servermerger;

import com.tgbus.servermerger.config.Config;
import com.tgbus.servermerger.config.ConfigManager;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-7-7
 * Time: 15:56:54
 * To change this template use File | Settings | File Templates.
 */
public class MergerDataSource {
    private static Log logger = LogFactory.getLog(MergerDataSource.class);
    private BasicDataSource srcDataSource;
    private BasicDataSource destDataSource;

    private ConfigManager configManager;


    protected boolean defaultAutoCommit = true;
    protected java.lang.Boolean defaultReadOnly = false;
    protected int defaultTransactionIsolation = -1;
    protected java.lang.String defaultCatalog = null;
    protected int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;
    protected int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;
    protected int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;
    protected int initialSize = 0;
    protected long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;
    protected boolean poolPreparedStatements = false;
    protected int maxOpenPreparedStatements = GenericKeyedObjectPool.DEFAULT_MAX_TOTAL;
    protected boolean testOnBorrow = true;
    protected boolean testOnReturn = true;
    protected long timeBetweenEvictionRunsMillis = GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    protected int numTestsPerEvictionRun = GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
    protected long minEvictableIdleTimeMillis = GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    protected boolean testWhileIdle = false;
    protected java.lang.String validationQuery = null;
    private boolean accessToUnderlyingConnectionAllowed = false;

    public void init() throws ServerMergerFatalException {
        Config config = configManager.getConfig();
        try {
            srcDataSource = getNewDataSource();
            srcDataSource.setDriverClassName(config.getDbconfig().getDriverName());
            srcDataSource.setUrl(config.getDbconfig().getSrcURL());
            srcDataSource.setUsername(config.getDbconfig().getSrcUser());
            srcDataSource.setPassword(config.getDbconfig().getSrcPass());
            Connection testConn = srcDataSource.getConnection();
            testConn.close();

        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
            throw new ServerMergerFatalException("Error when initailizing source data connection: " + e.getMessage(), e);
        }
        try {
            destDataSource = getNewDataSource();
            destDataSource.setDriverClassName(config.getDbconfig().getDriverName());
            destDataSource.setUrl(config.getDbconfig().getDestURL());
            destDataSource.setUsername(config.getDbconfig().getDestUser());
            destDataSource.setPassword(config.getDbconfig().getDestPass());
            Connection testConn = destDataSource.getConnection();
            testConn.close();

        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
            throw new ServerMergerFatalException("Error when initailizing dest data connection: " + e.getMessage(), e);
        }
    }

    private BasicDataSource getNewDataSource() {
        BasicDataSource result = new BasicDataSource();
        result.setDefaultAutoCommit(defaultAutoCommit);
        result.setDefaultReadOnly(defaultReadOnly);
        result.setDefaultTransactionIsolation(defaultTransactionIsolation);
        result.setDefaultCatalog(defaultCatalog);
        result.setMaxActive(maxActive);
        result.setMaxIdle(maxIdle);
        result.setMinIdle(minIdle);
        result.setInitialSize(initialSize);
        result.setMaxWait(maxWait);
        result.setPoolPreparedStatements(poolPreparedStatements);
        result.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        result.setTestOnBorrow(testOnBorrow);
        result.setTestOnReturn(testOnReturn);
        result.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        result.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        result.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        result.setTestWhileIdle(testWhileIdle);
        result.setValidationQuery(validationQuery);
        result.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
        return result;
    }

    public Connection getSrcConnection() throws ServerMergerFatalException {
        try {
            return srcDataSource.getConnection();
        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
            throw new ServerMergerFatalException("Error when creating source data connection: " + e.getMessage(), e);
        }
    }

    public Connection getDestConnection() throws ServerMergerFatalException {
        try {
            return destDataSource.getConnection();
        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
            throw new ServerMergerFatalException("Error when creating dest data connection: " + e.getMessage(), e);
        }
    }

    public void releaseSrcConnection(Connection c) {
        try {
            c.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void releaseDestConnection(Connection c) { 
        try {
            c.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}
