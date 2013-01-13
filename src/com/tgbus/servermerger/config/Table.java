package com.tgbus.servermerger.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Table {
    private String name;
    private String mergerImpl;
    private Set<Replacement> replacements;
    private PrimaryKey pk;
    private String condition;
    private boolean ignorefail;
    private List<NoInsertTableMerge> noInsertTableMerge = new ArrayList<NoInsertTableMerge>();
    private Set<String> metas;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMergerImpl() {
        return mergerImpl;
    }

    public void setMergerImpl(String mergerImpl) {
        this.mergerImpl = mergerImpl;
    }

    public Set<Replacement> getReplacements() {
        return replacements;
    }

    public void setReplacements(Set<Replacement> replacements) {
        this.replacements = replacements;
    }

    public PrimaryKey getPk() {
        return pk;
    }

    public void setPk(PrimaryKey pk) {
        this.pk = pk;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Set<String> getMetas() {
        return metas;
    }

    public void setMetas(Set<String> metas) {
        this.metas = metas;
    }

    public boolean isIgnorefail() {
        return ignorefail;
    }

    public void setIgnorefail(boolean ignorefail) {
        this.ignorefail = ignorefail;
    }

    public List<NoInsertTableMerge> getNoInsertTableMerge() {
        return noInsertTableMerge;
    }

    public void setNoInsertTableMerge(List<NoInsertTableMerge> noInsertTableMerge) {
        this.noInsertTableMerge = noInsertTableMerge;
    }
}
