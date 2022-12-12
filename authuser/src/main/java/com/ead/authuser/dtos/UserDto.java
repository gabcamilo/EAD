package com.ead.authuser.dtos;

import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    public interface UserView {
        interface RegistrationPost {}
        interface UserPut {}
        interface PasswordPut {}
        interface ImagePut {}

    }

    private UUID id;

    @JsonView(UserView.RegistrationPost.class)
    private String username;

    @JsonView(UserView.RegistrationPost.class)
    private String email;

    @JsonView({UserView.RegistrationPost.class, UserView.PasswordPut.class})
    private String password;

    @JsonView({UserView.PasswordPut.class})
    private String oldPassword;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String fullName;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String phoneNumber;

    @JsonView(UserView.RegistrationPost.class)
    private String cpf;

    @JsonView(UserView.ImagePut.class)
    private String imageUrl;

    public UserModel convertToModel() {
        var userModel = new UserModel(); //JDK 11+ only
        BeanUtils.copyProperties(this, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return userModel;

        //TODO: handle various user types
    }

    public void convertToUpdateUserModel(UserModel userModel) {
        userModel.setFullName(fullName);
        userModel.setPhoneNumber(phoneNumber);
        userModel.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

        //TODO: handle null field case
        //TODO: handle multiple fields error
    }

    public void convertToUpdatePasswordModel(UserModel userModel) {
        userModel.setPassword(fullName);
        userModel.setPhoneNumber(phoneNumber);
        userModel.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
    }

    public void convertToUpdateImageModel(UserModel userModel) {
        userModel.setImageUrl(imageUrl);
    }
}
