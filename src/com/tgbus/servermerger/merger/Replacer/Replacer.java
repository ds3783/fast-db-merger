package com.tgbus.servermerger.merger.Replacer;

import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.Replacement;

import java.util.Map;

public interface Replacer {
    public void init(String tablename, Replacement replacement) throws ServerMergerFatalException;

    /**
     * 替换数据
     *
     * @param rowData 被替换行的数据
     * @return 插入数据后是否需要调用afterInsert方法，如果为 true 则该数据插入数据库后会回调afterInsert方法，否则不会调用afterInsert方法
     * @throws ServerMergerFatalException
     */
    public ReplaceResult replace(Map<String, Object> rowData) throws ServerMergerFatalException;

    /**
     * 插入数据库后回调此方法用于回写缓存或更新Replacer自身的数据结构
     *
     * @param rowData 插入后的一行数据
     * @param newPk
     * @throws ServerMergerFatalException
     */
    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException;
}
