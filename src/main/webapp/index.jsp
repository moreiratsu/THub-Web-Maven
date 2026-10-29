<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- HEADER -->
<%@ include file="/html/common/header.jsp" %> 

<!-- BARRA SUPERIOR -->
<%@ include file="/html/common/topbar.jsp" %>

<!-- DISTRIBUCIÓN DE CONTENIDO -->
<div class="dashboard-layout">

<!-- MENU LATERAL -->
<%@ include file="/html/common/asidebar.jsp" %>

<!-- CONTENIDO PRINCIPAL -->
<main class="content-area">
        <h2 class="section-title">Últimos eventos</h2>

        <!-- Banner Carrusel / Evento Destacado -->
        <div class="featured-banner">
          <button class="carousel-arrow left" aria-label="Anterior">&lt;</button>

          <div class="banner-body">
            <div class="ball-icon">⚽</div>
            <h3 class="banner-title">Torneo de Futbolín</h3>
            <p class="banner-location">Bar Manolo · Monforte de Lemos</p>

            <div class="carousel-dots">
              <span class="dot active"></span>
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>

          <button class="carousel-arrow right" aria-label="Siguiente">&gt;</button>
        </div>

        <!-- Cards de Estadísticas / Métricas -->
        <div class="stats-grid">

          <div class="stat-card">
            <span class="stat-label">EVENTOS ACTIVOS</span>
            <div class="stat-value">5</div>
            <div class="stat-sub">+2 este mes</div>
          </div>

          <div class="stat-card">
            <span class="stat-label">PARTICIPANTES</span>
            <div class="stat-value">128</div>
            <div class="stat-sub">+14 esta semana</div>
          </div>

          <div class="stat-card">
            <span class="stat-label">EQUIPOS</span>
            <div class="stat-value">23</div>
            <div class="stat-sub">Temporada 25/26</div>
          </div>

        </div>
      </main>
      
<!-- PIE DE PÁGINA -->      
<%@ include file="/html/common/footer.jsp" %>
