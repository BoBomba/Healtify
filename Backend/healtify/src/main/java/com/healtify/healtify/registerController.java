//package com.healtify.healtify;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import com.healtify.healtify.models.UserAccount;
//import com.healtify.healtify.repository.UserAccountRepository;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//public class registerController {
//
//
//    @Autowired
//    private UserAccountRepository userAccountRepository;
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder; //= new BCryptPasswordEncoder();
//
//    @Autowired
//    public registerController(UserAccountRepository userAccountRepository, BCryptPasswordEncoder passwordEncoder) {
//        this.userAccountRepository = userAccountRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @PostMapping("/api/auth")
//    @CrossOrigin(origins = "http://localhost:3000")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<Boolean> registerUser(@RequestBody UserAccount request) {
//
//        String username = request.getUsername();
//        String email = request.getEmail();
//        String password = request.getPassword();
//        System.out.println("Przyjęto dane: " + username + " " + email + " " + password);
//
//        String passwordEnc = passwordEncoder.encode(password);
//
//        System.out.println("Zaszyfrowane hasło: " + passwordEnc);
//
//        UserAccount newUser = new UserAccount(username, email, passwordEnc);
//        userAccountRepository.save(newUser);
//        System.out.println("Zapisano użytkownika");
//
//        return ResponseEntity.ok(true);
//
//        // // Execute the query and handle any errors
//        //  try {
//        //      // Execute the query and handle any errors
//        //     PreparedStatement preparedStatement = connection.prepareStatement(query);
//        //     preparedStatement.setString(1, username);
//        //     preparedStatement.setString(2, email);
//        //     preparedStatement.setString(3, passwordEnc);
//        //     int result = preparedStatement.executeUpdate();
//
//        //     if (result == 0) {
//        //         System.err.println("Error executing query");
//        //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
//        //     }
//        // } catch (Exception e) {
//        //     System.err.println(e);
//        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
//        // }
//        // return ResponseEntity.ok(true);
//
//
//        //      connection.query(query, [username, email, passwordEnc], (err, result) => {
//        //          if (err) {
//        //              System.err.println(err);
//        //              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
//        //          }
//        //          return ResponseEntity.ok(true);
//        //      });
//        //  } catch (Exception e) {
//        //      System.err.println(e);
//        //      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
//        //  }
//    }
//}
//
