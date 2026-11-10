
// Funcion para desplegar el submenu de las opciones del sidebar
function toggleDropdown(button) {
  const dropdownContainer = button.closest('.nav-item-dropdown');
  dropdownContainer.classList.toggle('open');
}
function toggleAccountDropdown() {
  const button = document.getElementById("accountDropdownButton");
  const menu = document.getElementById("accountDropdownMenu");

  if (!button || !menu) return;

  const abrirMenu = menu.hidden;
  menu.hidden = !abrirMenu;
  button.setAttribute("aria-expanded", String(abrirMenu));
}

document.addEventListener("click", function (event) {
  const container = document.querySelector(".account-dropdown");
  const button = document.getElementById("accountDropdownButton");
  const menu = document.getElementById("accountDropdownMenu");

  if (!container || !button || !menu) return;

  // Si se pulsa fuera del menú, lo cerramos.
  if (!container.contains(event.target)) {
    menu.hidden = true;
    button.setAttribute("aria-expanded", "false");
  }
});

document.addEventListener("keydown", function (event) {
  if (event.key !== "Escape") return;

  const button = document.getElementById("accountDropdownButton");
  const menu = document.getElementById("accountDropdownMenu");

  if (!button || !menu) return;

  menu.hidden = true;
  button.setAttribute("aria-expanded", "false");
});
// Al cambiar país/provincia, recarga los combos dependientes sin buscar eventos.
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("eventSearchForm");
  if (!form) return;

  const actionInput = document.getElementById("eventSearchAction");
  const countrySelect = document.getElementById("eventCountry");
  const provinceSelect = document.getElementById("eventProvince");
  const localitySelect = document.getElementById("eventLocality");

  if (!actionInput || !countrySelect || !provinceSelect || !localitySelect) return;

  countrySelect.addEventListener("change", function () {
    actionInput.value = form.dataset.showSearchAction;
    provinceSelect.value = "";
    localitySelect.value = "";
    form.submit();
  });

  provinceSelect.addEventListener("change", function () {
    actionInput.value = form.dataset.showSearchAction;
    localitySelect.value = "";
    form.submit();
  });
});

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
// Recarga las provincias y localidades del formulario de creación.
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("eventCreateForm");
  if (!form) return;

  const actionInput = document.getElementById("eventCreateAction");
  const countrySelect = document.getElementById("createEventCountry");
  const provinceSelect = document.getElementById("createEventProvince");
  const localitySelect = document.getElementById("createEventLocality");

  if (!actionInput || !countrySelect || !provinceSelect || !localitySelect) return;

  countrySelect.addEventListener("change", function () {
    actionInput.value = form.dataset.showCreateAction;
    provinceSelect.value = "";
    localitySelect.value = "";
    form.submit();
  });

  provinceSelect.addEventListener("change", function () {
    actionInput.value = form.dataset.showCreateAction;
    localitySelect.value = "";
    form.submit();
  });
});

// Impide enviar un evento con una fecha de inicio anterior a hoy.
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("eventCreateForm");
  const startDateInput = document.getElementById("startDate");
  const errorBox = document.getElementById("startDateError");

  if (!form || !startDateInput || !errorBox) return;

  // La fecha local de hoy, con el formato que utiliza input type="date".
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  const todayString = `${year}-${month}-${day}`;

  // El selector de calendario no permite escoger un día anterior a hoy.
  startDateInput.min = todayString;

  function validateStartDate() {
    const selectedDate = startDateInput.value;

    if (!selectedDate || selectedDate >= todayString) {
      errorBox.textContent = "";
      errorBox.hidden = true;
      startDateInput.setCustomValidity("");
      return true;
    }

    const message = "La fecha de inicio no puede estar en el pasado.";

    errorBox.textContent = message;
    errorBox.hidden = false;
    startDateInput.setCustomValidity(message);

    return false;
  }

  startDateInput.addEventListener("input", validateStartDate);
  startDateInput.addEventListener("change", validateStartDate);

  form.addEventListener("submit", function (event) {
    if (!validateStartDate()) {
      event.preventDefault();
      startDateInput.reportValidity();
    }
  });
});