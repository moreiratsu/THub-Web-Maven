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
	*  hace una cosa u otra y usa el response para devolver la respuesta al cliente **/
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// lo primero de todo es sacar el parámetro action de la request, y el usuario de la sesión
		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		// para sacar el usuario de la sesión, usamos la clase SessionManager que es una clase de utilidad que hemos creado para manejar la sesión, y le pasamos el nombre del atributo de sesión que hemos definido en ConstantsUtils
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);
		
		/* ===========================================================================================================================================
		 *                                                   ACCION = LOGIN
		 ============================================================================================================================================*/
		if (ConstantsUtils.USER_ACTION_LOGIN.equalsIgnoreCase(action)) {
			// primero sacamos el email y la contraseña de la request, que es lo que nos ha enviado el cliente
			String email = request.getParameter(ConstantsUtils.REQUEST_PARAM_EMAIL);
			String password = request.getParameter(ConstantsUtils.REQUEST_PARAM_PASSWORD);
			// ahora llamamos al servicio de usuario para hacer el login
			try {
				 user = userService.login(email, password);
				 // si el usuario es distinto de null, entonces el login ha sido correcto, y guardamos el usuario en la sesión y redirect al index
				if (user != null) {
					SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, user);
					logger.info("El usuario "+user.getName()+" se ha logueado correctamente");
					response.sendRedirect( response.encodeRedirectURL(request.getContextPath() + ConstantsUtils.INDEX));
						return;
				} else { // si el usuario es null, entonces el login ha sido incorrecto, hacemos forward con un mensaje de error
					request.setAttribute("error", "Email o contraseña incorrectos.");
					logger.error("Error al procesar el inicio de sesión ");
					request.getRequestDispatcher(ConstantsUtils.USER_LOGIN).forward(request, response);
				}
			} catch (ServiceException se) { 
				logger.error("Error al procesar el inicio de sesión ", se);
				request.setAttribute("error", "Ocurrió un error al procesar el inicio de sesión."); // esto no es mas que un mensaje amigable de error que le vamos a pasar al JSP para que lo muestre al usuario
				request.getRequestDispatcher(ConstantsUtils.USER_LOGIN).forward(request, response); // despues del error redirigimos al login para que el usuario pueda volver a intentarlo
			}

			/* ========================================================================================================================================
			 *                                                   ACCION = LOGOUT
			 ============================================================================================================================================*/
		} else if (ConstantsUtils.USER_ACTION_LOGOUT.equalsIgnoreCase(action)) {
			try {
			// lo unico que hacemos es poner el atributo de sesión del usuario a null, y redirigimos al login
			SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, null);
			logger.info("Se ha cerrado la sesión correctamente");
			response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN); 
			} catch (Exception e) {
				logger.warn("Error al cerrar sesión: ", e);
				request.setAttribute("error", "Ocurrió un error al cerrar sesión.");
				request.getRequestDispatcher(ConstantsUtils.INDEX).forward(request, response);
			}
			/* ========================================================================================================================================
			 *                                                   ACCION = REGISTER
			 ============================================================================================================================================*/
		} else if (ConstantsUtils.USER_ACTION_REGISTER.equalsIgnoreCase(action)) {
			// sacamos los parámetros de la request (DEL FORMULARIO)
			String name = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
			String email = request.getParameter(ConstantsUtils.REQUEST_PARAM_EMAIL);
			String password = request.getParameter(ConstantsUtils.REQUEST_PARAM_PASSWORD);
			String confirmPassword = request.getParameter(ConstantsUtils.REQUEST_PARAM_CONFIRM_PASSWORD);
			
			// comprobamos que las contraseñas coinciden y que el campo contraseña no es null, si no, recargamos el registro con un mensaje de error
			if (password == null || !password.equals(confirmPassword)) {
				logger.warn("Las contraseñas no coinciden");
				request.setAttribute("error", "Las contraseñas no coinciden.");
				request.getRequestDispatcher(ConstantsUtils.USER_REGISTER).forward(request, response);
				return; 
			}
			
			// creamos un nuevo usuario con los datos que nos ha enviado el cliente, y lo registramos en la base de datos
			UserDTO newUser = new UserDTO();
			newUser.setName(name);
			newUser.setEmail(email);
			newUser.setPassword(password);
			newUser.setRolId(6L); // *IMPORTANTE* de momento el usuario al registrarse SIEMPRE va a tener el rol de FAN, el no va a elegir su rol.
				
			// en caso de que el registro sea correcto, guardamos el usuario en la sesión y redirigimos al index 
			try {
				userService.register(newUser);
				SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, newUser);
				logger.info("Registro completado de: "+newUser.getName());
				response.sendRedirect(request.getContextPath() + ConstantsUtils.INDEX);
			// si no, recargamos el registro con un mensaje de error y capturamos la excepción que nos puede lanzar el servicio de usuario
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


