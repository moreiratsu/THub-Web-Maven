<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.UserDTO"%>
<%@ page import="com.alvaro.thub.model.*"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils" %>

<%@ include file="/html/common/header.jsp"%>

<!-- Contenedor Principal -->
<main class="container"> 	
	
	<!-- Sección Izquierda -->
	<section class="left-content">
	
	<!-- VIDEO DE FONDO -->
    <video class="login-background-video"
           autoplay muted loop playsinline aria-hidden="true">
        <source
            src="${pageContext.request.contextPath}/video/fondo-login.mp4"
            type="video/mp4">
    </video>
	
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
