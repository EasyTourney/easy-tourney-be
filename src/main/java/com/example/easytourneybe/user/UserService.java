package com.example.easytourneybe.user;

import com.example.easytourneybe.user.dto.OrganizerDto;
import com.example.easytourneybe.user.dto.User;
import com.example.easytourneybe.user.repository.UserDao;
import com.example.easytourneybe.user.repository.UserRepository;
import com.example.easytourneybe.validations.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final CommonValidation commonValidation=new CommonValidation();


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: "+ username));
    }
    public List<OrganizerDto> organizerTable(String keyword, String sortType, int page, int size, String sortValue){
        commonValidation.validatePageAndSize(page, size);
        if(sortType==null || sortType.isEmpty()){
            sortValue="id";
            sortType="desc";
        }
        List<OrganizerDto> foundUser= userDao.organizerTable(keyword,sortValue,sortType,page,size);
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
            throw new NoSuchElementException("Category not found");
        }
    }
}
