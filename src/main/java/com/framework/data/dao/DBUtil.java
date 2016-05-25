package com.framework.data.dao;

import java.sql.*;

public class DBUtil
{
  private Connection ct;
  private DBConnection conn;
  private DBType type;
  private String[] columns;

  public DBUtil(String key)
  {
    try
    {
      this.type = DBType.valueOf(System.getProperty(new StringBuilder().append("DB.").append(key).append(".type").toString(), "MYSQL").toUpperCase());
    }
    catch (Exception e) {
      this.type = DBType.MYSQL;
    }
    this.conn = new DBConnection(this.type, System.getProperty(new StringBuilder().append("DB.").append(key).append(".host").toString()), System.getProperty(new StringBuilder().append("DB.").append(key).append(".username").toString()), System.getProperty(new StringBuilder().append("DB.").append(key).append(".password").toString()), System.getProperty(new StringBuilder().append("DB.").append(key).append(".database").toString()), System.getProperty(new StringBuilder().append("DB.").append(key).append(".port").toString(), ""));
  }

  public void connection()
  {
    try
    {
      Class.forName(this.conn.getJDBCDriver()).newInstance();
      this.ct = DriverManager.getConnection(this.conn.getJDBCString(), this.conn.getDbusername(), this.conn.getDbpasswd());
    }
    catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public void close()
  {
    try
    {
      this.ct.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Object selectForFirstColumn(String sql)
  {
    return selectForColumn(sql, 1, 1);
  }

  public Object selectForColumn(String sql, int row, String columnName)
  {
    Object[] rowdata = selectForRow(sql, row);
    int index = getColumnIndexByName(columnName);
    if ((index > 0) && (index <= rowdata.length))
      return rowdata[(index - 1)];
    return new Object();
  }

  public Object selectForColumn(String sql, int row, int column)
  {
    Object[] rowdata = selectForRow(sql, row);
    if ((column > 0) && (column <= rowdata.length))
      return rowdata[(column - 1)];
    return new Object();
  }

  public Object[] selectForColumn(String sql, int column)
  {
    Object[][] result = selectForResult(sql);
    if ((result.length > 0) && (column > 0) && (column <= result[0].length)) {
      Object[] o = new Object[result.length];
      for (int i = 0; i < result.length; i++) {
        o[i] = result[i][(column - 1)];
      }
      return o;
    }
    return new Object[0];
  }

  public Object[] selectForColumn(String sql, String columnName)
  {
    Object[][] result = selectForResult(sql);
    int index = getColumnIndexByName(columnName);
    if ((result.length > 0) && (index > 0) && (index <= result[0].length)) {
      Object[] o = new Object[result.length];
      for (int i = 0; i < result.length; i++) {
        o[i] = result[i][(index - 1)];
      }
      return o;
    }
    return new Object[0];
  }

  public Object[] selectForFristRow(String sql)
  {
    return selectForRow(sql, 1);
  }

  public Object[] selectForRow(String sql, int index)
  {
    Object[][] resultset = selectForResult(sql);
    if ((index > 0) && (index <= resultset.length)) {
      return resultset[(index - 1)];
    }
    return new Object[0];
  }

  public Object[][] selectForResult(String sql)
  {
    try
    {
      Statement cs = this.ct.createStatement(1004, 1007);
      ResultSet rs = cs.executeQuery(sql);
      getColumns(rs.getMetaData());
      int columnsize = this.columns.length;
      rs.last();
      int rowsize = rs.getRow();
      rs.beforeFirst();
      if (rs.next()) {
        Object[][] o = new Object[rowsize][columnsize];
        for (int r = 1; r <= rowsize; rs.next()) {
          for (int c = 1; c <= columnsize; c++)
            o[(r - 1)][(c - 1)] = rs.getObject(c);
          r++;
        }

        rs.close();
        return o;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new Object[0][];
  }

  public int execute(String sql)
  {
    try
    {
      this.ct.setAutoCommit(false);
      Statement cs = this.ct.createStatement();
      int i = cs.executeUpdate(sql);
      this.ct.commit();
      return i;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public void execute(String[] sqls)
  {
    try
    {
      this.ct.setAutoCommit(false);
      Statement cs = this.ct.createStatement();
      for (String sql : sqls) {
        cs.executeUpdate(sql);
      }
      this.ct.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void showSqlResult(String sql)
  {
    for (Object[] row : selectForResult(sql)) {
      StringBuilder rowline = new StringBuilder();
      for (Object column : row) {
        rowline.append(new StringBuilder().append(column).append(" ").toString());
      }
      showColumnName();
      System.out.println(rowline.toString());
    }
  }

  public void showSqlRow(String sql, int index)
  {
    StringBuilder rowline = new StringBuilder();
    for (Object o : selectForRow(sql, index)) {
      rowline.append(new StringBuilder().append(o).append(" ").toString());
    }
    showColumnName();
    System.out.println(rowline.toString());
  }

  private int getColumnIndexByName(String columnName) {
    for (int i = 0; i <= this.columns.length; i++) {
      if (this.columns[i].equals(columnName)) {
        return i + 1;
      }
    }
    return 0;
  }

  private void showColumnName() {
    StringBuilder rowline = new StringBuilder();
    for (int i = 0; i < this.columns.length; i++) {
      rowline.append(new StringBuilder().append(this.columns[i]).append(" ").toString());
    }
    System.out.println(rowline.toString());
  }

  private void getColumns(ResultSetMetaData metadata) {
    try {
      this.columns = new String[metadata.getColumnCount()];
      for (int i = 0; i < this.columns.length; i++)
        this.columns[i] = metadata.getColumnName(i + 1);
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}