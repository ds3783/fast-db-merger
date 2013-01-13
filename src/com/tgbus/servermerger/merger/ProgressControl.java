package com.tgbus.servermerger.merger;

import com.tgbus.servermerger.MergerDataSource;
import com.tgbus.servermerger.ServerMergerFatalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-21
 * Time: 15:11:30
 * To change this template use File | Settings | File Templates.
 */
public class ProgressControl {
    private static Log logger = LogFactory.getLog(ProgressControl.class);
    public static String STATE_INIT = "INIT";
    public static String STATE_DOING = "DOING";
    public static String STATE_COMPLETE = "COMPLETE";

    public static int PROGRESS_FINISHED = -1;

    private MergerDataSource mergerDataSource;


    public void init() throws SQLException, ServerMergerFatalException {
        Connection conn = mergerDataSource.getSrcConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS `_merger_progress_table` (\n" +
                "  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT," +
                "  `MODULE` VARCHAR(200) NOT NULL DEFAULT ''," +
                "  `TBLNAME` VARCHAR(100) NOT NULL DEFAULT ''," +
                "  `ROWNUMBER` BIGINT(20) NOT NULL," +
                "  PRIMARY KEY  (`ID`)" +
                ") ");
        stmt.execute("CREATE TABLE IF NOT EXISTS `_merger_progress_task` (" +
                "  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT," +
                "  `TASK` VARCHAR(200) NOT NULL," +
                "  `STATE` VARCHAR(100) NOT NULL," +
                "  PRIMARY KEY  (`ID`)" +
                ")");
        stmt.close();
        conn.close();
    }

    public void updateTaskProgress(String task, String state) {
        try {
            Connection conn = mergerDataSource.getSrcConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from _merger_progress_task where TASK='" + task + "' ");
            Long id = null;
            if (rs.next()) {
                id = rs.getLong("ID");
            }
            rs.close();
            if (id != null) {
                stmt.executeUpdate("update _merger_progress_task set STATE='" + state + "' where ID=" + id);
            } else {
                stmt.executeUpdate("insert into  _merger_progress_task(TASK,STATE) values ('" + task + "','" + state + "')");
            }
            stmt.close();
            conn.close();
        } catch (ServerMergerFatalException e) {
            logger.fatal(e.getMessage(), e);
        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
        }
    }

    public String getTaskProgress(String task) {
        String result = STATE_INIT;
        try {
            Connection conn = mergerDataSource.getSrcConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from _merger_progress_task where TASK='" + task + "' ");

            if (rs.next()) {
                result = rs.getString("STATE");
            }
            rs.close();

            stmt.close();
            conn.close();
        } catch (ServerMergerFatalException e) {
            logger.fatal(e.getMessage(), e);
        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
        }
        return result;
    }

    public void updateModuleProgress(String module, String tablename, long rownum) {
        try {
            Connection conn = mergerDataSource.getSrcConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from _merger_progress_table where `MODULE`='" + module + "' and  TBLNAME='" + tablename + "' ");
            Long id = null;
            if (rs.next()) {
                id = rs.getLong("ID");
            }
            rs.close();
            if (id != null) {
                stmt.executeUpdate("update _merger_progress_table set ROWNUMBER='" + rownum + "' where ID=" + id);
            } else {
                stmt.executeUpdate("insert into  _merger_progress_table(`MODULE`,TBLNAME,ROWNUMBER) values ('" + module + "','" + tablename + "'," + rownum + ")");
            }
            stmt.close();
            conn.close();
        } catch (ServerMergerFatalException e) {
            logger.fatal(e.getMessage(), e);
        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
        }
    }

    public long getModuleProgress(String module, String tablename) {
        long result = 0;
        try {
            Connection conn = mergerDataSource.getSrcConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from _merger_progress_table where `MODULE`='" + module + "' and  TBLNAME='" + tablename + "' ");

            if (rs.next()) {
                result = rs.getLong("ROWNUMBER");
            }
            rs.close();

            stmt.close();
            conn.close();
        } catch (ServerMergerFatalException e) {
            logger.fatal(e.getMessage(), e);
        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
        }
        return result;
    }

    public void setMergerDataSource(MergerDataSource mergerDataSource) {
        this.mergerDataSource = mergerDataSource;
    }
}
