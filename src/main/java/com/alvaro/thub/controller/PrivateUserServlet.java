package com.alvaro.thub.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.model.ContentData;
import com.alvaro.thub.model.ContentException;
import com.alvaro.thub.model.ContentMetadata;
import com.alvaro.thub.model.Gender;
import com.alvaro.thub.model.LocalityDTO;
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
 * Servlet que recibe las peticiones de la parte privada de la web (la que requiere que el usuario este logueado)
 */
@WebServlet("/Private/User")
public class PrivateUserServlet extends HttpServlet {
	private static Logger logger = LogManager.getLogger(PrivateUserServlet.class);
	
	private UserService userService = null;
	private LocalityService localityService = null;
	private GenderService genderService = null;
	private ContentService contentService = null;
	
	public PrivateUserServlet() {
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
			List<LocalityDTO> localidades = localityService.findAll();
			List<Gender> generos = genderService.findAll();
			request.setAttribute("localidades", localidades);
			request.setAttribute("generos", generos);
		} catch (ServiceException se) {
			logger.error("Error al cargar los desplegables", se);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		// si llega hasta aqui, es que el usuario esta logueado, ya que el filtro AuthenticationFilter se encarga de redirigir al login si no lo esta
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);
		
		// ACCIÓN: MOSTRAR PERFIL -----------------------------------------------------------------------
		if ("showProfile".equalsIgnoreCase(action)) {

			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
			return; 

				 
		// ACCIÓN: ACTUALIZAR DATOS PERSONALES -----------------------------------------------------------
		} else if ("updateProfile".equalsIgnoreCase(action)) {
			
			// pilla los valores que el usuario meta en el formulario de perfil
			String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);
			String dni = request.getParameter(ConstantsUtils.REQUEST_PARAM_DNI);
			String name = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
			String lastName1 = request.getParameter(ConstantsUtils.REQUEST_PARAM_LAST_NAME_1);
			String lastName2 = request.getParameter(ConstantsUtils.REQUEST_PARAM_LAST_NAME_2);
			String email = request.getParameter(ConstantsUtils.REQUEST_PARAM_EMAIL);
			String phoneNumber = request.getParameter(ConstantsUtils.REQUEST_PARAM_PHONE);
			String bornDateStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_BORN_DATE);
			String genderIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_GENDER_ID);
			String localityIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

			try {
				// crea un nuevo objeto UserDTO con los datos actualizados
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
				
				// como en la base de datos y el dto de usuario la variable de fecha es de tipo date
				// tenemos que facer este paso intermedio de convertir el string que viene del formulario
				// a un objeto date, para eso usamos SimpleDateFormat
				if (bornDateStr != null && !bornDateStr.trim().isEmpty()) {
					try {
						Date bornDate = new SimpleDateFormat("yyyy-MM-dd").parse(bornDateStr.trim());
						userToUpdate.setBornDate(bornDate);
					} catch (ParseException pe) {
						cargarDesplegables(request);
						logger.error("Formato de fecha de nacimiento no válido: " + bornDateStr, pe);
						request.setAttribute("error", "Formato de fecha de nacimiento no válido.");
						request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
						return;
					}
				}
				
				// como genderId y localityId son opcionales, si no vienen en la request, no los seteamos en el objeto userToUpdate
				if (genderIdStr != null && !genderIdStr.trim().isEmpty()) {
					userToUpdate.setGenderId(Long.valueOf(genderIdStr.trim()));
				}

				if (localityIdStr != null && !localityIdStr.trim().isEmpty()) {
					userToUpdate.setLocalityId(Long.valueOf(localityIdStr.trim()));
				}

				boolean updated = userService.update(userToUpdate);

				if (updated) {
				    // Actualizamos el objeto en la sesión con los nuevos datos para que la vista los refleje al instante
				    SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, userToUpdate);
				    logger.info("Perfil de usuario actualizado correctamente: " + userToUpdate.getId());
				    request.setAttribute("successMessage", "Perfil actualizado con éxito.");
				}

			} catch (ServiceException se) {
			    logger.error("Error al actualizar el perfil para el ID: " + idStr, se);
			    request.setAttribute("error", "Error al actualizar el perfil: " + se.getMessage());
			}

			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);

			
			
		// ACCIÓN: ACTUALIZAR CONTRASEÑA ----------------------------------------------------------------
		} else if ("updatePassword".equalsIgnoreCase(action)) {
			
			String oldPassword = request.getParameter(ConstantsUtils.REQUEST_PARAM_OLD_PASSWORD);
			String newPassword = request.getParameter(ConstantsUtils.REQUEST_PARAM_NEW_PASSWORD);
			String confirmNewPassword = request.getParameter(ConstantsUtils.REQUEST_PARAM_CONFIRM_NEW_PASSWORD);
			
			// si la contraseña nueva y la confirmacion no coinciden, mostramos un error y volvemos a la pagina de perfil
			if (newPassword == null || !newPassword.equals(confirmNewPassword)) {
				cargarDesplegables(request);
				logger.warn("Las nuevas contraseñas no coinciden para el usuario: " + user.getId());
				request.setAttribute("error", "Las nuevas contraseñas no coinciden.");
				request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
				return;
			}
			try {
				userService.updatePassword(user.getId(), oldPassword, newPassword);
				logger.info("Contraseña actualizada correctamente para el usuario: " + user.getId());
				request.setAttribute("successMessage", "Contraseña actualizada con éxito.");
			} catch (ServiceException se) {
				logger.error("Error al cambiar la contraseña para el usuario: " + user.getId(), se);
				request.setAttribute("error", "Error al cambiar la contraseña: " + se.getMessage());
			}
			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
		} 
		
		
		// ACCIÓN: SUBIR FOTO DE PERFIL -------------------------------------------------------------------
		else if ("uploadProfilePhoto".equalsIgnoreCase(action)) {
			
			// Leemos los bytes de la foto desde la request usando el método leerBytes de WebUtils
			byte[] bytesFoto = WebUtils.leerBytes(request, "photo");
			
			// si no selecciona nada, enviamos un mensaje de información y volvemos a la página de perfil
			if (bytesFoto == null || bytesFoto.length == 0) {
				cargarDesplegables(request);
				logger.info("No se seleccionó ninguna foto");
				request.setAttribute("error", "No se seleccionó ninguna foto.");
				request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
				return;
			}
			try {
		        // Guardamos la foto en el servicio de contenido
		        ContentData c = new ContentData();
		        ContentMetadata cm = new ContentMetadata();
		        cm.setEntityType(ConstantsUtils.USER_ENTITY_TYPE);
		        cm.setEntityId(user.getId().toString()); // El ID del usuario logueado
		        cm.setResourceType(ConstantsUtils.USER_PROFILE_RESOURCE_TYPE);
		        cm.setFormat(".png"); // TODO: extraer la extensión real en el futuro si lo necesitas
		        
		        c.setMetadata(cm);
		        c.setRaw(bytesFoto);
		        contentService.save(c);
		        
		        SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, user);
		        
		        logger.info("Foto de perfil guardada correctamente para el usuario: " + user.getId());
		        request.setAttribute("successMessage", "Foto de perfil actualizada con éxito."); // Mensaje de éxito successMessage

		    } catch (ServiceException se) {
		        logger.error("Error en el servicio al guardar la foto de perfil para el usuario: " + user.getId(), se);
		        request.setAttribute("error", "Error al guardar la foto de perfil: " + se.getMessage());
		    } catch (Exception e) {
		        logger.error("Error inesperado al procesar la foto de perfil para el usuario: " + user.getId(), e);
		        request.setAttribute("error", "Error inesperado al procesar la imagen.");
		    }		
			
			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.USER_PROFILE).forward(request, response);
			
			
			
		} else if ("showProfilePhoto".equalsIgnoreCase(action)) {
		    // ACCION = mostrar la foto de perfil del usuario ----------------------------------------------
			// primero creamos un objeto ContentMetadata con los metadatos de la imagen de usuario 
			ContentMetadata metadata = new ContentMetadata();
			metadata.setEntityType(ConstantsUtils.USER_ENTITY_TYPE);
			metadata.setEntityId(user.getId().toString());
			metadata.setResourceType(ConstantsUtils.USER_PROFILE_RESOURCE_TYPE);
			metadata.setFormat(".png");
			
			try {
			    ContentData foto = contentService.get(metadata); // Obtiene la foto de perfil del usuario desde el servicio de contenido
			    byte[] bytes = foto.getRaw(); // Obtiene los bytes de la foto

			    response.setContentType("image/png");
			    response.getOutputStream().write(bytes);
			    response.getOutputStream().flush();
			    
			} catch (ContentException ce) {
				logger.error("Error al obtener la foto de perfil para el usuario: " + user.getId(), ce);
			    response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			return;		
		
		}
	}

	
	
	// este metodo se llama cuando el usuario hace un POST, y lo que hace es llamar al doGet, para que se ejecute el mismo codigo que en el doGet
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	
}
