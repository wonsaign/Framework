package sharding.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;

public class ShardingJdbc {
	static List<Integer> ids;

	public static void main(String[] args) throws SQLException, InterruptedException {
		DataSource date2 = createDataSource("sharding_0");
		String sql = "SELECT * FROM bus_order";
		ids = new ArrayList<>(4663);
		try (Connection conn = date2.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next())
					ids.add(rs.getInt(1));
			}
		}
		int n = 200;
		ExecutorService pool1 = Executors.newCachedThreadPool();
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			pool1.execute(new Task1());
		}
		pool1.shutdown();
		while (true) {
			if (pool1.isTerminated()) {
				break;
			}
		}
		System.err.println(System.currentTimeMillis() - t1);
		ExecutorService pool2 = Executors.newCachedThreadPool();
		long t2 = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
//			pool2.execute(new Task2());
		}
		pool2.shutdown();
		while (true) {
			if (pool2.isTerminated()) {
				break;
			}
		}
		System.err.println(System.currentTimeMillis() - t2);

	}

	static class Task1 implements Runnable {
		static DataSource dataSource;
		static Random rand = new Random(System.nanoTime());
		static {
			Map<String, DataSource> dataSourceMap = new HashMap<>(2);
			dataSourceMap.put("sharding_0", createDataSource("sharding_0"));
			dataSourceMap.put("sharding_1", createDataSource("sharding_1"));

			DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap);

			// 分表分库的表，第一个参数是逻辑表名，第二个是实际表名，第三个是实际库
			TableRule orderTableRule = TableRule.builder("t_order")
					.actualTables(Arrays.asList("t_order_0", "t_order_1")).dataSourceRule(dataSourceRule).build();

			/**
			 * DatabaseShardingStrategy 分库策略 参数一：根据哪个字段分库 参数二：分库路由函数
			 * TableShardingStrategy 分表策略 参数一：根据哪个字段分表 参数二：分表路由函数
			 * 
			 */
			ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule)
					.tableRules(Arrays.asList(orderTableRule))
					.databaseShardingStrategy(new DatabaseShardingStrategy("Id", new ModuloDatabaseShardingAlgorithm()))
					.tableShardingStrategy(new TableShardingStrategy("CounterId", new ModuloTableShardingAlgorithm()))
					.build();
			try {
				dataSource = ShardingDataSourceFactory.createDataSource(shardingRule);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String sql = "SELECT * FROM t_order WHERE Id=?";
			try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
				int id = ids.get(rand.nextInt(4633));
				pstmt.setInt(1, id);
				try (ResultSet rs = pstmt.executeQuery()) {
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	static class Task2 implements Runnable {
		static Random rand = new Random(System.nanoTime());

		@Override
		public void run() {
			String sql2 = "SELECT * FROM bus_order WHERE Id=?";
			DataSource date2 = createDataSource("sharding_0");
			int id = ids.get(rand.nextInt(4633));
			try (Connection conn = date2.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql2)) {
				pstmt.setInt(1, id);
				try (ResultSet rs = pstmt.executeQuery()) {
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建数据源
	 * 
	 * @param dataSourceName
	 * @return
	 */
	public static DataSource createDataSource(String dataSourceName) {
		BasicDataSource result = new BasicDataSource();
		result.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
		result.setUrl(String.format("jdbc:mysql://localhost:3306/%s?useSSL=false", dataSourceName));
		result.setUsername("root");
		result.setPassword("root");
		return result;
	}
}
