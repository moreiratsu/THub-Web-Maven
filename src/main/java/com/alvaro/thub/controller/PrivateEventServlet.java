package com.alvaro.thub.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.dao.criteria.EventCriteria;
import com.alvaro.thub.model.ContentData;
import com.alvaro.thub.model.ContentException;
import com.alvaro.thub.model.ContentMetadata;
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
import com.alvaro.thub.utils.WebUtils;

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

			request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
			return;
		}
		// Accion = borrar
		else if (ConstantsUtils.REQUEST_EVENT_ACTION_DELETE.equalsIgnoreCase(action)) {
			// Action: Eliminar un evento

			// La petición debe pertenecer a una sesión iniciada.
			if (user == null || user.getId() == null) {
				response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN);
				return;
			}

			String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);

			// Comprobamos que la URL incluye un ID numérico válido.
			Long eventId;
			try {
				if (idStr == null || idStr.isBlank()) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el ID del evento.");
					return;
				}

				eventId = Long.valueOf(idStr.trim());

			} catch (NumberFormatException e) {
				logger.error("El ID no es válido : " + e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El ID del evento no es válido.");
				return;
			}

			try {
				// Buscamos el evento para saber quién lo creó.
				EventDTO evento = eventService.findById(eventId);

				if (evento == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "El evento no existe.");
					return;
				}

				// El creador del evento debe ser el usuario de la sesión.
				if (evento.getCreatorUserId() == null || !user.getId().equals(evento.getCreatorUserId())) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para borrar este evento.");
					return;
				}

				// Solo llegamos aquí si el usuario es el creador.
				boolean eliminado = eventService.delete(eventId);

				if (!eliminado) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo borrar el evento.");
					return;
				}

				Results<EventDTO> results = eventService.findBy(new EventCriteria(), 1, Integer.MAX_VALUE);

				request.setAttribute("results", results);
				request.getRequestDispatcher(ConstantsUtils.EVENT_SEARCH).forward(request, response);

			} catch (ServiceException se) {
				logger.error("Error al eliminar el evento con ID: " + eventId, se);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Se produjo un error al borrar el evento.");
			}

			// Accion = crear
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_CREATE.equalsIgnoreCase(action)) {

			Event nuevoEvento = new Event();

			String fechaInicioTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);
			String fechaFinTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);

			if (fechaInicioTexto == null || fechaInicioTexto.isBlank()) {
				request.setAttribute("error", "Debes indicar la fecha de inicio.");
				cargarDesplegables(request);
				request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
				return;
			}

			try {
				LocalDate fechaInicio = LocalDate.parse(fechaInicioTexto.trim());

				if (fechaInicio.isBefore(LocalDate.now())) {
					request.setAttribute("error", "La fecha de inicio no puede estar en el pasado.");
					cargarDesplegables(request);
					request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
					return;
				}
				if (fechaFinTexto == null || fechaFinTexto.isBlank()) {
					request.setAttribute("error", "Debes indicar la fecha de fin.");
					cargarDesplegables(request);
					request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
					return;
				}

				LocalDate fechaFin = LocalDate.parse(fechaFinTexto.trim());

				if (fechaFin.isBefore(fechaInicio)) {
					request.setAttribute("error", "La fecha de fin no puede ser anterior a la fecha de inicio.");
					cargarDesplegables(request);
					request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
					return;
				}

				String nombreEvento = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);

				String formatoIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);

				String paisIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);

				String provinciaIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);

				String localidadIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

				if (nombreEvento == null || nombreEvento.isBlank() || formatoIdTexto == null || formatoIdTexto.isBlank()
						|| paisIdTexto == null || paisIdTexto.isBlank()) {
					// dejo que los unicos campos que puedan ser null sean provincia y localidad
					// porque puede ser un evento nacional, privicial o local
					request.setAttribute("error", "Debes completar todos los campos del evento.");
					cargarDesplegables(request);
					request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
					return;
				}

				try {

					nuevoEvento.setName(nombreEvento.trim());
					nuevoEvento.setStartDate(java.sql.Date.valueOf(fechaInicio));
					nuevoEvento.setEndDate(java.sql.Date.valueOf(fechaFin));

					nuevoEvento.setFormatId(Long.valueOf(formatoIdTexto));
					nuevoEvento.setCountryId(Long.valueOf(paisIdTexto));

					// si en el formulario provincia o locality vienen vacios el servlet los
					// convierte a null
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
					 * las reglas:: al crear se asignará Programado si empieza en una fecha futura
					 * En curso si empieza hoy o cuando la fecha actual sea la inicial de un evento.
					 * Cancelado se asignará desde acción de edición. La transición automática a
					 * Finalizado sera cuando la fecha de fin sea igual a la fecha actual.
					 */
					String nombreEstadoInicial;

					if (fechaInicio.isAfter(LocalDate.now())) {
						nombreEstadoInicial = "Programado";
					} else {
						// Si empieza hoy, y las fechas ya están validadas, está en curso.
						nombreEstadoInicial = "En curso";
					}

					List<Status> estadosEncontrados = statusService.findByName(nombreEstadoInicial);

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
						request.setAttribute("error", "No se encontró el estado inicial " + nombreEstadoInicial + ".");
						cargarDesplegables(request);
						request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
						return;
					}

					nuevoEvento.setStatusId(estadoInicial.getId());

					// En el siguiente paso asignaremos el estado y llamaremos al servicio.
				} catch (NumberFormatException e) {
					request.setAttribute("error", "Alguna selección del formulario no es válida.");
					cargarDesplegables(request);
					request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
					return;
				} catch (ServiceException se) {
					logger.error("No se pudo buscar el estado inicial del evento", se);
					request.setAttribute("error", "No se pudo determinar el estado inicial.");
					cargarDesplegables(request);
					request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
					return;
				}

				// La fecha es válida.
				// En el siguiente paso añadiremos aquí la creación del evento.

			} catch (DateTimeParseException e) {
				request.setAttribute("error", "Una de las fechas no tiene un formato válido.");
				cargarDesplegables(request);
				request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
				return;
			}

			try {
				EventDTO eventoCreado = eventService.create(nuevoEvento);

				if (eventoCreado == null || eventoCreado.getId() == null) {
					request.setAttribute("error", "No se pudo crear el evento.");
					cargarDesplegables(request);
					request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
					return;
				}

				String urlDetalle = request.getContextPath() + ConstantsUtils.PUBLIC_EVENT_SERVLET + "?"
						+ ConstantsUtils.REQUEST_PARAM_ACTION + "=" + ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL + "&"
						+ ConstantsUtils.REQUEST_PARAM_ID + "=" + eventoCreado.getId();

				response.sendRedirect(urlDetalle);
				return;

			} catch (ServiceException se) {
				logger.error("Error al crear el evento", se);
				request.setAttribute("error", "No se pudo guardar el evento.");
				cargarDesplegables(request);
				request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
				return;
			}

			// Accion = actualizar
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_UPDATE.equalsIgnoreCase(action)) {

			    // Hace falta una sesión iniciada.
			    if (user == null || user.getId() == null) {
			        response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN);
			        return;
			    }

			    String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);
			    Long eventId;

			    // Comprobamos que llega un ID numérico.
			    try {
			        if (idStr == null || idStr.isBlank()) {
			            response.sendError(
			                    HttpServletResponse.SC_BAD_REQUEST,
			                    "Falta el ID del evento."
			            );
			            return;
			        }

			        eventId = Long.valueOf(idStr.trim());

			    } catch (NumberFormatException e) {
			        response.sendError(
			                HttpServletResponse.SC_BAD_REQUEST,
			                "El ID del evento no es válido."
			        );
			        return;
			    }

			    try {
			        // Buscamos el evento.
			        EventDTO evento = eventService.findById(eventId);

			        if (evento == null) {
			            response.sendError(
			                    HttpServletResponse.SC_NOT_FOUND,
			                    "El evento no existe."
			            );
			            return;
			        }

			        // Solo el creador puede abrir la vista de edición.
			        if (evento.getCreatorUserId() == null
			                || !user.getId().equals(evento.getCreatorUserId())) {
			            response.sendError(
			                    HttpServletResponse.SC_FORBIDDEN,
			                    "No tienes permiso para editar este evento."
			            );
			            return;
			        }

			        // Por ahora esta rama prepara y muestra el formulario mediante GET.
			        if ("GET".equalsIgnoreCase(request.getMethod())) {
			            request.setAttribute(ConstantsUtils.EVENT, evento);
			            request.setAttribute("formatos", formatService.findAll());
			            request.setAttribute("paises", countryService.findAll());

			            List<ProvinceDTO> provincias = evento.getCountryId() == null
			                    ? List.of()
			                    : provinceService.findByCountry(evento.getCountryId());

			            List<LocalityDTO> localidades = evento.getProvinceId() == null
			                    ? List.of()
			                    : localityService.findByProvinceId(evento.getProvinceId());

			            request.setAttribute("provincias", provincias);
			            request.setAttribute("localidades", localidades);

			            request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT)
			                   .forward(request, response);
			            return;
			        }

			        // En el siguiente paso añadiremos aquí el guardado por POST.
			        if ("POST".equalsIgnoreCase(request.getMethod())) {

			            String nombre = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
			            String formatoTexto =
			                    request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);
			            String inicioTexto =
			                    request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);
			            String finTexto =
			                    request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);
			            String paisTexto =
			                    request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);
			            String provinciaTexto =
			                    request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);
			            String localidadTexto =
			                    request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

			            if (nombre == null || nombre.isBlank()
			                    || formatoTexto == null || formatoTexto.isBlank()
			                    || inicioTexto == null || inicioTexto.isBlank()
			                    || finTexto == null || finTexto.isBlank()
			                    || paisTexto == null || paisTexto.isBlank()) {

			                request.setAttribute("error", "Completa los campos obligatorios.");
			                request.setAttribute(ConstantsUtils.EVENT, evento);
			                cargarDesplegables(request);
			                request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT)
			                       .forward(request, response);
			                return;
			            }

			            try {
			                LocalDate fechaInicio = LocalDate.parse(inicioTexto);
			                LocalDate fechaFin = LocalDate.parse(finTexto);

			                if (fechaFin.isBefore(fechaInicio)) {
			                    request.setAttribute(
			                            "error",
			                            "La fecha de fin no puede ser anterior a la de inicio."
			                    );
			                    request.setAttribute(ConstantsUtils.EVENT, evento);
			                    cargarDesplegables(request);
			                    request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT)
			                           .forward(request, response);
			                    return;
			                }

			                Long formatoId = Long.valueOf(formatoTexto);
			                Long paisId = Long.valueOf(paisTexto);

			                Long provinciaId = provinciaTexto == null || provinciaTexto.isBlank()
			                        ? null
			                        : Long.valueOf(provinciaTexto);

			                Long localidadId = localidadTexto == null || localidadTexto.isBlank()
			                        ? null
			                        : Long.valueOf(localidadTexto);

			                Event eventoActualizado = new Event();
			                eventoActualizado.setId(eventId);
			                eventoActualizado.setName(nombre.trim());
			                eventoActualizado.setStartDate(java.sql.Date.valueOf(fechaInicio));
			                eventoActualizado.setEndDate(java.sql.Date.valueOf(fechaFin));
			                eventoActualizado.setFormatId(formatoId);
			                eventoActualizado.setCountryId(paisId);
			                eventoActualizado.setProvinceId(provinciaId);
			                eventoActualizado.setLocalityId(localidadId);

			                // Estos valores vienen del evento guardado, no del formulario.
			                eventoActualizado.setCreatorUserId(evento.getCreatorUserId());
			                eventoActualizado.setStatusId(evento.getStatusId());

			                boolean actualizado = eventService.update(eventoActualizado);

			                if (!actualizado) {
			                    request.setAttribute("error", "No se pudieron guardar los cambios.");
			                    request.setAttribute(ConstantsUtils.EVENT, evento);
			                    cargarDesplegables(request);
			                    request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT)
			                           .forward(request, response);
			                    return;
			                }

			                String urlDetalle = request.getContextPath()
			                        + ConstantsUtils.PUBLIC_EVENT_SERVLET
			                        + "?"
			                        + ConstantsUtils.REQUEST_PARAM_ACTION
			                        + "="
			                        + ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL
			                        + "&"
			                        + ConstantsUtils.REQUEST_PARAM_ID
			                        + "="
			                        + eventId;

			                response.sendRedirect(urlDetalle);
			                return;

			            } catch (DateTimeParseException | NumberFormatException e) {
			                request.setAttribute(
			                        "error",
			                        "Revisa las fechas y los valores seleccionados."
			                );
			                request.setAttribute(ConstantsUtils.EVENT, evento);
			                cargarDesplegables(request);
			                request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT)
			                       .forward(request, response);
			                return;
			            }
			        }

			        // Si llega un método distinto de GET o POST, lo rechazamos.
			        response.sendError(
			                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
			                "Método no permitido."
			        );
			        response.sendError(
			                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
			                "El guardado de cambios todavía no está implementado."
			        );

			    } catch (ServiceException se) {
			        logger.error("Error al cargar la edición del evento con ID: " + eventId, se);
			        response.sendError(
			                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			                "No se pudo cargar el evento para editarlo."
			        );
			    }
			
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_MY_EVENTS.equalsIgnoreCase(action)) {

			// Para ver los eventos propios hace falta tener sesión iniciada.
			if (user == null || user.getId() == null) {
				response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN);
				return;
			}

			try {
				// Buscamos los eventos cuyo creador es el usuario conectado.
				List<EventDTO> misEventos = eventService.findByCreator(user.getId());

				// EventSearch.jsp recibe los resultados en formato Results.
				if (misEventos == null) {
					misEventos = new java.util.ArrayList<>();
				}

				Results<EventDTO> results = new Results<>();
				results.setPage(misEventos);

				request.setAttribute("results", results);

				cargarDesplegables(request);

				request.getRequestDispatcher(ConstantsUtils.EVENT_SEARCH).forward(request, response);
				return;

			} catch (ServiceException se) {
				logger.error("Error al cargar los eventos del usuario conectado.", se);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudieron cargar tus eventos.");
				return;
			}
		}

		
		
		
		
		
		// SUBIR CARTEL DE EVENTO
		else if (ConstantsUtils.REQUEST_EVENT_ACTION_UPLOAD_LINEUP.equalsIgnoreCase(action)) {
			// Leemos los bytes de la foto desde la request usando el método leerBytes de
			// WebUtils
			byte[] bytesFoto = WebUtils.leerBytes(request, "imagen");
			// si no selecciona nada, enviamos un mensaje de información y volvemos a la
			// página de evento detalle
			if (bytesFoto == null || bytesFoto.length == 0) {
				cargarDesplegables(request);
				logger.info("No se seleccionó ninguna foto");
				request.setAttribute("error", "No se seleccionó ninguna foto.");
				request.getRequestDispatcher(ConstantsUtils.EVENT_DETAIL).forward(request, response);
				return;
			}
			try {
		        // Guardamos la foto en el servicio de contenido
		        ContentData c = new ContentData();
		        ContentMetadata cm = new ContentMetadata();
		        cm.setEntityType(ConstantsUtils.EVENT_ENTITY_TYPE);
		     //   cm.setEntityId(event.getId().toString()); // El ID del evento
		        cm.setResourceType(ConstantsUtils.EVENT_LINEUP_RESOURCE_TYPE);
		        cm.setFormat(".png"); // TODO: extraer la extensión real en el futuro si lo necesitas
		        
		        c.setMetadata(cm);
		        c.setRaw(bytesFoto);
		        contentService.save(c);
		        
		        SessionManager.setAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE, user);
		        
		        logger.info("Foto del evento guardada correctamente para el usuario: " + user.getId());
		        request.setAttribute("successMessage", "Foto de evento actualizada con éxito."); // Mensaje de éxito successMessage

		    } catch (ServiceException se) {
		        logger.error("Error en el servicio al guardar la foto de evento para el usuario: " + user.getId(), se);
		        request.setAttribute("error", "Error al guardar la foto de evento: " + se.getMessage());
		    } catch (Exception e) {
		        logger.error("Error inesperado al procesar la foto de evento para el usuario: " + user.getId(), e);
		        request.setAttribute("error", "Error inesperado al procesar la imagen.");
		    }		
			
			request.getRequestDispatcher(ConstantsUtils.EVENT_DETAIL).forward(request, response);
			
		}
		
		
		
		
		
		
		// ACCION MOSTRAR IMAGEN DE EVENTO 
		else if (ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP.equalsIgnoreCase(action)) {
			// primero creamos un objeto ContentMetadata con los metadatos de la imagen de usuario 
			ContentMetadata metadata = new ContentMetadata();
			metadata.setEntityType(ConstantsUtils.EVENT_ENTITY_TYPE);
		//	metadata.setEntityId(event.getId().toString());
			metadata.setResourceType(ConstantsUtils.EVENT_LINEUP_RESOURCE_TYPE);
			metadata.setFormat(".png");
			
			try {
			    ContentData foto = contentService.get(metadata); 
			    byte[] bytes = foto.getRaw(); // 

			    response.setContentType("image/png");
			    response.getOutputStream().write(bytes);
			    response.getOutputStream().flush();
			    
			} catch (ContentException ce) {
				logger.error("Error al obtener la foto de evento " + ce);
			    response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			return;		
		
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
