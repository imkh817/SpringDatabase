package hello.jdbc.translator;


import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDuplicationKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void after(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,NAME,PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicationKey(){
        service.create("myId");
        service.create("myId");
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service{
        private final Repository repository;

        public void create(String memberId){
            try{
                repository.save(new Member(memberId,0));
                log.info("saveId={}",memberId);
            }catch (MyDuplicationKeyException e){
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                repository.save(new Member(retryId,0));
            }
        }

        public String generateNewId(String memberId){
            return memberId + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    static class Repository{
        private final DataSource dataSource;

        public Member save(Member member){
            String sql = "insert into member(member_id, money) values(?, ?)";

            Connection con = null;
            PreparedStatement preparedStatement = null;

            try{
                con = dataSource.getConnection();
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, member.getMemberId());
                preparedStatement.setInt(2,member.getMoney());
                preparedStatement.executeUpdate();
                return member;
            } catch (SQLException e) {
                // 키 중복인 경우 MyDuplicationKeyException을 throw
                if(e.getErrorCode() == 23505){
                    throw new MyDuplicationKeyException(e);
                }
                throw new RuntimeException(e);
            }finally{
                close(con,preparedStatement,null);
            }
        }

        private void close(Connection con, Statement statement, ResultSet rs){
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }
}
