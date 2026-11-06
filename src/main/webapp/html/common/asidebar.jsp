<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.UserDTO"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils"%>

<%
    UserDTO usuarioSidebar = (UserDTO) session.getAttribute(ConstantsUtils.USER_SESSION_ATTRIBUTE);

    // Detectamos la ruta o URL actual para saber qué elemento marcar como activo
    String currentURI = request.getRequestURI();
    boolean isIndex = currentURI.endsWith("index.jsp") || currentURI.endsWith("/");
    boolean isProfile = currentURI.contains("profile.jsp") || (request.getQueryString() != null && request.getQueryString().contains("showProfile"));
    boolean isEvent = currentURI.contains("/event/");
    boolean isTeam = currentURI.contains("/team/");
    boolean isPlayer = currentURI.contains("/player/");

%>

<!-- Barra lateral -->
<aside class="sidebar">
	
	<!-- Contenido del usuario en la barra lateral -->
	<div class="sidebar-user-container">
		<% if (usuarioSidebar == null) { %>
		<!-- Si NO está logueado -->
		<a href="${pageContext.request.contextPath}/html/public/user/login.jsp" class="user-profile user-profile-link">
			<div class="avatar-circle">
				<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
	              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
	              <circle cx="12" cy="7" r="4"></circle>
	            </svg>
			</div> 
			<span class="user-name">Mi cuenta</span>
		</a>
		<% } else { %>
		<!-- Si SÍ está logueado: Enlaza al Servlet mediante action=showProfile -->
		<a href="${pageContext.request.contextPath}<%= ConstantsUtils.PRIVATE_USER_SERVLET %>?action=showProfile" class="user-profile <%= isProfile ? "active" : "" %>">
			<div class="avatar-circle">
				<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
	              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
	              <circle cx="12" cy="7" r="4"></circle>
	            </svg>
			</div> 
			<span class="user-name"><%= usuarioSidebar.getName() %></span>
		</a>
		<% } %>
	</div>

	<!-- Menú de Navegación -->
	<nav class="sidebar-nav">
	
		<!-- INICIO -->	
		<a href="${pageContext.request.contextPath}/index.jsp" class="nav-item <%= isIndex ? "active" : "" %>">
			<svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
				<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
			</svg> 
			Inicio
		</a>

		<!-- EVENTOS -->
		<div class="nav-item-dropdown <%= isEvent ? "open active" : "" %>">
			<button class="nav-item nav-dropdown-btn" onclick="toggleDropdown(this)">
				<div class="nav-item-content">
					<svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
						<rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
						<line x1="16" y1="2" x2="16" y2="6"></line>
						<line x1="8" y1="2" x2="8" y2="6"></line>
						<line x1="3" y1="10" x2="21" y2="10"></line>
					</svg>
					<span>Eventos</span>
				</div>
				<svg class="chevron-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
					<polyline points="6 9 12 15 18 9"></polyline>
				</svg>
			</button>

			<div class="submenu">
				<a href="${pageContext.request.contextPath}/html/event/EventSearch.jsp" class="submenu-item">
					<span class="submenu-dot">•</span> Buscar Evento
				</a> 
				<a href="${pageContext.request.contextPath}/Event?action=showNewEvent">
					<span class="submenu-dot">•</span> Nuevo Evento
				</a>
			</div>
		</div>
		
		<!-- EQUIPOS -->		
		<div class="nav-item-dropdown <%= isTeam ? "open active" : "" %>">
			<button class="nav-item nav-dropdown-btn" onclick="toggleDropdown(this)">
				<div class="nav-item-content">
					<svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
						<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
					</svg>
					<span>Equipos</span>
				</div>
				<svg class="chevron-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
					<polyline points="6 9 12 15 18 9"></polyline>
				</svg>
			</button>

			<div class="submenu">
				<a href="${pageContext.request.contextPath}/html/team/TeamSearch.jsp" class="submenu-item">
					<span class="submenu-dot">•</span> Buscar Equipo
				</a> 
				<a href="${pageContext.request.contextPath}/html/team/NewTeam.jsp" class="submenu-item">
					<span class="submenu-dot">•</span> Nuevo Equipo
				</a>
			</div>
		</div>
		
		<!-- PARTICIPANTES -->
		<div class="nav-item-dropdown <%= isPlayer ? "open active" : "" %>">
			<button class="nav-item nav-dropdown-btn" onclick="toggleDropdown(this)">
				<div class="nav-item-content">
					<svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
						<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
						<circle cx="12" cy="7" r="4"></circle>
					</svg>
					<span>Participantes</span>
				</div>
				<svg class="chevron-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
					<polyline points="6 9 12 15 18 9"></polyline>
				</svg>
			</button>

			<div class="submenu">
				<a href="${pageContext.request.contextPath}/html/player/PlayerSearch.jsp" class="submenu-item">
					<span class="submenu-dot">•</span> Buscar Participante
				</a> 
				<a href="${pageContext.request.contextPath}/html/player/NewPlayer.jsp" class="submenu-item">
					<span class="submenu-dot">•</span> Nuevo Participante
				</a>
			</div>
		</div>
		
	</nav>
</aside>
