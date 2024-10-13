package dev.coms4156.project.kebabcase;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HousingUnitControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;

  private static final String BASE_URL = "http://localhost:8080";

  @BeforeEach
  void setUp() {
  }
}
