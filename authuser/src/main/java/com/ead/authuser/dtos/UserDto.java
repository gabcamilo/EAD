package com.ead.authuser.dtos;

import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.validation.UsernameConstraint;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    @NotBlank(groups = UserView.RegistrationPost.class)
    @Size(min=4, max=50, groups = UserView.RegistrationPost.class)
    @JsonView(UserView.RegistrationPost.class)
    @UsernameConstraint(groups = UserView.RegistrationPost.class)
    private String username;

    @NotBlank(groups = UserView.RegistrationPost.class)
    @Email
    @JsonView(UserView.RegistrationPost.class)
    private String email;

    @NotBlank(groups = {UserView.RegistrationPost.class, UserView.PasswordPut.class})
    @Size(min=4, max=20, groups = {UserView.RegistrationPost.class, UserView.PasswordPut.class})
    @JsonView({UserView.RegistrationPost.class, UserView.PasswordPut.class})
    private String password;

    @NotBlank(groups = UserView.PasswordPut.class)
    @Size(min=4, max=50, groups = UserView.PasswordPut.class)
    @JsonView({UserView.PasswordPut.class})
    private String oldPassword;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String fullName;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String phoneNumber;

    @JsonView(UserView.RegistrationPost.class)
    private String cpf;

    @NotBlank(groups = UserView.ImagePut.class)
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
