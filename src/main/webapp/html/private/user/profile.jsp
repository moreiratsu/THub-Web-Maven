<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.alvaro.thub.model.UserDTO"%>
<%@ page import="com.alvaro.thub.model.LocalityDTO"%>
<%@ page import="com.alvaro.thub.model.Gender"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils" %>

<%@ include file="/html/common/header.jsp"%>
<%@ include file="/html/common/topbar.jsp"%> 

<%   
    // Si no hay sesión iniciada, redirigimos al login
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/html/public/user/login.jsp");
        return;
    }

    // 2. Recuperamos las listas enviadas por el UserServlet
    List<LocalityDTO> localidades = (List<LocalityDTO>) request.getAttribute("localidades");
    List<Gender> generos = (List<Gender>) request.getAttribute("generos");
%>

<div class="dashboard-layout">

    <!-- Incluimos la barra lateral (Sidebar) -->
    <%@ include file="/html/common/asidebar.jsp"%>

    <!-- Área de Contenido Principal -->
    <main class="content-area">

        <div class="page-header">
            <h1 class="section-title">Mi Perfil</h1>
        </div>

        <%-- Mensajes de Éxito / Error --%>
        <% 
            String successMessage = (String) request.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("error");
            
            if (successMessage != null && !successMessage.isEmpty()) { 
        %>
            <div class="success-banner">
                <%= successMessage %>
            </div>
        <% } %>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-banner">
                <%= errorMessage %>
            </div>
        <% } %>

        <!-- Formulario para seleccionar y enviar la foto de perfil -->
        <section class="search-card">
            <h2 class="section-title" style="font-size: 1rem; margin-bottom: 16px;">Foto de perfil</h2>
			<!-- AQUI mostramos la foto de perfil actual del usuario, si existe , si no mostramos un placeholder -->
<img class="profile-photo" src="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_USER_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=showProfilePhoto"
     alt="Foto de perfil"><br>
			<form action="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_USER_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=uploadProfilePhoto" method="POST" enctype="multipart/form-data">

                <div class="input-group">
                    <label for="profilePhoto" class="field-label">SELECCIONA UNA IMAGEN</label>
					<input type="file" id="profilePhoto" name="photo" class="form-control"
                           accept=".png" aria-describedby="profilePhotoHelp"/>
                    <small id="profilePhotoHelp">Formato permitido: PNG.</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Subir foto</button>
                </div>
            </form>
        </section>

        <!-- Tarjeta de Edición de Datos Personales -->
        <section class="search-card">
            <h2 class="section-title" style="font-size: 1rem; margin-bottom: 16px;">Datos Personales</h2>

            <form action="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_USER_SERVLET %>" method="POST">
                <input type="hidden" name="<%= ConstantsUtils.REQUEST_PARAM_ACTION %>" value="updateProfile">
                <input type="hidden" name="<%= ConstantsUtils.REQUEST_PARAM_ID %>" value="<%= usuario.getId() %>">

                <div class="filter-grid">
                    
                    <!-- DNI -->
                    <div class="input-group">
                        <label for="dni" class="field-label">DNI / NIE</label>
                        <input type="text" id="dni" name="<%= ConstantsUtils.REQUEST_PARAM_DNI %>" class="form-control" 
                               value="<%= usuario.getDni() != null ? usuario.getDni() : "" %>" 
                               placeholder="12345678X" autocomplete="off">
                    </div>

                    <!-- Nombre -->
                    <div class="input-group">
                        <label for="name" class="field-label">NOMBRE</label>
                        <input type="text" id="name" name="<%= ConstantsUtils.REQUEST_PARAM_NAME %>" class="form-control" 
                               value="<%= usuario.getName() != null ? usuario.getName() : "" %>" 		
                               required autocomplete="off">
                    </div>

                    <!-- Primer Apellido -->
                    <div class="input-group">
                        <label for="lastName1" class="field-label">PRIMER APELLIDO</label>
                        <input type="text" id="lastName1" name="<%= ConstantsUtils.REQUEST_PARAM_LAST_NAME_1 %>" class="form-control" 
                               value="<%= usuario.getLastName1() != null ? usuario.getLastName1() : "" %>" 
                               placeholder="Primer apellido" autocomplete="off">
                    </div>

                    <!-- Segundo Apellido -->
                    <div class="input-group">
                        <label for="lastName2" class="field-label">SEGUNDO APELLIDO</label>
                        <input type="text" id="lastName2" name="<%= ConstantsUtils.REQUEST_PARAM_LAST_NAME_2 %>" class="form-control" 
                               value="<%= usuario.getLastName2() != null ? usuario.getLastName2() : "" %>" 
                               placeholder="Segundo apellido" autocomplete="off">
                    </div>

                    <!-- Correo Electrónico -->
                    <div class="input-group">
                        <label for="email" class="field-label">CORREO ELECTRÓNICO</label>
                        <input type="email" id="email" name="<%= ConstantsUtils.REQUEST_PARAM_EMAIL %>" class="form-control" 
                               value="<%= usuario.getEmail() != null ? usuario.getEmail() : "" %>" 
                               required autocomplete="off">
                    </div>

                    <!-- Teléfono -->
                    <div class="input-group">
                        <label for="phoneNumber" class="field-label">TELÉFONO DE CONTACTO</label>
                        <input type="tel" id="phoneNumber" name="<%= ConstantsUtils.REQUEST_PARAM_PHONE %>" class="form-control" 
                               value="<%= usuario.getPhoneNumber() != null ? usuario.getPhoneNumber() : "" %>" 
                               placeholder="+34 600 000 000" autocomplete="off">
                    </div>

                    <!-- Fecha de Nacimiento -->
                    <div class="input-group">
                        <label for="bornDate" class="field-label">FECHA DE NACIMIENTO</label>
                        <input type="date" id="bornDate" name="<%= ConstantsUtils.REQUEST_PARAM_BORN_DATE %>" class="form-control" 
                               value="<%= usuario.getBornDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(usuario.getBornDate()) : "" %>">
                    </div>

                    <!-- ComboBox de Género Dinámico -->
                    <div class="input-group">
                        <label for="genderId" class="field-label">GÉNERO</label>
                        <select id="genderId" name="<%= ConstantsUtils.REQUEST_PARAM_GENDER_ID %>" class="form-control">
                            <option value="">-- Selecciona Género --</option>
                            <% 
                                if (generos != null && !generos.isEmpty()) {
                                    for (Gender g : generos) {
                                        boolean isSelected = (usuario.getGenderId() != null && usuario.getGenderId().equals(g.getId()));
                            %>
                                <option value="<%= g.getId() %>" <%= isSelected ? "selected" : "" %>>
                                    <%= g.getName() %>
                                </option>
                            <% 
                                    }
                                } 
                            %>
                        </select>
                    </div>

                    <!-- ComboBox de Localidad Dinámico -->
                    <div class="input-group">
                        <label for="localityId" class="field-label">LOCALIDAD</label>
                        <select id="localityId" name="<%= ConstantsUtils.REQUEST_PARAM_LOCALITY_ID %>" class="form-control">
                            <option value="">-- Selecciona Localidad --</option>
                            <% 
                                if (localidades != null && !localidades.isEmpty()) {
                                    for (LocalityDTO loc : localidades) {
                                        boolean isSelected = (usuario.getLocalityId() != null && usuario.getLocalityId().equals(loc.getId()));
                            %>
                                <option value="<%= loc.getId() %>" <%= isSelected ? "selected" : "" %>>
                                    <%= loc.getName() %>
                                </option>
                            <% 
                                    }
                                } 
                            %>
                        </select>
                    </div>

                </div>

                <div class="form-actions" style="margin-top: 16px;">
                    <button type="submit" class="btn-primary">Guardar Cambios</button>
                </div>
            </form>
        </section>

        <!-- Tarjeta de Cambio de Contraseña -->
        <section class="search-card">
            <h2 class="section-title" style="font-size: 1rem; margin-bottom: 16px;">Seguridad</h2>

            <form action="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_USER_SERVLET %>" method="POST">
                <input type="hidden" name="<%= ConstantsUtils.REQUEST_PARAM_ACTION %>" value="updatePassword">
                <input type="hidden" name="<%= ConstantsUtils.REQUEST_PARAM_ID %>" value="<%= usuario.getId() %>">

                <div class="filter-grid">
                    
                    <div class="input-group">
                        <label for="oldPassword" class="field-label">CONTRASEÑA ACTUAL</label>
                        <input type="password" id="oldPassword" name="<%= ConstantsUtils.REQUEST_PARAM_OLD_PASSWORD %>" class="form-control" placeholder="••••••••" required>
                    </div>

                    <div class="input-group">
                        <label for="newPassword" class="field-label">NUEVA CONTRASEÑA</label>
                        <input type="password" id="newPassword" name="<%= ConstantsUtils.REQUEST_PARAM_NEW_PASSWORD %>" class="form-control" placeholder="••••••••" required>
                    </div>

                    <div class="input-group">
                        <label for="confirmNewPassword" class="field-label">CONFIRMAR NUEVA CONTRASEÑA</label>
                        <input type="password" id="confirmNewPassword" name="<%= ConstantsUtils.REQUEST_PARAM_CONFIRM_NEW_PASSWORD %>" class="form-control" placeholder="••••••••" required>
                    </div>

                </div>

                <div class="form-actions" style="margin-top: 16px;">
                    <button type="submit" class="btn-primary">Actualizar Contraseña</button>
                </div>
            </form>
        </section>

    </main>
</div>

<%@ include file="/html/common/footer.jsp"%>
