package com.ysx;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @Description 测试：普通连接 VS 连接池 性能差距
 * @Author ysx
 * @Date 2026/3/26 21:49
 **/
public class PoolTest {
    private static final QueryRunner queryRunner = new QueryRunner();
    private static final String SQL = "SELECT * FROM user";

    public static void main(String[] args) throws Exception {
        int count = 1000; // 执行1000次查询

        System.out.println("===== 1. 普通连接（每次新建）测试 =====");
        long normalStart = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            normalQuery();
            if (i % 100 == 0) System.out.println("普通连接执行：第" + i + "次");
        }
        long normalEnd = System.currentTimeMillis();
        System.out.println("普通连接总耗时：" + (normalEnd - normalStart) + "ms");

        System.out.println("\n===== 2. 连接池（复用连接）测试 =====");
        long poolStart = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            poolQuery();
            if (i % 100 == 0) System.out.println("连接池执行：第" + i + "次");
        }
        long poolEnd = System.currentTimeMillis();
        System.out.println("连接池总耗时：" + (poolEnd - poolStart) + "ms");

        System.out.println("\n===== 结论 =====");
        System.out.println("连接池比普通连接快：" + (normalEnd - normalStart - (poolEnd - poolStart)) + "ms");
    }

    // 普通连接查询
    public static void normalQuery() throws Exception {
        Connection conn = DBUtil.getNormalConnection();
        List<Map<String, Object>> list = queryRunner.query(conn, SQL, new MapListHandler());
        DBUtil.close(conn); // 真正关闭连接
    }

    // 连接池查询
    public static void poolQuery() throws Exception {
        Connection conn = DBUtil.getPoolConnection();
        List<Map<String, Object>> list = queryRunner.query(conn, SQL, new MapListHandler());
        DBUtil.close(conn); // 归还连接到池，不是关闭！
    }
}
