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


<%!private String escapar(String valor) {
		if (valor == null) {
			return "";
		}

		return valor.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("'", "&#39;");
	}%>

<%
if (usuario == null) {
	response.sendRedirect(request.getContextPath() + ConstantsUtils.USER_LOGIN);
	return;
}
String nombreIntroducido = request.getParameter(ConstantsUtils.REQUEST_PARAM_NAME);

String formatoIntroducido = request.getParameter(ConstantsUtils.REQUEST_PARAM_FORMAT_ID);

String fechaInicioIntroducida = request.getParameter(ConstantsUtils.REQUEST_PARAM_START_DATE);

String fechaFinIntroducida = request.getParameter(ConstantsUtils.REQUEST_PARAM_END_DATE);

List<Country> paises = (List<Country>) request.getAttribute("paises");

List<ProvinceDTO> provincias = (List<ProvinceDTO>) request.getAttribute("provincias");

List<LocalityDTO> localidades = (List<LocalityDTO>) request.getAttribute("localidades");

List<Format> formatos = (List<Format>) request.getAttribute("formatos");

String paisSeleccionado = request.getParameter(ConstantsUtils.REQUEST_PARAM_COUNTRY_ID);

String provinciaSeleccionada = request.getParameter(ConstantsUtils.REQUEST_PARAM_PROVINCE_ID);

String localidadSeleccionada = request.getParameter(ConstantsUtils.REQUEST_PARAM_LOCALITY_ID);

String errorMessage = (String) request.getAttribute("error");
%>

<div class="dashboard-layout">

	<%@ include file="/html/common/asidebar.jsp"%>

	<main class="content-area">

		<div class="page-header">
			<h1 class="section-title">Crear evento</h1>

			<a class="role-badge"
				href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH %>">
				Volver a eventos </a>
		</div>

		<%
		if (errorMessage != null && !errorMessage.isEmpty()) {
		%>
		<div class="error-banner">
			<%=errorMessage%>
		</div>
		<%
		}
		%>



		<!-- Formulario para seleccionar y enviar la foto de imagen -->
		<section class="search-card">
			<h2 class="section-title"
				style="font-size: 1rem; margin-bottom: 16px;">Cartel del evento</h2>
			<!-- AQUI mostramos la imagen del evento actual del usuario, si existe , si no mostramos un placeholder -->
			<img class="event-poster"
                 src="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP %>&<%= ConstantsUtils.REQUEST_PARAM_ID %>"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/event_poster.jpg';">
			<form
				action="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_USER_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_LINEUP %>"
				method="POST" enctype="multipart/form-data">
				
				<div class="input-group">
				<br>
					<label for="profilePhoto" class="field-label">SELECCIONA
						UNA IMAGEN</label> <input type="file" id="profilePhoto" name="photo"
						class="form-control" accept=".png"
						aria-describedby="profilePhotoHelp" /> <small
						id="profilePhotoHelp">Formato permitido: PNG.</small>
				</div>

			</form>
		</section>

		<!-- SECCION DEL FORMULARIO DE DATOS DEL EVENTO -->
		<section class="search-card">
			<h2 class="section-title">Información del evento</h2>

			<form id="eventCreateForm"
				action="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>"
				method="POST"
				data-show-create-action="<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_CREATE %>">

				<input type="hidden" id="eventCreateAction"
					name="<%=ConstantsUtils.REQUEST_PARAM_ACTION%>"
					value="<%=ConstantsUtils.REQUEST_EVENT_ACTION_CREATE%>">

				<div class="filter-grid">

					<div class="input-group">
						<label for="eventName" class="field-label"> NOMBRE DEL
							EVENTO * </label> <input type="text" id="eventName"
							name="<%=ConstantsUtils.REQUEST_PARAM_NAME%>"
							class="form-control" required placeholder="Ej.: Torneo de fútbol"
							value="<%=escapar(nombreIntroducido)%>">
					</div>

					<div class="input-group">
						<label for="formatId" class="field-label">FORMATO *</label> <select
							id="formatId" name="<%=ConstantsUtils.REQUEST_PARAM_FORMAT_ID%>"
							class="form-control" required>
							<option value="">Selecciona un formato</option>

							<%
							if (formatos != null) {
								for (Format formato : formatos) {
							%>
							<option value="<%=formato.getId()%>"
								<%=String.valueOf(formato.getId()).equals(formatoIntroducido) ? "selected" : ""%>>
								<%=escapar(formato.getName())%>
							</option>
							<%
							}
							}
							%>

						</select>
					</div>

					<div class="input-group">
						<label for="startDate" class="field-label"> FECHA DE
							INICIO * </label> <input type="date" id="startDate"
							name="<%=ConstantsUtils.REQUEST_PARAM_START_DATE%>"
							class="form-control" required
							value="<%=escapar(fechaInicioIntroducida)%>">
					</div>
					<div id="startDateError" class="error-banner" hidden></div>
					<!-- javascript para controlar que la fecha de inicio de evento no sea anterior a la fecha actual -->
					<div class="input-group">
						<label for="endDate" class="field-label"> FECHA DE FIN * </label>
						<input type="date" id="endDate"
							name="<%=ConstantsUtils.REQUEST_PARAM_END_DATE%>"
							class="form-control" required
							value="<%=escapar(fechaFinIntroducida)%>">
					</div>

					<div class="input-group">
						<label for="countryId" class="field-label">PAÍS *</label> <select
							id="createEventCountry"
							name="<%=ConstantsUtils.REQUEST_PARAM_COUNTRY_ID%>"
							class="form-control" required>
							<option value="">Selecciona un país</option>

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

					<div class="input-group">
						<label for="provinceId" class="field-label">PROVINCIA </label> <select
							id="createEventProvince"
							name="<%=ConstantsUtils.REQUEST_PARAM_PROVINCE_ID%>"
							class="form-control">
							<option value="">Selecciona una provincia</option>

							<%
							if (provincias != null) {
								for (ProvinceDTO provincia : provincias) {
							%>
							<option value="<%=provincia.getId()%>"
								<%=String.valueOf(provincia.getId()).equals(provinciaSeleccionada) ? "selected" : ""%>>
								<%=provincia.getName()%>
								<%
								if (provincia.getCountryName() != null) {
								%> (<%=provincia.getCountryName()%>)
								<%
								}
								%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>

					<div class="input-group">
						<label for="localityId" class="field-label">LOCALIDAD </label> <select
							id="createEventLocality"
							name="<%=ConstantsUtils.REQUEST_PARAM_LOCALITY_ID%>"
							class="form-control">
							<option value="">Selecciona una localidad</option>

							<%
							if (localidades != null) {
								for (LocalityDTO localidad : localidades) {
							%>
							<option value="<%=localidad.getId()%>"
								<%=String.valueOf(localidad.getId()).equals(localidadSeleccionada) ? "selected" : ""%>>
								<%=localidad.getName()%>
								<%
								if (localidad.getProvinceName() != null) {
								%> (<%=localidad.getProvinceName()%>)
								<%
								}
								%>
							</option>
							<%
							}
							}
							%>
						</select>
					</div>

				</div>

				<div class="search-card">
					<p>
						<strong>Organizador: </strong>
						<%=usuario.getName() != null ? usuario.getName() : ""%>
						<%=usuario.getLastName1() != null ? usuario.getLastName1() : ""%>
					</p>


				</div>

				<div class="form-actions"
					style="margin-top: 24px; display: flex; gap: 12px; justify-content: flex-end;">

					<a class="register-btn"
						style="width: auto; margin-top: 0; padding: 10px 20px;"
						href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_SEARCH %>">
						Cancelar </a>

					<button type="submit" class="btn-primary"
						style="padding: 10px 28px;">Crear evento</button>
				</div>

			</form>
		</section>

	</main>
</div>

<%@ include file="/html/common/footer.jsp"%>