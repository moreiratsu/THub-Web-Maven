<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.EventDTO"%>
<%@ page import="com.alvaro.thub.utils.Results"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils"%>
<%@ page import="java.util.List"%>
<%@ page import="com.alvaro.thub.model.Status"%>
<%@ page import="com.alvaro.thub.model.Format"%>
<%@ page import="com.alvaro.thub.model.Country"%>
<%@ page import="com.alvaro.thub.model.ProvinceDTO"%>
<%@ page import="com.alvaro.thub.model.LocalityDTO"%>
<%@ page import="com.alvaro.thub.model.UserDTO"%>
<!-- HEADER -->
<%@ include file="/html/common/header.jsp"%>

<!-- BARRA SUPERIOR -->
<%@ include file="/html/common/topbar.jsp"%>

<div class="dashboard-layout">
	<!-- MENU LATERAL -->
	<%@ include file="/html/common/asidebar.jsp"%>
	<!-- Área de Contenido Principal -->
	<main class="content-area">


		<%
		// recupero los datos del formulario para pintarlos en la jsp
		List<Country> paises = (List<Country>) request.getAttribute("paises");

		List<ProvinceDTO> provincias = (List<ProvinceDTO>) request.getAttribute("provincias");

		List<LocalityDTO> localidades = (List<LocalityDTO>) request.getAttribute("localidades");

		String paisSeleccionado = request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);

		String provinciaSeleccionada = request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);

		String localidadSeleccionada = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

		List<Status> estados = (List<Status>) request.getAttribute("estados");

		List<Format> formatos = (List<Format>) request.getAttribute("formatos");

		List<UserDTO> organizadores = (List<UserDTO>) request.getAttribute("organizadores");

		// paginacion
		Integer paginaActualRecibida = (Integer) request.getAttribute("paginaActual");

		Integer totalPaginasRecibido = (Integer) request.getAttribute("totalPaginas");

		int paginaActual = paginaActualRecibida == null ? 1 : paginaActualRecibida;
		int totalPaginas = totalPaginasRecibido == null ? 0 : totalPaginasRecibido;
		%>

		<!-- Encabezado de Sección y Botón Crear Evento -->
		<div class="page-header">
			<h1 class="section-title">Eventos</h1>
		</div>

		<!-- Panel / Formulario de Búsqueda -->
		<div class="card search-card">
			<form id="eventSearchForm"
				action="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>"
				method="GET"
				data-show-search-action="<%=ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH%>">
				<input type="hidden" id="eventSearchAction"
					name="<%=ConstantsUtils.REQUEST_PARAM_ACTION%>"
					value="<%=ConstantsUtils.REQUEST_EVENT_ACTION_SEARCH%>">

				<!-- Filtros Específicos Grid -->
				<div class="filter-grid">
					<div>
						<label class="field-label">ID EVENTO / CÓDIGO</label> <input
							type="text" name="<%=ConstantsUtils.REQUEST_PARAM_ID%>"
							class="form-control" placeholder="Ej: 1">
					</div>

					<div>
						<label class="field-label">NOMBRE</label> <input type="text"
							name="<%=ConstantsUtils.REQUEST_PARAM_NAME%>"
							class="form-control" placeholder="Nombre del evento">
					</div>

					<div>
						<label class="field-label">ESTADO</label> <select
							name="<%=ConstantsUtils.REQUEST_PARAM_STATUS_ID%>"
							class="form-control">
							<option value="">Todos los estados</option>
							<%
							if (estados != null) {
								for (Status estado : estados) {
							%>
							<option value="<%=estado.getId()%>">
								<%=estado.getName()%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>

					<div>
						<label class="field-label">FORMATO</label> <select
							name="<%=ConstantsUtils.REQUEST_PARAM_FORMAT_ID%>"
							class="form-control">
							<option value="">Todos los formatos</option>
							<%
							if (formatos != null) {
								for (Format formato : formatos) {
							%>
							<option value="<%=formato.getId()%>">
								<%=formato.getName()%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>

					<div>
						<label class="field-label">ORGANIZADOR</label> <select
							name="<%=ConstantsUtils.REQUEST_PARAM_USER_ID%>"
							class="form-control">
							<option value="">Todos los orhanizadores</option>
							<%
							if (organizadores != null) {
								for (UserDTO organizador : organizadores) {
							%>
							<option value="<%=organizador.getId()%>">
								<%=organizador.getName() != null ? organizador.getName() : ""%><%=organizador.getLastName1() != null ? " " + organizador.getLastName1() : ""%><%=organizador.getLastName2() != null ? " " + organizador.getLastName2() : ""%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>

					<div>
						<label class="field-label">FECHA DESDE</label> <input type="date"
							name="<%=ConstantsUtils.REQUEST_PARAM_START_DATE%>"
							class="form-control">
					</div>

					<div>
						<label class="field-label">FECHA HASTA</label> <input type="date"
							name="<%=ConstantsUtils.REQUEST_PARAM_END_DATE%>"
							class="form-control">
					</div>

					<div>
						<label class="field-label">PAÍS</label> <select id="eventCountry"
							name="<%=ConstantsUtils.REQUEST_PARAM_COUNTRY_ID%>"
							class="form-control">
							<option value="">Todos los países</option>

							<%
							if (paises != null) {
								for (Country pais : paises) {
							%>
							<option value="<%=pais.getId()%>"
								<%=String.valueOf(pais.getId()).equals(paisSeleccionado) ? "selected" : ""%>>
								<%=pais.getName()%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>

					<div>
						<label class="field-label">PROVINCIA</label> <select
							id="eventProvince"
							name="<%=ConstantsUtils.REQUEST_PARAM_PROVINCE_ID%>"
							class="form-control">
							<option value="">Todas las provincias</option>
							<%
							if (provincias != null) {
								for (ProvinceDTO provincia : provincias) {
							%>
							<option value="<%=provincia.getId()%>"
								<%=String.valueOf(provincia.getId()).equals(provinciaSeleccionada) ? "selected" : ""%>>
								<%=provincia.getName()%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>

					<div>
						<label class="field-label">LOCALIDAD</label> <select
							id="eventLocality"
							name="<%=ConstantsUtils.REQUEST_PARAM_LOCALITY_ID%>"
							class="form-control">
							<option value="">Todas las localidades</option>

							<%
							if (localidades != null) {
								for (LocalityDTO localidad : localidades) {
							%>
							<option value="<%=localidad.getId()%>"
								<%=String.valueOf(localidad.getId()).equals(localidadSeleccionada) ? "selected" : ""%>>
								<%=localidad.getName()%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>
				</div>

				<!-- Botón Buscar -->
				<div class="form-actions">
					<button type="submit" class="btn-primary btn-search">
						<svg class="btn-icon" viewBox="0 0 24 24" fill="none"
							stroke="currentColor" stroke-width="2">
                                <circle cx="11" cy="11" r="8"></circle>
                                <line x1="21" y1="21" x2="16.65"
								y2="16.65"></line>
                            </svg>
						Buscar
					</button>
				</div>
			</form>
		</div>

		<!-- Tabla de Resultados -->
		<div class="card table-card">
			<div class="table-wrapper">
				<table class="events-table">
					<thead>
						<tr>
							<th>ID</th>
							<th>NOMBRE</th>
							<th>ESTADO</th>
							<th>FORMATO</th>
							<th>INICIO</th>
							<th>FIN</th>
							<th>LOCALIDAD</th>
							<th>PROVINCIA</th>
							<th>PAÍS</th>
							<th class="text-center">ACCIONES</th>
						</tr>
					</thead>
					<tbody>
						<%
						Results<EventDTO> results = (Results<EventDTO>) request.getAttribute("results");

						if (results != null && results.getPage() != null && !results.getPage().isEmpty()) {
							for (EventDTO e : results.getPage()) {
						%>
						<tr>
							<!-- ID original directo del DTO -->
							<td class="event-id"><%=e.getId()%></td>

							<!-- Nombre del evento -->
							<td class="event-name"><%=e.getName() != null ? e.getName() : ""%></td>

							<!-- Estado con Badge de color -->

							<td>
								<%
								String nombreEstado = e.getStatusName() == null ? "" : e.getStatusName().trim().toLowerCase();

								String claseEstado;

								if (nombreEstado.equals("programado")) {
									claseEstado = "status-programmed";
								} else if (nombreEstado.equals("en curso")) {
									claseEstado = "status-in-progress";
								} else if (nombreEstado.equals("cancelado")) {
									claseEstado = "status-cancelled";
								} else if (nombreEstado.equals("finalizado")) {
									claseEstado = "status-finished";
								} else {
									claseEstado = "status-default";
								}
								%> <span class="status-badge <%=claseEstado%>"> <%=e.getStatusName() != null ? e.getStatusName() : "Programado"%>
							</span>
							</td>

							<!-- Formato -->
							<td><%=e.getFormatName() != null ? e.getFormatName() : "-"%></td>

							<!-- Fechas Inicio y Fin -->
							<td><%=e.getStartDate() != null ? e.getStartDate() : "-"%></td>
							<td><%=e.getEndDate() != null ? e.getEndDate() : "-"%></td>

							<!-- Localización -->
							<td><%=e.getLocalityName() != null ? e.getLocalityName() : "-"%></td>
							<td><%=e.getProvinceName() != null ? e.getProvinceName() : "-"%></td>
							<td><%=e.getCountryName() != null ? e.getCountryName() : "-"%></td>

							<!-- Botones de Acción centrados -->
							<td class="text-center">
								<div class="actions-wrapper">
									<!-- Ver Detalle -->
									<a
										href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= e.getId() %>"
										title="Ver Detalle" class="action-btn action-view"> <svg
											viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2">
                                    <path
												d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                    <circle cx="12" cy="12" r="3"></circle>
                                </svg> <!-- BLOQUE PARA QUE APAREZCAN LOS BOTONES DE ELIMINAR O EDITAR SI ERES EL CREADOR DE ESE EVENTO -->
									</a>
									<!-- Editar -->
									<%
									boolean esCreador = usuario != null && usuario.getId() != null && e.getCreatorUserId() != null
											&& usuario.getId().equals(e.getCreatorUserId());

									if (esCreador) {
									%>
									<a
										href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_UPDATE %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= e.getId() %>"
										title="Editar Evento" class="action-btn action-edit"> <svg
											viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2">
                                    <path
												d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path
												d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
									</a>
									<!-- Eliminar -->
									<a
										href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_DELETE %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= e.getId() %>"
										title="Eliminar Evento" class="action-btn action-delete"
										onclick="return confirm('¿Seguro que deseas eliminar este evento?');">
										<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2">
                                    <polyline points="3 6 5 6 21 6"></polyline>
                                    <path
												d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                </svg>
									</a>
									<%
									}
									%>
								</div>
							</td>
						</tr>
						<%
						}
						} else {
						%>
						<tr>
							<td colspan="10"
								style="text-align: center; padding: 20px; color: var(--text-secondary);">
								No se han encontrado eventos para la búsqueda realizada.</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</div>

			<!-- Paginación -->
			<div class="pagination-wrapper">
				<% if (totalPaginas > 0) { %>
				<button type="button" class="pagination-button" data-pagina="1"
					<%= paginaActual <= 1 ? "disabled" : "" %>>&laquo;</button>

				<button type="button" class="pagination-button"
					data-pagina="<%= paginaActual - 1 %>"
					<%= paginaActual <= 1 ? "disabled" : "" %>>&lsaquo;</button>

				<span><%= paginaActual %> de <%= totalPaginas %></span>

				<button type="button" class="pagination-button"
					data-pagina="<%= paginaActual + 1 %>"
					<%= paginaActual >= totalPaginas ? "disabled" : "" %>>
					&rsaquo;</button>

				<button type="button" class="pagination-button"
					data-pagina="<%= totalPaginas %>"
					<%= paginaActual >= totalPaginas ? "disabled" : "" %>>
					&raquo;</button>
				<% } %>
			</div>
		</div>

	</main>
</div>


<!-- FOOTER -->
<%@ include file="/html/common/footer.jsp"%>
