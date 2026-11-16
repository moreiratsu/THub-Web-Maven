package com.alvaro.thub.filter;

import java.io.IOException;

import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.utils.ConstantsUtils;
import com.alvaro.thub.utils.SessionManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticacion para la parte privada de la web (la que requiere que el usuario este logueado)
 */

public class AuthenticationFilter extends HttpFilter implements Filter {
       

	public AuthenticationFilter() {
        super();
    }

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// a traves del SessionManager obtenemos el usuario logueado de la sesion, si no hay usuario logueado, devuelve null
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);

		if (user != null) { // si el usuario esta logueado
			chain.doFilter(request, response); // significa: esta petición puede continuar
		} else {
			// En este caso el usuario no esta logueado, se le redirige a la pagina de login PERO POR FUERA DEL NAVEGADOR con un redirect
			// Por dentro seria haciendo un forward, pero eso no es lo que queremos, queremos que el usuario vea la URL de login en el navegador			
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			httpResponse.sendRedirect( httpRequest.getContextPath() + ConstantsUtils.USER_LOGIN);
		}
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
	}
		
	public void destroy() {
	}
}
