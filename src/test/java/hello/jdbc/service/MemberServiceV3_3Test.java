package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {

    @Autowired
    private MemberRepositoryV3 memberRepository;
    @Autowired
    private MemberServiceV3_3 memberService;

    @AfterEach
    void delete() throws SQLException {
        memberRepository.init();
    }

    @Test
    @DisplayName("Aop 체크")
    void AopCheck(){
        log.info("memberService class = {}", memberService.getClass());
        log.info("memberRepository class = {}",memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("이체 중 오류 발생")
    void accountTransefer() throws SQLException {
        // given
        memberRepository.save(new Member("MemberA",10000));
        memberRepository.save(new Member("MemberEX",10000));
        // when
        assertThatThrownBy(()->memberService.accountTransfer("MemberA","MemberEX",3000))
                .isInstanceOf(IllegalArgumentException.class);
        Member memberA = memberRepository.findById("MemberA");
        Member memberEX = memberRepository.findById("MemberEX");
        // then
        assertThat(memberA.getMoney()).isEqualTo(10000);
        assertThat(memberEX.getMoney()).isEqualTo(10000);

    }

    @TestConfiguration
    static class TestConfig{
        @Bean
        DataSource dataSource(){
            return new DriverManagerDataSource(URL,NAME,PASSWORD);
        }


        @Bean
        MemberRepositoryV3 memberRepositoryV3(){
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3(){
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }


}