package com.tgbus.servermerger.merger.Replacer;

import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.Replacement;
import com.tgbus.servermerger.datacache.CacheData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-25
 * Time: 21:53:50
 * To change this template use File | Settings | File Templates.
 */
public class ListedCachedReplacer extends CachedReplacer implements Replacer {

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
        String keyColumn = null;
        if (replacement.getColumnAlias() != null) {
            for (String repkey : replacement.getColumnAlias().keySet()) {
                Object keyc = replacement.getColumnAlias().get(repkey);
                if (keyc != null) {
                    keyColumn = keyc.toString();
                }
            }
        }
        if (keyColumn == null) {
            return new ReplaceResult(false, true);
        }
        String metaId = replacement.getSearchMetaId();
        String col = replacement.getColumnName();
        String keyData = null;
        if (rowData.get(keyColumn) != null) {
            keyData = rowData.get(keyColumn).toString();
        } else {
            return new ReplaceResult(false, true);
        }
        String[] keys = keyData.split(replacement.getDeterminter());
        List<String> vals = new ArrayList<String>();
        for (String key : keys) {
            Map<String, Object> tempData = new HashMap<String, Object>(rowData);
            tempData.put(keyColumn, key);
            CacheData data = getCache(metaId, tempData);
            if (data != null) {
                vals.add(data.getValue());
            } else {
                if (!replacement.isCanMiss()) {
                    throw new ReplaceException("Invalid cache replacement:\r\nMeta:" + metaId + "\r\nTable:" + tablename);
                }
            }
        }
        StringBuffer newval = new StringBuffer();
        for (Iterator<String> iterator = vals.iterator(); iterator.hasNext();) {
            String str = iterator.next();
            newval.append(str);
            if (iterator.hasNext()) {
                newval.append(replacement.getDeterminter());
            }
        }
        rowData.put(replacement.getColumnName(), newval.toString());
        return new ReplaceResult(false, true);
    }

    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException {

    }

}
