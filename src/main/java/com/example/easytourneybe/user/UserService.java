package com.example.easytourneybe.user;

import com.example.easytourneybe.constants.DefaultPassword;
import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.user.dto.OrganizerTableDto;
import com.example.easytourneybe.user.dto.User;
import com.example.easytourneybe.user.dto.OrganizerUpSertDto;
import com.example.easytourneybe.user.repository.UserDao;
import com.example.easytourneybe.user.repository.UserRepository;
import com.example.easytourneybe.util.DateValidatorUtils;
import com.example.easytourneybe.validations.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDao userDao;
    private final CommonValidation commonValidation = new CommonValidation();

    @Autowired
    PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: " + username));
    }

    public List<OrganizerTableDto> organizerTable(String keyword, String sortType, int page, int size, String sortValue) {
        commonValidation.validatePageAndSize(page, size);
        if (sortType == null || sortType.isEmpty()) {
            sortValue = "id";
            sortType = "desc";
        }
        List<OrganizerTableDto> foundUser = userDao.organizerTable(keyword, sortValue, sortType, page, size);
        if (foundUser.isEmpty()) {
            throw new NoSuchElementException("Organizer not found");
        }

        return foundUser;
    }

    public long totalOrganizer(String keyword) {
        return userRepository.totalOrganizer(keyword);
    }

    public Optional<User> deleteOrganizer(Integer id) {
        Optional<User> foundUser = userRepository.findOrganizerById(id);

        if (foundUser.isPresent()) {
            User organizer = foundUser.get();
            organizer.setIsDeleted(true);
            organizer.setDeletedAt(LocalDateTime.now());
            userRepository.save(organizer);
            return Optional.of(organizer);
        } else {
            throw new NoSuchElementException("Organizer not found");
        }
    }

    public User createOrganizer(OrganizerUpSertDto organizer) {
        if (!DateValidatorUtils.isBeforeToday(organizer.getDateOfBirth())) {
            throw new InvalidRequestException("Date of birth must be today or after today");
        }

        User user = userRepository.findExistEmail(organizer.getEmail());
        if (user != null) {
            throw new InvalidRequestException("Email already exist");
        }

        user = User.builder()
                .email(organizer.getEmail())
                .password(passwordEncoder.encode(DefaultPassword.ORGANIZER_DEFAULT_PASSWORD))
                .firstName(organizer.getFirstName())
                .lastName(organizer.getLastName())
                .phoneNumber(organizer.getPhoneNumber())
                .dateOfBirth(organizer.getDateOfBirth())
                .role(UserRole.ORGANIZER)
                .build();
        return userRepository.save(user);
    }

    public User updateOrganizer(Integer id, OrganizerUpSertDto organizer) {
        if (!DateValidatorUtils.isBeforeToday(organizer.getDateOfBirth())) {
            throw new InvalidRequestException("Date of birth must be today or after today");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new InvalidRequestException("Organizer not found"));
        User userByEmail = userRepository.findExistEmail(organizer.getEmail());
        if (userByEmail != null && !userByEmail.getId().equals(id)) {
            throw new InvalidRequestException("Email already exist");
        }

        user.setEmail(organizer.getEmail());
        user.setFirstName(organizer.getFirstName());
        user.setLastName(organizer.getLastName());
        user.setPhoneNumber(organizer.getPhoneNumber());
        user.setDateOfBirth(organizer.getDateOfBirth());

        return userRepository.save(user);
    }

    public Optional<User> getOrganizer(Integer id) {
        Optional<User> user = userRepository.findOrganizerById(id);
        if (user.isEmpty()) {
            throw new InvalidRequestException("Organizer not found");
        }
        return user;
    }
}
