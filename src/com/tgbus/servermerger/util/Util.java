package com.tgbus.servermerger.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class Util {
    private static Log logger = LogFactory.getLog(Util.class);

    public static <T> T multicast(Object o, Class<? extends T> clazz) {
        if (o == null) {
            return null;
        }
        if (instanceOf(o, clazz)) return (T) o;
        if (clazz.equals(String.class)) {
            return (T) o.toString();
        }
        if (clazz.equals(Long.class) || clazz.getName().equals("long")) {
            try {
                return (T) new Long(o.toString());
            } catch (NumberFormatException e) {
                try {
                    return (T) new Long(new Double(o.toString()).longValue());
                } catch (NumberFormatException e1) {
                    return null;
                }
            }
        }
        if (clazz.equals(Integer.class) || clazz.getName().equals("int")) {
            try {
                return (T) new Integer(o.toString());
            } catch (NumberFormatException e) {
                try {
                    return (T) new Integer(new Double(o.toString()).intValue());
                } catch (NumberFormatException e1) {
                    return null;
                }
            }
        }
        if (clazz.equals(Double.class) || clazz.getName().equals("double")) {
            try {
                return (T) new Double(o.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        try {
            return (T) o;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static boolean instanceOf(Object o, Class clazz) {
        /*if (o.getClass().equals(clazz)) return true;
        Class c = o.getClass();
        boolean result = false;

        while (c.getSuperclass() != null && !c.getSuperclass().equals(Object.class)) {
            if (c.equals(clazz)) {
                result = true;
                break;
            }
            c = c.getSuperclass();
        }
        return result;*/
        return clazz.isInstance(o);
    }

    public static Map<String, Object> readRowData(ResultSet rs, Map<String, String> meta) throws SQLException {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String colname : meta.keySet()) {
            String type = meta.get(colname);
            Object data = Util.readObject(rs, colname, type);

            result.put(colname, data);
        }
        return result;
    }

    private static Object readObject(ResultSet rs, String colname, String type) throws SQLException {
        Object data = null;
        if (StringUtils.equalsIgnoreCase("TINYINT", type)) {
            data = rs.getInt(colname);
        } else if (StringUtils.equalsIgnoreCase("SMALLINT", type)) {
            data = rs.getInt(colname);
        } else if (StringUtils.equalsIgnoreCase("MEDIUMINT", type)) {
            data = rs.getInt(colname);
        } else if (StringUtils.equalsIgnoreCase("INT", type)) {
            data = rs.getInt(colname);
        } else if (StringUtils.equalsIgnoreCase("INTEGER", type)) {
            data = rs.getInt(colname);
        } else if (StringUtils.equalsIgnoreCase("BOOL", type)) {
            data = rs.getBoolean(colname);
        } else if (StringUtils.equalsIgnoreCase("BOOLEAN", type)) {
            data = rs.getBoolean(colname);
        } else if (StringUtils.equalsIgnoreCase("BIGINT", type)) {
            data = rs.getLong(colname);
        } else if (StringUtils.equalsIgnoreCase("FLOAT", type)) {
            data = rs.getDouble(colname);
        } else if (StringUtils.equalsIgnoreCase("DOUBLE", type)) {
            data = rs.getDouble(colname);
        } else if (StringUtils.equalsIgnoreCase("DECIMAL", type)) {
            data = rs.getDouble(colname);
        } else if (StringUtils.equalsIgnoreCase("DEC", type)) {
            data = rs.getDouble(colname);
        } else if (StringUtils.equalsIgnoreCase("DATETIME", type)) {
            data = rs.getTimestamp(colname);
        } else if (StringUtils.equalsIgnoreCase("DATE", type)) {
            data = rs.getTimestamp(colname);
        } else if (StringUtils.equalsIgnoreCase("TIMESTAMP", type)) {
            Long coldata = rs.getLong(colname);
            if (coldata == null || coldata == 0) {
                data = null;
            } else {
                data = rs.getTimestamp(colname);
            }
        } else if (StringUtils.equalsIgnoreCase("TIME", type)) {
            data = rs.getTime(colname);
        } else if (StringUtils.equalsIgnoreCase("YEAR", type)) {
            data = rs.getDate(colname);
        } else if (StringUtils.equalsIgnoreCase("CHAR", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("VARCHAR", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("BINARY", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("VARBINARY", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("TINYBLOB ", type)) {
            data = rs.getBlob(colname);
        } else if (StringUtils.equalsIgnoreCase("BLOB ", type)) {
            data = rs.getBlob(colname);
        } else if (StringUtils.equalsIgnoreCase("MEDIUMBLOB ", type)) {
            data = rs.getBlob(colname);
        } else if (StringUtils.equalsIgnoreCase("LONGBLOB ", type)) {
            data = rs.getBlob(colname);
        } else if (StringUtils.equalsIgnoreCase("TINYTEXT", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("TEXT", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("MEDIUMTEXT", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("LONGTEXT", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("ENUM", type)) {
            data = rs.getString(colname);
        } else if (StringUtils.equalsIgnoreCase("SET", type)) {
            data = rs.getString(colname);
        }
        return data;
    }

    private static Object readObject(ResultSet rs, int colIndex, String type) throws SQLException {
        Object data = null;
        if (StringUtils.equalsIgnoreCase("TINYINT", type)) {
            data = rs.getInt(colIndex);
        } else if (StringUtils.equalsIgnoreCase("SMALLINT", type)) {
            data = rs.getInt(colIndex);
        } else if (StringUtils.equalsIgnoreCase("MEDIUMINT", type)) {
            data = rs.getInt(colIndex);
        } else if (StringUtils.equalsIgnoreCase("INT", type)) {
            data = rs.getInt(colIndex);
        } else if (StringUtils.equalsIgnoreCase("INTEGER", type)) {
            data = rs.getInt(colIndex);
        } else if (StringUtils.equalsIgnoreCase("BOOL", type)) {
            data = rs.getBoolean(colIndex);
        } else if (StringUtils.equalsIgnoreCase("BOOLEAN", type)) {
            data = rs.getBoolean(colIndex);
        } else if (StringUtils.equalsIgnoreCase("BIGINT", type)) {
            data = rs.getLong(colIndex);
        } else if (StringUtils.equalsIgnoreCase("FLOAT", type)) {
            data = rs.getDouble(colIndex);
        } else if (StringUtils.equalsIgnoreCase("DOUBLE", type)) {
            data = rs.getDouble(colIndex);
        } else if (StringUtils.equalsIgnoreCase("DECIMAL", type)) {
            data = rs.getDouble(colIndex);
        } else if (StringUtils.equalsIgnoreCase("DEC", type)) {
            data = rs.getDouble(colIndex);
        } else if (StringUtils.equalsIgnoreCase("DATETIME", type)) {
            data = rs.getTimestamp(colIndex);
        } else if (StringUtils.equalsIgnoreCase("DATE", type)) {
            data = rs.getTimestamp(colIndex);
        } else if (StringUtils.equalsIgnoreCase("TIMESTAMP", type)) {
            data = rs.getTimestamp(colIndex);
        } else if (StringUtils.equalsIgnoreCase("TIME", type)) {
            data = rs.getTime(colIndex);
        } else if (StringUtils.equalsIgnoreCase("YEAR", type)) {
            data = rs.getDate(colIndex);
        } else if (StringUtils.equalsIgnoreCase("CHAR", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("VARCHAR", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("BINARY", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("VARBINARY", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("TINYBLOB ", type)) {
            data = rs.getBlob(colIndex);
        } else if (StringUtils.equalsIgnoreCase("BLOB ", type)) {
            data = rs.getBlob(colIndex);
        } else if (StringUtils.equalsIgnoreCase("MEDIUMBLOB ", type)) {
            data = rs.getBlob(colIndex);
        } else if (StringUtils.equalsIgnoreCase("LONGBLOB ", type)) {
            data = rs.getBlob(colIndex);
        } else if (StringUtils.equalsIgnoreCase("TINYTEXT", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("TEXT", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("MEDIUMTEXT", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("LONGTEXT", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("ENUM", type)) {
            data = rs.getString(colIndex);
        } else if (StringUtils.equalsIgnoreCase("SET", type)) {
            data = rs.getString(colIndex);
        }
        return data;
    }

    public static PreparedStatement prepareInsert(String tablename, Map<String, String> meta, Connection conn) throws SQLException {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(tablename).append("(");
        TreeMap<String, String> sortedMeta = new TreeMap<String, String>(meta);
        for (Iterator<String> iterator = sortedMeta.keySet().iterator(); iterator.hasNext();) {
            String colname = iterator.next();
            sql.append("`").append(colname).append("`");
            if (iterator.hasNext()) {
                sql.append(",");
            }
        }
        sql.append(")  values (");
        for (int i = 0; i < sortedMeta.size(); i++) {
            sql.append("? ");
            if (i + 1 < sortedMeta.size()) {
                sql.append(", ");
            }
        }
        sql.append(")");
        String realSql = sql.toString();
        //日志SQL
        logger.info("Insert SQL:" + realSql);
        return conn.prepareStatement(realSql, Statement.RETURN_GENERATED_KEYS);
    }

    public static Map<String, Object> doInsert(PreparedStatement stmt, Map<String, String> meta, Map<String, String> keymeta, Map<String, Object> data) throws SQLException {
        TreeMap<String, String> sortedMeta = new TreeMap<String, String>(meta);
        int counter = 1;
        for (String colname : sortedMeta.keySet()) {
            String coltype = meta.get(colname);
            Object obj = data.get(colname);
            if (StringUtils.equalsIgnoreCase("TINYINT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.INTEGER);
                } else {
                    Integer d;
                    if (obj instanceof Integer) {
                        d = (Integer) obj;
                    } else {
                        d = Integer.parseInt(obj.toString());
                    }
                    stmt.setInt(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("SMALLINT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.INTEGER);
                } else {
                    Integer d;
                    if (obj instanceof Integer) {
                        d = (Integer) obj;
                    } else {
                        d = Integer.parseInt(obj.toString());
                    }
                    stmt.setInt(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("MEDIUMINT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.INTEGER);
                } else {
                    Integer d;
                    if (obj instanceof Integer) {
                        d = (Integer) obj;
                    } else {
                        d = Integer.parseInt(obj.toString());
                    }
                    stmt.setInt(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("INT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.INTEGER);
                } else {
                    Integer d;
                    if (obj instanceof Integer) {
                        d = (Integer) obj;
                    } else {
                        d = Integer.parseInt(obj.toString());
                    }
                    stmt.setInt(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("INTEGER", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.INTEGER);
                } else {
                    Integer d;
                    if (obj instanceof Integer) {
                        d = (Integer) obj;
                    } else {
                        d = Integer.parseInt(obj.toString());
                    }
                    stmt.setInt(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("BOOL", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.INTEGER);
                } else {
                    Integer d;
                    if (obj instanceof Integer) {
                        d = (Integer) obj;
                    } else {
                        d = Integer.parseInt(obj.toString());
                    }
                    stmt.setInt(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("BOOLEAN", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.INTEGER);
                } else {
                    Integer d;
                    if (obj instanceof Integer) {
                        d = (Integer) obj;
                    } else {
                        d = Integer.parseInt(obj.toString());
                    }
                    stmt.setInt(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("BIGINT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.BIGINT);
                } else {
                    Long d;
                    if (obj instanceof Long) {
                        d = (Long) obj;
                    } else {
                        d = Long.parseLong(obj.toString());
                    }
                    stmt.setLong(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("FLOAT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.FLOAT);
                } else {
                    Double d;
                    if (obj instanceof Double) {
                        d = (Double) obj;
                    } else {
                        d = Double.parseDouble(obj.toString());
                    }
                    stmt.setFloat(counter, d.floatValue());
                }
            } else if (StringUtils.equalsIgnoreCase("DOUBLE", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.DOUBLE);
                } else {
                    Double d;
                    if (obj instanceof Double) {
                        d = (Double) obj;
                    } else {
                        d = Double.parseDouble(obj.toString());
                    }
                    stmt.setDouble(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("DECIMAL", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.DECIMAL);
                } else {
                    Double d;
                    if (obj instanceof Double) {
                        d = (Double) obj;
                    } else {
                        d = Double.parseDouble(obj.toString());
                    }
                    stmt.setDouble(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("DEC", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.DECIMAL);
                } else {
                    Double d;
                    if (obj instanceof Double) {
                        d = (Double) obj;
                    } else {
                        d = Double.parseDouble(obj.toString());
                    }
                    stmt.setDouble(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("DATETIME", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.TIMESTAMP);
                } else {
                    Timestamp d;
                    if (obj instanceof Timestamp) {
                        d = (Timestamp) obj;
                    } else {
                        d = Timestamp.valueOf(obj.toString());
                    }
                    stmt.setTimestamp(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("DATE", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.DATE);
                } else {
                    Timestamp d;
                    if (obj instanceof Timestamp) {
                        d = (Timestamp) obj;
                    } else {
                        d = Timestamp.valueOf(obj.toString());
                    }
                    stmt.setTimestamp(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("TIMESTAMP", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.TIMESTAMP);
                } else {
                    Timestamp d;
                    if (obj instanceof Timestamp) {
                        d = (Timestamp) obj;
                    } else {
                        d = Timestamp.valueOf(obj.toString());
                    }
                    stmt.setTimestamp(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("TIME", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.TIME);
                } else {
                    Time d;
                    if (obj instanceof Time) {
                        d = (Time) obj;
                    } else {
                        d = Time.valueOf(obj.toString());
                    }
                    stmt.setTime(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("YEAR", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.DATE);
                } else {
                    Date d;
                    if (obj instanceof Date) {
                        d = (Date) obj;
                    } else {
                        d = Date.valueOf(obj.toString());
                    }
                    stmt.setDate(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("CHAR", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("VARCHAR", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("BINARY", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("VARBINARY", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("TINYBLOB ", coltype)) {
                //不处理
            } else if (StringUtils.equalsIgnoreCase("BLOB ", coltype)) {
                //不处理
            } else if (StringUtils.equalsIgnoreCase("MEDIUMBLOB ", coltype)) {
                //不处理
            } else if (StringUtils.equalsIgnoreCase("LONGBLOB ", coltype)) {
                //不处理
            } else if (StringUtils.equalsIgnoreCase("TINYTEXT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("TEXT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("MEDIUMTEXT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("LONGTEXT", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("ENUM", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            } else if (StringUtils.equalsIgnoreCase("SET", coltype)) {
                if (obj == null) {
                    stmt.setNull(counter, java.sql.Types.CHAR);
                } else {
                    String d;
                    if (obj instanceof String) {
                        d = (String) obj;
                    } else {
                        d = obj.toString();
                    }
                    stmt.setString(counter, d);
                }
            }
            counter++;
        }

        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        try {
            if (rs.next()) {
                //知其仅有一列，故获取第一列
                HashMap<String, Object> result = new HashMap<String, Object>();
                for (String colname : keymeta.keySet()) {
                    String type = keymeta.get(colname);
                    result.put(colname, Util.readObject(rs, 1, type));
                }
                return result;
            }
            return new HashMap<String, Object>();
        } finally {
            rs.close();
        }
    }


}
