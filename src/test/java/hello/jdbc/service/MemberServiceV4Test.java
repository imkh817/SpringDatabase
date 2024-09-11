package hello.jdbc.service;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV4_1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberServiceV4 memberServiceV4;

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

}