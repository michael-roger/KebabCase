package dev.coms4156.project.kebabcase.controller;

import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for authenticating users.
 *
 * <p>Provides an endpoint to authenticate users by email and password. This controller
 * checks the email and password against the stored user records and returns the user ID
 * if authentication is successful or null otherwise.
 *
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li><strong>POST /authenticate</strong>: Authenticates a user and returns the user ID or null.</li>
 * </ul>
 *
 * <h2>Error Handling:</h2>
 * <p>
 * - HTTP 200: Authentication result (user ID or null).<br>
 * - HTTP 400: Missing or invalid parameters.<br>
 * - HTTP 500: Internal server error (optional, in case of unforeseen issues).
 * </p>
 */
@RestController
public class UserController {

    private final UserRepositoryInterface userRepository;

    /**
     * Constructs a new {@link AuthController}.
     *
     * @param userRepository the repository for user entities
     */
    public UserController(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user by email and password.
     *
     * @param email the email of the user as requested.
     * @param password the plain-text password of that user.
     * @return the user ID if authentication is successful, or null if not.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<Integer> authenticate(
            @RequestParam String email,
            @RequestParam String password) {

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<UserEntity> userResult = userRepository.findByEmailAddress(email);

        if (userResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }

        UserEntity user = userResult.get();

        if (user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.OK).body(user.getId());
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
