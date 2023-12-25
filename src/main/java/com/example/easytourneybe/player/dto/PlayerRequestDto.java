package com.example.easytourneybe.player.dto;

import com.example.easytourneybe.util.RegexpUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRequestDto {
    @NotBlank(message = "Player name must not be empty")
    @Pattern(regexp = RegexpUtils.NAME_REGEXP, message = "Player name must be alphabetic")
    @Length(max = 30, message = "Player name must be maximum 30 characters")
    private String playerName;
    private String dateOfBirth;
    @Pattern(regexp = RegexpUtils.PHONE_NUMBER_REGEXP, message = "Phone number must be valid")
    private String phone;
}
