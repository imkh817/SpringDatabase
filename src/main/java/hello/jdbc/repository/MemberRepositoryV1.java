package hello.jdbc.repository;


import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource 사용, JdbcUtils 사용
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Member save(Member member){
        String sql = "insert into member(member_id, money) values(?,?)";

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

    public Member findById(String memberId) throws SQLException {
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
            throw e;
        }
        finally {
            close(con,pstmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money =? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            int resultSize = pstmt.executeUpdate();
        }catch (Exception e){
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }

    public void init() throws SQLException {
        String sql = "delete from member";
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
        }catch (Exception e){
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }

    public void delete(String member_id) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member_id);

            pstmt.executeUpdate();
        }catch (Exception e){
            throw  e;
        }finally{
            close(con,pstmt,null);
        }
    }




    private void close(Connection con, Statement statement, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(statement);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        log.info("start getConnection()");
        Connection con = dataSource.getConnection();
        return con;
    }
}
