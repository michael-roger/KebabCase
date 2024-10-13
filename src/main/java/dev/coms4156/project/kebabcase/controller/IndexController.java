package dev.coms4156.project.kebabcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** TODO */
@RestController
public class IndexController {

  /**
   * TODO.
   *
   * @return TODO.
   */
  @GetMapping({"/", "/index", "/home"})
  public String index() {
    return """
            Welcome! \
            """;
  }
}
