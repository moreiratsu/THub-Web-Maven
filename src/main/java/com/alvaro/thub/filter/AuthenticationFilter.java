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

 @WebFilter("/html/private/*")
public class AuthenticationFilter extends HttpFilter implements Filter {
       

	public AuthenticationFilter() {
        super();
    }

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// a traves del SessionManager obtenemos el usuario logueado de la sesion, si no hay usuario logueado, devuelve null
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, ConstantsUtils.USER_SESSION_ATTRIBUTE);

		if (user != null) {
			// si el usuario esta logueado, se le permite acceder a la parte privada
			// pasa la request a traves del filtro
			chain.doFilter(request, response);
		} else {
			// En este caso el usuario no esta logueado, se le redirige a la pagina de login PERO POR FUERA DEL NAVEGADOR
			// Por dentro seria haciendo un forward, pero eso no es lo que queremos, queremos que el usuario vea la URL de login en el navegador			
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			httpResponse.sendRedirect( // que la respuesta sea un redirect a la pagina de login
			    httpRequest.getContextPath() + ConstantsUtils.USER_LOGIN // getContextPath() devuelve el nombre de la aplicacion, para que no importe si se despliega en un contexto diferente al de desarrollo
				);
		}
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
	}
		
	public void destroy() {
	}
}
