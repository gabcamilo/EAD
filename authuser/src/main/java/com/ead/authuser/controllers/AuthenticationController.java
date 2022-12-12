package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody
                                                   @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto) {
        try {
            validate(userDto);
        } catch (DuplicateKeyException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        var userModel = userDto.convertToModel();
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userModel));
    }

    private void validate(UserDto user) throws DuplicateKeyException {
        Boolean exists = userService.existsByUsername(user.getUsername());
        if(exists){
            throw new DuplicateKeyException("Error: Username is Already Taken!");
        }
        exists = userService.existsByEmail(user.getEmail());
        if(exists){
            throw new DuplicateKeyException("Error: email is Already Taken!");
        }
    }
}
