package com.nbgo.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * hikaricp 连接池示例
 * 
 * @author wanghonggang 2018-10-29
 */
public class HikariSingleDemo {

	public static void main(String[] args) throws SQLException, IOException {

		// 配置文件获取datasource
//		InputStream is = HikaricpDAO.class.getClassLoader().getResourceAsStream("hikaricp.properties");
//		Properties props = new Properties();
//		props.load(is);
//		HikariConfig config = new HikariConfig(props);
//		HikariDataSource dataSource = new HikariDataSource(config);

		// datasource配置
		HikariConfig hikariConfig = new HikariConfig();
		// hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/mydata");//mysql
		// hikariConfig.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:orcl");// oracle
		hikariConfig.setJdbcUrl("jdbc:sqlserver://127.0.0.1:1433;DatabaseName=hikaricp_data");
		// hikariConfig.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		hikariConfig.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		hikariConfig.setUsername("sa");
		hikariConfig.setPassword("z");
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		HikariDataSource ds = new HikariDataSource(hikariConfig);
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try {

			// 创建connection
			conn = ds.getConnection();
			statement = conn.createStatement();

			// 执行sql
			rs = statement.executeQuery("select 100 s  from dual");

			// 取数据
			if (rs.next()) {
				System.out.println(rs.getString("s"));
			}

			// 关闭connection
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
