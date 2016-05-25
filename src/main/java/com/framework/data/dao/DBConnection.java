package com.framework.data.dao;

public class DBConnection
{
  private String dbusername;
  private String dbpasswd;
  private DBType dbtype;
  private String JDBCString;

  public DBConnection(DBType type, String host, String username, String passwd, String base, String port)
  {
    this.dbusername = username;
    this.dbpasswd = passwd;
    switch (type.ordinal()) {
    case 1:
      this.dbtype = DBType.MYSQL;
    case 2:
      this.dbtype = DBType.MYSQL3;
    case 3:
      this.dbtype = DBType.MYSQL4;
    case 4:
      this.dbtype = DBType.MYSQL5;
      if (port.isEmpty()) port = "3306";
      this.JDBCString = String.format("jdbc:mysql://%s:%s/%s", new Object[] { host, port, base });
      break;
    case 5:
      this.dbtype = DBType.ORCALE8;
    case 6:
      this.dbtype = DBType.ORCALE9;
    case 7:
      this.dbtype = DBType.ORCALE10;
    case 8:
      this.dbtype = DBType.ORCALE11;
    case 9:
      this.dbtype = DBType.ORCALE;
      if (port.isEmpty()) port = "1521";
      this.JDBCString = String.format("jdbc:oracle:thin:@%s:%s:%s", new Object[] { host, port, base });
      break;
    case 10:
      this.dbtype = DBType.MSSQL2000;
    case 11:
      this.dbtype = DBType.MSSQL2005;
    case 12:
      this.dbtype = DBType.MSSQL2008;
    case 13:
      this.dbtype = DBType.MSSQL;
      if (port.isEmpty()) port = "1433";
      this.JDBCString = String.format("jdbc:sqlserver://%s:%s;DatabaseName=%s", new Object[] { host, port, base });
      break;
    }
  }

  public String getJDBCString()
  {
    return this.JDBCString;
  }

  public String getJDBCDriver() {
    return this.dbtype.getDBType();
  }

  public String getDbusername() {
    return this.dbusername;
  }

  public String getDbpasswd() {
    return this.dbpasswd;
  }
}