package org.ylabHomework.mappers;

import javax.annotation.processing.Generated;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.models.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-27T02:42:17+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
public class LoginMapperImpl implements LoginMapper {

    @Override
    public User toModel(LoginDTO loginDTO) {
        if ( loginDTO == null ) {
            return null;
        }

        String email = null;
        String password = null;

        email = loginDTO.getEmail();
        password = loginDTO.getPassword();

        String name = null;
        int role = 0;

        User user = new User( name, email, password, role );

        return user;
    }
}
