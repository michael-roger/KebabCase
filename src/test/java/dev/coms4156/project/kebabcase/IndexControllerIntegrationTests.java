package dev.coms4156.project.kebabcase;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;

  private static final String BASE_URL = "http://localhost:8080";

  @BeforeEach
  void setUp() {
  }

  @Test
  void indexSuccess() throws Exception {
    mockMvc
        .perform(get(BASE_URL + "/"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Welcome!")));
  }
}
