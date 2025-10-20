package com.kusur.Kusur.mapper;

import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserToUserDetailsDto {
    @Mapping(source = "username",target = "username")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "nickname",target = "nickname")
    UserDetailsDto outUser(User user);
}
