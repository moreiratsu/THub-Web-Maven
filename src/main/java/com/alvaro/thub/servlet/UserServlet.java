package com.alvaro.thub.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.model.ContentData;
import com.alvaro.thub.model.ContentMetadata;
import com.alvaro.thub.model.Gender;
import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.service.ContentService;
import com.alvaro.thub.service.GenderService;
import com.alvaro.thub.service.LocalityService;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.UserService;
import com.alvaro.thub.service.impl.ContentServiceFileImpl;
import com.alvaro.thub.service.impl.GenderServiceImpl;
import com.alvaro.thub.service.impl.LocalityServiceImpl;
import com.alvaro.thub.service.impl.UserServiceImpl;
import com.alvaro.thub.utils.ConstantsUtils;
import com.alvaro.thub.utils.SessionManager;
import com.alvaro.thub.utils.WebUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para login, logout, registro y gestión de perfil de Usuario.
 */
@WebServlet("/Usuario")
public class UserServlet extends HttpServlet {
	private static Logger logger = LogManager.getLogger(UserServlet.class);
	
	private UserService userService = null;
	private LocalityService localityService = null;
	private GenderService genderService = null;
	private ContentService contentService = null;
	
	public UserServlet() {
		super();
		userService = new UserServiceImpl();
		localityService = new LocalityServiceImpl();
		genderService = new GenderServiceImpl();
		contentService = new ContentServiceFileImpl();
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
		
		request.getParameterMap().forEach((key, value) -> {
		    System.out.println("Parameter Name: " + key + ", Value: " + String.join(", ", value));
		});
		
		String action = request.getParameter("action");
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);
		
		// ACCIÓN: MOSTRAR PERFIL
		// todo: hacerlo en la parte de servlet provada
		if ("showProfile".equalsIgnoreCase(action)) {
			if (user == null) {
				response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN);
				return;
			}

			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
			return;

		// ACCIÓN: LOGIN
		} else if ("login".equalsIgnoreCase(action)) {
			String email = request.getParameter("email");
			String password = request.getParameter("password");

			try {
				UserDTO usuario = userService.login(email, password);

				if (usuario != null) {
					SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, usuario);
					request.getRequestDispatcher(ConstantsUtils.INDEX).forward(request, response);
				} else {
					request.setAttribute("error", "Email o contraseña incorrectos.");
					request.getRequestDispatcher(ConstantsUtils.USER_LOGIN).forward(request, response);
				}

			} catch (ServiceException se) {
				request.setAttribute("error", "Ocurrió un error al procesar el inicio de sesión.");
				request.getRequestDispatcher(ConstantsUtils.USER_LOGIN).forward(request, response);
			}

		// ACCIÓN: LOGOUT
		} else if ("logout".equalsIgnoreCase(action)) {
			SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, null);
			response.sendRedirect(request.getContextPath() + ConstantsUtils.INDEX);

		// ACCIÓN: REGISTRO
		} else if ("register".equalsIgnoreCase(action)) {
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String confirmPassword = request.getParameter("confirmPassword");
			String rolIdStr = request.getParameter("rolId");

			if (password == null || !password.equals(confirmPassword)) {
				request.setAttribute("error", "Las contraseñas no coinciden.");
				request.getRequestDispatcher(ConstantsUtils.USER_REGISTER).forward(request, response);
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
					request.getRequestDispatcher(ConstantsUtils.USER_REGISTER).forward(request, response);
					return;
				}
			}

			try {
				userService.register(newUser);
				SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, newUser);
				response.sendRedirect(request.getContextPath() + ConstantsUtils.INDEX);
			} catch (ServiceException e) {
				request.setAttribute("error", "Error al crear el usuario: " + e.getMessage());
				request.getRequestDispatcher(ConstantsUtils.USER_REGISTER).forward(request, response);
			}

		// ACCIÓN: ACTUALIZAR DATOS PERSONALES
		} else if ("updateProfile".equalsIgnoreCase(action)) {
			
			if (user == null) {
				response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN);
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
				
				userToUpdate.setRolId(user.getRolId());
				userToUpdate.setRolName(user.getRolName());
				userToUpdate.setPassword(user.getPassword());

				if (bornDateStr != null && !bornDateStr.trim().isEmpty()) {
					try {
						Date bornDate = new SimpleDateFormat("yyyy-MM-dd").parse(bornDateStr.trim());
						userToUpdate.setBornDate(bornDate);
					} catch (ParseException pe) {
						cargarDesplegables(request);
						request.setAttribute("error", "Formato de fecha de nacimiento no válido.");
						request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
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
					// TODO: Mensajes de error
				}

			} catch (Exception e) {
				request.setAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
			}

			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);

		// ACCIÓN: ACTUALIZAR CONTRASEÑA
			// TODO ACTUALIZAR
		} else if ("updatePassword".equalsIgnoreCase(action)) {
			
			if (user == null) {
				response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN);
				return;
			}

			String oldPassword = request.getParameter("oldPassword");
			String newPassword = request.getParameter("newPassword");
			String confirmNewPassword = request.getParameter("confirmNewPassword");

			if (newPassword == null || !newPassword.equals(confirmNewPassword)) {
				cargarDesplegables(request);
				request.setAttribute("error", "Las nuevas contraseñas no coinciden.");
				request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
				return;
			}

			try {
				userService.updatePassword(user.getId(), oldPassword, newPassword);
				request.setAttribute("successMessage", "Contraseña actualizada con éxito.");
			} catch (ServiceException e) {
				request.setAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
			}

			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
		} 
		// ACCIÓN: MOSTRAR FORMULARIO DE NUEVO EVENTO
		else if ("showNewEvent".equalsIgnoreCase(action)) {
		    // Cargar las localidades para el desplegable
		 //   List<LocalityDTO> localidades = localityService.findAll();
		  //  request.setAttribute("localidades", localidades);
		    
		    request.getRequestDispatcher("/html/event/NewEvent.jsp").forward(request, response);
		    return;
		}
		// ACCIÓN: SUBIR FOTO DE PERFIL
		else if ("uploadProfilePhoto".equalsIgnoreCase(action)) {
			if (logger.isInfoEnabled()) {
				logger.info("Guardando foto de perfil de " + user.getId());
			}

			byte[] bytesFoto = WebUtils.leerBytes(request, "photo");

			ContentData c = new ContentData();
			ContentMetadata cm = new ContentMetadata();
			cm.setEntityType(ConstantsUtils.USER_ENTITY_TYPE);
			cm.setEntityId(user.getId().toString());
			cm.setResourceType(ConstantsUtils.USER_PROFILE_RESOURCE_TYPE);
			cm.setFormat(".jpg");

			c.setMetadata(cm);
			c.setRaw(bytesFoto);
			contentService.save(c);

			if (logger.isInfoEnabled()) {
				logger.info("Foto de perfil de " + user.getId() + " guardada.");
			}

			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	
}
