package com.alvaro.thub.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.dao.criteria.EventCriteria;
import com.alvaro.thub.model.EventDTO;
import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.service.EventService;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.impl.EventServiceImpl;
import com.alvaro.thub.utils.ConstantsUtils;
import com.alvaro.thub.utils.Results;
import com.alvaro.thub.utils.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Servlet que maneja las peticiones privadas relacionadas con eventos.
 * - editar eventos propios del usuario
 * - eliminar eventos propios del usuario 
 * - crear eventos
 */

@WebServlet("/Private/Event")
public class PrivateEventServlet extends HttpServlet {
	
	private static Logger logger = LogManager.getLogger(PrivateEventServlet.class);
	private EventService eventService = null;
	
	public PrivateEventServlet() {
		super();
		this.eventService = new EventServiceImpl();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);

		// Accion = borrar
		if (ConstantsUtils.REQUEST_EVENT_ACTION_DELETE.equalsIgnoreCase(action)) {
			// Action: Eliminar un evento
			String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);
			try {
				eventService.delete(Long.valueOf(idStr));
				// Tras eliminar, refrescamos la lista
                Results<EventDTO> results = eventService.findBy(new EventCriteria(), 1, Integer.MAX_VALUE);
                request.setAttribute("results", results);
                request.getRequestDispatcher(ConstantsUtils.EVENT_SEARCH).forward(request, response);
			} catch (ServiceException se) {
				se.printStackTrace();
				// Esto es para que el navegador devuelva texto plano como cuando intentas borrar un evento y no hay permisos en el action delete
				PrintWriter writer = response.getWriter();
				logger.error("Error al eliminar el evento con id: " + idStr, se);
				writer.append("<i>No ha podido borrarse el evento. Por favor contacte con su administrador</i>");
			}
			
			
			
			// Accion = crear
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_CREATE.equalsIgnoreCase(action)) {
			
			// TODO: HACER LA JSP DE CREACION Y ESta accion
			
			
			
			// Accion = actualizar 
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_UPDATE.equalsIgnoreCase(action)) {
			
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
