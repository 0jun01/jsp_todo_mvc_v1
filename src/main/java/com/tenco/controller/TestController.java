package com.tenco.controller;

import java.io.IOException;

import com.tenco.model.TodoDAO;
import com.tenco.model.TodoDAOImpl;
import com.tenco.model.TodoDTO;
import com.tenco.model.UserDAO;
import com.tenco.model.UserDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/test/*")
public class TestController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private UserDAO userDAO;
	private TodoDAO todoDAO;
	
	public TestController() {
		super();
	}

	@Override
	public void init() throws ServletException {
		userDAO = new UserDAOImpl();
		todoDAO = new TodoDAOImpl();
	}

	// http://localhost:8080/mvc/test/t1
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getPathInfo();
		
		TodoDTO todoDTO =todoDAO.getTodoById(1);
		todoDAO.updateTodo(new TodoDTO(), 1);
		todoDAO.deleteTodo(2, 1);
//		todoDAO.addTodo(todoDTO, 3);
//		List<TodoDTO> asd =todoDAO.getTodosByUserId(3);
//		List<TodoDTO> all =todoDAO.getAllTodos();
//		todoDAO.deleteTodo(3, 1);
//		System.out.println("getTodosByUserId : "+asd.toString());
		//System.out.println(all.toString());
		
//		System.out.println(todoDTO.toString());
//		System.out.println("------------------" + all);
//		switch (action) {
//		case "/byId":
			// http:// localhost:8080/mvc/test/byId
			// userDAO.getUserByUsername("홍길동");
			// List<UserDTO> list = userDAO.getAllUsers();
//			if(list.isEmpty()) {
//				
//			}
//			UserDTO dto = UserDTO.builder().password("999").email("h@naver.com").build();
//			int count = userDAO.updateUser(dto, 3);
//			System.out.println("count" + count);

//			System.out.println(userDAO.deleteUser(4));
//			break;
//		default:
//			break;
//		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}
