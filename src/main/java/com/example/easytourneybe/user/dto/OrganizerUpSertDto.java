package com.example.easytourneybe.user.dto;

import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.util.RegexpUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerUpSertDto {
    private Integer id;

    @Email(message = "Email must be valid")
    @Pattern(regexp = RegexpUtils.EMAIL_REGEXP, message = "Email must be valid")
    @NotBlank(message = "Email must not be empty")
    private String email;

    @NotBlank(message = "First name must not be empty")
    @Pattern(regexp = RegexpUtils.NAME_REGEXP, message = "First name must be alphabetic")
    @Length(max = 30, message = "First name must be maximum 30 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    @Pattern(regexp = RegexpUtils.NAME_REGEXP, message = "Last name must be alphabetic")
    @Length(max = 30, message = "Last name must be maximum 30 characters")
    private String lastName;

    @NotNull(message = "Phone number must not be empty")
    @Pattern(regexp = RegexpUtils.PHONE_NUMBER_REGEXP, message = "Phone number must be valid")
    private String phoneNumber;

    private LocalDate dateOfBirth;

    private UserRole role;

    private LocalDateTime createdAt;

    @JsonIgnore
    private String password;

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim().toLowerCase() : null;
    }

    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName.trim() : null;
    }

    public void setLastName(String lastName) {
        this.lastName = (lastName != null) ? lastName.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber != null) ? phoneNumber.trim() : null;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = (phoneNumber != null) ? phoneNumber.toString().trim() : null;
    }

    public void setPassword(String password) {
        this.password = (password != null) ? password.trim() : null;
    }

    public void setDateOfBirth (String dateOfBirth) {
        if (dateOfBirth == null) {
            return;
        }
        try {
            OffsetDateTime odt = OffsetDateTime.parse(dateOfBirth.trim());
            this.dateOfBirth = odt.toLocalDate();
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Date of birth must be valid");
        }
    }

    public static OrganizerUpSertDto fromUser(User user) {
        return OrganizerUpSertDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
