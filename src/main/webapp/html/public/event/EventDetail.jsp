<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.EventDTO"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils"%>

<%@ include file="/html/common/header.jsp"%>
<%@ include file="/html/common/topbar.jsp"%>

<div class="dashboard-layout">
	<%@ include file="/html/common/asidebar.jsp"%>

	<main class="content-area">

		<div class="page-header">
			<h1 class="section-title">Detalle del evento</h1>
		</div>

		<%
EventDTO event = (EventDTO) request.getAttribute(ConstantsUtils.EVENT);

if (event != null) {
    String nombreEstado = event.getStatusName() == null
            ? ""
            : event.getStatusName().trim().toLowerCase();

    String claseEstado;

    if ("programado".equals(nombreEstado)) {
        claseEstado = "status-programmed";
    } else if ("en curso".equals(nombreEstado)) {
        claseEstado = "status-in-progress";
    } else if ("cancelado".equals(nombreEstado)) {
        claseEstado = "status-cancelled";
    } else if ("finalizado".equals(nombreEstado)) {
        claseEstado = "status-finished";
    } else {
        claseEstado = "status-default";
    }
%>

		<div class="card event-detail-card">
		<div class="event-detail-grid">
		
		<!-- parte izquierda con los datos del evento -->
		<section class="event-detail-info">
			<h2 class="event-detail-title">
				<%=event.getName() != null ? event.getName() : "Evento sin nombre"%>
			</h2>
			<p>
				<strong>Estado:</strong> <span
					class="status-badge <%=claseEstado%>"> <%=event.getStatusName() != null ? event.getStatusName() : "Sin estado"%>
				</span>
			</p>

			<p>
				<strong>Formato:</strong>
				<%=event.getFormatName() != null ? event.getFormatName() : "-"%>
			</p>

			<p>
				<strong>Fecha de inicio:</strong>
				<%=event.getStartDate() != null ? event.getStartDate() : "-"%>
			</p>

			<p>
				<strong>Fecha de fin:</strong>
				<%=event.getEndDate() != null ? event.getEndDate() : "-"%>
			</p>

			<p>
				<strong>Localidad:</strong>
				<%=event.getLocalityName() != null ? event.getLocalityName() : "-"%>
			</p>

			<p>
				<strong>Provincia:</strong>
				<%=event.getProvinceName() != null ? event.getProvinceName() : "-"%>
			</p>

			<p>
				<strong>País:</strong>
				<%=event.getCountryName() != null ? event.getCountryName() : "-"%>
			</p>
			</section>
			
			<div class="event-poster-panel">
            <img class="event-poster"
                 src="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= event.getId() %>"
                 alt="Cartel de <%= event.getName() %>"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/event_poster.jpg';">
        </div>
</div>

<!-- BLOQUE PARA QUE APAREZCAN LOS BOTONES DE ELIMINAR O EDITAR SI ERES EL CREADOR DE ESE EVENTO -->
<%
boolean esCreador = usuario != null
        && usuario.getId() != null
        && event.getCreatorUserId() != null
        && usuario.getId().equals(event.getCreatorUserId());

if (esCreador) {
%>
    <div class="actions-wrapper event-detail-actions">

        <!-- Botón para editar -->
        <a href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_UPDATE %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= event.getId() %>"
           title="Editar evento"
           aria-label="Editar evento"
           class="action-btn action-edit">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
        </a>

        <!-- Botón para borrar -->
        <a href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_DELETE %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= event.getId() %>"
           title="Borrar evento"
           aria-label="Borrar evento"
           class="action-btn action-delete"
           onclick="return confirm('¿Seguro que deseas borrar este evento?');">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2">
                <polyline points="3 6 5 6 21 6"></polyline>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
            </svg>
        </a>

    </div>
    
    <!-- ------------------------------------------------------------------------------------------- -->
    
<%
}
%>
<a class="btn-primary"
   href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH %>">
    Volver a la búsqueda
</a>
</div>
		<%
		}
		%>


	</main>
</div>

<%@ include file="/html/common/footer.jsp"%>
