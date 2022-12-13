package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.naming.AuthenticationException;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    //TODO: error handler
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(
            @PageableDefault(
                page=0,
                size=10,
                sort="username",
                direction = Sort.Direction.ASC)
            Pageable pageable) {

        Page<UserModel> userModelPage = userService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable(value = "userId")UUID userId) {
        UserModel user;
        try {
            user = verifyUserExists(userService.findById(userId));
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId")UUID userId) {

        UserModel user;
        try {
            user = verifyUserExists(userService.findById(userId));
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userService.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId")UUID userId,
                                             @RequestBody @Validated(UserDto.UserView.UserPut.class)
                                             @JsonView(UserDto.UserView.UserPut.class) UserDto userDto) {

        UserModel user;
        try {
            user = verifyUserExists(userService.findById(userId));
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userDto.convertToUpdateUserModel(user);
        var updatedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId")UUID userId,
                                             @RequestBody @Validated(UserDto.UserView.PasswordPut.class)
                                             @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto) {

        UserModel user;
        try {
            user = verifyUserExists(userService.findById(userId));
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            validatePassword(user.getPassword(), userDto.getOldPassword());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        userDto.convertToUpdatePasswordModel(user);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId")UUID userId,
                                                 @RequestBody @Validated(UserDto.UserView.ImagePut.class)
                                                 @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto) {

        UserModel user;
        try {
            user = verifyUserExists(userService.findById(userId));
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userDto.convertToUpdateImageModel(user);
        var updatedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    private void validatePassword (String realPassword, String inputtedPassword) throws AuthenticationException {
        if(inputtedPassword.equals(realPassword))
            throw new AuthenticationException("Error: Mismatched old password!");
    }

    private UserModel verifyUserExists (Optional<UserModel> userModelOptional) throws ChangeSetPersister.NotFoundException {
        if (userModelOptional.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        return userModelOptional.get();
    }
}
