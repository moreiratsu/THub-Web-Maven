package com.alvaro.thub.controller;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.annotation.MultipartConfig;

import com.alvaro.thub.dao.DataException;
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
import java.io.InputStream;
import jakarta.servlet.http.Part;
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

/*
 * Esto indica al servlet que puede recibir formularios con datos y un archivo
 * Es lo que habilita es poder leer campos normals con request.getParameter() y
 * archivos con request.getPart() Le especificamos que el tamaño maximo del
 * archivo sean 2MB y la peticion entera con datos y archivo incluidos no puede
 * superar 3MB para no sobrecargar al servidor
 */
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)

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


	// Método para llenar los combox con los datos de la base de datos
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

	/*
	 * Método auxiliar que hace un FORWARD AL FORMULARIO DE CREAR EVENTO CON UN
	 * MENSAJE DE ERROR, logicamente al volver al formulario tiene que recargar los
	 * combox con los datos
	 */
	private void volverAlFormularioConError(HttpServletRequest request, HttpServletResponse response, String mensaje)
			throws ServletException, IOException {
		request.setAttribute("error", mensaje);
		cargarDesplegables(request);
		request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter(ConstantsUtils.REQUEST_PARAM_ACTION);
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);

		// ACCION = MOSTRAR NewEvent.jsp
		if (ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_CREATE.equalsIgnoreCase(action)) {
			cargarDesplegables(request);
			request.getRequestDispatcher(ConstantsUtils.EVENT_NEW).forward(request, response);
			return;
		}

		// ACCION = CREAR nuevo evento
		else if (ConstantsUtils.REQUEST_EVENT_ACTION_CREATE.equalsIgnoreCase(action)) {

			// 1. Cojo la plantilla del objeto evento
			Event nuevoEvento = new Event();

			// 2. cojo (GET) los parametros del formulario
			String fechaInicioTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);
			String fechaFinTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);
			String nombreEvento = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
			String formatoIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);
			String paisIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);
			String provinciaIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);
			String localidadIdTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

			// 3. Valido que no vengan vacios, tengan formato correcto y no sean null
			if (fechaInicioTexto == null || fechaInicioTexto.isBlank()) {
				volverAlFormularioConError(request, response, "Debes indicar la fecha de inicio.");
				return;
			}

			try {
				LocalDate fechaInicio = LocalDate.parse(fechaInicioTexto.trim());

				if (fechaInicio.isBefore(LocalDate.now())) {
					volverAlFormularioConError(request, response,
							"La fecha de inicio no puede ser anterior a la fecha actual.");
					return;
				}
				if (fechaFinTexto == null || fechaFinTexto.isBlank()) {
					volverAlFormularioConError(request, response, "Debes indicar la fecha de fin.");
					return;
				}

				LocalDate fechaFin = LocalDate.parse(fechaFinTexto.trim());

				if (fechaFin.isBefore(fechaInicio)) {
					volverAlFormularioConError(request, response,
							"La fecha de fin no puede ser anterior a la fecha de inicio.");
					return;
				}

				if (nombreEvento == null || nombreEvento.isBlank() || formatoIdTexto == null || formatoIdTexto.isBlank()
						|| paisIdTexto == null || paisIdTexto.isBlank()) {
					// dejo que los unicos campos que puedan ser null sean provincia y localidad
					// porque puede ser un evento nacional, privicial o local
					volverAlFormularioConError(request, response, "Debes completar todos los campos del evento.");
					return;
				}

				// 4. Seteo los datos en la plantilla del objeto evento
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

					// El estado inicial que seteamos cuando creamos un eventosiempre va a ser
					// programado
					List<Status> estados = statusService.findByName(ConstantsUtils.ESTADO_PROGRAMADO);

					if (estados == null || estados.isEmpty()) {
						volverAlFormularioConError(request, response, "No se encontró el estado Programado.");
						return;
					}

					nuevoEvento.setStatusId(estados.get(0).getId());

					// 5. capturo las posibles excepciones
				} catch (NumberFormatException e) {
					volverAlFormularioConError(request, response, "Alguna selección del formulario no es válida.");
					return;
				} catch (ServiceException se) {
					logger.error("No se pudo buscar el estado inicial del evento", se);
					volverAlFormularioConError(request, response, "No se pudo determinar el estado inicial.");
					return;
				}

			} catch (DateTimeParseException e) {
				volverAlFormularioConError(request, response, "Una de las fechas no tiene un formato válido.");
				return;
			}

			try {

				// 6. Cargar una foto
				byte[] bytesCartel = null; // de momento no hay imagen, con lo cual el array de bytes es null, si el
											// usuario no sube una imagen de carte al crear el evento, el array queda en
											// null
				String formatoCartel = null;
				Part parteCartel = request.getPart("imagen"); // cogemos la parte del formulario que contiene un archivo
																// en este caso con nombre= "imagen". Recordatorio, aqui
																// uso getPart y no file upload porque con fileUpload
																// solo leo los bytes del fichero, pero esta imagen
																// viene seguida de mas elementos de forlmulariop los
																// cuales no me dejaria leer si no es con part

				if (parteCartel != null && parteCartel.getSize() > 0) { // si ha subido un archivo, significa que no es
																		// null y tiene tamaño mayor q 0
					try (InputStream entrada = parteCartel.getInputStream()) { // leemos el archivo
						bytesCartel = entrada.readAllBytes(); // le damos al array de bytes lo que lee inputstream
					}
					// Para validar la imagen y el formato uso el método que implementé en WebUtils, 
					// que recibe el nombre del archivo y el array de bytes. getSubmittedFileName() 
					// es un método de la clase Part que nos da el nombre de un archivo.
					formatoCartel = WebUtils.validarImagen(parteCartel.getSubmittedFileName(), bytesCartel); 
					if (formatoCartel == null) {
						volverAlFormularioConError(request, response,
								"El archivo debe ser una imagen PNG, JPG o JPEG válida.");
						return;
					}

				} // el siguiente paso es guardarlo en el CMS (tiene q hacerse justo despues de
					// crear porq necesito saber el ID del evento creado)

				// 7. Usamos el servicio para crear el objeto con la plantilla que hemos hecho
				// de evento
				EventDTO eventoCreado = eventService.create(nuevoEvento);

				// 8. Hacemos los checkeos por si ha pasado algo
				if (eventoCreado == null || eventoCreado.getId() == null) {
					volverAlFormularioConError(request, response, "No se pudo crear el evento.");
					return;
				}

				// 9. Si se añadio una imagen en el formulario la guardamos en el CMS
				if (bytesCartel != null) {
					ContentMetadata metadata = new ContentMetadata();
					metadata.setEntityType(ConstantsUtils.EVENT_ENTITY_TYPE);
					metadata.setEntityId(eventoCreado.getId().toString());
					metadata.setResourceType(ConstantsUtils.EVENT_LINEUP_RESOURCE_TYPE);
					metadata.setFormat(formatoCartel); // le pasamos el que obtenga en el meetodo de la clase utilitaria
														// de WEBUTILS

					ContentData cartel = new ContentData();
					cartel.setMetadata(metadata);
					cartel.setRaw(bytesCartel);
					try {
						contentService.save(cartel);
					} catch (ContentException ce) {
						logger.error(
								"El evento " + eventoCreado.getId() + " se creó, pero no se pudo guardar su cartel.",
								ce);
					}
				}

				// 10. Cuando se crea el evento hago Redirect a la vista detalle del mismo
				String urlDetailView = request.getContextPath() + ConstantsUtils.PUBLIC_EVENT_SERVLET + "?"
						+ ConstantsUtils.REQUEST_PARAM_ACTION + "=" + ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL + "&"
						+ ConstantsUtils.REQUEST_PARAM_ID + "=" + eventoCreado.getId();

				response.sendRedirect(urlDetailView);
				return;

			} catch (ServiceException se) {
				logger.error("Error al crear el evento", se);
				volverAlFormularioConError(request, response, "No se pudo guardar el evento.");
				return;
			}

			
			
			
			
			// ACCION = ELIMINAR un evento
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_DELETE.equalsIgnoreCase(action)) {

			String idUrlString = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID); // obtenemos el id del evento de la peticion (viene como STRING)
			Long eventId = null; // el verdadero ID tipo long de momento lo pongo como null
			
			// Comprobamos si el string tiene valor
			try {
				if (idUrlString == null || idUrlString.isBlank()) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el ID del evento.");
					return;
				}
				//convertimos el string a Long y ya tenemos el id del evento a eliminar
				eventId = Long.valueOf(idUrlString.trim());

			} catch (NumberFormatException e) {
				logger.error("El ID no es válido : " + e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El ID del evento no es válido.");
				return;
			}

			// Buscamos el evento para saber quién lo creó.
			try {	
				EventDTO evento = eventService.findById(eventId);

				if (evento == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "El evento no existe.");
					return;
				}

				// Solo el creador del evento puede borrar sus eventos
				if (evento.getCreatorUserId() == null || !user.getId().equals(evento.getCreatorUserId())) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para borrar este evento.");
					return;
				}

				// Solo llegamos aquí si el usuario es el creador.
				boolean eliminado = eventService.delete(eventId); // ya tenemos el id del evento y somos el usuario con permido de borrar solo queda llamar al servicio

				if (!eliminado) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo borrar el evento.");
					return;
				}
				
				// si lo elimina lo redirigimos a a lista de eventos (aqui no se hace forward porque si hiciera un forward y f5 volveria a cargar la url del borrado pero ya no hay un evento con ese id entoces me daria el error 404)

				response.sendRedirect(response.encodeRedirectURL(request.getContextPath()
				        + ConstantsUtils.PUBLIC_EVENT_SERVLET
				        + "?"
				        + ConstantsUtils.REQUEST_PARAM_ACTION
				        + "="
				        + ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH));
				return;

			} catch (ServiceException se) {
				logger.error("Error al eliminar el evento con ID: " + eventId, se);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Se produjo un error al borrar el evento.");
			}
			
			
			
			
			// Accion = EDITAR/ACTUALIZAR un evento 
			//(en esta accion voy a probar a hacer dos cosas en la misma accion,
			//con get muestro los datos actuales y con POST guardo los cambios)
		} else if (ConstantsUtils.REQUEST_EVENT_ACTION_UPDATE.equalsIgnoreCase(action)) {


			String idStr = request.getParameter(ConstantsUtils.REQUEST_PARAM_ID);
			Long eventId;

			// Comprobamos que llega un ID numérico.
			try {
				if (idStr == null || idStr.isBlank()) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el ID del evento.");
					return;
				}
				
				// lo paso a long 
				eventId = Long.valueOf(idStr.trim());

			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El ID del evento no es válido.");
				return;
			}

			try {
				// Buscamos el evento.
				EventDTO evento = eventService.findById(eventId);

				if (evento == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "El evento no existe.");
					return;
				}

				// Comprobamos que el usuario de la sesion es el creador, que es el unico que puede editar sus eventos
				if (evento.getCreatorUserId() == null || !user.getId().equals(evento.getCreatorUserId())) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para editar este evento.");
					return;
				}

				// Por ahora esta rama prepara y muestra el formulario mediante GET.
				if ("GET".equalsIgnoreCase(request.getMethod())) {
					
					request.setAttribute(ConstantsUtils.EVENT, evento); // por un lado llena los campos con los datos del objeto
					
					// y a mayores tambien llena los desplegables
					request.setAttribute("formatos", formatService.findAll());
					request.setAttribute("paises", countryService.findAll());
					
					//aqui saca el desplegable a traves del pais guardado en el evento para pintarlos 
					// si el evento no tiene provincia, la lista queda vacía; si tiene, busca las localidades de esa provincia.
					List<ProvinceDTO> provincias = evento.getCountryId() == null ? List.of()
							: provinceService.findByCountry(evento.getCountryId());

					List<LocalityDTO> localidades = evento.getProvinceId() == null ? List.of()
							: localityService.findByProvinceId(evento.getProvinceId());

					request.setAttribute("provincias", provincias);
					request.setAttribute("localidades", localidades);

					request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT).forward(request, response);
					return;
				}

				// Aquí el guardado por POST.
				if ("POST".equalsIgnoreCase(request.getMethod())) {
					
					// cojemos los datos del formulario guardados en la peticion EN STRING
					String nombre = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
					String formatoTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);
					String inicioTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);
					String finTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);
					String paisTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);
					String provinciaTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);
					String localidadTexto = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);
					
					// tambien leemos la imagen si la hay, no la guardamos aun
					byte[] bytesCartel = null;
					String formatoCartel = null;

					try {
					    Part parteCartel = request.getPart("imagen");

					    // Si no eligió otra imagen, bytesCartel queda en null
					    // y conservaremos el cartel que ya tiene.
					    if (parteCartel != null && parteCartel.getSize() > 0) {
					        try (InputStream entrada = parteCartel.getInputStream()) {
					            bytesCartel = entrada.readAllBytes();
					        }

					        formatoCartel = WebUtils.validarImagen(
					                parteCartel.getSubmittedFileName(),
					                bytesCartel
					        );

					        if (formatoCartel == null) {
					            request.setAttribute(
					                    "error",
					                    "El cartel debe ser una imagen PNG, JPG o JPEG válida."
					            );
					            request.setAttribute(ConstantsUtils.EVENT, evento);
					            cargarDesplegables(request);
					            request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT)
					                    .forward(request, response);
					            return;
					        }
					    }

					} catch (IOException e) {
					    logger.error("No se pudo leer el cartel del evento.", e);
					    request.setAttribute("error", "No se pudo leer la imagen seleccionada.");
					    request.setAttribute(ConstantsUtils.EVENT, evento);
					    cargarDesplegables(request);
					    request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT)
					            .forward(request, response);
					    return;
					}
					
					// checkeamos 
					if (nombre == null || nombre.isBlank() || formatoTexto == null || formatoTexto.isBlank()
							|| inicioTexto == null || inicioTexto.isBlank() || finTexto == null || finTexto.isBlank()
							|| paisTexto == null || paisTexto.isBlank()) {

						request.setAttribute("error", "Completa los campos obligatorios.");
						request.setAttribute(ConstantsUtils.EVENT, evento);
						cargarDesplegables(request);
						request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT).forward(request, response);
						return;
					}

					try {
						LocalDate fechaInicio = LocalDate.parse(inicioTexto);
						LocalDate fechaFin = LocalDate.parse(finTexto);

						if (fechaFin.isBefore(fechaInicio)) {
							request.setAttribute("error", "La fecha de fin no puede ser anterior a la de inicio.");
							request.setAttribute(ConstantsUtils.EVENT, evento);
							cargarDesplegables(request);
							request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT).forward(request, response);
							return;
						}
						
						// pasamos los string a long 
						Long formatoId = Long.valueOf(formatoTexto);
						Long paisId = Long.valueOf(paisTexto);

						Long provinciaId = provinciaTexto == null || provinciaTexto.isBlank() ? null
								: Long.valueOf(provinciaTexto);

						Long localidadId = localidadTexto == null || localidadTexto.isBlank() ? null
								: Long.valueOf(localidadTexto);
						
						// y ahora si le seteamos al nuevo evento los nuevos valores
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
						
						// Y llamamos al servicio
						boolean actualizado = eventService.update(eventoActualizado);

						if (!actualizado) { // si algo falla: 
							request.setAttribute("error", "No se pudieron guardar los cambios.");
							request.setAttribute(ConstantsUtils.EVENT, evento);
							cargarDesplegables(request);
							request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT).forward(request, response);
							return;
						}
						// si actualizo bien guardamos la imagen nueva si la cambió (si bytescarterl es nu ll es que conserva la foto anterior y esto se lo salta)
						if (bytesCartel != null) {
						    ContentMetadata metadata = new ContentMetadata();
						    metadata.setEntityType(ConstantsUtils.EVENT_ENTITY_TYPE);
						    metadata.setEntityId(eventId.toString());
						    metadata.setResourceType(ConstantsUtils.EVENT_LINEUP_RESOURCE_TYPE); // yo siempre voy a guardar el cartel con nombre "cartel"
						    metadata.setFormat(formatoCartel); // pero si ya tengo un cartel anterior antes de editarlo y tiene por ej formato png, y guardo uno nuevo con formato jpg van a haber dos carteles y puede cargarse el que no es

						    ContentData cartel = new ContentData();
						    cartel.setMetadata(metadata);
						    cartel.setRaw(bytesCartel);

						    try {
						    	// entonces primero GUARDO el nuevo
						        contentService.save(cartel);
						        List<ContentData> contenidosEvento =
						                contentService.get( // despues obtengo todos los que haya
						                        ConstantsUtils.EVENT_ENTITY_TYPE,
						                        eventId.toString()
						                );

						        for (ContentData contenido : contenidosEvento) {
						            ContentMetadata metadataAnterior = contenido.getMetadata();

						            if (metadataAnterior != null
						                    && ConstantsUtils.EVENT_LINEUP_RESOURCE_TYPE.equals(
						                            metadataAnterior.getResourceType())
						                    && !formatoCartel.equalsIgnoreCase( // el cartel que tenga un formato distinto al ultimo que guarde lo elimina
						                            metadataAnterior.getFormat())) { // y si es el mismo formato sobreescribe el archivo

						                contentService.remove(metadataAnterior);
						            }
						        }
						        }catch (DataException de) {  // content remove puede lanzar un dataexception
						            logger.error(
						                    "El cartel nuevo se guardó, pero no se pudo borrar el cartel anterior.",
						                    de
						            );
						            response.sendError(
						                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						                    "El cartel se guardó, pero no se pudo eliminar la versión anterior."
						            );
						            return;
						    } catch (ContentException ce) {
						        logger.error(
						                "El evento se actualizó, pero no se pudo guardar el nuevo cartel.",
						                ce
						        );
						        response.sendError(
						                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						                "El evento se actualizó, pero no se pudo guardar el cartel."
						        );
						        return;
						    }
						}
						
						// si funciona bien redirect
						response.sendRedirect(request.getContextPath() + ConstantsUtils.PUBLIC_EVENT_SERVLET + "?"
								+ ConstantsUtils.REQUEST_PARAM_ACTION + "=" + ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL
								+ "&" + ConstantsUtils.REQUEST_PARAM_ID + "=" + eventId);
						return;

					} catch (DateTimeParseException | NumberFormatException e) {
						request.setAttribute("error", "Revisa las fechas y los valores seleccionados.");
						request.setAttribute(ConstantsUtils.EVENT, evento);
						cargarDesplegables(request);
						request.getRequestDispatcher(ConstantsUtils.EVENT_EDIT).forward(request, response);
						return;
					}
				}

				// Si llega un método distinto de GET o POST, lo rechazamos.
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método no permitido.");

			} catch (ServiceException se) {
				logger.error("Error al cargar la edición del evento con ID: " + eventId, se);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"No se pudo cargar el evento para editarlo.");
			}
			
			
			
			
			// ACCION = MOSTRAR TODOS LOS EVENTOS DE UN USUARIO CONCRETO
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

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
