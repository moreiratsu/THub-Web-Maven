package com.alvaro.thub.filter;

import java.io.IOException;

import com.alvaro.thub.model.UserDTO;
import com.alvaro.thub.utils.SessionManager;
import com.alvaro.thub.utils.Views;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticacion para la parte privada
 */
 @WebFilter("/html/private")
public class AuthenticationFilter extends HttpFilter implements Filter {
       

	public AuthenticationFilter() {
        super();
    }


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		UserDTO user = (UserDTO) SessionManager.getAttribute(request, "user");

		if (user != null) {
			// si el usuario esta logueado, se le permite acceder a la parte privada
			// pasa la request a traves del filtro
			chain.doFilter(request, response);
		} else {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			// TODO: Explicar el porque de las URL
			// En este caso el usuario no esta logueado, se le redirige a la pagina de login PERO POR FUERA DEL NAVEGADOR
			// Por dentro seria haciendo un forward, pero eso no es lo que queremos, queremos que el usuario vea la URL de login en el navegador
			httpResponse.sendRedirect("/THubWeb/"+Views.USER_LOGIN);
		}
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
	}
		
	public void destroy() {
	}
}
