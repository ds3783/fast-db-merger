package com.tgbus.servermerger.merger;

import com.tgbus.servermerger.MergerDataSource;
import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.ConfigManager;
import com.tgbus.servermerger.config.NoInsertTableMerge;
import com.tgbus.servermerger.config.Replacement;
import com.tgbus.servermerger.config.Table;
import com.tgbus.servermerger.datacache.CacheData;
import com.tgbus.servermerger.datacache.CacheKey;
import com.tgbus.servermerger.datacache.CacheManager;
import com.tgbus.servermerger.datacache.CacheMeta;
import com.tgbus.servermerger.merger.Replacer.ReplaceException;
import com.tgbus.servermerger.merger.Replacer.ReplaceResult;
import com.tgbus.servermerger.merger.Replacer.Replacer;
import com.tgbus.servermerger.util.SpringHelper;
import com.tgbus.servermerger.util.Util;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.sql.*;
import java.util.*;

public class TableMerger {
    public static Log logger = LogFactory.getLog(TableMerger.class);
    private ConfigManager configManager;
    private CacheManager cacheManager;
    private MergerDataSource mergerDataSource;
    private SpringHelper springHelper;
    private ProgressControl progressControl;
    private int step;
    private long complete;
    private String module;
    private String tablename;
    private long totalrows;
    public boolean finished;

    private TimeredLogger timeredLogger;

    public void mergeTable(String m, String name, Connection srcConn, Connection destConn) throws ServerMergerFatalException {
        finished = false;
        complete = 0;
        tablename = name;
        module = m;
        timeredLogger = new TimeredLogger();

        //找到合并表的配置
        Set<Table> tables = configManager.getConfig().getTables();   //所有配置的表


        Table table = null;
        for (Table t : tables) {
            if (t.getName().equalsIgnoreCase(tablename)) {
                table = t;
                break;
            }
        }
        if (table == null) {
            logger.error("没有找到表" + tablename + "的配置");
            throw new ServerMergerFatalException("没有找到表" + tablename + "的配置");
        }
        //初始化所有的Replacer
        List<Replacer> tableReplacers = new ArrayList<Replacer>();//replacer集合
        //初始化raplacer
        if (table.getReplacements() != null) {
            for (Replacement replacement : table.getReplacements()) {
                Replacer replacer = springHelper.getBean("replacer_" + replacement.getType(), Replacer.class);
                replacer.init(tablename, replacement);
                tableReplacers.add(replacer);
            }
        }

        //初始化表结构
        Map<String, String> tableMeta = null;//表结构
        Map<String, String> writeMeta = null;//表结构
        Map<String, String> keyMeta = new HashMap<String, String>();//表结构

        //获得数据量
        String condition;
        if (StringUtils.isEmpty(table.getCondition())) {
            condition = "";
        } else {
            condition = " where " + table.getCondition();
        }
        String sql = "select count(*) from " + tablename + condition;
        totalrows = 0;
        // Connection srcConn = mergerDataSource.getSrcConnection();
        try {
            Statement srcSt = srcConn.createStatement();
            ResultSet rs = srcSt.executeQuery(sql);
            if (rs.next()) {
                totalrows = rs.getInt(1);
            }
            rs.close();
            srcSt.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ServerMergerFatalException("无法取得表" + tablename + "记录数：" + e.getMessage(), e);
        }

        long start = progressControl.getModuleProgress(module, tablename);
        complete = start;

        //合并数据
        try {
            Thread t = new Thread(timeredLogger);
            timeredLogger.setWrapperedThread(t);
            timeredLogger.setMerger(this);
            t.start();
            String orderby = "";
            if (table.getPk() != null) {
                for (Iterator<String> iterator = table.getPk().getColumns().iterator(); iterator.hasNext(); ) {
                    String col = iterator.next();
                    orderby += col;
                    if (iterator.hasNext()) {
                        orderby += ",";
                    }
                }
            }


            PreparedStatement selectStmt = srcConn.prepareStatement("select * from " + tablename + condition + " order by " + orderby + "  limit ?,?");
            logger.info("SELECTSQL; select * from " + tablename + condition + " order by " + orderby + "  limit ?,?");
            PreparedStatement insertStmt = null;
            while (true) {
                //读取数据
                selectStmt.setLong(1, start);
                selectStmt.setInt(2, step);
                ResultSet rs = selectStmt.executeQuery();
                if (tableMeta == null) {
                    tableMeta = new HashMap<String, String>();
                    ResultSetMetaData meta = rs.getMetaData();
                    for (int i = 0; i < meta.getColumnCount(); i++) {
                        String coltype = meta.getColumnTypeName(i + 1);
                        coltype = coltype.replaceAll(" .*", "");
                        tableMeta.put(meta.getColumnName(i + 1), coltype);
                    }
                    writeMeta = new HashMap<String, String>(tableMeta);
                    if (table.getPk() != null) {
                        for (String s : table.getPk().getColumns()) {
                            if (table.getPk().isUnset()) {
                                writeMeta.remove(s);
                            }
                            keyMeta.put(s, tableMeta.get(s));
                        }
                    }
                }
                boolean nodata = true;
                while (rs.next()) {
                    nodata = false;
                    Map<String, Object> rowData = Util.readRowData(rs, tableMeta);
                    //调用replacer
                    List<Replacer> writeBackReplacers = new ArrayList<Replacer>();
                    boolean needinsert = true;
                    Map<String, Object> newPk = new HashMap<String, Object>();
                    try {
                        for (Replacer replacer : tableReplacers) {
                            ReplaceResult result = replacer.replace(rowData);
                            if (result.isWriteback()) {
                                writeBackReplacers.add(replacer);
                            }
                            if (needinsert && !result.isNeedinsert()) {
                                needinsert = false;
                                newPk = result.getNewPk();
                            }
                        }
                    } catch (ReplaceException e) {
                        //遇到无效数据
                        complete++;
                        continue;
                    }

                    if (insertStmt == null) {
                        insertStmt = Util.prepareInsert(tablename, writeMeta, destConn);
                    }

                    //插入数据
                    try {
                        if (needinsert) {
                            newPk = Util.doInsert(insertStmt, writeMeta, keyMeta, rowData);
                        } else {
                            if (table.getNoInsertTableMerge() != null) {
                                for (NoInsertTableMerge merge : table.getNoInsertTableMerge()) {
                                    noInsertMerge(merge, rowData, newPk, keyMeta);
                                }
                            }
                        }
                    } catch (SQLException e) {
                        //增加忽略插入错误
                        if (!table.isIgnorefail()) {
                            throw e;
                        }
                    }
                    //回调replacer
                    for (Replacer replacer : writeBackReplacers) {
                        replacer.afterInsert(rowData, newPk);
                    }
                    //写入缓存
                    if (table.getMetas() != null) {
                        for (String metaId : table.getMetas()) {
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
                            CacheKey key = cacheManager.generateKey(cacheMeta);
                            key.setKey(rowData, new HashMap<String, String>());

                            CacheData cdata = new CacheData();
                            cdata.setMeta(cacheMeta);
                            cdata.setKey(key);
                            if (newPk.containsKey(cacheMeta.getKeyColumn())) {
                                cdata.setValue(newPk.get(cacheMeta.getKeyColumn()).toString());
                            } else {
                                cdata.setValue(rowData.get(cacheMeta.getKeyColumn()).toString());
                            }
                            cacheManager.attach(cdata);
                        }
                    }

                    complete++;
                }
                progressControl.updateModuleProgress(module, tablename, complete);
                //如果退出文件存在则抛出退出异常
                File quitfile = new File("merger.quit");
                if (quitfile.exists()) {
                    throw new ServerMergerFatalException("merger.quit exists,program exit!");
                }
                if (nodata) {
                    break;
                } else {
                    start += step;
                }
            }
            finished = true;
            progressControl.updateModuleProgress(module, name, ProgressControl.PROGRESS_FINISHED);

            logger.info("FINISH MERGE" + name + "!");
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ServerMergerFatalException("合并表" + name + "出错：" + e.getMessage(), e);
        } finally {
            progressControl.updateModuleProgress(module, tablename, complete);
            try {
                timeredLogger.getWrapperedThread().interrupt();
            } catch (Exception ignored) {

            }
        }


    }

    private void noInsertMerge(NoInsertTableMerge merge, Map<String, Object> rowData, Map<String, Object> newPk, Map<String, String> keyMeta) throws ServerMergerFatalException, SQLException {
        Connection conn = mergerDataSource.getDestConnection();
        Statement stmt = conn.createStatement();
        String addval = null;
        if (rowData.get(merge.getColname()) != null) {
            addval = rowData.get(merge.getColname()).toString();
        }
        if (StringUtils.isEmpty(addval)) {
            return;
        }
        List<String> where = new ArrayList<String>();
        for (String keycol : keyMeta.keySet()) {
            where.add("`" + keycol + "`=" + newPk.get(keycol).toString());
        }
        if (where.size() == 0) {
            return;
        }
        String strWhere = "";
        for (Iterator<String> iterator = where.iterator(); iterator.hasNext(); ) {
            String s = iterator.next();
            strWhere += s;
            if (iterator.hasNext()) {
                strWhere += " and ";
            }
        }
        String query = "update " + tablename + " set " + merge.getColname() + "=" + merge.getColname() + merge.getOperation() + addval + " where " + strWhere;
        stmt.executeUpdate(query);
        stmt.close();
        conn.close();
    }

    public void onExit() {
        if (progressControl.getModuleProgress(module, tablename) != ProgressControl.PROGRESS_FINISHED) {
            progressControl.updateModuleProgress(module, tablename, complete);
        }
    }

    public String getProgressString() {
        return "Module:" + module + " Table:" + tablename + " " + complete + "/" + totalrows + "  (" + ((totalrows > 0) ? "" + ((int) ((double) complete / totalrows * 100)) + "%" : "") + ")";
    }


    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    public void setStep(int step) {
        this.step = step;
    }

    public void setSpringHelper(SpringHelper springHelper) {
        this.springHelper = springHelper;
    }

    public void setProgressControl(ProgressControl progressControl) {
        this.progressControl = progressControl;
    }

    public void setMergerDataSource(MergerDataSource mergerDataSource) {
        this.mergerDataSource = mergerDataSource;
    }

    private void init() {
        this.step = configManager.getConfig().getStep();
    }
}
