package gr.aueb.cf.booklibapp.dto;


import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public class UserUpdateDTO {

    private String firstname;

    private String lastname;

    private String password;

    @PastOrPresent(message = "Date of birth cannot be in the future.")
    private LocalDate dateOfBirth;

    private String profilePicture;

    public UserUpdateDTO() {
    }

    public UserUpdateDTO(String firstname, String lastname, String password, LocalDate dateOfBirth, String profilePicture) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.profilePicture = profilePicture;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
