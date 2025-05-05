package es.users.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import es.users.dto.UserRequest;
import es.users.entities.User;
import es.users.records.UserResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "nif", target = "nif")
    @Mapping(source = "email", target = "email")
    User userRequestToUser(UserRequest userRequest);


    @Mapping(source = "name", target = "nombre")
    @Mapping(source = "nif", target = "nif")
    @Mapping(source = "email", target = "email")
    UserResponse userToUserResponse(User user);


    List<UserResponse> listUserToListUserResponse(List<User> users);
}
