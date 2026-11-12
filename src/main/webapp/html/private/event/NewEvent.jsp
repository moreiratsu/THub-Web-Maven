<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="com.alvaro.thub.model.Country"%>
<%@ page import="com.alvaro.thub.model.Format"%>
<%@ page import="com.alvaro.thub.model.LocalityDTO"%>
<%@ page import="com.alvaro.thub.model.ProvinceDTO"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils"%>

<%@ include file="/html/common/header.jsp"%>
<%@ include file="/html/common/topbar.jsp"%>

<%

// Valores del formulario, para conservarlos si se muestra un error.
String nombreIntroducido =
        request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);
String formatoIntroducido =
        request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);
String fechaInicioIntroducida =
        request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);
String fechaFinIntroducida =
        request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);
String paisSeleccionado =
        request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);
String provinciaSeleccionada =
        request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);
String localidadSeleccionada =
        request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

// Datos preparados por el servlet.
String errorMessage = (String) request.getAttribute("error");
List<Country> paises =
        (List<Country>) request.getAttribute("paises");
List<ProvinceDTO> provincias =
        (List<ProvinceDTO>) request.getAttribute("provincias");
List<LocalityDTO> localidades =
        (List<LocalityDTO>) request.getAttribute("localidades");
List<Format> formatos =
        (List<Format>) request.getAttribute("formatos");

// Dirección para volver a la lista de eventos.
String urlEventos = request.getContextPath()
        + ConstantsUtils.PUBLIC_EVENT_SERVLET
        + "?"
        + ConstantsUtils.REQUEST_PARAM_ACTION
        + "="
        + ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH;
%>

<div class="dashboard-layout">

    <%@ include file="/html/common/asidebar.jsp"%>

    <main class="content-area">

        <div class="page-header">
            <h1 class="section-title">Crear evento</h1>

            <a class="role-badge" href="<%=urlEventos%>">
                Volver a eventos
            </a>
        </div>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-banner">
                <%=errorMessage%>
            </div>
        <% } %>

        <section class="search-card">
            <h2 class="section-title">Información del evento</h2>

            <form id="eventCreateForm"
                  action="${pageContext.request.contextPath}<%=ConstantsUtils.PRIVATE_EVENT_SERVLET%>"
                  method="POST"
                  enctype="multipart/form-data"
                  data-show-create-action="<%=ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_CREATE%>">

                <input type="hidden"
                       name="<%=ConstantsUtils.REQUEST_PARAM_ACTION%>"
                       value="<%=ConstantsUtils.REQUEST_EVENT_ACTION_CREATE%>">

                <div class="input-group">
                    <label for="imagen" class="field-label">
                        Cartel del evento
                    </label>

                    <input type="file"
                           id="imagen"
                           name="imagen"
                           class="form-control"
                           accept=".png"
                           aria-describedby="imageHelp">

                    <small id="imageHelp">Formato permitido: PNG.</small>

                    <img id="posterPreview"
                         class="event-poster-preview"
                         alt="Vista previa del cartel"
                         hidden>
                </div>

                <div class="filter-grid">

                    <div class="input-group">
                        <label for="eventName" class="field-label">
                            Nombre del evento *
                        </label>

                        <input type="text"
                               id="eventName"
                               name="<%=ConstantsUtils.REQUEST_PARAM_NAME%>"
                               class="form-control"
                               required
                               placeholder="Ej.: Torneo de fútbol"
                               value="<%=nombreIntroducido == null ? "" : nombreIntroducido%>">
                    </div>

                    <div class="input-group">
                        <label for="formatId" class="field-label">
                            Formato *
                        </label>

                        <select id="formatId"
                                name="<%=ConstantsUtils.REQUEST_PARAM_FORMAT_ID%>"
                                class="form-control"
                                required>
                            <option value="">Selecciona un formato</option>

                            <% if (formatos != null) {
                                for (Format formato : formatos) { %>
                                    <option value="<%=formato.getId()%>"
                                        <%=String.valueOf(formato.getId())
                                                .equals(formatoIntroducido)
                                                ? "selected" : ""%>>
                                        <%=formato.getName() == null
                                                ? "" : formato.getName()%>
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
                               name="<%=ConstantsUtils.REQUEST_PARAM_START_DATE%>"
                               class="form-control"
                               required
                               value="<%=fechaInicioIntroducida == null
                                       ? "" : fechaInicioIntroducida%>">
                    </div>

                    <div id="startDateError" class="error-banner" hidden></div>

                    <div class="input-group">
                        <label for="endDate" class="field-label">
                            Fecha de fin *
                        </label>

                        <input type="date"
                               id="endDate"
                               name="<%=ConstantsUtils.REQUEST_PARAM_END_DATE%>"
                               class="form-control"
                               required
                               value="<%=fechaFinIntroducida == null
                                       ? "" : fechaFinIntroducida%>">
                    </div>

                    <div class="input-group">
                        <label for="createEventCountry" class="field-label">
                            País *
                        </label>

                        <select id="createEventCountry"
                                name="<%=ConstantsUtils.REQUEST_PARAM_COUNTRY_ID%>"
                                class="form-control"
                                required>
                            <option value="">Selecciona un país</option>

                            <% if (paises != null) {
                                for (Country pais : paises) { %>
                                    <option value="<%=pais.getId()%>"
                                        <%=String.valueOf(pais.getId())
                                                .equals(paisSeleccionado)
                                                ? "selected" : ""%>>
                                        <%=pais.getName() == null
                                                ? "" : pais.getName()%>
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </div>

                    <div class="input-group">
                        <label for="createEventProvince" class="field-label">
                            Provincia
                        </label>

                        <select id="createEventProvince"
                                name="<%=ConstantsUtils.REQUEST_PARAM_PROVINCE_ID%>"
                                class="form-control">
                            <option value="">Selecciona una provincia</option>

                            <% if (provincias != null) {
                                for (ProvinceDTO provincia : provincias) { %>
                                    <option value="<%=provincia.getId()%>"
                                        <%=String.valueOf(provincia.getId())
                                                .equals(provinciaSeleccionada)
                                                ? "selected" : ""%>>
                                        <%=provincia.getName() == null
                                                ? "" : provincia.getName()%>

                                        <% if (provincia.getCountryName() != null) { %>
                                            (<%=provincia.getCountryName()%>)
                                        <% } %>
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </div>

                    <div class="input-group">
                        <label for="createEventLocality" class="field-label">
                            Localidad
                        </label>

                        <select id="createEventLocality"
                                name="<%=ConstantsUtils.REQUEST_PARAM_LOCALITY_ID%>"
                                class="form-control">
                            <option value="">Selecciona una localidad</option>

                            <% if (localidades != null) {
                                for (LocalityDTO localidad : localidades) { %>
                                    <option value="<%=localidad.getId()%>"
                                        <%=String.valueOf(localidad.getId())
                                                .equals(localidadSeleccionada)
                                                ? "selected" : ""%>>
                                        <%=localidad.getName() == null
                                                ? "" : localidad.getName()%>

                                        <% if (localidad.getProvinceName() != null) { %>
                                            (<%=localidad.getProvinceName()%>)
                                        <% } %>
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </div>

                </div>

                <div class="search-card event-organizer">
                    <p>
                        <strong>Organizador:</strong>
                        <%=usuario.getName() == null ? "" : usuario.getName()%>
                        <%=usuario.getLastName1() == null
                                ? "" : usuario.getLastName1()%>
                    </p>
                </div>

                <div class="form-actions event-create-actions">
                    <a class="register-btn" href="<%=urlEventos%>">
                        Cancelar
                    </a>

                    <button type="submit" class="btn-primary">
                        Crear evento
                    </button>
                </div>

            </form>
        </section>

    </main>
</div>

<%@ include file="/html/common/footer.jsp"%>