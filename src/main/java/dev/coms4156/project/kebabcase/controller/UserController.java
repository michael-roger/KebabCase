package dev.coms4156.project.kebabcase.controller;

import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authenticating users.
 *
 * <p>Provides an endpoint to authenticate users by email and password. This controller
 * checks the email and password against the stored user records and returns the user ID
 * if authentication is successful or null otherwise.
 *
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li><strong>POST /authenticate</strong>: Authenticates user, returns userID or null.</li>
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
   * Creates a new user with a hashed password.
   *
   * @param firstName the first name of the user.
   * @param lastName the surname of the user.
   * @param emailAddress the email of the user, which will be used for login.
   * @param password the user's selected password, which will be hashed and used for login.
   * @return a {@link ResponseEntity} containing the resulting user and the new
   *     ID. If the email is in use by another user, an HTTP 409 conflict is returned.
   */
  @PostMapping("/users")
  public ResponseEntity<?> createUser(
      @RequestParam String firstName,
      @RequestParam String lastName,
      @RequestParam String emailAddress,
      @RequestParam String password) {

    Optional<UserEntity> userResult = userRepository.findByEmailAddress(emailAddress);
    if (userResult.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
        .body("There is an account already associated with " + emailAddress);
    }

    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
      BigInteger number = new BigInteger(1, bytes);
      StringBuilder hexString = new StringBuilder(number.toString(16));
      while (hexString.length() < 64) {
        hexString.insert(0, '0');
      }

      String hashedPassword = hexString.toString();

      UserEntity newUser = new UserEntity();

      newUser.setFirstName(firstName);
      newUser.setLastName(lastName);
      newUser.setEmailAddress(emailAddress);
      newUser.setPassword(hashedPassword);
      OffsetDateTime time = OffsetDateTime.now();
      newUser.setCreatedDatetime(time);
      newUser.setModifiedDatetime(time);
      UserEntity savedUser = userRepository.save(newUser);

      String response = "User was added successfully! User ID: "
              + savedUser.getId().toString();

      return new ResponseEntity<>(response, HttpStatus.CREATED);

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error: MD5 algorithm not found.", e);
    }

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
      @RequestParam String email, @RequestParam String password) {

    if (email == null || email.isBlank() || password == null || password.isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    Optional<UserEntity> userResult = userRepository.findByEmailAddress(email);

    if (userResult.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    UserEntity user = userResult.get();

    try {
      String hashedInputPassword = hashPassword(password);

      if (!hashedInputPassword.equals(user.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }

      return ResponseEntity.status(HttpStatus.OK).body(user.getId());
    } catch (NoSuchAlgorithmException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Hashes the password using SHA-256.
   *
   * @param password the plain-text password to hash.
   * @return the hashed password as a hexadecimal string.
   * @throws NoSuchAlgorithmException if SHA-256 is not available.
   */
  private String hashPassword(String password) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(encodedHash);
  }

  /**
   * Converts a byte array to a hexadecimal string.
   *
   * @param hash the byte array to convert.
   * @return the hexadecimal string representation of the byte array.
   */
  private String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
  
}

