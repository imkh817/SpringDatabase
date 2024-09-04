package hello.jdbc.connection;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV0;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DBConnectionUtilTest {

    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void getConnection(){
        Connection con = DBConnectionUtil.getConnection();
        Assertions.assertThat(con).isNotNull();
    }

    @Test
    void crud() throws SQLException{
        //save
        Member member = new Member("memberV0", 10000);
        repository.save(member);
    }
}