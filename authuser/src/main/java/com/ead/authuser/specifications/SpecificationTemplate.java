package com.ead.authuser.specifications;

import com.ead.authuser.models.UserModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationTemplate {

    @And({ //@Or is also available
        @Spec(path = "userType", spec = Equal.class),
        @Spec(path = "userStatus", spec = Equal.class),
        @Spec(path = "email", spec = Like.class)
        /*
         * path: which attribute we are filtering
         * spec: Equal or Like as used in SQL
         */
    })

    public interface UserSpec extends Specification<UserModel> {}
}
