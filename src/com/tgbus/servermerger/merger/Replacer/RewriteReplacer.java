package com.tgbus.servermerger.merger.Replacer;


import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.Replacement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class RewriteReplacer implements Replacer {
    private Replacement replacement;
    private String tablename;
    private static Log logger = LogFactory.getLog(RewriteReplacer.class);

    public void init(String tablename, Replacement replacement) throws ServerMergerFatalException {
        logger.info("Init RewriteReplacer For :" + tablename + "=>" + replacement.getColumnName());
        this.tablename = tablename;
        this.replacement = replacement;
    }

    public ReplaceResult replace(Map<String, Object> rowData) throws ServerMergerFatalException {
        if (rowData == null || replacement == null) {
            return new ReplaceResult(false, true);
        }
        rowData.put(replacement.getColumnName(), replacement.getRewriteValue().equalsIgnoreCase("null") ? null : replacement.getRewriteValue());
        return new ReplaceResult(false, true);
    }

    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException {
    }
}
