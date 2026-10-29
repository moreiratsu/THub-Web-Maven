
// Funcion para desplegar el submenu de las opciones del sidebar
function toggleDropdown(button) {
  const dropdownContainer = button.closest('.nav-item-dropdown');
  dropdownContainer.classList.toggle('open');
}

// ==========================================
// MODO OSCURO / CLARO (THEME TOGGLE GLOBAL)
// ==========================================

// SVGs de los iconos
const SUN_ICON = '<circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line><line x1="12" y1="21" x2="12" y2="23"></line><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line><line x1="1" y1="12" x2="3" y2="12"></line><line x1="21" y1="12" x2="23" y2="12"></line><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>';
const MOON_ICON = '<path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>';

// Función principal que se ejecuta al hacer clic
function toggleTheme() {
    const themeIcon = document.getElementById("theme-icon");
    const themeToggleBtn = document.getElementById("theme-toggle");
    
    document.body.classList.toggle("light-theme");

    let theme = "dark";
    if (document.body.classList.contains("light-theme")) {
        theme = "light";
        if (themeIcon) themeIcon.innerHTML = MOON_ICON;
        if (themeToggleBtn) themeToggleBtn.title = "Cambiar a modo oscuro";
    } else {
        if (themeIcon) themeIcon.innerHTML = SUN_ICON;
        if (themeToggleBtn) themeToggleBtn.title = "Cambiar a modo claro";
    }

    // Guarda la preferencia en el navegador
    localStorage.setItem("theme", theme);
}

// Carga de la preferencia guardada al refrescar o cambiar de página
(function initTheme() {
    const currentTheme = localStorage.getItem("theme");
    if (currentTheme === "light") {
        document.body.classList.add("light-theme");
        
        // Esperamos brevemente a que el DOM pinte el SVG para cambiarlo a Luna
        window.addEventListener("DOMContentLoaded", function () {
            const themeIcon = document.getElementById("theme-icon");
            const themeToggleBtn = document.getElementById("theme-toggle");
            if (themeIcon) themeIcon.innerHTML = MOON_ICON;
            if (themeToggleBtn) themeToggleBtn.title = "Cambiar a modo oscuro";
        });
    }
})();