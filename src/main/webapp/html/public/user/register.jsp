<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.alvaro.thub.model.UserDTO"%>
<%@ page import="com.alvaro.thub.utils.ConstantsUtils" %>

<%@ include file="/html/common/header.jsp"%>

<!-- Contenedor Principal -->
<main class="container">

	<!-- Sección Izquierda (Informativa / Hero) -->
	<section class="left-content">
		<div class="badge">ÚNETE A LA PLATAFORMA</div>

		<div class="hero-header-wrapper">
			<h1 class="main-title">
				Comienza a<br> <span class="highlight">Competir.</span><br>
				Hoy mismo.
			</h1>

			<!-- Logo -->
			<a href="${pageContext.request.contextPath}/index.jsp" class="#">
				<img src="${pageContext.request.contextPath}/img/logo.png"
				alt="Logo Darkness TCG" class="hero-logo">
			</a>
		</div>

		<p class="description">Crea tu cuenta en TRNMT HUB para organizar
			torneos, inscribir tus equipos o participar como deportista en
			nuestras competiciones.</p>

		<!-- Grid de Características -->
		<div class="features-grid">
			<div class="feature-card">
				<span class="feature-icon">🛡️</span>
				<div class="feature-info">
					<h3>Perfil personalizado</h3>
					<p>Acceso a tu panel según tu rol en la plataforma.</p>
				</div>
			</div>

			<div class="feature-card">
				<span class="feature-icon">⚡</span>
				<div class="feature-info">
					<h3>Inscripciones rápidas</h3>
					<p>Únete a eventos y torneos en cuestión de clics.</p>
				</div>
			</div>
		</div>
	</section>

	<!-- Sección Derecha (Formulario de Registro) -->
	<aside class="right-content">
		<h2>Crear una cuenta</h2>
		<p class="subtitle">Rellena tus datos para registrarte</p>

		<%-- Bloque para mostrar errores traídos desde el Servlet --%>
		<%
		String errorMessage = (String) request.getAttribute("error");
		if (errorMessage != null && !errorMessage.isEmpty()) {
		%>
		<div class="error-banner">
			<%=errorMessage%>
		</div>
		<%
		}
		%>

		<!-- Formulario apuntando al UserServlet publico -->
		<form class="login-form"
			action="${pageContext.request.contextPath}<%= ConstantsUtils.PUBLIC_USER_SERVLET %>" method="POST">

			<input type="hidden" name="action" value="register">

			<!-- Selector de Rol -->
<span class="field-label">SELECCIONA TU ROL</span>

<div class="role-selector">
  <label class="role-option">
    <input type="radio" name="rolId" value="3" checked>
    <span class="radio-custom"></span>
    <div class="role-text">
      <strong>Organizador</strong>
      <p>Gestiona torneos, inscripciones y eventos.</p>
    </div>
  </label>

  <label class="role-option">
    <input type="radio" name="rolId" value="4">
    <span class="radio-custom"></span>
    <div class="role-text">
      <strong>Deportista / Participante</strong>
      <p>Inscríbete en eventos y consulta tus estadísticas.</p>
    </div>
  </label>

  <label class="role-option">
    <input type="radio" name="rolId" value="5">
    <span class="radio-custom"></span>
    <div class="role-text">
      <strong>Entrenador</strong>
      <p>Crea y gestiona equipos.</p>
    </div>
  </label>
</div>

			<hr class="divider">

			<!-- Campo Nombre -->
			<div class="input-group">
				<label for="name" class="field-label">NOMBRE</label> <input
					type="text" id="name" name="name"
					placeholder="Ej: Cual es tu nombre?" required autocomplete="off">
			</div>

			<!-- Campo Email -->
			<div class="input-group">
				<label for="email" class="field-label">CORREO ELECTRÓNICO</label> <input
					type="email" id="email" name="email" placeholder="tu@email.com"
					required autocomplete="off">
			</div>

			<!-- Campo Contraseña -->
			<div class="input-group">
				<label for="password" class="field-label">CONTRASEÑA</label>
				<div class="password-wrapper">
					<input type="password" id="password" name="password"
						placeholder="••••••••" required>
					<button type="button" class="toggle-password"
						aria-label="Mostrar contraseña">
						<svg width="20" height="20" viewBox="0 0 24 24" fill="none"
							stroke="currentColor" stroke-width="1.8">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
					</button>
				</div>
			</div>

			<!-- Confirmar Contraseña -->
			<div class="input-group">
				<label for="confirmPassword" class="field-label">CONFIRMAR
					CONTRASEÑA</label>
				<div class="password-wrapper">
					<input type="password" id="confirmPassword" name="confirmPassword"
						placeholder="••••••••" required>
				</div>
			</div>

			<!-- Botón Registrarse -->
			<button type="submit" class="submit-btn submit-btn-register">Registrarse</button>

			<!-- Enlace a Login -->
			<a href="${pageContext.request.contextPath}/html/public/user/login.jsp"
				class="register-btn"> ¿Ya tienes cuenta? Inicia sesión </a>
		</form>
	</aside>

</main>

<!-- Botón de Ayuda Flotante -->
<button class="help-btn" aria-label="Ayuda">?</button>

<%@ include file="/html/common/footer.jsp"%>
