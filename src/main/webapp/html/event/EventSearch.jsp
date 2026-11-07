<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.EventDTO"%>
<%@ page import="com.alvaro.thub.utils.Results"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils" %>
<!-- HEADER -->
<%@ include file="/html/common/header.jsp"%>

<!-- BARRA SUPERIOR -->
<%@ include file="/html/common/topbar.jsp"%>

<div class="dashboard-layout"> 
	<!-- MENU LATERAL -->
	<%@ include file="/html/common/asidebar.jsp"%>
	<!-- Área de Contenido Principal -->
	<main class="content-area">

		<!-- Encabezado de Sección y Botón Crear Evento -->
		<div class="page-header">
			<h1 class="section-title">Eventos</h1>
		</div>

		<!-- Panel / Formulario de Búsqueda -->
		<div class="card search-card">
			<form action="${pageContext.request.contextPath}/Event"
				method="GET">
				<input type="hidden" name="<%= ConstantsUtils.REQUEST_PARAM_ACTION %>" value="search">

				<!-- Filtros Específicos Grid -->
				<div class="filter-grid">
					<div>
						<label class="field-label">ID EVENTO / CÓDIGO</label> <input
							type="text" name="<%= ConstantsUtils.REQUEST_PARAM_ID %>" class="form-control" placeholder="Ej: 1">
					</div>

					<div>
						<label class="field-label">NOMBRE</label> <input type="text"
							name="<%= ConstantsUtils.REQUEST_PARAM_NAME %>" class="form-control" placeholder="Nombre del evento">
					</div>

					<div>
						<label class="field-label">ESTADO</label> <select name="stateId"
							class="form-control">
							<option value="">Todos los estados</option>
							<option value="1">Programado</option>
							<option value="2">En curso</option>
							<option value="3">Finalizado</option>
						</select>
					</div>

					<div>
						<label class="field-label">FORMATO</label> <select name="formatId"
							class="form-control">
							<option value="">Todos los formatos</option>
							<option value="1">Torneo</option>
							<option value="2">Liga</option>
							<option value="3">Grupos</option>
						</select>
					</div>

					<div>
						<label class="field-label">ORGANIZADOR</label> <select
							name="organizerId" class="form-control">
							<option value="">Todos los usuarios</option>
						</select>
					</div>

					<div>
						<label class="field-label">FECHA DESDE</label> <input type="date"
							name="startDate" class="form-control">
					</div>

					<div>
						<label class="field-label">FECHA HASTA</label> <input type="date"
							name="endDate" class="form-control">
					</div>

					<div>
						<label class="field-label">PAÍS</label> <select name="countryId"
							class="form-control">
							<option value="">Todos los países</option>
						</select>
					</div>

					<div>
						<label class="field-label">PROVINCIA</label> <select
							name="provinceId" class="form-control">
							<option value="">Todas las provincias</option>
						</select>
					</div>

					<div>
						<label class="field-label">LOCALIDAD</label> <select
							name="<%= ConstantsUtils.REQUEST_PARAM_LOCALITY_ID %>" class="form-control">
							<option value="">Todas las localidades</option>
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
                    <td class="event-id"><%= e.getId() %></td>
                    
                    <!-- Nombre del evento -->
                    <td class="event-name"><%= e.getName() != null ? e.getName() : "" %></td>
                    
                    <!-- Estado con Badge de color -->
                    <td>
                        <span class="status-badge status-programmed">
                            <%= e.getStatusName() != null ? e.getStatusName() : "Programado" %>
                        </span>
                    </td>
                    
                    <!-- Formato -->
                    <td><%= e.getFormatName() != null ? e.getFormatName() : "-" %></td>
                    
                    <!-- Fechas Inicio y Fin -->
                    <td><%= e.getStartDate() != null ? e.getStartDate() : "-" %></td>
                    <td><%= e.getEndDate() != null ? e.getEndDate() : "-" %></td>
                    
                    <!-- Localización -->
                    <td><%= e.getLocalityName() != null ? e.getLocalityName() : "-" %></td>
                    <td><%= e.getProvinceName() != null ? e.getProvinceName() : "-" %></td>
                    <td><%= e.getCountryName() != null ? e.getCountryName() : "-" %></td>
                    
                    <!-- Botones de Acción centrados -->
                    <td class="text-center">
                        <div class="actions-wrapper">
                            <!-- Ver Detalle -->
                            <a href="${pageContext.request.contextPath}/Event?action=detail&id=<%= e.getId() %>" title="Ver Detalle" class="action-btn action-view">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                    <circle cx="12" cy="12" r="3"></circle>
                                </svg>
                            </a> 
                            <!-- Editar -->
                            <a href="${pageContext.request.contextPath}/Event?action=editView&id=<%= e.getId() %>" title="Editar Evento" class="action-btn action-edit">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                            </a> 
                            <!-- Eliminar -->
                            <a href="${pageContext.request.contextPath}/Event?action=delete&id=<%= e.getId() %>" title="Eliminar Evento" class="action-btn action-delete" onclick="return confirm('¿Seguro que deseas eliminar este evento?');">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="3 6 5 6 21 6"></polyline>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                </svg>
                            </a>
                        </div>
                    </td>
                </tr>
            <%
                }
            } else {
            %>
                <tr>
                    <td colspan="10" style="text-align: center; padding: 20px; color: var(--text-secondary);">
                        No se han encontrado eventos para la búsqueda realizada.
                    </td>
                </tr>
            <%
            }
            %>
            </tbody>
        </table>
    </div>

			<!-- Paginación -->
			<div class="pagination-wrapper">
				<span>&lt;&lt; &lt; 1 de 10 &gt; &gt;&gt;</span>
			</div>
		</div>

	</main>
</div>


<!-- FOOTER -->
<%@ include file="/html/common/footer.jsp"%>
