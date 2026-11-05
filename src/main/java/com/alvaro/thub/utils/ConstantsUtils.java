package com.alvaro.thub.utils;

/**
 * Constantes compartidas por la aplicación web.
 */
public final class ConstantsUtils {
	
	/*------------------------------------------------------------------------------
	 * ESTAS CONSTANTES SON PARA EL MANEJO DE USUARIOS Y FOTOS DE PERFIL, PARA QUE NO HAYA QUE PONER LA RUTA COMPLETA EN CADA SERVLET O FILTRO
	 -----------------------------------------------------------------------------*/
	
	/** Nombre del atributo de sesión con el usuario autenticado. */
	public static final String USER_SESSION_ATTRIBUTE = "usuario";

	/** Tipo de entidad usado por el CMS para identificar contenido asociado a un usuario. */
	public static final String USER_ENTITY_TYPE = "usuario";

	/** Tipo de recurso del CMS para la foto de perfil del usuario. */
	public static final String USER_PROFILE_RESOURCE_TYPE = "perfil";

	
	
	
	
	
	/* ------------------------------------------------------------------------------------
	 ESTAS CONSTANTES SON DE LAS VISTAS JSP, PARA QUE NO HAYA QUE PONER LA RUTA COMPLETA EN CADA SERVLET O FILTRO
	---------------------------------------------------------------------------------------*/
	
	/** Página de inicio en la raíz de la aplicación web. */
	public static final String INDEX = "/index.jsp";
	
	/** JSP público de inicio de sesión */
	public static final String USER_LOGIN = "/html/public/user/login.jsp";

	/** JSP público de registro */
	public static final String USER_REGISTER = "/html/public/user/register.jsp";

	/** JSP privado de perfil */
	public static final String USER_PROFILE = "/html/private/user/profile.jsp";

	/** JSP de búsqueda de eventos */
	public static final String EVENT_SEARCH = "/html/event/EventSearch.jsp";

	/** JSP de detalle de evento */
	public static final String EVENT_DETAIL = "/html/event/EventDetail.jsp";

	private ConstantsUtils() {
	}
}
