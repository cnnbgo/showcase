package com.nbgo.t;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.RandomStringUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Hikaricp数据库连接池+Apache DBUtils增刪改查示例
 * 
 * @author nbgo
 */
public class HikaricpDAO {

	final static String jdbcUrl = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=hikaricp_data";
	final static String drvierName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	final static String username = "sa";
	final static String password = "z";

	public static HikariDataSource dataSource;

	public static DataSource getDataSource() {

		try {
			if (dataSource == null) {

//				InputStream is = HikaricpDAO.class.getClassLoader().getResourceAsStream("hikaricp.properties");
//				Properties props = new Properties();
//				props.load(is);
//				HikariConfig config = new HikariConfig(props);
//				dataSource = new HikariDataSource(config);

				HikariConfig hikariConfig = new HikariConfig();
				hikariConfig.setJdbcUrl(jdbcUrl);
				hikariConfig.setDriverClassName(drvierName);
				hikariConfig.setUsername(username);
				hikariConfig.setPassword(password);
				hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
				hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
				hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

				dataSource = new HikariDataSource(hikariConfig);
			}
			return dataSource;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	public static Car get(int id) throws SQLException {

		QueryRunner runner = new QueryRunner(getDataSource());
		String sql = "select * from car where id=?";
		Car car = runner.query(sql, new BeanHandler<Car>(Car.class), new Object[] { id });
		return car;

	}

	public static List<Car> getAll() throws SQLException {

		QueryRunner runner = new QueryRunner(getDataSource());
		String sql = "select * from car";
		List<Car> list = runner.query(sql, new BeanListHandler<Car>(Car.class));
		return list;

	}

	public static BigDecimal add(Car Car) throws SQLException, IOException {
		QueryRunner runner = new QueryRunner(getDataSource());
		String sql = "insert into car (name,password) values (?,?)";
		BigDecimal autoID = runner.insert(sql, new ScalarHandler<>(), Car.getName(), Car.getPassword());
		System.out.println("自增id:" + autoID);
		return autoID;
	}

	public static Boolean update(Car Car) throws SQLException {

		QueryRunner runner = new QueryRunner(getDataSource());
		String sql = "update Car set name=?, password=? where id=?";
		int done = runner.update(sql, new Object[] { Car.getName(), Car.getPassword(), Car.getId() });
		return done > 0 ? true : false;

	}

	public static Boolean delete(int id) throws SQLException {

		QueryRunner runner = new QueryRunner(getDataSource());
		String sql = "delete from car where id=?";
		int done = runner.update(sql, id);
		return done > 0 ? true : false;

	}

	public static void crudDemo() throws SQLException, IOException {

		// int password4 = (int) ((Math.random() * 9 + 1) * 1000); // 4为随机数
		String name4 = RandomStringUtils.randomAlphanumeric(24);
		String password4 = RandomStringUtils.randomAlphanumeric(24);
		System.out.println("add new car:" + add(new Car(name4, password4)));

		Car car88 = get(88);
		System.out.println(car88);
		car88.setName("8888");
		car88.setPassword("8888");
		System.out.println("修改：" + update(car88));
		System.out.println("总数：" + getAll().size());
		System.out.println("刪除：" + delete(88));
		System.out.println("总数：" + getAll().size());

	}

	public static void transactionDemo() throws SQLException, IOException {
		long startTime = System.currentTimeMillis(); // 获取开始时间

		Connection conn = getConnection();
		try {
			QueryRunner qr = new QueryRunner();
			conn.setAutoCommit(false);
			String sql = "insert into car (name,password) values (?,?)";
			String name4 = "";
			String password4 = "";
			for (int i = 0; i < 100; i++) {
				name4 = RandomStringUtils.randomAlphanumeric(24);
				password4 = RandomStringUtils.randomAlphanumeric(24);
				qr.update(conn, sql, new Object[] { name4, password4 });
			}
			conn.commit();

		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
	}

	public static void BatchDemo() throws SQLException, IOException {
		QueryRunner runner = new QueryRunner(getDataSource());
		String sql = "insert into car (name,password) values (?,?)";

		Object[][] params = new Object[100][2];
		String name4 = "";
		String password4 = "";
		for (int i = 0; i < 100; i++) {
			name4 = RandomStringUtils.randomAlphanumeric(24);
			password4 = RandomStringUtils.randomAlphanumeric(24);
			params[i] = new Object[] { name4, password4 };
		}
		int[] batch = runner.batch(sql, params);
		System.out.println(batch.length);
	}

	public static void main(String[] args) throws SQLException, IOException {
		String name4 = RandomStringUtils.randomAlphanumeric(24);
		String password4 = RandomStringUtils.randomAlphanumeric(24);
		System.out.println("add new car:" + add(new Car(name4, password4)));
	}
}
