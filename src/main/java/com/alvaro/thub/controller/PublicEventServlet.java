package com.alvaro.thub.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.dao.criteria.EventCriteria;
import com.alvaro.thub.model.ContentData;
import com.alvaro.thub.model.ContentMetadata;
import com.alvaro.thub.model.Country;
import com.alvaro.thub.model.EventDTO;
import com.alvaro.thub.model.Format;
import com.alvaro.thub.model.LocalityDTO;
import com.alvaro.thub.model.ProvinceDTO;
import com.alvaro.thub.model.Status;
import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.dao.criteria.UserCriteria;
import com.alvaro.thub.service.ContentService;
import com.alvaro.thub.service.CountryService;
import com.alvaro.thub.service.EventService;
import com.alvaro.thub.service.FormatService;
import com.alvaro.thub.service.LocalityService;
import com.alvaro.thub.service.ProvinceService;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.StatusService;
import com.alvaro.thub.service.UserService;
import com.alvaro.thub.service.impl.ContentServiceFileImpl;
import com.alvaro.thub.service.impl.CountryServiceImpl;
import com.alvaro.thub.service.impl.EventServiceImpl;
import com.alvaro.thub.service.impl.FormatServiceImpl;
import com.alvaro.thub.service.impl.LocalityServiceImpl;
import com.alvaro.thub.service.impl.ProvinceServiceImpl;
import com.alvaro.thub.service.impl.StatusServiceImpl;
import com.alvaro.thub.service.impl.UserServiceImpl;
import com.alvaro.thub.utils.ConstantsUtils;
import com.alvaro.thub.utils.Results;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Public/Event")
public class PublicEventServlet extends HttpServlet {

	private static Logger logger = LogManager.getLogger(PublicEventServlet.class);
	private UserService userService = null;
	private EventService eventService = null;
	private StatusService statusService = null;
	private FormatService formatService = null;
	private LocalityService localityService = null;
	private ProvinceService provinceService = null;
	private CountryService countryService = null;
	private ContentService contentService = null;

	public PublicEventServlet() {
		super();
		this.userService = new UserServiceImpl();
		this.eventService = new EventServiceImpl();
		this.statusService = new StatusServiceImpl();
		this.formatService = new FormatServiceImpl();
		this.localityService = new LocalityServiceImpl();
		this.provinceService = new ProvinceServiceImpl();
		this.countryService = new CountryServiceImpl();
		this.contentService = new ContentServiceFileImpl();
	}

	/**
	 * Carga las listas de localidades y combobox desde la base de datos en el
	 * objeto request.
	 */
	private void cargarDesplegables(HttpServletRequest request) {
		try {
			List<Status> statusList = statusService.findAll();
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
			Results<UserDTO> resultadoUsuarios = userService.findBy(new UserCriteria(), 1, Integer.MAX_VALUE);

			List<UserDTO> organizadores = resultadoUsuarios != null && resultadoUsuarios.getPage() != null
					? resultadoUsuarios.getPage()
					: List.of();

			request.setAttribute("estados", statusList);
			request.setAttribute("formatos", formatList);
			request.setAttribute("paises", paises);
			request.setAttribute("provincias", provincias);
			request.setAttribute("localidades", localidades);
			request.setAttribute("organizadores", organizadores);

		} catch (ServiceException se) {
			logger.error("Error al cargar los desplegables", se);
		}
	}
	
	
	

	// Por hacerlo de otra manera, en este Servlet voy a gestionar las acciones de
	// la ç
	// request a traves de un switch..
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		if (action == null || action.trim().isEmpty()) {
			action = ConstantsUtils.REQUEST_EVENT_ACTION_SEARCH; // O redirigir a una página principal
		}
		switch (action) {

		// Accion = mostrar la vista de busqueda y cargar los combobox
		case ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH:
			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.EVENT_SEARCH).forward(request, response);
			break;

		// Accion = "search"
		case ConstantsUtils.REQUEST_EVENT_ACTION_SEARCH:
			cargarDesplegables(request);
			// 1. Lectura de parámetros de la petición
			String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);
			String name = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
			String statusIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_STATUS_ID);
			String formatIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);
			String userIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_USER_ID);
			String startDateStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);
			String endDateStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);
			String countryIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);
			String provinceIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);
			String localityIdStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

			// 2. Creamos un criteria y le asignamos los parámetros de búsqueda
			EventCriteria criteria = new EventCriteria();
			// los criterios los trimeamos para evitar espacios en blanco al inicio y al
			// final
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
			if (countryIdStr != null && !countryIdStr.trim().isEmpty()) {
				criteria.setCountryId(Long.valueOf(countryIdStr.trim()));
			}
			if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
				criteria.setProvinceId(Long.valueOf(provinceIdStr.trim()));
			}
			if (localityIdStr != null && !localityIdStr.trim().isEmpty()) {
				criteria.setLocalityId(Long.valueOf(localityIdStr.trim()));
			}
			// Formateador para convertir el String del input HTML (yyyy-MM-dd) a
			// java.util.Date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (startDateStr != null && !startDateStr.trim().isEmpty()) {
				try {
					Date startDate = sdf.parse(startDateStr.trim());
					criteria.setStartDate(startDate); // Ahora se pasa un objeto Date
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha...", e);
				}
			}
			if (endDateStr != null && !endDateStr.trim().isEmpty()) {
				try {
					Date endDate = sdf.parse(endDateStr.trim());
					criteria.setEndDate(endDate); // Ahora se pasa un objeto Date
				} catch (ParseException e) {
					logger.error("Error al parsear la fecha...", e);
				}
			}
			
			
			// paginacion de los resultados
			try {
				int tamanoPagina = 10;
				int paginaActual = 1;
				String paginaTexto = request.getParameter("pagina");

				if (paginaTexto != null && !paginaTexto.isBlank()) {
				    try {
				        paginaActual = Integer.parseInt(paginaTexto);

				        if (paginaActual < 1) {
				            paginaActual = 1;
				        }
				    } catch (NumberFormatException e) {
				        paginaActual = 1;
				    }
				}
				// El DAO espera la fila inicial, empezando en 1.
				// Página 1 empieza en la fila 1; página 2, en la 26; página 3, en la 51.
				int primeraFila = (paginaActual - 1) * tamanoPagina + 1;

				Results<EventDTO> results =
				        eventService.findBy(criteria, primeraFila, tamanoPagina);

				int totalPaginas =
				        (results.getTotal() + tamanoPagina - 1) / tamanoPagina;

				request.setAttribute("results", results);
				request.setAttribute("paginaActual", paginaActual);
				request.setAttribute("totalPaginas", totalPaginas);
				
			} catch (ServiceException se) {
				logger.error("Error al buscar eventos: ", se);
				request.setAttribute("error", "Error al realizar la búsqueda de eventos.");
			}

			request.getRequestDispatcher(ConstantsUtils.EVENT_SEARCH).forward(request, response);
			break;

		// Accion = "detail"
		case ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL:

			// primero recibimos el id de la request (cuando el usuario por ejemplo le da al
			// boton de ver el evento con id 1, se manda el id aqui y la accion de ver d
			// etalle)
			String idStr2 = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);

			// si el id viene a nulo o esta vacio error
			try {
				if (idStr2 == null || idStr2.trim().isEmpty()) {
					throw new IllegalArgumentException("El ID del evento no puede estar vacío.");
				}
				
				
				// ahora el servlet lee esa acción y el ID, buscar el evento con el servicio y
				// guardar el resultado en un atributo de la request.
				EventDTO event = eventService.findById(Long.valueOf(idStr2.trim()));
				if (event == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "El evento solicitado no existe.");
					return;
				}				
					
				request.setAttribute(ConstantsUtils.EVENT, event);
				request.getRequestDispatcher(ConstantsUtils.EVENT_DETAIL).forward(request, response);

			} catch (ServiceException se) {
				logger.error("Error al mostrar el detalle del evento con ID: " + idStr2, se);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se pudo cargar el detalle del evento.");
			}
			break;

		case ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP:
		    String idEvento =
		            request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);

		    try {
		        // Recupera todos los archivos asociados al evento.
		        List<ContentData> contenidos =
		                contentService.get(ConstantsUtils.EVENT_ENTITY_TYPE, idEvento);

		        ContentData cartel = null;

		        // De todos ellos, buscamos el que tiene el tipo de recurso "cartel".
		        if (contenidos != null) {
		            for (ContentData contenido : contenidos) {
		                if (contenido != null
		                        && contenido.getMetadata() != null
		                        && ConstantsUtils.EVENT_LINEUP_RESOURCE_TYPE.equals(
		                                contenido.getMetadata().getResourceType())) {
		                    cartel = contenido;
		                    break;
		                }
		            }
		        }

		        if (cartel == null || cartel.getRaw() == null) {
		            response.sendError(HttpServletResponse.SC_NOT_FOUND);
		            return;
		        }

		        // El formato viene guardado en los metadatos del contenido.
		        String formato = cartel.getMetadata().getFormat();

		        if (".png".equalsIgnoreCase(formato)) {
		            response.setContentType("image/png");
		        } else if (".jpg".equalsIgnoreCase(formato)
		                || ".jpeg".equalsIgnoreCase(formato)) {
		            response.setContentType("image/jpeg");
		        } else {
		            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		            return;
		        }

		        response.getOutputStream().write(cartel.getRaw());

		    } catch (ServiceException se) {
		        logger.error("Error al obtener el cartel del evento " + idEvento, se);
		        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    }

		    return;
			
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
