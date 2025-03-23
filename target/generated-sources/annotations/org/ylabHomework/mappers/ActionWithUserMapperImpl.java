package org.ylabHomework.mappers;

import javax.annotation.processing.Generated;
import org.ylabHomework.DTOs.UserDTOs.ActionsWithUserDTO;
import org.ylabHomework.models.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-23T22:26:47+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
public class ActionWithUserMapperImpl implements ActionWithUserMapper {

    @Override
    public User toModel(ActionsWithUserDTO actionsWithUserDTO) {
        if ( actionsWithUserDTO == null ) {
            return null;
        }

        String name = null;
        String email = null;

        name = actionsWithUserDTO.getName();
        email = actionsWithUserDTO.getEmail();

        int role = 0;
        String password = null;

        User user = new User( name, email, password, role );

        return user;
    }
}
