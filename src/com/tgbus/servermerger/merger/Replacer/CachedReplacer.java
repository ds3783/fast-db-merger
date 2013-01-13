package com.tgbus.servermerger.merger.Replacer;


import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.ConfigManager;
import com.tgbus.servermerger.config.Replacement;
import com.tgbus.servermerger.datacache.CacheData;
import com.tgbus.servermerger.datacache.CacheKey;
import com.tgbus.servermerger.datacache.CacheManager;
import com.tgbus.servermerger.datacache.CacheMeta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class CachedReplacer implements Replacer {
    private CacheManager cacheManager;
    private ConfigManager configManager;
    protected Replacement replacement;
    private String tablename;
    public static Log logger = LogFactory.getLog(CachedReplacer.class);

    public void init(String tablename, Replacement replacement) {
        logger.info("Init CachedReplacer For :" + tablename + "=>" + replacement.getColumnName());
        this.tablename = tablename;
        this.replacement = replacement;
    }

    public ReplaceResult replace(Map<String, Object> rowData) throws ServerMergerFatalException {
        if (rowData == null) {
            return new ReplaceResult(false, true);
        }
        if (replacement == null) {
            return new ReplaceResult(false, true);
        }
        String metaId = replacement.getSearchMetaId();
        String col = replacement.getColumnName();
        if (metaId.equals("")) {
            rowData.put(col, "");
        } else {
            CacheData data = getCache(metaId, rowData);
            if (data != null) {
                ReplaceResult result = new ReplaceResult(false, !replacement.isPk());
                if (replacement.isPk()) {
                    result.getNewPk().put(col, data.getValue());
                } else {
                    rowData.put(col, data.getValue());
                }
                return result;
            } else {
                if (replacement.isCanMiss()) {
                    return new ReplaceResult(replacement.isMissWriteBack(), true);
                } else {
                    throw new ReplaceException("Invalid cache replacement:\r\nMeta:" + metaId + "\r\nTable:" + tablename);
                }
            }
        }
        return new ReplaceResult(false, true);
    }

    protected CacheData getCache(String metaId, Map<String, Object> rowData) throws ServerMergerFatalException {
        CacheMeta cacheMeta = null;
        for (CacheMeta meta : configManager.getConfig().getCacheMetas()) {
            if (meta.getId().equals(metaId)) {
                cacheMeta = meta;
                break;
            }
        }
        if (cacheMeta == null) {
            throw new ServerMergerFatalException("Wrong cached meta!!! ID:" + metaId);

        }
        CacheKey key = null;
        key = cacheManager.generateKey(cacheMeta);
        key.setKey(rowData, replacement.getColumnAlias());
        return cacheManager.get(cacheMeta, key);
    }

    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException {
        String metaId = replacement.getSearchMetaId();
        String col = replacement.getColumnName();
        CacheMeta cacheMeta = null;
        for (CacheMeta meta : configManager.getConfig().getCacheMetas()) {
            if (meta.getId().equals(metaId)) {
                cacheMeta = meta;
                break;
            }
        }
        if (cacheMeta == null) {
            throw new ServerMergerFatalException("没有相应的META");

        }
        CacheKey key = null;
        key = cacheManager.generateKey(cacheMeta);
        key.setKey(rowData, replacement.getColumnAlias());
        CacheData cdata = new CacheData();
        cdata.setMeta(cacheMeta);
        cdata.setKey(key);
        if (newPk.containsKey(col)) {
            cdata.setValue(newPk.get(col).toString());
        } else {
            cdata.setValue(rowData.get(col).toString());
        }
        cacheManager.attach(cdata);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}
