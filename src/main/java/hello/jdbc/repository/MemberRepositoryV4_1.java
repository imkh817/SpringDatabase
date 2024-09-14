package hello.jdbc.repository;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제 해결
 * 체크 예외를 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLException 제거
 */
@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository{

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member){
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection con = null;
        PreparedStatement preparedStatement = null;

        try{
            con = getConnection();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2,member.getMoney());
            preparedStatement.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally{
            close(con,preparedStatement,null);
        }
    }

    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        }catch (SQLException e){
            // 기존 예외를 넣어줘야 로그찍힐때 어떤 에러에서 발생한건지 확인할 수 있다.
            throw new MyRuntimeException(e);
        }
        finally {
            close(con,pstmt,rs);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money =? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            int resultSize = pstmt.executeUpdate();
        }catch (SQLException e){
            // 기존 예외를 넣어줘야 로그찍힐때 어떤 에러에서 발생한건지 확인할 수 있다.
            throw new MyRuntimeException(e);
        }finally {
            close(con,pstmt,null);
        }
    }

    @Override
    public void init() {
        String sql = "delete from member";
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
        }catch (SQLException e){
            // 기존 예외를 넣어줘야 로그찍힐때 어떤 에러에서 발생한건지 확인할 수 있다.
            throw new MyRuntimeException(e);
        }finally {
            close(con,pstmt,null);
        }
    }

    @Override
    public void delete(String member_id) {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member_id);

            pstmt.executeUpdate();
        }catch (SQLException e){
            // 기존 예외를 넣어줘야 로그찍힐때 어떤 에러에서 발생한건지 확인할 수 있다.
            throw new MyRuntimeException(e);
        }finally{
            close(con,pstmt,null);
        }
    }




    private void close(Connection con, Statement statement, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(statement);
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        log.info("start getConnection()");
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get Connection = {} class = {}",con, con.getClass());
        return con;
    }
}
