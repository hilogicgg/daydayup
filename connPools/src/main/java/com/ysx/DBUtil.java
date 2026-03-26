package com.ysx;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * 最终修复版：普通连接 + 连接池都能用
 */
public class DBUtil {
    private static Properties props = new Properties();
    private static DataSource dataSource;

    static {
        try {
            // 加载配置文件
            props.load(DBUtil.class.getClassLoader().getResourceAsStream("jdbc.properties"));
            // 初始化连接池
            dataSource = DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== 普通连接（修复：读取新的key） =====================
    public static Connection getNormalConnection() {
        Connection conn = null;
        try {
            // 这里改成读取 driverClassName / url / username / password
            Class.forName(props.getProperty("driverClassName"));
            conn = DriverManager.getConnection(
                    props.getProperty("url"),
                    props.getProperty("username"),
                    props.getProperty("password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // ===================== 连接池连接（不用改） =====================
    public static Connection getPoolConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===================== 关闭方法（已修复） =====================
    public static void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    public static void close(Connection conn, PreparedStatement pstmt) throws SQLException {
        DbUtils.close(pstmt);
        DbUtils.close(conn);
    }

    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) throws SQLException {
        DbUtils.close(rs);
        DbUtils.close(pstmt);
        DbUtils.close(conn);
    }
}