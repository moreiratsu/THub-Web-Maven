
// FUNCION PARA DESPLEGAR LOS SUBMENÚS DE SIDEBAR =====================================================
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

document.addEventListener("click", function(event) {
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

document.addEventListener("keydown", function(event) {
    if (event.key !== "Escape") return;

    const button = document.getElementById("accountDropdownButton");
    const menu = document.getElementById("accountDropdownMenu");

    if (!button || !menu) return;

    menu.hidden = true;
    button.setAttribute("aria-expanded", "false");
});
// ---------------------------------------------------------------------------------------------------








// ==========================================================================================================================
// MODO OSCURO / CLARO (THEME TOGGLE GLOBAL) 
// ==============================================================================================================================

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
        window.addEventListener("DOMContentLoaded", function() {
            const themeIcon = document.getElementById("theme-icon");
            const themeToggleBtn = document.getElementById("theme-toggle");
            if (themeIcon) themeIcon.innerHTML = MOON_ICON;
            if (themeToggleBtn) themeToggleBtn.title = "Cambiar a modo oscuro";
        });
    }
})();
//---------------------------------------------------------------------------------------------------








// Impide enviar un evento con una fecha de inicio anterior a hoy---------------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", function() {
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

    form.addEventListener("submit", function(event) {
        if (!validateStartDate()) {
            event.preventDefault();
            startDateInput.reportValidity();
        }
    });
});
//-----------------------------------------------------------------------------------------------------------------------------------------
//FUNCION PARA PREVISUALIZAR UNA IMAGEN ANTES DE ENVIARLA CON EL FORMULARIO, NO LA SUBE SOLO LA MUESTRA
document.addEventListener("DOMContentLoaded", function() {
    const input = document.getElementById("imagen");
    const preview = document.getElementById("posterPreview");

    if (!input || !preview) return;

    input.addEventListener("change", function() {
        const archivo = input.files && input.files[0];

        if (!archivo) {
            preview.removeAttribute("src");
            preview.hidden = true;
            return;
        }

        const lector = new FileReader();

        lector.addEventListener("load", function() {
            if (typeof lector.result !== "string") return;

            preview.src = lector.result;
            preview.hidden = false;
        });

        lector.readAsDataURL(archivo);
    });
});



// Actualiza provincias y localidades sin recargar la página (AJAX). ---------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("eventCreateForm");
    if (!form) return;

    const countrySelect = document.getElementById("createEventCountry");
    const provinceSelect = document.getElementById("createEventProvince");
    const localitySelect = document.getElementById("createEventLocality");

    if (!countrySelect || !provinceSelect || !localitySelect) return;

    function actualizarDesplegables() {
        const parametros = new URLSearchParams();

        // Pide al servlet la vista de creación con las opciones actualizadas.
        parametros.set("action", form.dataset.showCreateAction);

        if (countrySelect.value) {
            parametros.set("countryId", countrySelect.value);
        }

        if (provinceSelect.value) {
            parametros.set("provinceId", provinceSelect.value);
        }

        const url = new URL(form.getAttribute("action"), window.location.href);
        url.search = parametros.toString();

        // AJAX: pide la respuesta en segundo plano, sin navegar a ella.
        return fetch(url.toString())
            .then(function(respuesta) {
                if (!respuesta.ok) {
                    throw new Error("No se pudieron cargar las opciones.");
                }

                return respuesta.text();
            })
            .then(function(html) {
                // Lee el HTML recibido y extrae solo los dos desplegables.
                const paginaRecibida =
                    new DOMParser().parseFromString(html, "text/html");

                const provinciasRecibidas =
                    paginaRecibida.querySelector("#createEventProvince");
                const localidadesRecibidas =
                    paginaRecibida.querySelector("#createEventLocality");

                if (!provinciasRecibidas || !localidadesRecibidas) {
                    throw new Error("No se encontraron los desplegables recibidos.");
                }

                // Actualiza las opciones sin reemplazar el formulario.
                provinceSelect.innerHTML = provinciasRecibidas.innerHTML;
                localitySelect.innerHTML = localidadesRecibidas.innerHTML;
            });
    }

    // Al cambiar el país, limpia provincia y localidad y carga sus provincias.
    countrySelect.addEventListener("change", function() {
        provinceSelect.innerHTML =
            '<option value="">Selecciona una provincia</option>';
        localitySelect.innerHTML =
            '<option value="">Selecciona una localidad</option>';

        if (countrySelect.value) {
            actualizarDesplegables().catch(function(error) {
                console.error("Error al cargar las provincias:", error);
            });
        }
    });

    // Al cambiar la provincia, limpia y carga sus localidades.
    provinceSelect.addEventListener("change", function() {
        localitySelect.innerHTML =
            '<option value="">Selecciona una localidad</option>';

        if (provinceSelect.value) {
            actualizarDesplegables().catch(function(error) {
                console.error("Error al cargar las localidades:", error);
            });
        }
    });
});



// SCRIPT para la ventanita de borrado de evento --------------------------------------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", function () {
    const dialogoBorrado = document.getElementById("confirmacionBorrado");
    const botonAbrir = document.getElementById("abrirConfirmacionBorrado");
    const botonCancelar = document.getElementById("cancelarBorrado");

    // Esta página no tiene la ventana de borrado: no hacemos nada.
    if (!dialogoBorrado || !botonAbrir || !botonCancelar) {
        return;
    }

    botonAbrir.addEventListener("click", function () {
        dialogoBorrado.showModal();
    });

    botonCancelar.addEventListener("click", function () {
        dialogoBorrado.close();
    });
});



// SCRIPT para la PAGINACION
document.addEventListener("DOMContentLoaded", function () {
    const botonesPagina =
        document.querySelectorAll(".pagination-button[data-pagina]");

    botonesPagina.forEach(function (boton) {
        boton.addEventListener("click", function () {
            const url = new URL(window.location.href);

            url.searchParams.set("pagina", boton.dataset.pagina);

            window.location.href = url.toString();
        });
    });
});
