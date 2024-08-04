package com.team;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

	Connection conn;

	public DBConn() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String user = "scott";
			String pwd = "1234";

			conn = DriverManager.getConnection(url, user, pwd);
		} catch (ClassNotFoundException e) {
			System.out.println("접속경로 오류");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("로그인 오류");
			e.printStackTrace();
		}
		System.out.println("DB 연결되었습니다.");
	}

	public Connection getConn() {
		return conn;
	}

	public void closeConn() {
		if (conn != null) {
			try {
				conn.close();
				System.out.println("DB 연결이 닫혔습니다.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
