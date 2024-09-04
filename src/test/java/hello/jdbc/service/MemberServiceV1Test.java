package hello.jdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceV1Test {

    private final static String MEMBER_A = "MemberA";
    private final static String MEMBER_B = "MemberB";
    private final static String MEMBER_EX = "MemberEX";


    private MemberRepositoryV1 memberRepositoryV1;
    private MemberServiceV1 memberServiceV1;

    @BeforeEach
    void init(){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(URL);
        hikariDataSource.setUsername(NAME);
        hikariDataSource.setPassword(PASSWORD);

        memberRepositoryV1 = new MemberRepositoryV1(hikariDataSource);
        memberServiceV1 = new MemberServiceV1(memberRepositoryV1);
    }

    @AfterEach
    void delete() throws SQLException {
        memberRepositoryV1.init();
    }

    @Test
    @DisplayName("정상 이체")
    void accoutTransfer() throws SQLException {
        // given
        memberRepositoryV1.save(new Member(MEMBER_A,10000));
        memberRepositoryV1.save(new Member(MEMBER_B, 10000));

        // when
        memberServiceV1.accountTransfer(MEMBER_A,MEMBER_B,2000);

        // then
        Member memberA = memberRepositoryV1.findById(MEMBER_A);
        assertThat(memberA.getMoney()).isEqualTo(8000);
    }

    @Test
    @DisplayName("이체 중 오류 발생")
    void accountTranseferEx() throws SQLException {
        // given
        memberRepositoryV1.save(new Member(MEMBER_A,10000));
        memberRepositoryV1.save(new Member(MEMBER_EX, 10000));
        // when
        assertThatThrownBy(()->{memberServiceV1.accountTransfer(MEMBER_A,MEMBER_EX,3000);})
                .isInstanceOf(IllegalArgumentException.class);

        //then
        Member fromMember = memberRepositoryV1.findById(MEMBER_A);
        Member toMember = memberRepositoryV1.findById(MEMBER_EX);
        // 오류가 발생해서 MemberA는 3000원이 차감됏지만, MemberEx는 3000이 추가되지 않았다.
        // 결론적으로는 MemberA만 3000원을 잃은셈이다.
        assertThat(fromMember.getMoney()).isEqualTo(7000);
        assertThat(toMember.getMoney()).isEqualTo(10000);


    }

}