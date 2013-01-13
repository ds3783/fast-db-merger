package com.tgbus.servermerger;


import com.tgbus.servermerger.config.ConfigManager;
import com.tgbus.servermerger.config.Task;
import com.tgbus.servermerger.datacache.CacheManager;
import com.tgbus.servermerger.merger.Merger;
import com.tgbus.servermerger.merger.ModuleMerger;
import com.tgbus.servermerger.merger.ProgressControl;
import com.tgbus.servermerger.merger.TableMerger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    private static ApplicationContext context = new ClassPathXmlApplicationContext(
            "beans.xml");

    /**
     * @param args
     */

    private static ConfigManager configManager;
    private static Merger merger;
    private static TableMerger tableMerger;
    private static ModuleMerger moduleMerger;
    private static CacheManager cacheManager;
    private static ProgressControl progressControl;
    public static Log logger = LogFactory.getLog(Main.class);

    public static void main(String[] args) {
        configManager = (ConfigManager) context.getBean("configManager");
        merger = (Merger) context.getBean("merger");
        tableMerger = (TableMerger) context.getBean("tableMerger");
        moduleMerger = (ModuleMerger) context.getBean("moduleMerger");
        cacheManager = (CacheManager) context.getBean("cacheManager");
        progressControl = (ProgressControl) context.getBean("progressControl");
        //ctl+cµÄÀ¹½Ø
        ShutdownHooker shutdownHooker = (ShutdownHooker) context.getBean("shutdownHooker");
        Thread t = new Thread(shutdownHooker);
        Runtime.getRuntime().addShutdownHook(t);


        MergerDataSource mergerDataSource = (MergerDataSource) context.getBean("mergerDataSource");
        Connection srcConn = null;
        Connection destConn = null;
        for (Task task : configManager.getConfig().getTasks()) {
            if (ProgressControl.STATE_COMPLETE.equals(progressControl.getTaskProgress(task.getName()))) {
                continue;
            }
            try {
                srcConn = mergerDataSource.getSrcConnection();
                destConn = mergerDataSource.getDestConnection();
                PreparedStatement ps = null;
                progressControl.updateTaskProgress(task.getName(), ProgressControl.STATE_DOING);
                if (task.getType().equalsIgnoreCase("sql")) {
                    try {
                        if (task.getSide().equalsIgnoreCase("src"))
                            ps = srcConn.prepareCall(task.getText());
                        else if (task.getSide().equalsIgnoreCase("dest"))
                            ps = destConn.prepareCall(task.getText());
                        logger.info("Insert Sql to " + task.getSide() + " : " + task.getText());
                        ps.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        logger.error("Failed To Execute Merge SQL And Exit Merge !");
                        break;
                    } finally {
                        if (ps != null) {
                            try {
                                ps.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            ps = null;
                        }
                    }

                } else {
                    merger.merge(srcConn, destConn);
                }
                progressControl.updateTaskProgress(task.getName(), ProgressControl.STATE_COMPLETE);
            } catch (ServerMergerFatalException e) {
                e.printStackTrace();
                logger.error("Failed To Execute Merge And Exit !");
                break;
            }


        }
        if (srcConn != null) {
            mergerDataSource.releaseSrcConnection(srcConn);
        }
        if (destConn != null) {
            mergerDataSource.releaseDestConnection(destConn);
        }

    }

}
