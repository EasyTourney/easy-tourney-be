package com.example.easytourneybe.user;


import com.example.easytourneybe.constants.DefaultPassword;
import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.user.dto.*;
import com.example.easytourneybe.user.repository.UserDao;
import com.example.easytourneybe.user.repository.UserRepository;
import com.example.easytourneybe.util.DateValidatorUtils;
import com.example.easytourneybe.validations.CommonValidation;
import jakarta.persistence.EntityManager;
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
    @Autowired
    EntityManager entityManager;
    private final String FIND_USER_BY_TOURNAMENT_ID =
            "SELECT u.*                                                 \n" +
            "FROM users u                                               \n" +
            "JOIN organizer_tournament ot ON u.id = ot.user_id          \n" +
            "WHERE tournament_id = ?";

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
        List<OrganizerTableDto> foundUser = userDao.organizerTable(commonValidation.escapeSpecialCharacters(keyword.trim()), sortValue, sortType, page, size);
        if (foundUser.isEmpty()) {
            throw new NoSuchElementException("Organizer not found");
        }

        return foundUser;
    }

    public long totalOrganizer(String keyword) {
        return userRepository.totalOrganizer(commonValidation.escapeSpecialCharacters(keyword.trim()));
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
        if (organizer.getDateOfBirth() != null
                && !DateValidatorUtils.isBeforeToday(organizer.getDateOfBirth())) {
            throw new InvalidRequestException("Date of birth must be before today");
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
        if (organizer.getDateOfBirth() != null
                && !DateValidatorUtils.isBeforeToday(organizer.getDateOfBirth())) {
            throw new InvalidRequestException("Date of birth must be before today");
        }

        User user = userRepository.findOrganizerById(id).orElseThrow(() -> new InvalidRequestException("Organizer not found"));
        User userByEmail = userRepository.findExistEmail(organizer.getEmail());
        if (userByEmail != null && !userByEmail.getId().equals(id)) {
            throw new InvalidRequestException("Email already exist");
        }

        user.setEmail(organizer.getEmail());
        user.setFirstName(organizer.getFirstName());
        user.setLastName(organizer.getLastName());
        user.setPhoneNumber(organizer.getPhoneNumber());
        user.setDateOfBirth(organizer.getDateOfBirth() != null ? organizer.getDateOfBirth() : user.getDateOfBirth());

        return userRepository.save(user);
    }

    public User getOrganizer(Integer id) {
        return userRepository.findOrganizerById(id).orElseThrow(() -> new InvalidRequestException("Organizer not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email).get();
    }

    public List<UserDto> findUserByTournamentId(Integer tournamentId) {
        return userRepository.findUserByTournamentId(tournamentId);
    }

    public List<OrganizerInGeneralDto> findOrganizerInGeneral(Integer tournamentId) {
        return userRepository.findOrganizerInGeneral(tournamentId);
    }

    public boolean isOrganizerOfTournament(String email, Integer tournamentId) {
        // Find UserId by email
        Integer userId = userRepository.findUserByEmail(email).orElseThrow(() -> new InvalidRequestException("Organizer not found")).getId();

        // Find User by userId and tournamentId
        User user = userRepository.isOrganizerOfTournament(userId, tournamentId);

        // If user is null -> user is not organizer of this tournament
        return user != null;
    }

    public boolean isExistUser(String email) {
        return userRepository.existsByEmailAndIsDeletedFalse(email);
    }
}
