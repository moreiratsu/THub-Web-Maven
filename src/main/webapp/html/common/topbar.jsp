<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.UserDTO" %>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils" %>

<% UserDTO usuarioTopbar = (UserDTO) session.getAttribute(ConstantsUtils.USER_SESSION_ATTRIBUTE); %>

<!-- BARRA SUPERIOR -->
<header class="topbar">
  <!-- Logo -->
  <div class="topbar-logo-box">
    <a href="${pageContext.request.contextPath}/index.jsp" class="topbar-logo-link">
      <img src="${pageContext.request.contextPath}/img/logo.png" alt="THUB Logo" class="topbar-logo">
    </a>
  </div>

  <div class="topbar-main">
    <h1 class="page-title"></h1>
    
    <div class="topbar-actions">
    
      <!-- Botón cambiar de tema -->
      <button id="theme-toggle" class="theme-toggle-btn" type="button" onclick="toggleTheme()" title="Cambiar tema">
          <svg id="theme-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="5"></circle>
              <line x1="12" y1="1" x2="12" y2="3"></line>
              <line x1="12" y1="21" x2="12" y2="23"></line>
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
              <line x1="1" y1="12" x2="3" y2="12"></line>
              <line x1="21" y1="12" x2="23" y2="12"></line>
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
          </svg>
      </button>
    
      <!-- Botón de Organizar Evento -->
      <button class="btn-primary">
      <a href="${pageContext.request.contextPath}/Event?action=showNewEvent">
        <span>+</span> Organiza un evento
        </a>
      </button>
      
      <!-- Botón de Cuenta de Usuario -->
      <%
        
        
        if (usuarioTopbar == null) {
      %>
          <!-- Si NO está logueado: Muestra el botón para ir al login -->
          <a href="${pageContext.request.contextPath}/html/public/user/login.jsp" class="role-badge">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <span>Mi cuenta</span>
          </a>
      <% 
        } else { 
      %>
          <!-- Si SÍ está logueado: Llama a la acción showProfile del Servlet -->
          <a href="${pageContext.request.contextPath}/Usuario?action=showProfile" class="role-badge role-badge-user">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <div class="user-badge-text">
              <span class="user-name-text"><%= usuarioTopbar.getName() %></span>
              <span class="user-role-text">
                <%= usuarioTopbar.getRolName() != null ? usuarioTopbar.getRolName() : "Usuario" %>
               </span>
            </div>
          </a>

          <!-- Botón de Cierre de Sesión -->
          <a href="${pageContext.request.contextPath}/Usuario?action=logout" class="btn-primary" style="background-color: #222; color: #fff; text-decoration: none;">
            Salir
          </a>
      <% 
        } 
      %>
    </div>
  </div>
</header>
