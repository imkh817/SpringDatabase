package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

class memberServiceTest {

    private MemberServiceV2 memberService;
    private MemberRepositoryV2 memberRepository;
    
    @BeforeEach
    void init(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,NAME,PASSWORD);
        memberRepository = new MemberRepositoryV2(dataSource);
        memberService = new MemberServiceV2(dataSource,memberRepository);
    }
    
    @AfterEach
    void delete() throws SQLException {
        memberRepository.init();
    }
    
    
    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws SQLException {
        // given
        memberRepository.save(new Member("memberA",10000));
        memberRepository.save(new Member("memberB",10000));

        // when
        memberService.accountTransfer("memberA","memberB",3000);

        // then
        Member memberA = memberRepository.findById("memberA");
        Member memberB = memberRepository.findById("memberB");
        assertThat(memberA.getMoney()).isEqualTo(7000);
        assertThat(memberB.getMoney()).isEqualTo(13000);

    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        memberRepository.save(new Member("MemberA",10000));
        memberRepository.save(new Member("MemberEX",10000));

        // when
        assertThatThrownBy(()->memberService.accountTransfer("MemberA","MemberEX",3000))
                .isInstanceOf(IllegalArgumentException.class);


        // then
        Member memberA = memberRepository.findById("MemberA");
        Member memberEx = memberRepository.findById("MemberEX");

        assertThat(memberA.getMoney()).isEqualTo(10000);
        assertThat(memberEx.getMoney()).isEqualTo(10000);

    }
}