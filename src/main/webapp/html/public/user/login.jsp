<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.UserDTO"%>
<%@ page import="com.alvaro.thub.model.*"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils" %>

<%@ include file="/html/common/header.jsp"%>

<!-- Contenedor Principal -->
<main class="container"> 

	<!-- Sección Izquierda -->
	<section class="left-content">
		<div class="badge">PLATAFORMA DE TORNEOS DEPORTIVOS</div>

		<div class="hero-header-wrapper">
			<h1 class="main-title">
				Organiza.<br> <span class="highlight">Compite.</span><br>
				Triunfa.
			</h1>

			<!-- Logo -->
			<a href="${pageContext.request.contextPath}/index.jsp">
				<img src="${pageContext.request.contextPath}/img/logo.png" alt="Logo Thub" class="hero-logo">
			</a>
		</div>

		<p class="description">
			La herramienta todo en uno para gestionar torneos deportivos: inscripciones, equipos, resultados y clasificaciones desde un solo lugar.
		</p>

		<!-- Grid de Características -->
		<div class="features-grid">
			<div class="feature-card">
				<span class="feature-icon">🏆</span>
				<div class="feature-info">
					<h3>Gestión de torneos</h3>
					<p>Crea y administra competiciones de cualquier deporte.</p>
				</div>
			</div>

			<div class="feature-card">
				<span class="feature-icon">👥</span>
				<div class="feature-info">
					<h3>Equipos y participantes</h3>
					<p>Inscripciones, plantillas y estadísticas en tiempo real.</p>
				</div>
			</div>

			<div class="feature-card">
				<span class="feature-icon">📅</span>
				<div class="feature-info">
					<h3>Calendario integrado</h3>
					<p>Planifica jornadas, fases y eliminatorias.</p>
				</div>
			</div>

			<div class="feature-card">
				<span class="feature-icon">🏅</span>
				<div class="feature-info">
					<h3>Clasificaciones y premios</h3>
					<p>Resultados automáticos y gestión de premios.</p>
				</div>
			</div>
		</div>
	</section>

	<!-- Sección Derecha (Panel de Inicio de Sesión) -->
	<aside class="right-content">
		<h2>Accede a tu cuenta</h2>
		<p class="subtitle">Selecciona tu rol y entra a la plataforma</p>

		<%-- Bloque de error traído desde el UserServlet --%>
		<% 
			String errorMessage = (String) request.getAttribute("error");
			if (errorMessage != null && !errorMessage.isEmpty()) { 
		%>
			<div class="error-banner">
				<%= errorMessage %>
			</div>
		<% } %>

		<!-- Formulario apuntando directamente al Servlet PUBLICO de Usuario -->
		<form class="login-form" action="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_USER_SERVLET %>" method="POST">

			<input type="hidden" name="<%= ConstantsUtils.REQUEST_PARAM_ACTION %>" value="login"> 
			<span class="field-label">ROL</span>

			<div class="role-selector">
				<label class="role-option">
					<input type="radio" name="<%= ConstantsUtils.REQUEST_PARAM_ROL_ID %>" value="3" checked>
					<span class="radio-custom"></span>
					<div class="role-text">
						<strong>Organizador</strong>
						<p>Crea eventos, equipos, participantes. Gestiona inscripciones y pagos.</p>
					</div>
				</label>

				<label class="role-option">
					<input type="radio" name="<%= ConstantsUtils.REQUEST_PARAM_ROL_ID %>" value="4">
					<span class="radio-custom"></span>
					<div class="role-text">
						<strong>Deportista</strong>
						<p>Consulta datos, resultados y estadísticas. Inscríbete en eventos individuales.</p>
					</div>
				</label>

				<label class="role-option">
					<input type="radio" name="<%= ConstantsUtils.REQUEST_PARAM_ROL_ID %>" value="5">
					<span class="radio-custom"></span>
					<div class="role-text">
						<strong>Entrenador</strong>
						<p>Gestiona equipos y consulta calendarios.</p>
					</div>
				</label>

				<label class="role-option">
					<input type="radio" name="<%= ConstantsUtils.REQUEST_PARAM_ROL_ID %>" value="2">
					<span class="radio-custom"></span>
					<div class="role-text">
						<strong>Árbitro</strong>
						<p>Actualiza resultados de encuentros y partidos.</p>
					</div>
				</label>

				<label class="role-option">
					<input type="radio" name="<%= ConstantsUtils.REQUEST_PARAM_ROL_ID %>" value="6">
					<span class="radio-custom"></span>
					<div class="role-text">
						<strong>Fan</strong>
						<p>Sigue eventos y equipos en tiempo real.</p>
					</div>
				</label>
			</div>

			<hr class="divider">

			<!-- Formulario de credenciales -->
			<div class="input-group">
				<label for="email" class="field-label">USUARIO / EMAIL</label> 
				<input type="email" id="email" name="<%= ConstantsUtils.REQUEST_PARAM_EMAIL %>" placeholder="tu@email.com" required autocomplete="off">
			</div>

			<div class="input-group">
				<label for="password" class="field-label">CONTRASEÑA</label>
				<div class="password-wrapper">
					<input type="password" id="password" name="<%= ConstantsUtils.REQUEST_PARAM_PASSWORD %>" placeholder="••••••••" required>
					<button type="button" class="toggle-password" aria-label="Mostrar contraseña">
						<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
							<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
							<circle cx="12" cy="12" r="3"></circle>
						</svg>
					</button>
				</div>
			</div>

			<a href="#" class="forgot-password">¿Olvidaste tu contraseña?</a>

			<!-- Botón de envío del formulario -->
			<button type="submit" class="submit-btn">Iniciar Sesión</button>

			<!-- Enlace de Registro -->
			<a href="${pageContext.request.contextPath}/html/public/user/register.jsp" class="register-btn">
				Crear cuenta 
			</a>
		</form>
	</aside>

</main>

<!-- Botón de Ayuda Flotante -->
<button class="help-btn" aria-label="Ayuda">?</button>

<%@ include file="/html/common/footer.jsp"%>
