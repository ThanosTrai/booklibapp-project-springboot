package gr.aueb.cf.booklibapp.controllers;

import gr.aueb.cf.booklibapp.auth.AuthenticationResponse;
import gr.aueb.cf.booklibapp.dto.UserLoginDTO;
import gr.aueb.cf.booklibapp.dto.UserRegisterDTO;
import gr.aueb.cf.booklibapp.service.UserServiceImpl;
import gr.aueb.cf.booklibapp.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final UserValidator userValidator;

    @Autowired
    public AuthController(UserServiceImpl userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @InitBinder("userRegisterDTO")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO registerDto, BindingResult result) {
        if (result.hasErrors()) {
            // Map to hold the field errors
            Map<String, String> fieldErrors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Default error message")
                    ));

            // Response body containing the errors
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Validation failed");
            responseBody.put("errors", fieldErrors);

            return ResponseEntity.badRequest().body(responseBody);
        }
        AuthenticationResponse response = userService.registerUser(registerDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserLoginDTO loginDTO) {

        AuthenticationResponse response = userService.authenticateUser(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        userService.refreshToken(req, res);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok().body("Current user is: " + username);
    }
}
