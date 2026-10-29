package com.alvaro.thub.utils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/*
 * Clase utilitaria para manejar la sesion del usuario
 */
public class SessionManager {
	
	/*
	 * Recibe un ServletRequest 
	 * Devuelve la sesion del usuario, si no existe la crea
	 */
	public static final HttpSession getSession(ServletRequest request) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// TODO: Cuando no crear la sesion
		HttpSession session = httpRequest.getSession();
		return session;
	}
	
	/*
	 * Recibe un ServletRequest y el nombre de un atributo
	 * Devuelve el valor del atributo de la sesion del usuario
	 */
	public static final Object getAttribute(ServletRequest request, String attributeName) {
		HttpSession session = getSession(request);
		return session.getAttribute(attributeName);
	}

	/*
	 * Recibe un ServletRequest, el nombre de un atributo y un valor
	 * Asigna el valor del atributo de la sesion del usuario
	 */
	public static final void setAttribute(ServletRequest request, String attributeName, Object value) {
		HttpSession session = getSession(request);
		session.setAttribute(attributeName, value);
	}
}
