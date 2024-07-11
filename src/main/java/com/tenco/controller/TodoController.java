package com.tenco.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.tenco.model.TodoDAO;
import com.tenco.model.TodoDAOImpl;
import com.tenco.model.TodoDTO;
import com.tenco.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// .../mvc/todo/xxx

@WebServlet("/todo/*")
public class TodoController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TodoDAO todoDAO;

	public TodoController() {
		todoDAO = new TodoDAOImpl();
	}

	// http://localhost:8080/mvc/todo/todoForm (권장x)
	// http://localhost:8080/mvc/todo/formzzz
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getPathInfo();
		// 로그인한 사용자만 접근을 허용하도록 설계
		HttpSession session = request.getSession();
		UserDTO principal = (UserDTO) session.getAttribute("principal");

		// 인증 검사
		if (principal == null) {
			// 로그인을 안한 상태
			response.sendRedirect("/mvc/user/signIn?message=invalid");
			return;
		}
		switch (action) {
		case "/todoForm":
			todoFormPage(request, response);
			break;
		case "/list":
			todoListPage(request, response, principal.getId());
			break;
		case "/detail":
			todoDetailPage(request, response, principal.getId());
			break;
		case "/delete":
			deleteTodo(request, response, principal.getId());
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
			break;
		}

	}

	/**
	 * todo 삭제 기능
	 * 
	 * @param request
	 * @param response
	 * @param principalId
	 * @throws IOException
	 */
	private void deleteTodo(HttpServletRequest request, HttpServletResponse response, int principalId)
			throws IOException {

		try {

			int todID = Integer.parseInt(request.getParameter("id"));
			todoDAO.deleteTodo(todID, principalId);
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/todo/list?error=invalid");
		}

		response.sendRedirect(request.getContextPath() + "/todo/list");
	}

	/**
	 * 상세 보기 화면
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	// http://localhost:8080/mvc/todo/detail?id=2
	private void todoDetailPage(HttpServletRequest request, HttpServletResponse response, int principalId)
			throws IOException {
		// detail?id = 8
		try {

			// todo - pk - 1 , 3 , 5 (야스오)
			// todo - pk - 2 , 4 , 6 (홍길동)
			int todoId = Integer.parseInt(request.getParameter("id"));
			TodoDTO dto = todoDAO.getTodoById(todoId);
			if (dto.getUserId() == principalId) {
				// 상세보기 화면으로 이동 처리
				request.setAttribute("todo", dto);
				request.getRequestDispatcher("/WEB-INF/views/todoDetail.jsp").forward(request, response);
			} else {
				// 권한이 없습니다 or 잘못된 접근입니다.
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script> alert('권한이 없습니다'); history.back() </script>");
			}

		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/todo/list?error=invalid");

		}

	}

	// http://localhost:8080/mvc/todo/list
	/**
	 * 사용자별 todo 리스트 화면 이동
	 * 
	 * @param request
	 * @param response
	 * @param principalId
	 * @throws IOException
	 * @throws ServletException
	 */
	private void todoListPage(HttpServletRequest request, HttpServletResponse response, int principalId)
			throws IOException, ServletException {
		List<TodoDTO> list = todoDAO.getTodosByUserId(principalId);
		request.setAttribute("list", list);
		// todoList.jsp 페이지로 내부에서 이동 처리
		request.getRequestDispatcher("/WEB-INF/views/todoList.jsp").forward(request, response);
	}

	/**
	 * todo 작성 페이지 이동
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void todoFormPage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		request.getRequestDispatcher("/WEB-INF/views/todoForm.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// todoform 페이지 바로 접근할 수 있게 !
		// TODO - 인증검사 추후 추가 처리
		HttpSession session = request.getSession();
		UserDTO principal = (UserDTO) session.getAttribute("principal");
		if (principal == null) {
			response.sendRedirect(request.getContentType() + "/user/signIn?error=invalid");
			return;
		}

		String action = request.getPathInfo();

		switch (action) {
		case "/add":
			// TODO 수정 예정
			addTodo(request, response, principal.getId());
			break;

		case "/update":
			UpdateTodo(request, response, principal.getId());
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
			break;
		}
	}
	
	/**
	 * todo 수정 기능
	 * @param request
	 * @param response
	 * @param principalId -- 세션 ID값
	 * @throws IOException
	 */
	private void UpdateTodo(HttpServletRequest request, HttpServletResponse response, int principalId)
			throws IOException {
		try {

			int todoId = Integer.parseInt(request.getParameter("id"));
			String title = request.getParameter("title");
			String description = request.getParameter("description");
			String dueDate = request.getParameter("dueDate");
			System.out.println("3333" + request.getParameter("completed"));
			boolean completed = "on".equalsIgnoreCase(request.getParameter("completed"));
			System.out.println("completed : " + completed);
			TodoDTO dto = TodoDTO.builder()
					.id(todoId)
					.userId(principalId)
					.title(title)
					.description(description)
					.dueDate(dueDate)
					.completed(String.valueOf(completed))
					.build();
			
			System.out.println("todoId : " + todoId);
			
			todoDAO.updateTodo(dto, principalId);
			
		} catch (Exception e) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script> alert('잘못된 요청입니다.'); history.back() </script>");
		}
		// list 화면 재요청 처리
		response.sendRedirect(request.getContextPath()+"/todo/list");
	}

	/**
	 * 세션별 사용자 todo 등록
	 * 
	 * @param request
	 * @param response
	 * @param principalId : 세션에 담겨 있는 UserId 값 !
	 * @throws IOException
	 */
	private void addTodo(HttpServletRequest request, HttpServletResponse response, int principalId) throws IOException {
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		String dueDate = request.getParameter("dueDate");

		// 여러개 String[] 배열로 선언 했음
		// 이번에 checkbox 하나만 사용 중
		// 체크박스가 선택되지 않았으면 null을 반환하고 체크가 되어 있다면 on으로 넘어 온다.
		boolean completed = "on".equalsIgnoreCase(request.getParameter("completed"));
		System.out.println("completed : " + completed);

		TodoDTO dto = TodoDTO.builder().userId(principalId).title(title).description(description).dueDate(dueDate)
				.completed(String.valueOf(completed)).build();
		
		response.sendRedirect(request.getContextPath() + "/todo/list");
		List<TodoDTO> list = new ArrayList<TodoDTO>();
		todoDAO.addTodo(dto, principalId);
		request.setAttribute("todoList", list);
		
	}

}
