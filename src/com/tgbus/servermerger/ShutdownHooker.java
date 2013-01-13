package com.tgbus.servermerger;

import com.tgbus.servermerger.merger.TableMerger;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-22
 * Time: 15:00:06
 * To change this template use File | Settings | File Templates.
 */
public class ShutdownHooker implements Runnable {
    private TableMerger tableMerger;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        tableMerger.onExit();
    }

    public void setTableMerger(TableMerger tableMerger) {
        this.tableMerger = tableMerger;
    }
}
