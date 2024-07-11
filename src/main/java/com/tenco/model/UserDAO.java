package com.tenco.model;

import java.util.List;

public interface UserDAO {

	int addUser(UserDTO userDTO);
	UserDTO getUserById(int id);
	UserDTO getUserByUsername(String username);
	List<UserDTO> getAllUsers();
	int updateUser(UserDTO user, int principalId); // 권한 (마이정보 나만) - 인증 (세션ID) where id = ? ;
	int deleteUser(int id);
}
