package com.tgbus.servermerger.datacache;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-8-25
 * Time: 13:13:41
 * To change this template use File | Settings | File Templates.
 */
public class MultiColumnKey implements CacheKey {
    public static Log logger = LogFactory.getLog(MultiColumnKey.class);

    private CacheMeta meta;
    private String keyval;

    private Map<String, String> keyvals;

    public MultiColumnKey(CacheMeta meta) {
        this.meta = meta;
    }

    public void setKey(Map<String, Object> key, Map<String, String> columnalians) {
        this.keyvals = new TreeMap<String, String>();
        for (Iterator<Column> iterator = meta.getColumns().iterator(); iterator.hasNext();) {
            Column column = iterator.next();
            String columnName = column.getName();
            if (columnalians.containsKey(columnName)) {
                columnName = columnalians.get(columnName);
            }
            Object val = key.get(columnName);
            keyvals.put(column.getName(), val == null ? null : val.toString());
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return equals((CacheKey) o);
    }

    public boolean equals(CacheKey that) {
        if (that == null) return false;
        MultiColumnKey kthat;
        if (that instanceof MultiColumnKey) {
            kthat = (MultiColumnKey) that;
        } else {
            return false;
        }
        if (keyvals != null)
            return keyvals.equals(kthat.keyvals);
        else {
            return StringUtils.equals(this.keyval, kthat.keyval);
        }
    }

    public String serialize() {
        Gson gson = new Gson();
        keyval = gson.toJson(keyvals);
        return keyval;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deserialize(String keyid) {
        try {
            Gson gson = new Gson();
            TreeMap<String, String> keyvals = gson.fromJson(keyid, new TypeToken<TreeMap<String, String>>() {
            }.getType());
            this.keyvals = keyvals;
            this.keyval = keyid;
        } catch (Exception e) {
            logger.error(e);
        }


    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
