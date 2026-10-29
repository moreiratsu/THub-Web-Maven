package com.alvaro.thub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alvaro.thub.model.Gender;
import com.alvaro.thub.model.LocalityDTO;
import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.service.GenderService;
import com.alvaro.thub.service.LocalityService;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.UserService;
import com.alvaro.thub.service.impl.GenderServiceImpl;
import com.alvaro.thub.service.impl.LocalityServiceImpl;
import com.alvaro.thub.service.impl.UserServiceImpl;
import com.alvaro.thub.utils.Views;

/**
 * Servlet para login, logout, registro y gestión de perfil de Usuario.
 */
@WebServlet("/Usuario")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private UserService userService = null;
	private LocalityService localityService = null;
	private GenderService genderService = null;
	
	public UserServlet() {
		super();
		userService = new UserServiceImpl();
		localityService = new LocalityServiceImpl();
		genderService = new GenderServiceImpl();
	}

	/**
	 * Carga las listas de localidades y géneros desde la base de datos en el objeto request.
	 */
	private void cargarDesplegables(HttpServletRequest request) {
		try {
		//	List<LocalityDTO> localidades = localityService.findAll();
			List<Gender> generos = genderService.findAll();
			// request.setAttribute("localidades", localidades);
			request.setAttribute("generos", generos);
		} catch (ServiceException e) {
			request.setAttribute("error", "Error al cargar las listas desplegables: " + e.getMessage());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		
		// ACCIÓN: MOSTRAR PERFIL
		if ("showProfile".equalsIgnoreCase(action)) {
			UserDTO sessionUser = (UserDTO) session.getAttribute("usuario");
			if (sessionUser == null) {
				response.sendRedirect(request.getContextPath() + Views.USER_LOGIN);
				return;
			}

			cargarDesplegables(request);
			request.getRequestDispatcher("/html/user/profile.jsp").forward(request, response);
			return;

		// ACCIÓN: LOGIN
		} else if ("login".equalsIgnoreCase(action)) {
			String email = request.getParameter("email");
			String password = request.getParameter("password");

			try {
				UserDTO usuario = userService.login(email, password);

				if (usuario != null) {
					session.setAttribute("usuario", usuario);
					request.getRequestDispatcher(Views.INDEX).forward(request, response);
				} else {
					request.setAttribute("error", "Email o contraseña incorrectos.");
					request.getRequestDispatcher(Views.USER_LOGIN).forward(request, response);
				}

			} catch (ServiceException se) {
				request.setAttribute("error", "Ocurrió un error al procesar el inicio de sesión.");
				request.getRequestDispatcher(Views.USER_LOGIN).forward(request, response);
			}

		// ACCIÓN: LOGOUT
		} else if ("logout".equalsIgnoreCase(action)) {
			session.invalidate();
			response.sendRedirect(request.getContextPath() + Views.INDEX);

		// ACCIÓN: REGISTRO
		} else if ("register".equalsIgnoreCase(action)) {
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String confirmPassword = request.getParameter("confirmPassword");
			String rolIdStr = request.getParameter("rolId");

			if (password == null || !password.equals(confirmPassword)) {
				request.setAttribute("error", "Las contraseñas no coinciden.");
				request.getRequestDispatcher("/html/user/register.jsp").forward(request, response);
				return;
			}

			UserDTO newUser = new UserDTO();
			newUser.setName(name);
			newUser.setEmail(email);
			newUser.setPassword(password);

			if (rolIdStr != null && !rolIdStr.trim().isEmpty()) {
				try {
					newUser.setRolId(Long.valueOf(rolIdStr.trim()));
				} catch (NumberFormatException e) {
					request.setAttribute("error", "El rol seleccionado no es válido.");
					request.getRequestDispatcher("/html/user/register.jsp").forward(request, response);
					return;
				}
			}

			try {
				userService.register(newUser);
				session.setAttribute("usuario", newUser);
				response.sendRedirect(request.getContextPath() + Views.INDEX);
			} catch (ServiceException e) {
				request.setAttribute("error", "Error al crear el usuario: " + e.getMessage());
				request.getRequestDispatcher("/html/user/register.jsp").forward(request, response);
			}

		// ACCIÓN: ACTUALIZAR DATOS PERSONALES
		} else if ("updateProfile".equalsIgnoreCase(action)) {
			UserDTO sessionUser = (UserDTO) session.getAttribute("usuario");
			if (sessionUser == null) {
				response.sendRedirect(request.getContextPath() + Views.USER_LOGIN);
				return;
			}

			String idStr = request.getParameter("id");
			String dni = request.getParameter("dni");
			String name = request.getParameter("name");
			String lastName1 = request.getParameter("lastName1");
			String lastName2 = request.getParameter("lastName2");
			String email = request.getParameter("email");
			String phoneNumber = request.getParameter("phoneNumber");
			String bornDateStr = request.getParameter("bornDate");
			String genderIdStr = request.getParameter("genderId");
			String localityIdStr = request.getParameter("localityId");

			try {
				UserDTO userToUpdate = new UserDTO();
				userToUpdate.setId(Long.valueOf(idStr));
				userToUpdate.setDni(dni);
				userToUpdate.setName(name);
				userToUpdate.setLastName1(lastName1);
				userToUpdate.setLastName2(lastName2);
				userToUpdate.setEmail(email);
				userToUpdate.setPhoneNumber(phoneNumber);
				
				userToUpdate.setRolId(sessionUser.getRolId());
				userToUpdate.setRolName(sessionUser.getRolName());
				userToUpdate.setPassword(sessionUser.getPassword());

				if (bornDateStr != null && !bornDateStr.trim().isEmpty()) {
					try {
						Date bornDate = new SimpleDateFormat("yyyy-MM-dd").parse(bornDateStr.trim());
						userToUpdate.setBornDate(bornDate);
					} catch (ParseException pe) {
						cargarDesplegables(request);
						request.setAttribute("error", "Formato de fecha de nacimiento no válido.");
						request.getRequestDispatcher("/html/user/profile.jsp").forward(request, response);
						return;
					}
				}

				if (genderIdStr != null && !genderIdStr.trim().isEmpty()) {
					userToUpdate.setGenderId(Long.valueOf(genderIdStr.trim()));
				}

				if (localityIdStr != null && !localityIdStr.trim().isEmpty()) {
					userToUpdate.setLocalityId(Long.valueOf(localityIdStr.trim()));
				}

				boolean updated = userService.update(userToUpdate);

				if (updated) {
					UserDTO updatedUser = userService.findById(userToUpdate.getId());
					session.setAttribute("usuario", updatedUser != null ? updatedUser : userToUpdate);
					request.setAttribute("successMessage", "Perfil actualizado correctamente.");
				} else {
					request.setAttribute("error", "No se pudo actualizar el perfil.");
				}

			} catch (Exception e) {
				request.setAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
			}

			cargarDesplegables(request);
			request.getRequestDispatcher("/html/user/profile.jsp").forward(request, response);

		// ACCIÓN: ACTUALIZAR CONTRASEÑA
		} else if ("updatePassword".equalsIgnoreCase(action)) {
			UserDTO sessionUser = (UserDTO) session.getAttribute("usuario");
			if (sessionUser == null) {
				response.sendRedirect(request.getContextPath() + Views.USER_LOGIN);
				return;
			}

			String oldPassword = request.getParameter("oldPassword");
			String newPassword = request.getParameter("newPassword");
			String confirmNewPassword = request.getParameter("confirmNewPassword");

			if (newPassword == null || !newPassword.equals(confirmNewPassword)) {
				cargarDesplegables(request);
				request.setAttribute("error", "Las nuevas contraseñas no coinciden.");
				request.getRequestDispatcher("/html/user/profile.jsp").forward(request, response);
				return;
			}

			try {
				userService.updatePassword(sessionUser.getId(), oldPassword, newPassword);
				request.setAttribute("successMessage", "Contraseña actualizada con éxito.");
			} catch (ServiceException e) {
				request.setAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
			}

			cargarDesplegables(request);
			request.getRequestDispatcher("/html/user/profile.jsp").forward(request, response);
		} 
		// ACCIÓN: MOSTRAR FORMULARIO DE NUEVO EVENTO
		else if ("showNewEvent".equalsIgnoreCase(action)) {
		    // Cargar las localidades para el desplegable
		 //   List<LocalityDTO> localidades = localityService.findAll();
		  //  request.setAttribute("localidades", localidades);
		    
		    request.getRequestDispatcher("/html/event/NewEvent.jsp").forward(request, response);
		    return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}