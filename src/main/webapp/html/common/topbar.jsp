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
      <a class="btn-primary"
   href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_CREATE %>">
    <span>+</span> Crea tu propio evento!
</a>
      
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
          <!-- Usuario conectado: botón que abrirá el desplegable -->
<div class="account-dropdown">

    <button type="button"
            id="accountDropdownButton"
            class="role-badge role-badge-user account-menu-button"
            aria-expanded="false"
            aria-controls="accountDropdownMenu"
            onclick="toggleAccountDropdown()">

        <svg width="18" height="18" viewBox="0 0 24 24"
             fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
        </svg>

        <div class="user-badge-text">
            <span class="user-name-text"><%= usuarioTopbar.getName() %></span>
            <span class="user-role-text">
                <%= usuarioTopbar.getRolName() != null
                        ? usuarioTopbar.getRolName()
                        : "Usuario" %>
            </span>
        </div>

    </button>

    <div id="accountDropdownMenu"
         class="account-dropdown-menu"
         hidden>


	<a class="account-dropdown-item account-profile"
           href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_USER_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=showProfile">
            <svg width="18" height="18" viewBox="0 0 24 24"
             fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
        </svg>
            <span>Mi perfil</span>
        </a>

        <a class="account-dropdown-item"
           href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_EVENT_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=<%= ConstantsUtils.REQUEST_EVENT_ACTION_SHOW_MY_EVENTS %>">
            <svg width="18" height="18" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2">
                <path d="M8 6h13M8 12h13M8 18h13"></path>
                <path d="M3 6h.01M3 12h.01M3 18h.01"></path>
            </svg>
            <span>Mis eventos</span>
        </a>

        <a class="account-dropdown-item account-logout-item"
           href="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_USER_SERVLET %>?<%= ConstantsUtils.REQUEST_PARAM_ACTION %>=logout">
            <svg width="18" height="18" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2">
                <path d="M10 17l5-5-5-5"></path>
                <path d="M15 12H3"></path>
                <path d="M12 3h7a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-7"></path>
            </svg>
            <span>Cerrar sesión</span>
        </a>

    </div>
</div>

      <% 
        } 
      %>
    </div>
  </div>
</header>
