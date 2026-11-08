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
		%>
		<%
		String nombreEstado = event.getStatusName() == null ? "" : event.getStatusName().trim().toLowerCase();

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
		<%
		if (event != null) {
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
                 src="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= event.getId() %>"
                 alt="Cartel de <%= event.getName() %>"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/event_poster.jpg';">
        </div>
</div>
		<%
		}
		%>
<a class="btn-primary"
   href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH %>">
    Volver a la búsqueda
</a>

	</main>
</div>

<%@ include file="/html/common/footer.jsp"%>
