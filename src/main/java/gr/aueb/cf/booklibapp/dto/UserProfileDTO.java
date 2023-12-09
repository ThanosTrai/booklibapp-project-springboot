package gr.aueb.cf.booklibapp.dto;

import java.time.LocalDate;

public class UserProfileDTO {
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String profilePicture;

    public UserProfileDTO() {
    }

    public UserProfileDTO(String firstname, String lastname, LocalDate dateOfBirth, String profilePicture) {
        this.firstname = firstname;
        this.lastname = lastname;
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
