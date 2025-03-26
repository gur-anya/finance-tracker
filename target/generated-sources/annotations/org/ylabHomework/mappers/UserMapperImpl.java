package org.ylabHomework.mappers;

import javax.annotation.processing.Generated;
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.models.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-26T03:13:47+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public User toModel(BasicUserDTO basicUserDTO) {
        if ( basicUserDTO == null ) {
            return null;
        }

        String name = null;
        String email = null;
        String password = null;
        int role = 0;

        name = basicUserDTO.getName();
        email = basicUserDTO.getEmail();
        password = basicUserDTO.getPassword();
        role = basicUserDTO.getRole();

        User user = new User( name, email, password, role );

        return user;
    }
}
