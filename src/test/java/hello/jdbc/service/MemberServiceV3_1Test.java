package hello.jdbc.service;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
class MemberServiceV3_1Test {

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void init(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,NAME,PASSWORD);
        PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSource);
        memberRepository = new MemberRepositoryV3(dataSource);
        memberService = new MemberServiceV3_1(platformTransactionManager,memberRepository);
    }

    @AfterEach
    void delete() throws SQLException {
        memberRepository.init();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        Member memberA = new Member("memberA",10000);
        Member memberB = new Member("memberB",10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),4000);

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());


        assertThat(findMemberA.getMoney()).isEqualTo(6000);
        assertThat(findMemberB.getMoney()).isEqualTo(14000);

    }
}