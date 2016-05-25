package com.framework.data.dao;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoUtil
{
  Mongo mongo;
  DB db;
  DBCollection coll;
  private int limit = 10;

  public void setLimit(int limit)
  {
    this.limit = limit;
  }

  @SuppressWarnings("deprecation")
public MongoUtil(String key)
  {
    try {
      this.mongo = new Mongo(System.getProperty("DB." + key + ".host"), Integer.valueOf(System.getProperty("DB." + key + ".port")).intValue());

      this.db = this.mongo.getDB(System.getProperty("DB." + key + ".database"));
      if (!this.db.authenticate(System.getProperty("DB." + key + ".username"), System.getProperty("DB." + key + ".password").toCharArray()));
    }
    catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  public void setCollection(String collName)
  {
    this.coll = this.db.getCollection(collName);
  }

  public String[] selectForResult(Map<String, Object> sql, int limit)
  {
    BasicDBObject searchQuery = new BasicDBObject();
    List<String> r = new ArrayList<String>();
    for (String key : sql.keySet()) {
      searchQuery.put(key, sql.get(key));
    }
    DBCursor cursor = this.coll.find(searchQuery).limit(limit);
    while (cursor.hasNext()) {
      r.add(cursor.next().toString());
    }
    String[] result = new String[r.size()];
    int i = 0;
    for (String s : r) {
      result[(i++)] = s;
    }
    return result;
  }

  public String[] selectForResult(Map<String, Object> sql, int limit, String columnName)
  {
    BasicDBObject searchQuery = new BasicDBObject();
    List<String> r = new ArrayList<String>();
    for (String key : sql.keySet()) {
      searchQuery.put(key, sql.get(key));
    }
    DBCursor cursor = this.coll.find(searchQuery).limit(limit);
    while (cursor.hasNext()) {
      r.add(cursor.next().get(columnName).toString());
    }
    String[] result = new String[r.size()];
    int i = 0;
    for (String s : r) {
      result[(i++)] = s;
    }
    return result;
  }

  public String[] selectForResult(Map<String, Object> sql)
  {
    return selectForResult(sql, this.limit);
  }

  public String[] selectForResult(String sql, int limit)
  {
    return selectForResult(parseSql(sql), limit);
  }

  public String[] selectForResult(String sql)
  {
    return selectForResult(parseSql(sql));
  }

  public String selectForRow(Map<String, Object> sql)
  {
    String[] result = selectForResult(sql, 1);
    if (result.length == 1) {
      return selectForResult(sql, 1)[0];
    }
    return "";
  }

  public String selectForRow(String sql)
  {
    return selectForRow(parseSql(sql));
  }

  public String[] selectForColumn(Map<String, Object> sql, int limit, String columnName)
  {
    return selectForResult(sql, limit, columnName);
  }

  public String[] selectForColumn(String sql, int limit, String columnName)
  {
    return selectForResult(parseSql(sql), limit, columnName);
  }

  public String selectForColumn(Map<String, Object> sql, String columnName)
  {
    String[] result = selectForResult(sql, 1, columnName);
    if (result.length == 1) {
      return result[0];
    }
    return "";
  }

  public String selectForColumn(String sql, String columnName)
  {
    return selectForColumn(parseSql(sql), columnName);
  }

  private Map<String, Object> parseSql(String sql) {
    Map<String, Object> sqls = new HashMap<String, Object>();
    if ((sql.startsWith("{")) && (sql.endsWith("}")) && (sql.length() > 2)) {
      sql = sql.substring(1, sql.length() - 1);
      String[] keyvalues = sql.split(",");
      for (String k : keyvalues) {
        String[] columns = k.split(":");
        columns[0] = columns[0].substring(1, columns[0].length() - 1);
        if ((columns[1].startsWith("'")) && (columns[1].endsWith("'"))) {
          columns[1] = columns[1].substring(1, columns[1].length() - 1);

          sqls.put(columns[0], columns[1]);
        } else {
          sqls.put(columns[0], Long.valueOf(Long.parseLong(columns[1])));
        }
      }
    }
    return sqls;
  }
}