package com.tgbus.servermerger.merger;

import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.ConfigManager;
import com.tgbus.servermerger.config.Module;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

public class ModuleMerger {
    private TableMerger tableMerger;
    private ConfigManager configManager;
    private ProgressControl progressControl;
    public static Log logger = LogFactory.getLog(ModuleMerger.class);

    public void mergeModule(String id, Connection srcConn, Connection destConn) throws ServerMergerFatalException {
        Set<Module> modules = configManager.getConfig().getModules();
        for (Module module : modules) {
            if (module.getId().equals(id)) {
                List<String> tableNames = module.getTableNames();
                logger.info("START MERGE MODULE ;" + id + " ...");
                for (String name : tableNames) {
                    logger.info("START MERGE TABLE ;" + name + " ...");
                    if (progressControl.getModuleProgress(id, name) == ProgressControl.PROGRESS_FINISHED) {
                        logger.info("TABLE ;" + name + " progress finished!");
                        continue;
                    } else {
                        tableMerger.mergeTable(id, name, srcConn, destConn);
                    }
                }
                logger.info("FINISHED MERGE MODULE ;" + id);

                break;
            }

        }
    }

    public TableMerger getTableMerger() {
        return tableMerger;
    }

    public void setTableMerger(TableMerger tableMerger) {
        this.tableMerger = tableMerger;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setProgressControl(ProgressControl progressControl) {
        this.progressControl = progressControl;
    }
}
