<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.alvaro.thub.model.EventDTO"%>
<%@ page import="com.alvaro.thub.model.Format"%>
<%@ page import="com.alvaro.thub.model.Country"%>
<%@ page import="com.alvaro.thub.model.ProvinceDTO"%>
<%@ page import="com.alvaro.thub.model.LocalityDTO"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils"%>

<%@ include file="/html/common/header.jsp"%>
<%@ include file="/html/common/topbar.jsp"%>

<%!
private String escaparHtml(String texto) {
    if (texto == null) {
        return "";
    }

    return texto.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&#39;");
}

private String fechaParaInput(java.util.Date fecha) {
    if (fecha == null) {
        return "";
    }

    return new java.text.SimpleDateFormat("yyyy-MM-dd").format(fecha);
}
%>

<%
EventDTO event = (EventDTO) request.getAttribute(ConstantsUtils.EVENT);

List<Format> formatos =
        (List<Format>) request.getAttribute("formatos");
List<Country> paises =
        (List<Country>) request.getAttribute("paises");
List<ProvinceDTO> provincias =
        (List<ProvinceDTO>) request.getAttribute("provincias");
List<LocalityDTO> localidades =
        (List<LocalityDTO>) request.getAttribute("localidades");

String errorMessage = (String) request.getAttribute("error");
String successMessage = (String) request.getAttribute("successMessage");
%>

<div class="dashboard-layout">
    <%@ include file="/html/common/asidebar.jsp"%>

    <main class="content-area">
        <div class="page-header">
            <h1 class="section-title">Editar evento</h1>
        </div>

        <% if (errorMessage != null && !errorMessage.isBlank()) { %>
            <div class="error-banner">
                <%= escaparHtml(errorMessage) %>
            </div>
        <% } %>

        <% if (successMessage != null && !successMessage.isBlank()) { %>
            <div class="success-banner">
                <%= escaparHtml(successMessage) %>
            </div>
        <% } %>

        <% if (event == null) { %>

            <div class="card">
                <p>No se han recibido los datos del evento.</p>
                <a class="btn-primary"
                   href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH %>">
                    Volver a eventos
                </a>
            </div>

        <% } else { %>

            <div class="card event-detail-card">
                <div class="event-detail-grid">

                    <section class="event-detail-info">
                        <h2 class="event-detail-title">
                            <%= escaparHtml(event.getName()) %>
                        </h2>

                        <!-- Formulario para guardar los datos del evento -->
                        <form
                            action="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>"
                            method="POST">

                            <input type="hidden"
                                   name="<%= ConstantsUtils.REQUEST_PARAM_ACTION %>"
                                   value="<%= ConstantsUtils.REQUEST_EVENT_ACTION_UPDATE %>">

                            <input type="hidden"
                                   name="<%= ConstantsUtils.REQUEST_PARAM_ID %>"
                                   value="<%= event.getId() %>">

                            <div class="input-group">
                                <label for="eventName" class="field-label">
                                    Nombre del evento *
                                </label>
                                <input type="text"
                                       id="eventName"
                                       name="<%= ConstantsUtils.REQUEST_PARAM_NAME %>"
                                       class="form-control"
                                       value="<%= escaparHtml(event.getName()) %>"
                                       required>
                            </div>

                            <div class="input-group">
                                <label for="formatId" class="field-label">
                                    Formato *
                                </label>
                                <select id="formatId"
                                        name="<%= ConstantsUtils.REQUEST_PARAM_FORMAT_ID %>"
                                        class="form-control"
                                        required>
                                    <option value="">Selecciona un formato</option>

                                    <% if (formatos != null) {
                                        for (Format formato : formatos) {
                                            boolean seleccionado =
                                                event.getFormatId() != null
                                                && event.getFormatId().equals(formato.getId());
                                    %>
                                        <option value="<%= formato.getId() %>"
                                            <%= seleccionado ? "selected" : "" %>>
                                            <%= escaparHtml(formato.getName()) %>
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                            </div>

                            <div class="input-group">
                                <label for="startDate" class="field-label">
                                    Fecha de inicio *
                                </label>
                                <input type="date"
                                       id="startDate"
                                       name="<%= ConstantsUtils.REQUEST_PARAM_START_DATE %>"
                                       class="form-control"
                                       value="<%= fechaParaInput(event.getStartDate()) %>"
                                       required>
                            </div>

                            <div class="input-group">
                                <label for="endDate" class="field-label">
                                    Fecha de fin *
                                </label>
                                <input type="date"
                                       id="endDate"
                                       name="<%= ConstantsUtils.REQUEST_PARAM_END_DATE %>"
                                       class="form-control"
                                       value="<%= fechaParaInput(event.getEndDate()) %>"
                                       required>
                            </div>

                            <div class="input-group">
                                <label for="countryId" class="field-label">
                                    País *
                                </label>
                                <select id="countryId"
                                        name="<%= ConstantsUtils.REQUEST_PARAM_COUNTRY_ID %>"
                                        class="form-control"
                                        required>
                                    <option value="">Selecciona un país</option>

                                    <% if (paises != null) {
                                        for (Country pais : paises) {
                                            boolean seleccionado =
                                                event.getCountryId() != null
                                                && event.getCountryId().equals(pais.getId());
                                    %>
                                        <option value="<%= pais.getId() %>"
                                            <%= seleccionado ? "selected" : "" %>>
                                            <%= escaparHtml(pais.getName()) %>
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                            </div>

                            <div class="input-group">
                                <label for="provinceId" class="field-label">
                                    Provincia
                                </label>
                                <select id="provinceId"
                                        name="<%= ConstantsUtils.REQUEST_PARAM_PROVINCE_ID %>"
                                        class="form-control">
                                    <option value="">Selecciona una provincia</option>

                                    <% if (provincias != null) {
                                        for (ProvinceDTO provincia : provincias) {
                                            boolean seleccionado =
                                                event.getProvinceId() != null
                                                && event.getProvinceId().equals(provincia.getId());
                                    %>
                                        <option value="<%= provincia.getId() %>"
                                            <%= seleccionado ? "selected" : "" %>>
                                            <%= escaparHtml(provincia.getName()) %>
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                            </div>

                            <div class="input-group">
                                <label for="localityId" class="field-label">
                                    Localidad
                                </label>
                                <select id="localityId"
                                        name="<%= ConstantsUtils.REQUEST_PARAM_LOCALITY_ID %>"
                                        class="form-control">
                                    <option value="">Selecciona una localidad</option>

                                    <% if (localidades != null) {
                                        for (LocalityDTO localidad : localidades) {
                                            boolean seleccionado =
                                                event.getLocalityId() != null
                                                && event.getLocalityId().equals(localidad.getId());
                                    %>
                                        <option value="<%= localidad.getId() %>"
                                            <%= seleccionado ? "selected" : "" %>>
                                            <%= escaparHtml(localidad.getName()) %>
                                        </option>
                                    <%  }
                                    } %>
                                </select>
                            </div>

                            <!-- El estado y el organizador se muestran, no se editan aquí. -->
                            <p>
                                <strong>Estado:</strong>
                                <%= escaparHtml(event.getStatusName()) %>
                            </p>
                            <p>
                                <strong>Organizador:</strong>
                                <%= escaparHtml(event.getCreatorUserName()) %>
                                <%= escaparHtml(event.getCreatorUserLastName()) %>
                            </p>

                            <div class="form-actions"
                                 style="margin-top: 24px; display: flex; gap: 12px; justify-content: flex-end;">
                                <a class="register-btn"
                                   href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_DETAIL %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= event.getId() %>">
                                    Cancelar
                                </a>

                                <button type="submit" class="btn-primary">
                                    Guardar cambios
                                </button>
                            </div>
                        </form>
                    </section>

                    <div class="event-poster-panel">
                        <img class="event-poster"
                             src="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= event.getId() %>"
                             alt="Cartel de <%= escaparHtml(event.getName()) %>"
                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/event_poster.jpg';">

                        <!-- Formulario independiente para subir el cartel -->
                        <form
                            action="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_UPLOAD_LINEUP %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>=<%= event.getId() %>"
                            method="POST"
                            enctype="multipart/form-data">

                            <div class="input-group">
                                <label for="imagen" class="field-label">
                                    Cambiar cartel
                                </label>
                                <input type="file"
                                       id="imagen"
                                       name="imagen"
                                       accept=".png,image/png"
                                       class="form-control"
                                       required>
                            </div>

                            <button type="submit" class="btn-primary">
                                Subir cartel
                            </button>
                        </form>
                    </div>

                </div>
            </div>

        <% } %>
    </main>
</div>

<%@ include file="/html/common/footer.jsp"%>