package com.tgbus.servermerger.merger;

import java.sql.Connection;
import java.util.List;

import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.ConfigManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Merger {
    private static Log logger = LogFactory.getLog(TableMerger.class);
    private ConfigManager configManager;
    private ModuleMerger moduleMerger;

    public void merge(Connection srcConn, Connection destConn) throws ServerMergerFatalException {
        logger.info("START MERGE");


        List<String> importModules = configManager.getConfig()
                .getImportModules();

        for (String id : importModules) {
            moduleMerger.mergeModule(id, srcConn, destConn);
        }
        logger.info("END MERGE");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public ModuleMerger getModuleMerger() {
        return moduleMerger;
    }

    public void setModuleMerger(ModuleMerger moduleMerger) {
        this.moduleMerger = moduleMerger;
    }

}
