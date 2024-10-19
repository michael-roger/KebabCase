package dev.coms4156.project.kebabcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Sanity check controller. */
@RestController
public class IndexController {

  /**
   * Basic sanity check controller showing string
   * welcome message.
   *
   * @return String welcome message.
   */
  @GetMapping({"/", "/index", "/home"})
  public String index() {
    return """
            Welcome! \
            """;
  }
}
