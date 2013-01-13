package com.tgbus.servermerger.merger.Replacer;


import com.tgbus.servermerger.MergerDataSource;
import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.Replacement;
import com.tgbus.servermerger.util.Util;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;

public class NameReplacer implements Replacer {
    private static Log logger = LogFactory.getLog(NameReplacer.class);

    private Replacement replacement;
    private String tablename;
    private HashSet<String> names;

    private MergerDataSource mergerDataSource;

    public void init(String tablename, Replacement replacement) throws ServerMergerFatalException {
        logger.info("Init NameReplacer For :" + tablename + "=>" + replacement.getColumnName());
        this.tablename = tablename;
        this.replacement = replacement;
        Connection conn = mergerDataSource.getDestConnection();
        this.names = new HashSet<String>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select " + replacement.getColumnName() + " from " + tablename);
            while (rs.next()) {
                String name = rs.getString(1);
                if (name != null) {
                    name = name.toLowerCase();
                    names.add(name);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ServerMergerFatalException(e.getMessage(), e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ignored) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public ReplaceResult replace(Map<String, Object> rowData) throws ServerMergerFatalException {
        if (rowData == null || replacement == null) {
            return new ReplaceResult(false, true);
        }
        String name = Util.multicast(rowData.get(replacement.getColumnName()), String.class);
        String orininalName = name;
        if (StringUtils.isEmpty(name)) {
            return new ReplaceResult(false, true);
        }
        while (names.contains(name.toLowerCase())) {
            long add = System.currentTimeMillis() % 1000000;
            name = Util.multicast(rowData.get(replacement.getColumnName()), String.class);
            name = name + add;
        }
        rowData.put(replacement.getColumnName(), name);
        return new ReplaceResult(!StringUtils.equals(orininalName, name), true);
    }

    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException {
        String name = Util.multicast(rowData.get(replacement.getColumnName()), String.class);
        names.add(name);
    }

    public void setMergerDataSource(MergerDataSource mergerDataSource) {
        this.mergerDataSource = mergerDataSource;
    }
}
