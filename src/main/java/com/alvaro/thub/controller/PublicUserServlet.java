package com.alvaro.thub.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.UserService;
import com.alvaro.thub.service.impl.UserServiceImpl;
import com.alvaro.thub.utils.ConstantsUtils;
import com.alvaro.thub.utils.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet que recibe las peticiones de la parte pública de la web (la que no requiere que el usuario este logueado)
 */

@WebServlet("/Public/User")
public class PublicUserServlet extends HttpServlet {
	private static Logger logger = LogManager.getLogger(PublicUserServlet.class);
	
	private UserService userService = null;
	
	public PublicUserServlet() {
		super();
		userService = new UserServiceImpl();
	}

	/* el doGet se encarga de recibir las peticiones GET (y las POST ya que el doPost redirige a doGet)
	* y dependiendo del parámetro action(que saca de la request(peticion)),
	*  hace una cosa u otra y usa el response para devolver 
	* la respuesta al cliente
	**/
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// lo primero de todo es sacar el parámetro action de la request, y el usuario de la sesión
		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		// para sacar el usuario de la sesión, usamos la clase SessionManager que es una clase de utilidad que hemos creado para manejar la sesión, y le pasamos el nombre del atributo de sesión que hemos definido en ConstantsUtils
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);
		
		
		// imaginemos que la acción es "login", entonces entramos en este if, y hacemos el login del usuario
		// ACCIÓN = LOGIN -----------------------------------------------------------------------
		if ("login".equalsIgnoreCase(action)) {
			// primero sacamos el email y la contraseña de la request, que es lo que nos ha enviado el cliente
			String email = request.getParameter(ConstantsUtils.REQUEST_PARAM_EMAIL);
			String password = request.getParameter(ConstantsUtils.REQUEST_PARAM_PASSWORD);
			// ahora llamamos al servicio de usuario para hacer el login
			try {
				 user = userService.login(email, password);
				 // si el usuario es distinto de null, entonces el login ha sido correcto, y guardamos el usuario en la sesión y redirigimos al index
				if (user != null) {
					SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, user);
					// recordemos que request.foward va por dentro del servidor, y response.sendRedirect va por fuera, es decir, el cliente hace otra petición al servidor
					request.getRequestDispatcher(ConstantsUtils.INDEX).forward(request, response);
				} else { // si el usuario es null, entonces el login ha sido incorrecto, y redirigimos al login con un mensaje de error
					request.setAttribute("error", "Email o contraseña incorrectos.");
					request.getRequestDispatcher(ConstantsUtils.USER_LOGIN).forward(request, response);
				}

			} catch (ServiceException se) { // ahora capturamos la excepción que nos puede lanzar el servicio de usuario, y redirigimos al login con un mensaje de error
				// eso si ponemos un logger para que nos deje un rastro en el log de lo que ha pasado para poder depurarlo y saber que ha pasado
				logger.error("Error al procesar el inicio de sesión ", se);
				request.setAttribute("error", "Ocurrió un error al procesar el inicio de sesión."); // esto no es mas que un mensaje amigable de error que le vamos a pasar al JSP para que lo muestre al usuario
				request.getRequestDispatcher(ConstantsUtils.USER_LOGIN).forward(request, response); // despues del error redirigimos al login para que el usuario pueda volver a intentarlo
			}

		// ACCIÓN = LOGOUT -------------------------------------------------------------------------------------
		} else if ("logout".equalsIgnoreCase(action)) {
			try {
			// lo unico que hacemos es poner el atributo de sesión del usuario a null, y redirigimos al index
			SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, null);
			logger.info("El usuario ha cerrado sesión correctamente.");
			response.sendRedirect(request.getContextPath() + ConstantsUtils.INDEX); // como ves aquí usamos sendRedirect, porque queremos que el cliente haga otra petición al servidor, y no queremos que el servidor haga un forward interno
			} catch (Exception e) {
				logger.warn("Error al cerrar sesión: ", e);
				request.setAttribute("error", "Ocurrió un error al cerrar sesión.");
				request.getRequestDispatcher(ConstantsUtils.INDEX).forward(request, response);
			}
		// ACCIÓN = REGISTRO --------------------------------------------------------------------------------
		} else if ("register".equalsIgnoreCase(action)) {
			// sacamos los parámetros de la request, que son los que nos ha enviado el cliente
			String name = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
			String email = request.getParameter(ConstantsUtils.REQUEST_PARAM_EMAIL);
			String password = request.getParameter(ConstantsUtils.REQUEST_PARAM_PASSWORD);
			String confirmPassword = request.getParameter(ConstantsUtils.REQUEST_PARAM_CONFIRM_PASSWORD);
			String rolIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ROL_ID);
			
			// comprobamos que las contraseñas coinciden y que el campo contraseña no es null, si no , redirigimos al registro con un mensaje de error
			if (password == null || !password.equals(confirmPassword)) {
				logger.info("Las contraseñas no coinciden: password=" + password + ", confirmPassword=" + confirmPassword);
				request.setAttribute("error", "Las contraseñas no coinciden.");
				request.getRequestDispatcher(ConstantsUtils.USER_REGISTER).forward(request, response);
				return; // este return es importante, porque si no lo ponemos, el código sigue ejecutándose y se intenta registrar el usuario aunque las contraseñas no coincidan, y eso no es lo que queremos
			}
			
			// creamos un nuevo usuario con los datos que nos ha enviado el cliente, y lo registramos en la base de datos
			UserDTO newUser = new UserDTO();
			newUser.setName(name);
			newUser.setEmail(email);
			newUser.setPassword(password);
			
			// si el rolIdStr no es null ni vacío, entonces lo convertimos a Long y lo asignamos al nuevo usuario, si no, redirigimos al registro con un mensaje de error
			if (rolIdStr != null && !rolIdStr.trim().isEmpty()) {
				try {
					newUser.setRolId(Long.valueOf(rolIdStr.trim()));
				} catch (NumberFormatException e) {
					logger.error("El rolId no es un número válido: " + rolIdStr, e);
					request.setAttribute("error", "El rol seleccionado no es válido.");
					request.getRequestDispatcher(ConstantsUtils.USER_REGISTER).forward(request, response);
					return;
				}
			}
			
			// en caso de que el registro sea correcto, guardamos el usuario en la sesión y redirigimos al index, si no, redirigimos al registro con un mensaje de error y capturamos la excepción que nos puede lanzar el servicio de usuario
			try {
				userService.register(newUser);
				SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, newUser);
				response.sendRedirect(request.getContextPath() + ConstantsUtils.INDEX);
			} catch (ServiceException se) {
				logger.error("Error al crear el usuario: ", se);
				request.setAttribute("error", "Error al crear el usuario: ");
				request.getRequestDispatcher(ConstantsUtils.USER_REGISTER).forward(request, response);
			}

		
		} 
	}
		


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	}


