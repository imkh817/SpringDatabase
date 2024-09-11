package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

@Slf4j
public class checkedTest {

    @Test
    @DisplayName("예외 잡아서 처리")
    void checked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    @DisplayName("예외 던지기")
    void checked_throw(){
        Service service = new Service();
        Assertions.assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }


    /**
     * Exception을 상속받은 예외는 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception{
        public MyCheckedException(String message){
            super(message);
        }
    }

    /**
     * Checked 예외는 예외를 잡아서 처리하거나, 던지거나 둘 중 하나를 해야한다.
     */
    static class Service{
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch(){
            try{
                repository.call();
            }catch(MyCheckedException e){
                log.info("예외 처리, message={}",e.getMessage(),e);
            }
        }

        /**
         * 예외를 밖으로 던지는 메서드
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository{
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
