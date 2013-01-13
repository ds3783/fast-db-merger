package com.tgbus.servermerger.datacache;

import com.tgbus.servermerger.ServerMergerFatalException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ds.3783
 * Date: 2010-7-12
 * Time: 18:30:52
 * To change this template use File | Settings | File Templates.
 */
public class SinglonCacheKey implements CacheKey {
    private CacheMeta meta;
    private String keyval;

    public SinglonCacheKey(CacheMeta meta) {
        this.meta = meta;
    }

    public String getId() {
        return keyval;
    }

    public boolean equals(CacheKey that) {
        if (that == null) return false;
        SinglonCacheKey kthat;
        if (that instanceof SinglonCacheKey) {
            kthat = (SinglonCacheKey) that;
        } else {
            return false;
        }
        return StringUtils.equals(keyval, kthat.keyval);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return equals((CacheKey) o);
    }

    public void setKey(Map<String, Object> key, Map<String, String> columnalians) throws ServerMergerFatalException {
        Object data;
        Column column = null;
        if (meta.getColumns() != null) {
            for (Column column1 : meta.getColumns()) {
                column = column1;
            }
        }
        if (column == null) {
            throw new ServerMergerFatalException("META中未指定COLUMN");
        }
        if (columnalians.containsKey(column.getName())) {
            data = key.get(columnalians.get(column.getName()));
        } else {
            data = key.get(column.getName());
        }
        if (data != null) {
            this.keyval = data.toString();
        } else {
            throw new ServerMergerFatalException("无效的KEY值");
        }
    }

    public String serialize() {
        return keyval;
    }

    public void deserialize(String keyid) {
        this.keyval = keyid;
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
