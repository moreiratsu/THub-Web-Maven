package com.alvaro.thub.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.io.PrintWriter;
import com.alvaro.thub.dao.criteria.EventCriteria;
import com.alvaro.thub.model.EventDTO;
import com.alvaro.thub.service.EventService;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.impl.EventServiceImpl;
import com.alvaro.thub.utils.Results;
import com.alvaro.thub.utils.ConstantsUtils;

/**
 * Servlet de Eventos 
 */
@WebServlet("/Event")
public class EventServlet extends HttpServlet {

	private static final long serialVersionUID = 4261821117721713160L;
	private EventService eventService = null;
	
    public EventServlet() {
        eventService = new EventServiceImpl();
    }

	/**
	 * El DOGET recibe los parámetros de la peticion
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Esto es para que el navegador devuelva texto plano como cuando intentas borrar un evento y no hay permisos en el action delete
		PrintWriter writer = response.getWriter();
		
		// Obtenemos el parámetro action de la petición y se lo enganchamos a la variable action para saber que acción realizar
		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		
		// si la action es "detail" se ejecuta el bloque de código para ver el detalle de un evento
		if ("detail".equalsIgnoreCase(action)) {
			// Action: Ver el detalle de un evento
			String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);
            
			try {
				EventDTO event = eventService.findById(Long.valueOf(idStr));
				request.setAttribute("event", event);
				request.getRequestDispatcher(ConstantsUtils.EVENT_DETAIL).forward(request, response);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
			
		} else if ("search".equalsIgnoreCase(action)) {
			// Action: Buscar eventos
			
			// 1. Lectura de parámetros de la petición
			String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);
			String name = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
            String statusIdStr = request.getParameter("statusId");
            String formatIdStr = request.getParameter("formatId");
            String userIdStr = request.getParameter("userId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String countryIdStr = request.getParameter("countryId");
            String provinceIdStr = request.getParameter("provinceId");
			String localityIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);
            
            // 2. Creamos un objeto EventCriteria para asignar los parámetros de búsqueda
			EventCriteria criteria = new EventCriteria();
			// los criterios los trimeamos para evitar espacios en blanco al inicio y al final
			if (idStr != null && !idStr.trim().isEmpty()) {
			    criteria.setId(Long.valueOf(idStr.trim()));
			}

			if (name != null && !name.trim().isEmpty()) {
			    criteria.setName(name.trim());
			}

			if (statusIdStr != null && !statusIdStr.trim().isEmpty()) {
			    criteria.setStatusId(Long.valueOf(statusIdStr.trim()));
			}

			if (formatIdStr != null && !formatIdStr.trim().isEmpty()) {
			    criteria.setFormatId(Long.valueOf(formatIdStr.trim()));
			}

			if (userIdStr != null && !userIdStr.trim().isEmpty()) {
			    criteria.setCreatorUserId(Long.valueOf(userIdStr.trim()));
			}

			// Formateador para convertir el String del input HTML (yyyy-MM-dd) a java.util.Date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			if (startDateStr != null && !startDateStr.trim().isEmpty()) {
			    try {
			        Date startDate = sdf.parse(startDateStr.trim());
			        criteria.setStartDate(startDate); // Ahora se pasa un objeto Date
			    } catch (ParseException e) {
			        e.printStackTrace();
			    }
			}

			if (endDateStr != null && !endDateStr.trim().isEmpty()) {
			    try {
			        Date endDate = sdf.parse(endDateStr.trim());
			        criteria.setEndDate(endDate); // Ahora se pasa un objeto Date
			    } catch (ParseException e) {
			        e.printStackTrace();
			    }
			}

			if (countryIdStr != null && !countryIdStr.trim().isEmpty()) {
			    criteria.setCountryId(Long.valueOf(countryIdStr.trim()));
			}

			if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
			    criteria.setProvinceId(Long.valueOf(provinceIdStr.trim()));
			}

			if (localityIdStr != null && !localityIdStr.trim().isEmpty()) {
			    criteria.setLocalityId(Long.valueOf(localityIdStr.trim()));
			}

			try {
				// 3. Ejecución de la búsqueda con el criterio construido
				Results<EventDTO> results = eventService.findBy(criteria, 1, Integer.MAX_VALUE);
				request.setAttribute("results", results);
				request.getRequestDispatcher(ConstantsUtils.EVENT_SEARCH).forward(request, response);
				
			} catch (ServiceException se) {
				se.printStackTrace();
			}
			
			
			
		}else if ("delete".equalsIgnoreCase(action)) {
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
				writer.append("<i>No ha podido borrarse el evento. Por favor contacte con su administrador</i>");
			}
		} else {
			// ninguna action		
	}
}
	
	/**
	 * EL POST se encarga de redirigir la petición al doGet y obtiene los parametros de la url
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
