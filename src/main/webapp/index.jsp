<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.alvaro.thub.model.EventDTO" %>

<jsp:include page='<%= "/Public/Event?action=showRecentEvents" %>' />

<%
List<EventDTO> eventosRecientes =
        (List<EventDTO>) request.getAttribute("eventosRecientes");
%>

<!-- HEADER -->
<%@ include file="/html/common/header.jsp" %> 

<!-- BARRA SUPERIOR -->
<%@ include file="/html/common/topbar.jsp" %>

<!-- DISTRIBUCIÓN DE CONTENIDO -->
<div class="dashboard-layout">

<!-- MENU LATERAL -->
<%@ include file="/html/common/asidebar.jsp" %>


<!-- CONTENIDO PRINCIPAL -->
<main class="content-area">
<section class="home-hero" aria-labelledby="home-title">
    <div class="home-hero-content">
        <span class="home-eyebrow">EL DEPORTE SE VIVE EN COMPAÑÍA</span>

        <h1 id="home-title">Encuentra tu próximo evento deportivo</h1>

        <p>Compite, participa y vive el deporte con THUB.</p>

        <div class="home-hero-actions">
            <a class="home-button home-button-primary"
               href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH %>">
                Explorar eventos
            </a>

            <a class="home-button home-button-secondary"
               href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_CREATE %>">
                Crear evento
            </a>
        </div>
    </div>
</section>
        <section class="home-sports">
    <div class="home-section-heading">
        <div>
            <span class="home-eyebrow">THUB</span>
            <h2>Últimos eventos:</h2>
        </div>

        <a class="home-all-events"
           href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH %>">
            Ver todos los eventos →
        </a>
    </div>

    <div class="home-sport-grid">
       <%
if (eventosRecientes != null && !eventosRecientes.isEmpty()) {
    for (EventDTO evento : eventosRecientes) {
%>
    <article class="home-sport-card home-event-card">
        <div class="home-event-poster">
            <img
                src="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= evento.getId() %>"
                alt="Cartel del evento"
                onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/event_poster.jpg';">
        </div>

        <div class="home-event-info">
            <h3><%= evento.getName() %></h3>
            <p>Inicio:
                <%= evento.getStartDate() != null ? evento.getStartDate() : "Sin fecha" %>
            </p>
            <p>Formato:
                <%= evento.getFormatName() != null ? evento.getFormatName() : "Sin formato" %>
            </p>

            <a href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= evento.getId() %>">
                Ver evento →
            </a>
        </div>
    </article>
<%
    }
} else {
%>
    <p>Todavía no hay eventos creados.</p>
<%
}
%>
    </div>
</section>
      </main>
      
<!-- PIE DE PÁGINA -->      
<%@ include file="/html/common/footer.jsp" %>
