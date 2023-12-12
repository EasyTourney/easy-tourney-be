package com.example.easytourneybe.user;

import com.example.easytourneybe.model.ResponseObject;
import com.example.easytourneybe.user.dto.OrganizerTableDto;
import com.example.easytourneybe.user.dto.User;
import com.example.easytourneybe.user.dto.OrganizerUpSertDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.example.easytourneybe.constants.DefaultListParams.PAGE;
import static com.example.easytourneybe.constants.DefaultListParams.SIZE;

@RestController

@CrossOrigin
@RequestMapping("/organizer")
public class UserController {
    @Autowired
    private UserService userService;

    private static final String DEFAULT_SORT_VALUE = "fullName";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> organizerTable(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String sortType,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_VALUE) String sortValue
    ) {
        long totalOrganizer = userService.totalOrganizer(keyword.trim());
        List<OrganizerTableDto> listUser = userService.organizerTable(keyword.trim(), sortType, page - 1, size, sortValue);
        ResponseObject responseObject = new ResponseObject(
                true,
                listUser.size(),
                listUser
        );
        responseObject.setAdditionalData(java.util.Collections.singletonMap("totalOrganizer", totalOrganizer));
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteOrganizer(@PathVariable Integer id) {
        Optional<User> deletedOrganizer = userService.deleteOrganizer(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, 1, deletedOrganizer));
    }

    @PostMapping
    public ResponseEntity<Object> createOrganizer(@Valid @RequestBody OrganizerUpSertDto organizer) {
        User user = userService.createOrganizer(organizer);

        OrganizerUpSertDto temp = OrganizerUpSertDto.fromUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                .success(true)
                .total(1)
                .data(temp)
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateOrganizer(@PathVariable Integer id, @Valid @RequestBody OrganizerUpSertDto organizer) {
        User user = userService.updateOrganizer(id, organizer);

        OrganizerUpSertDto temp = OrganizerUpSertDto.fromUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                .success(true)
                .total(1)
                .data(temp)
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrganizer(@PathVariable Integer id) {
        Optional<User> user = userService.getOrganizer(id);

        OrganizerUpSertDto temp = OrganizerUpSertDto.fromUser(user.get());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                .success(true)
                .total(1)
                .data(temp)
                .build()
        );
    }
}
