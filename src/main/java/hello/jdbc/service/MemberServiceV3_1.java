package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 *
 * PlatformTransarionManager.getTransaction(TransactionDefinition transationDefinition)
 * -> 트랜잭션의 현재 상태를 나타내는 TransactionStatus 객체 반환
 *
 * DefalutTransactionDefinition
 * -> 트랜잭션의 기본 속성을 정의하는 역할
 */
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepositoryV3;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            bizLogic(fromId,toId,money);
            transactionManager.commit(status);
        }catch (Exception e){
            transactionManager.rollback(status);
            throw new IllegalArgumentException(e);
        }
    }

    public void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);

        memberRepositoryV3.update(fromId,fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV3.update(toId,toMember.getMoney() + money);
    }

    private void validation(Member member){
        if("MemberEX".equals(member.getMemberId())){
            throw new IllegalArgumentException("이체 중 예외 발생");
        }
    }
}
