package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class uncheckedTest {

    @Test
    @DisplayName("예외 잡아서 처리하기")
    void unchecked_catch(){
        Service service = new Service();
        service.unchecked_catch();
    }

    @Test
    @DisplayName("예외 던지기")
    void chekcd_throw(){
        Service service = new Service();
        Assertions.assertThatThrownBy(()-> service.unchecked_throw())
                .isInstanceOf(MyUncheckedException.class);
    }

    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    static class Service{
        Repository repository = new Repository();

        public void unchecked_catch(){
            try{
                repository.call();
            }catch (MyUncheckedException e){
                log.info("예외 처리, message={}",e.getMessage(),e);
            }
        }

        public void unchecked_throw(){
            repository.call();
        }
    }



    static class Repository{

        public void call(){
            throw new MyUncheckedException("ex");
        }
    }
}
