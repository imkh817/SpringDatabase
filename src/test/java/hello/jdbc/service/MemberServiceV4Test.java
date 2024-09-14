package hello.jdbc.service;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV4_1;
import hello.jdbc.repository.ex.MyRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 예외 누수 문제 해결
 * SQLExcpetion 제거
 *
 * MemberRepository 인터페이스 의존
 */
@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberServiceV4 memberService;

    @AfterEach
    void after(){
        memberRepository.init();;
    }

    @TestConfiguration
    @RequiredArgsConstructor
    static class TestConfig{

       private final DataSource dataSource;

        @Bean
        MemberRepository memberRepository(){
            return new MemberRepositoryV4_1(dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4(){
            return new MemberServiceV4(memberRepository());
        }
    }

    @Test
    void AopCheck(){
        log.info("memberService class = {}", memberService.getClass());
        log.info("memberRepository class = {}", memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer(){
        memberRepository.save(new Member("memberA",1000));
        memberRepository.save(new Member("memberB",2000));

        memberService.accountTransfer("memberA","memberB",1000);

        Member findMemberA = memberRepository.findById("memberA");
        Member findMemberB = memberRepository.findById("memberB");

        assertThat(findMemberA.getMoney()).isEqualTo(0);
        assertThat(findMemberB.getMoney()).isEqualTo(3000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx(){
        memberRepository.save(new Member("MemberA",1000));
        memberRepository.save(new Member("MemberEX",2000));

        Assertions.assertThatThrownBy(()-> memberService.accountTransfer("MemberA","MemberEX",1000))
                .isInstanceOf(IllegalArgumentException.class);

        Member memberA = memberRepository.findById("MemberA");
        Member memberEx = memberRepository.findById("MemberEX");
        assertThat(memberA.getMoney()).isEqualTo(1000);
        assertThat(memberEx.getMoney()).isEqualTo(2000);
    }

}