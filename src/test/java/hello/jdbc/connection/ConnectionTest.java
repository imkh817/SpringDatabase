package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
class ConnectionTest {


    @Test
    @DisplayName(value = "DriverManager를 통해 커넥션을 얻는 법")
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL,NAME,PASSWORD);
        Connection con2 = DriverManager.getConnection(URL,NAME,PASSWORD);
        log.info("connection1={},class={}",con1,con1.getClass());
        log.info("connection2={},class={}",con2,con2.getClass());
    }

    @Test
    @DisplayName(value = "DriverManagerDataSource를 통해 커넥션을 얻는 방법")
    void dataSourceDriverManager() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,NAME, PASSWORD);
        userDataSource(dataSource);
    }

    private void userDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection1={},class={}",con1,con1.getClass());
        log.info("connection2={},class={}",con2,con2.getClass());
    }

    @Test
    @DisplayName(value = "DataSource를 통해 커넥션 풀 이용하기")
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(NAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MY POOL");

        userDataSource(dataSource);
        Thread.sleep(1000);
    }
}
