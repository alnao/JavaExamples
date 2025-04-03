# Corso Spring boot 25 - saluti

Preso spunto dal corso su udemy [Sviluppare Full Stack Apps con Spring Boot 3 e Angular 17](https://www.udemy.com/course/sviluppare-full-stack-applications-con-spring-boot-e-angular).

Per creare il progetto eseguiti i passi
- da [start.spring.io](https://start.spring.io/) creato progetto con le caratteristiche
    - maven
    - versione 3.4.4
    - artifact = corso25saluti
    - jar
    - java17
    - ricordarsi di aggiungere la dipendenza "Spring web" e "spring boot web tools"
- importato lo zip scaricato e decompresso
- un bel `mvn install`
- modificato il file `application.properties` dove ho aggiunto `server.port=5432` 
- creata la classe controller
    ```
    @RestController
    @RequestMapping("/api/v1/corso25saluti")
    public class Corso25salutiController {

        @GetMapping()
        public String getSaluto(){
            return "Ciao Corso 25 Saluti!";
        }
    }
    ```
- funziona `http://localhost:5432/api/v1/corso25saluti`
- test: creata la classe `Corso25salutiControllerTest` e inserito il codice
    ```
    @SpringBootTest
    @ContextConfiguration(classes = Corso25salutiApplication.class)
    public class Corso25salutiControllerTest {
        private MockMvc mockMvc;
        @Autowired
        private WebApplicationContext webApplicationContext;

        @BeforeEach
        public void setUp() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @Test
        public void testSaluto() throws Exception {
            mockMvc.perform(get("/api/v1/corso25saluti"))
                //.contentType(MediaType.TEXT_PLAIN)
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Ciao")));
        }
    }
    ```
    eseguito il comando `mvn test`


# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.4/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.4/reference/web/servlet.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.4/reference/using/devtools.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

