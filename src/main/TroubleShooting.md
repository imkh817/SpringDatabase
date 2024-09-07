# 트러블 슛팅
### 1. 테스트 코드 작성 시 필드(ULR,USERNAME,PASSWORD) 등 주입 안되는 문제 발생<br>
##### DBConnectionUtil.java
```java
@Slf4j
public class DBConnectionUtil {
    
    @Value("#{URL}")
    public static String URL;
    
    @Value("#{USERNAME}")
    public static String USERNAME;
    
    @Value("#{PASSWORD}")
    private static String PASSWORD;
    
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("GET CONNECTION");
            log.info("Connection = {}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

```
##### DBConnectionUtilTest.java
```java
@Slf4j
class DBConnectionUtilTest {

    @Test
    void getConnection(){
        Connection con = DBConnectionUtil.getConnection();
        Assertions.assertThat(con).isNotNull();
    }
}
```
   - `@Value` 애노테이션은 스프링의 DI 기능을 통해 주입되는 값인데 `static` 필드는 스프링의 DI 컨테이너가 관리하는 객체와 별도로 존재
   - 그래서 `static` 필드는 `@Value` 애노테이션으로 값 주입 불가
   - 이유 : `static` 필드는 `JVM 클래스 로더`에 의해 JVM의 메모리 영역 중 하나인 `Class Area(Static Area, Method Area)에 런타임에 저장됨.` 이 시점은 스프링 컨테이너인 `ApplicationContext가 로드되기 전`이므로, `static` 필드는 `ApplicationContext`에 의존적인 `@Value`가 동작하지 않음. `(@Autowired도 동일)`
   - 단순 `static` 선언을 지우면 되지만, `getConnection()` 함수 자체를 `static`으로 사용할거기 때문에 필드도 `static`으로 선언해야 하는데 방법이 없을까?

#### 해결 방법 1 
- 환경 설정 파일을 읽을 수 있는 `Environment` 객체를 주입받아서 `@PostConstruct`로 초기화 시 설정 정보값 필드에 넣어주기
 

##### DBConnectionUtil.java
```java
@Slf4j
@Data
@Component
public class DBConnectionUtil {

    @Autowired
    private Environment environment;

    public static String URL;

    public static String USERNAME;

    private static String PASSWORD;

    @PostConstruct
    private void init(){
        log.info("DBConnection init");
        URL = environment.getProperty("URL");
        USERNAME = environment.getProperty("USERNAME");
        PASSWORD = environment.getProperty("PASSWORD");
    }

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("GET CONNECTION");
            log.info("Connection = {}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

```


##### DBConnectionUtilTest.java
```java
@Slf4j
@SpringBootTest
class DBConnectionUtilTest {

    @Autowired
    DBConnectionUtil dbConnectionUtil;

    @Test
    void getConnection(){
        Connection con = DBConnectionUtil.getConnection();
        Assertions.assertThat(con).isNotNull();
    }
}
```
<br>
<br>


### 2. 테스트 코드 작성 시 @Autowird로는 필드 주입이 되는데 @RequiredArgumentsConstructor를 사용해서 필드 주입 불가<br>

##### 사용할 수 없었던 이유 ❓<br>
- 테스트 코드가 아닌 곳에서는 스프링 컨테이너를 이용하여 빈을 가져와 자동 주입을 하여 @RequiredArgsConstructor도 되고 @Autowired도 사용이 가능하다.
- 그러나 테스트 코드에서는 JUnit5 프레임워크를 사용하여 스프링 컨테이너가 아닌 JUnit5가 스스로 지원을 하게 됩니다. 그래서 의존성 주입의 타입이 정해져 있어 @Autowired만 사용이 가능하다.

##### 테스트 코드에서 사용하는 메서드 자세히 알아보기 ✍🏻
👉 https://wonyong-jang.github.io/spring/2020/06/09/Spring-Test-Code-With-Junit.html
