package com.tgbus.servermerger.config;

import java.util.*;
import java.util.List;

import com.tgbus.servermerger.datacache.CacheMeta;

public class Config {
    private Set<Module> modules;
    private Set<Table> tables;
    /*private ArrayList<String> moduleSequence;*/
    private List<String> importModules;
    private Set<Cleaner> cleaners;
    private DBConfig dbconfig;
    private Set<CacheMeta> cacheMetas;
    private List<Task> tasks;
    private int step=500;


    public Config(Set<Cleaner> cleaners, DBConfig dbconfig,
                  List<String> importModules,
                  Set<Module> modules, Set<Table> tables, Set<CacheMeta> cacheMetas,List<Task> tasks) {
        super();
        this.cleaners = cleaners;
        this.dbconfig = dbconfig;
        this.importModules = importModules;
//		this.moduleSequence = moduleSequence;
        this.modules = modules;
        this.tables = tables;
        this.cacheMetas = cacheMetas;
        this.tasks=tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Set<Module> getModules() {
        return modules;
    }


    public Set<Table> getTables() {
        return tables;
    }


//	public ArrayList<String> getModuleSequence() {
//		return moduleSequence;
//	}


    public List<String> getImportModules() {
        return importModules;
    }


    public Set<Cleaner> getCleaners() {
        return cleaners;
    }


    public DBConfig getDbconfig() {
        return dbconfig;
    }


    public Set<CacheMeta> getCacheMetas() {
        return cacheMetas;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
