package it.alnao.corso25saluti;

import javax.print.attribute.standard.Media;

// Aggiungi questa importazione statica
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.contentType;
// E probabilmente ti serviranno anche queste
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

    @Test
    public void testSalutoConNome() throws Exception {
        mockMvc.perform(get("/api/v1/corso25saluti/Alberto"))
            //.contentType(MediaType.APPLICATION_JSON)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ciao Alberto!")));
    }

}
