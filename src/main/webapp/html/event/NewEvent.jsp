<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.alvaro.thub.model.UserDTO"%>
<%@ page import="com.alvaro.thub.model.LocalityDTO"%>

<%@ include file="/html/common/header.jsp"%>
<%@ include file="/html/common/topbar.jsp"%>

<%
    // Verificación de sesión de usuario
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/html/user/login.jsp");
        return;
    }

    // Recuperamos las listas necesarias cargadas por el Servlet
    List<LocalityDTO> localidades = (List<LocalityDTO>) request.getAttribute("localidades");
%>

<div class="dashboard-layout">

    <!-- Barra lateral -->
    <%@ include file="/html/common/asidebar.jsp"%>

    <!-- Área de Contenido Principal -->
    <main class="content-area">

        <div class="page-header">
            <h1 class="section-title">Crear Nuevo Evento</h1>
            <a href="${pageContext.request.contextPath}/Event?action=search" class="role-badge">
                ← Volver a Eventos
            </a>
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

        <!-- Formulario de Creación de Evento -->
        <section class="search-card">
            <h2 class="section-title" style="font-size: 1rem; margin-bottom: 20px;">Información del Torneo / Evento</h2>

            <form action="${pageContext.request.contextPath}/Event" method="POST">
                <input type="hidden" name="action" value="createEvent">

                <div class="filter-grid">
                    
                    <!-- Nombre del Evento -->
                    <div class="input-group" style="grid-column: span 2;">
                        <label for="name" class="field-label">NOMBRE DEL EVENTO / TORNEO *</label>
                        <input type="text" id="name" name="name" class="form-control" 
                               placeholder="Ej: Torneo Regional de Baloncesto 2026" required autocomplete="off">
                    </div>

                    <!-- Deporte / Tipo de Evento -->
                    <div class="input-group">
                        <label for="sport" class="field-label">DEPORTE / DISCIPLINA *</label>
                        <input type="text" id="sport" name="sport" class="form-control" 
                               placeholder="Ej: Fútbol 7, Baloncesto, Pádel..." required autocomplete="off">
                    </div>

                    <!-- Localidad -->
                    <div class="input-group">
                        <label for="localityId" class="field-label">LOCALIDAD *</label>
                        <select id="localityId" name="localityId" class="form-control" required>
                            <option value="">-- Selecciona Localidad --</option>
                            <% 
                                if (localidades != null && !localidades.isEmpty()) {
                                    for (LocalityDTO loc : localidades) {
                            %>
                                <option value="<%= loc.getId() %>">
                                    <%= loc.getName() %> (<%= loc.getProvinceName() != null ? loc.getProvinceName() : "" %>)
                                </option>
                            <% 
                                    }
                                } 
                            %>
                        </select>
                    </div>

                    <!-- Dirección / Instalación -->
                    <div class="input-group" style="grid-column: span 2;">
                        <label for="location" class="field-label">LUGAR / INSTALACIÓN DEPORTIVA</label>
                        <input type="text" id="location" name="location" class="form-control" 
                               placeholder="Ej: Pabellón Municipal Polideportivo" autocomplete="off">
                    </div>

                    <!-- Fecha de Inicio -->
                    <div class="input-group">
                        <label for="startDate" class="field-label">FECHA DE INICIO *</label>
                        <input type="date" id="startDate" name="startDate" class="form-control" required>
                    </div>

                    <!-- Fecha de Fin -->
                    <div class="input-group">
                        <label for="endDate" class="field-label">FECHA DE FIN *</label>
                        <input type="date" id="endDate" name="endDate" class="form-control" required>
                    </div>

                    <!-- Límite de Participantes / Equipos -->
                    <div class="input-group">
                        <label for="maxParticipants" class="field-label">Nº MÁXIMO DE PARTICIPANTES/EQUIPOS</label>
                        <input type="number" id="maxParticipants" name="maxParticipants" class="form-control" 
                               placeholder="Ej: 16" min="2">
                    </div>

                    <!-- Precio de Inscripción -->
                    <div class="input-group">
                        <label for="price" class="field-label">PRECIO DE INSCRIPCIÓN (€)</label>
                        <input type="number" id="price" name="price" class="form-control" 
                               placeholder="0.00" step="0.01" min="0">
                    </div>

                </div>

                <!-- Descripción del Evento -->
                <div class="input-group" style="margin-top: 16px;">
                    <label for="description" class="field-label">DESCRIPCIÓN Y REGLAMENTO</label>
                    <textarea id="description" name="description" class="form-control" rows="4" 
                              placeholder="Añade detalles sobre las reglas, premios, formato de la competición..." style="resize: vertical;"></textarea>
                </div>

                <div class="form-actions" style="margin-top: 24px; display: flex; gap: 12px; justify-content: flex-end;">
                    <a href="${pageContext.request.contextPath}/Event?action=search" class="register-btn" style="width: auto; margin-top: 0; padding: 10px 20px;">
                        Cancelar
                    </a>
                    <button type="submit" class="btn-primary" style="padding: 10px 28px;">
                        Crear Evento
                    </button>
                </div>

            </form>
        </section>

    </main>
</div>

<%@ include file="/html/common/footer.jsp"%>