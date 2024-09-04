package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 커넥션 파라미터 전달 방식 동기화
 */
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try{
            con.setAutoCommit(false);
            bizLogic(con,fromId,toId,money);
        }catch (Exception e){
            con.rollback();
            throw new IllegalArgumentException(e);
        }finally {
            if(con != null){
                con.setAutoCommit(true);
                con.close();
            }
        }

    }

    public void bizLogic(Connection con,String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con,fromId);
        Member toMember = memberRepository.findById(con,toId);

        memberRepository.update(con,fromId,fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con,toId,toMember.getMoney() + money);
    }

    private void validation(Member member){
        if("MemberEX".equals(member.getMemberId())){
            throw new IllegalArgumentException("이체 중 예외 발생");
        }
    }

    private void close(Connection con) throws SQLException {
        if(con != null){
            con.setAutoCommit(true);
            con.close();
        }
    }
}
