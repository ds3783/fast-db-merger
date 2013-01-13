package com.tgbus.servermerger.datacache;

import com.tgbus.servermerger.ServerMergerFatalException;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-7-12
 * Time: 17:56:54
 * To change this template use File | Settings | File Templates.
 */
public interface CacheKey {


    public void setKey(Map<String, Object> key, Map<String, String> columnalians) throws ServerMergerFatalException;

    public boolean equals(CacheKey that);

    public String serialize();

    public void deserialize(String keyid);


}
