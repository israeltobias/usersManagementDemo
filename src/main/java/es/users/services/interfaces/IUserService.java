package es.users.services.interfaces;

import java.util.List;

import es.users.dto.UserRequest;
import es.users.dto.UserResponse;
import es.users.entities.User;

public interface IUserService {

	public List<UserResponse> getAll();
	 public UserResponse createUser(UserRequest user);
}
