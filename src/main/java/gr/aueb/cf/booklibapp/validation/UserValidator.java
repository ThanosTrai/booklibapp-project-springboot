package gr.aueb.cf.booklibapp.validation;

import gr.aueb.cf.booklibapp.dto.UserRegisterDTO;
import gr.aueb.cf.booklibapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {

    private final IUserService userService;
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{6,}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)?@[a-zA-Z]{1,}\\.[a-zA-Z]{2,6}$";

    @Autowired
    public UserValidator(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegisterDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
            UserRegisterDTO registerDto = (UserRegisterDTO) target;

            // Validate username
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.empty", "Username is required");
            if (registerDto.getUsername().length() < 5 || registerDto.getUsername().length() > 25) {
                errors.rejectValue("username", "username.size", "Username must be between 5 and 25 characters long.");
            } else if (userService.isUsernameTaken(registerDto.getUsername())) {
                errors.rejectValue("username", "username.duplicate", "Username is already taken.");
            }

            // Validate email
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.empty", "Email cannot be empty");
            if (!errors.hasFieldErrors("email")) {
                String email = registerDto.getEmail();
                if (registerDto.getEmail().length() < 6 || registerDto.getEmail().length() > 32) {
                    errors.rejectValue("email", "email.size", "Email must be between 6 and 32 characters long");
                } else {
                    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                    if (!pattern.matcher(email).matches()) {
                        errors.rejectValue("email", "email.pattern", "Email format is invalid");
                    } else if (userService.isEmailTaken(registerDto.getEmail())) {
                        errors.rejectValue("email", "email.duplicate", "Email is already in use.");
                    }
                }
            }

            // Validate password
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.empty", "Password cannot be empty");
            if (!errors.hasFieldErrors("password")) {
                String password = registerDto.getPassword();
                if (password.length() < 6 || password.length() > 32) {
                    errors.rejectValue("password", "password.size", "Password must be between 6 and 32 characters long");
                } else {
                    Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
                    if (!pattern.matcher(password).matches()) {
                        errors.rejectValue("password", "password.pattern", "Password must contain at least one lowercase, one uppercase, one digit, and one special character.");
                    }
                }
            }

            // Validate confirmPassword
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "confirmPassword.empty", "Confirm password cannot be empty");
            if (!errors.hasFieldErrors("confirmPassword")) {
                String confirmPassword = registerDto.getConfirmPassword();
                if (!Objects.equals(registerDto.getPassword(), confirmPassword)) {
                    errors.rejectValue("confirmPassword", "mismatch", "Confirm password does not match the password.");
                }
            }
    }
}
