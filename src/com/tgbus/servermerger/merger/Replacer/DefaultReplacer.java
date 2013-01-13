package com.tgbus.servermerger.merger.Replacer;

import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.Replacement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-19
 * Time: 16:20:27
 * To change this template use File | Settings | File Templates.
 */
public class DefaultReplacer implements Replacer {
    private Replacement replacement;
    private String tablename;
    public static Log logger = LogFactory.getLog(DefaultReplacer.class);

    public void init(String tablename, Replacement replacement) throws ServerMergerFatalException {
        logger.info("Init DefaultReplacer For :" + tablename + "=>" + replacement.getColumnName());
        this.tablename = tablename;
        this.replacement = replacement;
    }

    public ReplaceResult replace(Map<String, Object> rowData) throws ServerMergerFatalException {
        if (rowData == null || replacement == null) {
            return new ReplaceResult(false, true);
        }
        Object data = rowData.get(replacement.getColumnName());
        if (data == null || StringUtils.isEmpty(data.toString())) {
            rowData.put(replacement.getColumnName(), replacement.getRewriteValue().equalsIgnoreCase("null") ? null : replacement.getRewriteValue());
        }
        return new ReplaceResult(false, true);
    }

    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException {
    }
}
