<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>404 | Página no encontrada - THUB</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>
    <main class="error-page">
        <section class="error-card" aria-labelledby="error-title">
            <img class="error-logo"
                 src="${pageContext.request.contextPath}/img/logo.png"
                 alt="THUB">

            <svg class="error-illustration"
                 viewBox="0 0 360 220"
                 role="img"
                 aria-labelledby="ball-title ball-description"
                 xmlns="http://www.w3.org/2000/svg">
                <title id="ball-title">El balón ha dado en el poste</title>
                <desc id="ball-description">
                    Un balón de fútbol rebota contra el poste de una portería.
                </desc>

                <ellipse cx="170" cy="193" rx="125" ry="12"
                         fill="var(--card-border)" opacity=".65"/>
                <path d="M48 190H310"
                      stroke="var(--text-secondary)"
                      stroke-width="3"
                      stroke-linecap="round"
                      opacity=".6"/>

                <!-- Red de la portería -->
                <path d="M199 65H305V190H199Z"
                      fill="var(--sidebar-bg)"
                      stroke="var(--text-secondary)"
                      stroke-width="3"
                      opacity=".8"/>
                <path d="M220 68V188M242 68V188M264 68V188M286 68V188
                         M202 90H302M202 115H302M202 140H302M202 165H302"
                      stroke="var(--card-border)"
                      stroke-width="2"/>

                <!-- Poste y larguero -->
                <path d="M199 65V190M199 65H305"
                      fill="none"
                      stroke="var(--accent-gold)"
                      stroke-width="9"
                      stroke-linecap="round"
                      stroke-linejoin="round"/>

                <!-- Movimiento e impacto -->
                <path d="M74 105L96 112M66 128L88 130M78 151L99 146"
                      stroke="var(--accent-gold)"
                      stroke-width="4"
                      stroke-linecap="round"/>
                <path d="M185 100L191 111L203 105L198 118L211 122L198 127
                         L203 140L191 134L184 146L181 132L167 135L175 123
                         L165 114L180 114Z"
                      fill="var(--accent-gold)"
                      opacity=".95"/>

                <!-- Balón -->
                <g transform="translate(133 125) rotate(-18)">
                    <circle r="35"
                            fill="var(--card-bg)"
                            stroke="var(--text-primary)"
                            stroke-width="3"/>
                    <path d="M0-13L13-4L8 11H-8L-13-4Z"
                          fill="var(--text-primary)"/>
                    <path d="M0-13L-4-31M13-4L31-10M8 11L20 27M-8 11L-20 27
                             M-13-4L-31-10"
                          fill="none"
                          stroke="var(--text-primary)"
                          stroke-width="4"
                          stroke-linecap="round"/>
                </g>
            </svg>

            <p class="error-code">404</p>
            <h1 class="error-title" id="error-title">
                ¡Uy! Se nos fue al palo
            </h1>
            <p class="error-message">
                No encontramos la dirección que buscas. Puede que el enlace
                esté mal escrito o que esa página todavía no esté disponible.
            </p>

            <div class="error-actions">
                <button class="error-button error-button-primary"
                        type="button"
                        onclick="if (history.length > 1) { history.back(); } else { window.location.href='${pageContext.request.contextPath}/index.jsp'; }">
                    Volver atrás
                </button>

                <a class="error-button error-button-secondary"
                   href="${pageContext.request.contextPath}/index.jsp">
                    Ir al inicio
                </a>
            </div>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/js/js.js"></script>
</body>
</html>