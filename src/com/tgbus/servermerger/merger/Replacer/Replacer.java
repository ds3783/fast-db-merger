package com.tgbus.servermerger.merger.Replacer;

import com.tgbus.servermerger.ServerMergerFatalException;
import com.tgbus.servermerger.config.Replacement;

import java.util.Map;

public interface Replacer {
    public void init(String tablename, Replacement replacement) throws ServerMergerFatalException;

    /**
     * �滻����
     *
     * @param rowData ���滻�е�����
     * @return �������ݺ��Ƿ���Ҫ����afterInsert���������Ϊ true ������ݲ������ݿ���ص�afterInsert���������򲻻����afterInsert����
     * @throws ServerMergerFatalException
     */
    public ReplaceResult replace(Map<String, Object> rowData) throws ServerMergerFatalException;

    /**
     * �������ݿ��ص��˷������ڻ�д��������Replacer��������ݽṹ
     *
     * @param rowData ������һ������
     * @param newPk
     * @throws ServerMergerFatalException
     */
    public void afterInsert(Map<String, Object> rowData, Map<String, Object> newPk) throws ServerMergerFatalException;
}
