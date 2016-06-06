package com.framework.data.dao;

public enum DBType
{
  MSSQL2000("com.microsoft.jdbc.sqlserver.SQLServerDriver"), MSSQL2005("com.microsoft.sqlserver.jdbc.SQLServerDriver"), 
  MSSQL2008("com.microsoft.sqlserver.jdbc.SQLServerDriver"), 
  MSSQL(MSSQL2005.getDBType()), 

  MYSQL3("com.mysql.jdbc.Driver"), MYSQL4("com.mysql.jdbc.Driver"), MYSQL5("com.mysql.jdbc.Driver"), 
  MYSQL(MYSQL5.getDBType()), 

  ORCALE8("oracle.jdbc.browser.OracleDriver"), ORCALE9("oracle.jdbc.browser.OracleDriver"),
  ORCALE10("oracle.jdbc.browser.OracleDriver"),
  ORCALE11("oracle.jdbc.browser.OracleDriver"),
  ORCALE(ORCALE11.getDBType());

  private String db;

  private DBType(String u) {
    this.db = u;
  }

  public String getDBType() {
    return this.db;
  }
}