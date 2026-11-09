package com.alvaro.thub.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.dao.criteria.EventCriteria;
import com.alvaro.thub.model.Country;
import com.alvaro.thub.model.EventDTO;
import com.alvaro.thub.model.Format;
import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.service.ContentService;
import com.alvaro.thub.service.CountryService;
import com.alvaro.thub.service.EventService;
import com.alvaro.thub.service.FormatService;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.impl.ContentServiceFileImpl;
import com.alvaro.thub.service.impl.CountryServiceImpl;
import com.alvaro.thub.service.impl.EventServiceImpl;
import com.alvaro.thub.service.impl.FormatServiceImpl;
import com.alvaro.thub.utils.ConstantsUtils;
import com.alvaro.thub.utils.Results;
import com.alvaro.thub.utils.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import com.alvaro.thub.model.LocalityDTO;
import com.alvaro.thub.model.ProvinceDTO;
import com.alvaro.thub.service.LocalityService;
import com.alvaro.thub.service.ProvinceService;
import com.alvaro.thub.service.impl.LocalityServiceImpl;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import com.alvaro.thub.model.Event;
import com.alvaro.thub.model.Status;
import com.alvaro.thub.service.StatusService;
import com.alvaro.thub.service.impl.StatusServiceImpl;
/*
 * Servlet que maneja las peticiones privadas relacionadas con eventos.
 * - editar eventos propios del usuario
 * - eliminar eventos propios del usuario 
 * - crear eventos
 */
import com.alvaro.thub.service.impl.ProvinceServiceImpl;

@WebServlet("/Private/Event")
public class PrivateEventServlet extends HttpServlet {
	
	private static Logger logger = LogManager.getLogger(PrivateEventServlet.class);
	private ContentService contentService = null;
	private EventService eventService = null;
	private LocalityService localityService = null;
	private ProvinceService provinceService = null;
	private CountryService countryService = null;
	private FormatService formatService = null;
	private StatusService statusService = null;

	
	public PrivateEventServlet() {
		super();
		this.eventService = new EventServiceImpl();
		this.localityService = new LocalityServiceImpl();
		this.provinceService = new ProvinceServiceImpl();
		this.countryService = new CountryServiceImpl();
		this.contentService = new ContentServiceFileImpl();
		this.formatService = new FormatServiceImpl();
		this.statusService = new StatusServiceImpl();

	}
	private void cargarDesplegables(HttpServletRequest request) {
		try {
			List<Format> formatList = formatService.findAll();
			List<Country> paises = countryService.findAll();

			String countryIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);

			Long countryId = null;
			if (countryIdStr != null && !countryIdStr.trim().isEmpty()) {
				countryId = Long.valueOf(countryIdStr.trim());
			}

			String provinceIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);

			Long provinceId = null;
			if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
				provinceId = Long.valueOf(provinceIdStr.trim());
			}

			List<ProvinceDTO> provincias = provinceService.findByCountry(countryId);

			List<LocalityDTO> localidades = localityService.findByProvinceId(provinceId);
			
			request.setAttribute("formatos", formatList);
			request.setAttribute("paises", paises);
			request.setAttribute("provincias", provincias);
			request.setAttribute("localidades", localidades);

		} catch (ServiceException se) {
			logger.error("Error al cargar los desplegables", se);
		}
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);
		
		// accion = mostrar vista de creacion de evento
		if (ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_CREATE.equalsIgnoreCase(action)) {
		    
			cargarDesplegables(request);

		    request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		           .forward(request, response);
		    return;
		}
		// Accion = borrar
		else if (ConstantsUtils.REQUEST_EVENT_ACTION_DELETE.equalsIgnoreCase(action)) {
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
			
			Event nuevoEvento = new Event();
			
			String fechaInicioTexto =
		            request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);
			String fechaFinTexto =
			        request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);

		    if (fechaInicioTexto == null || fechaInicioTexto.isBlank()) {
		        request.setAttribute("error", "Debes indicar la fecha de inicio.");
		        cargarDesplegables(request);
		        request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		               .forward(request, response);
		        return;
		    }

		    try {
		        LocalDate fechaInicio = LocalDate.parse(fechaInicioTexto.trim());

		        if (fechaInicio.isBefore(LocalDate.now())) {
		            request.setAttribute(
		                "error",
		                "La fecha de inicio no puede estar en el pasado."
		            );
		            cargarDesplegables(request);
		            request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                   .forward(request, response);
		            return;
		        }
		        if (fechaFinTexto == null || fechaFinTexto.isBlank()) {
		            request.setAttribute("error", "Debes indicar la fecha de fin.");
		            cargarDesplegables(request);
		            request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                   .forward(request, response);
		            return;
		        }

		        LocalDate fechaFin = LocalDate.parse(fechaFinTexto.trim());

		        if (fechaFin.isBefore(fechaInicio)) {
		            request.setAttribute(
		                "error",
		                "La fecha de fin no puede ser anterior a la fecha de inicio."
		            );
		            cargarDesplegables(request);
		            request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                   .forward(request, response);
		            return;
		        }
		        
		        String nombreEvento =
		                request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);

		        String formatoIdTexto =
		                request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);

		        String paisIdTexto =
		                request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);

		        String provinciaIdTexto =
		                request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);

		        String localidadIdTexto =
		                request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

		        if (nombreEvento == null || nombreEvento.isBlank()
		                || formatoIdTexto == null || formatoIdTexto.isBlank()
		                || paisIdTexto == null || paisIdTexto.isBlank()
		                ) {
		        	// dejo que los unicos campos que puedan ser null sean provincia y localidad porque puede ser un evento nacional, privicial o local
		            request.setAttribute("error", "Debes completar todos los campos del evento.");
		            cargarDesplegables(request);
		            request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                   .forward(request, response);
		            return;
		        }
		        
		        try {	            

		            nuevoEvento.setName(nombreEvento.trim());
		            nuevoEvento.setStartDate(java.sql.Date.valueOf(fechaInicio));
		            nuevoEvento.setEndDate(java.sql.Date.valueOf(fechaFin));

		            nuevoEvento.setFormatId(Long.valueOf(formatoIdTexto));
		            nuevoEvento.setCountryId(Long.valueOf(paisIdTexto));
		            
		            // si en el formulario provincia o locality vienen vacios el servlet los convierte a null 
		            Long provinciaId = null;
		            if (provinciaIdTexto != null && !provinciaIdTexto.isBlank()) {
		                provinciaId = Long.valueOf(provinciaIdTexto);
		            }

		            Long localidadId = null;
		            if (localidadIdTexto != null && !localidadIdTexto.isBlank()) {
		                localidadId = Long.valueOf(localidadIdTexto);
		            }

		            nuevoEvento.setProvinceId(provinciaId);
		            nuevoEvento.setLocalityId(localidadId);

		            // El organizador sale de la sesión, no de un campo del formulario.
		            nuevoEvento.setCreatorUserId(user.getId());
		            
		            // para darle un estado tenemos que comprobar y tener en cuenta varias reglas:
		            /*
		             * las reglas::
		             * al crear se asignará Programado si empieza en una fecha futura
		             * En curso si empieza hoy o cuando la fecha actual sea la inicial de un evento.
		             *  Cancelado se asignará desde acción de edición. 
		             *  La transición automática a Finalizado sera cuando la fecha de fin sea igual a la fecha actual.
		             */
		            String nombreEstadoInicial;

		            if (fechaInicio.isAfter(LocalDate.now())) {
		                nombreEstadoInicial = "Programado";
		            } else {
		                // Si empieza hoy, y las fechas ya están validadas, está en curso.
		                nombreEstadoInicial = "En curso";
		            }

		            List<Status> estadosEncontrados =
		                    statusService.findByName(nombreEstadoInicial);

		            Status estadoInicial = null;

		            if (estadosEncontrados != null) {
		                for (Status estado : estadosEncontrados) {
		                    if (estado.getName() != null
		                            && estado.getName().trim().equalsIgnoreCase(nombreEstadoInicial)) {
		                        estadoInicial = estado;
		                        break;
		                    }
		                }
		            }

		            if (estadoInicial == null) {
		                request.setAttribute(
		                    "error",
		                    "No se encontró el estado inicial " + nombreEstadoInicial + "."
		                );
		                cargarDesplegables(request);
		                request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                       .forward(request, response);
		                return;
		            }

		            nuevoEvento.setStatusId(estadoInicial.getId());
		            
		            // En el siguiente paso asignaremos el estado y llamaremos al servicio.
		        } catch (NumberFormatException e) {
		            request.setAttribute("error", "Alguna selección del formulario no es válida.");
		            cargarDesplegables(request);
		            request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                   .forward(request, response);
		            return;
		        }
		        catch (ServiceException se) {
		            logger.error("No se pudo buscar el estado inicial del evento", se);
		            request.setAttribute("error", "No se pudo determinar el estado inicial.");
		            cargarDesplegables(request);
		            request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                   .forward(request, response);
		            return;
		        }
		        
		        // La fecha es válida.
		        // En el siguiente paso añadiremos aquí la creación del evento.

		    } catch (DateTimeParseException e) {
		    	request.setAttribute("error", "Una de las fechas no tiene un formato válido.");
		        cargarDesplegables(request);
		        request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		               .forward(request, response);
		        return;
		    }
			
		    try {
		        EventDTO eventoCreado = eventService.create(nuevoEvento);

		        if (eventoCreado == null || eventoCreado.getId() == null) {
		            request.setAttribute("error", "No se pudo crear el evento.");
		            cargarDesplegables(request);
		            request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		                   .forward(request, response);
		            return;
		        }

		        String urlDetalle =
		                request.getContextPath()
		                + ConstantsUtils.PUBLIC_EVENT_SERVLET
		                + "?"
		                + ConstantsUtils.REQUEST_PARAM_ACTION
		                + "="
		                + ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL
		                + "&"
		                + ConstantsUtils.REQUEST_PARAM_ID
		                + "="
		                + eventoCreado.getId();

		        response.sendRedirect(urlDetalle);
		        return;

		    } catch (ServiceException se) {
		        logger.error("Error al crear el evento", se);
		        request.setAttribute("error", "No se pudo guardar el evento.");
		        cargarDesplegables(request);
		        request.getRequestDispatcher(ConstantsUtils.EVENT_NEW)
		               .forward(request, response);
		        return;
		    }
			
			// Accion = actualizar 
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_UPDATE.equalsIgnoreCase(action)) {
			
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
