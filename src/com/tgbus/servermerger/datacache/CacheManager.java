package com.tgbus.servermerger.datacache;

import com.tgbus.servermerger.MergerDataSource;
import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.ConfigManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    public static Log logger = LogFactory.getLog(CacheManager.class);
    private ConfigManager configManager;

    private MergerDataSource mergerDataSource;
    private Map<String, CacheMeta> metas = new HashMap<String, CacheMeta>();
    private Map<CacheMeta, Map<String, CacheData>> cacheMap = new HashMap<CacheMeta, Map<String, CacheData>>();

    private String selfTableName = "_MERGER_CACHE";
    private String globalTableName = "_MERGER_CACHE_GLOBAL";

    /**
     * 在源数据库中建立临时表
     */
    public void init() {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        //初始化metas
        metas.clear();
        cacheMap.clear();
        for (CacheMeta meta : configManager.getConfig().getCacheMetas()) {
            metas.put(meta.getId(), meta);
        }
        //初始化cacheMap
        try {
            //初始化公共Cache
            connection = mergerDataSource.getDestConnection();
            String sql = ("create table if not exists  " + globalTableName + " (\n"
                    + "ID int(11) auto_increment,\n" + "METAID varchar(400),\n"
                    + "OLDCOL varchar(4000),\n" + "NEWCOL varchar(4000),\n"
                    + "primary key(ID)\n" + ")");
            logger.debug(sql);
            statement = connection.createStatement();
            statement.execute(sql);
            sql = "select * FROM " + globalTableName;
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String metaid = rs.getString("METAID");
                String keyid = rs.getString("OLDCOL");
                String data = rs.getString("NEWCOL");
                CacheMeta meta = metas.get(metaid);
                if (meta == null) {
                    logger.warn("CacheMeta NOT exists :" + metaid);
                    continue;
                }
                CacheKey key = this.deserializeCacheKey(meta, keyid);
                CacheData cdata = new CacheData();
                cdata.setMeta(meta);
                cdata.setKey(key);
                cdata.setValue(data);
                if (!cacheMap.containsKey(meta)) {
                    Map<String, CacheData> mapdata = new HashMap<String, CacheData>();
                    cacheMap.put(meta, mapdata);
                }
                Map<String, CacheData> mapdata = cacheMap.get(meta);
                mapdata.put(keyid, cdata);
            }
        } catch (Exception e) {
            logger.error("Error occored when init Cache:" + e.getMessage(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException e) {
                    logger.error("Error occored when init Cache:" + e.getMessage(), e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                    statement = null;
                } catch (SQLException e) {
                    logger.error("Error occored when init Cache:" + e.getMessage(), e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e) {
                    logger.error("Error occored when init Cache:" + e.getMessage(), e);
                }
            }
        }

        try {
            //初始化私有Cache
            connection = mergerDataSource.getSrcConnection();
            String sql = ("create table if not exists  " + selfTableName + " (\n"
                    + "ID int(11) auto_increment,\n" + "METAID varchar(400),\n"
                    + "OLDCOL varchar(4000),\n" + "NEWCOL varchar(4000),\n"
                    + "primary key(ID)\n" + ")");
            logger.debug(sql);
            statement = connection.createStatement();
            statement.execute(sql);
            sql = "select * FROM " + selfTableName;
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String metaid = rs.getString("METAID");
                String keyid = rs.getString("OLDCOL");
                String data = rs.getString("NEWCOL");
                CacheMeta meta = metas.get(metaid);
                if (meta == null) {
                    logger.warn("CacheMeta NOT exists :" + metaid);
                    continue;
                }
                CacheKey key = this.deserializeCacheKey(meta, keyid);
                CacheData cdata = new CacheData();
                cdata.setMeta(meta);
                cdata.setKey(key);
                cdata.setValue(data);
                if (!cacheMap.containsKey(meta)) {
                    Map<String, CacheData> mapdata = new HashMap<String, CacheData>();
                    cacheMap.put(meta, mapdata);
                }
                Map<String, CacheData> mapdata = cacheMap.get(meta);
                mapdata.put(keyid, cdata);
            }
        } catch (Exception e) {
            logger.error("Error occored when init Cache:" + e.getMessage(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException e) {
                    logger.error("Error occored when init Cache:" + e.getMessage(), e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                    statement = null;
                } catch (SQLException e) {
                    logger.error("Error occored when init Cache:" + e.getMessage(), e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e) {
                    logger.error("Error occored when init Cache:" + e.getMessage(), e);
                }
            }
        }

    }

    private CacheKey deserializeCacheKey(CacheMeta meta, String keyid) {
        CacheKey key = generateKey(meta);
        key.deserialize(keyid);
        return key;
    }

    public CacheKey generateKey(CacheMeta meta) {
        CacheKey result;
        if ("singlon".equals(meta.getType())) {
            result = new SinglonCacheKey(meta);
        } else {
            //TODO::multicolumnkey
            result = new MultiColumnKey(meta);
        }
        return result;
    }

    /**
     * 将数据插入缓存
     *
     * @throws SQLException
     */
    public void attach(CacheData data) throws ServerMergerFatalException {

        CacheMeta meta = data.getMeta();
        //处理自身数据结构
        if (!cacheMap.containsKey(meta)) {
            Map<String, CacheData> mapdata = new HashMap<String, CacheData>();
            cacheMap.put(meta, mapdata);
        }
        Map<String, CacheData> mapdata = cacheMap.get(meta);

        mapdata.put(data.getKey().serialize(), data);
        //保存数据库
        if (meta.isGlobal()) {
            Connection connection = mergerDataSource.getDestConnection();
            String sql = "INSERT INTO " + globalTableName + " ( METAID, OLDCOL,NEWCOL) VALUES(?,?,?)";
            PreparedStatement statement = null;
            try {
                statement = connection.prepareCall(sql);
                statement.setString(1, data.getMeta().getId());
                statement.setString(2, data.getKey().serialize());
                statement.setString(3, data.getValue());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                throw new ServerMergerFatalException(e.getMessage(), e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (Exception ignored) {
                }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception ignored) {
                }
            }
        } else {
            Connection srcConn = mergerDataSource.getSrcConnection();
            String sql = "INSERT INTO " + selfTableName + " ( METAID, OLDCOL,NEWCOL) VALUES(?,?,?)";
            PreparedStatement srcSt = null;
            try {
                srcSt = srcConn.prepareCall(sql);
                srcSt.setString(1, data.getMeta().getId());
                srcSt.setString(2, data.getKey().serialize());
                srcSt.setString(3, data.getValue());
                srcSt.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                throw new ServerMergerFatalException(e.getMessage(), e);
            } finally {
                try {
                    if (srcSt != null) {
                        srcSt.close();
                    }
                } catch (Exception ignored) {
                }
                try {
                    if (srcConn != null) {
                        srcConn.close();
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 从缓存删除数据
     *
     * @param meta
     * @param key
     * @throws SQLException
     * @throws ServerMergerFatalException
     */
    public void deattach(CacheMeta meta, CacheKey key) throws ServerMergerFatalException {
        //处理自身数据结构
        if (cacheMap.containsKey(meta)) {
            Map<CacheKey, CacheData> mapdata = new HashMap<CacheKey, CacheData>();
            if (mapdata.containsKey(key)) {
                mapdata.remove(key);
            }
            if (mapdata.size() == 0) {
                cacheMap.remove(meta);
            }
        }
        if (meta.isGlobal()) {
            Connection srcConn = null;
            PreparedStatement srcSt = null;
            try {
                String sql = "DELETE FROM " + globalTableName + " WHERE METAID=? AND OLDCOL=? ";
                srcConn = mergerDataSource.getDestConnection();
                srcSt = srcConn.prepareCall(sql);
                srcSt.setString(1, meta.getId());
                srcSt.setString(2, key.serialize());
                srcSt.execute();

            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                throw new ServerMergerFatalException(e.getMessage(), e);
            } finally {
                try {
                    if (srcSt != null) srcSt.close();
                } catch (SQLException ignored) {

                }
                try {
                    if (srcConn != null) srcConn.close();
                } catch (SQLException ignored) {

                }
            }
        } else {
            Connection srcConn = null;
            PreparedStatement srcSt = null;
            try {
                String sql = "DELETE FROM " + selfTableName + " WHERE METAID=? AND OLDCOL=? ";
                srcConn = mergerDataSource.getSrcConnection();
                srcSt = srcConn.prepareCall(sql);
                srcSt.setString(1, meta.getId());
                srcSt.setString(2, key.serialize());
                srcSt.execute();

            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                throw new ServerMergerFatalException(e.getMessage(), e);
            } finally {
                try {
                    if (srcSt != null) srcSt.close();
                } catch (SQLException ignored) {

                }
                try {
                    if (srcConn != null) srcConn.close();
                } catch (SQLException ignored) {

                }
            }
        }
    }


    /**
     * 查询缓存
     *
     * @throws SQLException
     */
    public CacheData get(CacheMeta meta, CacheKey key) {
        Map<String, CacheData> mapdata = cacheMap.get(meta);
        if (mapdata == null) return null;
        return mapdata.get(key.serialize());
    }

    public CacheData get(String metaId, CacheKey key) throws Exception {
        CacheMeta cMeta = null;
        for (CacheMeta meta : configManager.getConfig().getCacheMetas()) {
            if (meta.getId().equals(metaId)) {
                cMeta = meta;
                break;
            }
        }
        if (cMeta == null) {
            throw new Exception("没有相应的META");
        }
        return get(cMeta, key);
    }

    public void setMergerDataSource(MergerDataSource mergerDataSource) {
        this.mergerDataSource = mergerDataSource;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}
