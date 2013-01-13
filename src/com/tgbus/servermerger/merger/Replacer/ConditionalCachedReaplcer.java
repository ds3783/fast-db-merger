package com.tgbus.servermerger.merger.Replacer;

import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.ReplaceCondition;
import com.tgbus.servermerger.config.Replacement;
import com.tgbus.servermerger.datacache.CacheData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-25
 * Time: 13:46:53
 */
public class ConditionalCachedReaplcer extends CachedReplacer implements Replacer {

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
        ReplaceCondition cond = null;
        if (replacement.getReplaceConditions() != null) {
            for (ReplaceCondition condition : replacement.getReplaceConditions()) {
                if (rowData.get(condition.getColumnName()) != null && StringUtils.equals(rowData.get(condition.getColumnName()).toString(), condition.getValue())) {
                    cond = condition;
                }
            }
        }
        if (cond == null) {
            if (replacement.isCanMiss()) {
                return new ReplaceResult(false, true);
            } else {
                throw new ReplaceException("ÎÞÐ§µÄÌæ»»:\r\nReplacement:" + replacement.toString());
            }
        }
        String metaId = cond.getSearchMetaId();
        String col = replacement.getColumnName();
        if (metaId.equals("")) {
            rowData.put(col, "");
        } else {
            CacheData data = getCache(metaId, rowData);
            if (data != null) {
                rowData.put(col, data.getValue());
                return new ReplaceResult(false, !replacement.isPk());
            } else {
                if (replacement.isCanMiss()) {
                    return new ReplaceResult(false, true);
                } else {
                    throw new ReplaceException("Invalid cache replacement:\r\nMeta:" + metaId + "\r\nTable:" + tablename);
                }
            }
        }
        return new ReplaceResult(false, true);
    }

    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException {

    }

}

