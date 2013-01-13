package com.tgbus.servermerger.merger.Replacer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-25
 * Time: 11:44:34
 * To change this template use File | Settings | File Templates.
 */
public class ReplaceResult implements Serializable {
    private boolean writeback;
    private boolean needinsert;
    private Map<String, Object> newPk = new HashMap<String, Object>();

    public ReplaceResult(boolean writeback, boolean needinsert) {
        this.writeback = writeback;
        this.needinsert = needinsert;
    }

    public boolean isWriteback() {
        return writeback;
    }

    public void setWriteback(boolean writeback) {
        this.writeback = writeback;
    }

    public boolean isNeedinsert() {
        return needinsert;
    }

    public void setNeedinsert(boolean needinsert) {
        this.needinsert = needinsert;
    }

    public Map<String, Object> getNewPk() {
        return newPk;
    }

    public void setNewPk(Map<String, Object> newPk) {
        this.newPk = newPk;
    }
}
