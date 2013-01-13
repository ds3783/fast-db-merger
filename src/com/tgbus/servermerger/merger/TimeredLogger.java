package com.tgbus.servermerger.merger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-9-22
 * Time: 15:05:08
 * To change this template use File | Settings | File Templates.
 */
public class TimeredLogger implements Runnable {
    private static Log logger = LogFactory.getLog(TimeredLogger.class);
    private TableMerger merger;
    private Thread wrapperedThread;


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
        if (merger == null) return;
        while (true) {
            if (merger.finished) {
                break;
            }
            logger.info(merger.getProgressString());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void setMerger(TableMerger merger) {
        this.merger = merger;
    }


    public Thread getWrapperedThread() {
        return wrapperedThread;
    }

    public void setWrapperedThread(Thread wrapperedThread) {
        this.wrapperedThread = wrapperedThread;
    }
}
