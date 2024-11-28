package dev.coms4156.project.kebabcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Warmup controller used by Google App Engine. */
@RestController
public class WarmupController {

  /**
   * Allows for Google App Engine's warmup feature.
   *
   * @return String simple message.
   */
  @GetMapping({"/_ah/warmup"})
  public String warmup() {
    return """
            Warmup!\
            """;
  }
}
