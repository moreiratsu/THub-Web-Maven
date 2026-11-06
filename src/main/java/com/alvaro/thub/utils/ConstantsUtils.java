package com.alvaro.thub.utils;

/**
 * Clase de utilidad que contiene:
 * Las constantes compartidas por la aplicación web.
 */

public final class ConstantsUtils {
	
	private ConstantsUtils() {
	}
	
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

	
	/*------------------------------------------------------------------------------
	 * CONSTANTES DE LOS CONTEXTOS DE LOS SERVLETS 
	 -----------------------------------------------------------------------------*/
	// IMPORTANTISIMO!!!!!! EN LOS @WebServlet de los servlets,
	// hay que poner exactamente el mismo valor que en estas constantes
	public static final String PUBLIC_USER_SERVLET = "/Public/User";
	public static final String PRIVATE_USER_SERVLET = "/Private/User";
	
	
	/*------------------------------------------------------------------------------
	 * CONSTANTES DE LOS PARAMETROS ED LA REQUEST DE LOS SERVLETS 
	 -----------------------------------------------------------------------------*/
	// parametros de usuario
	public static final String REQUEST_PARAM_EMAIL = "email";
	public static final String REQUEST_PARAM_PASSWORD = "password";
	public static final String REQUEST_PARAM_CONFIRM_PASSWORD = "confirmPassword";
	public static final String REQUEST_PARAM_NAME = "name";
	public static final String REQUEST_PARAM_ROL_ID = "rolId";
	public static final String REQUEST_PARAM_ID = "id";
    public static final String REQUEST_PARAM_DNI = "dni";
    public static final String REQUEST_PARAM_LAST_NAME_1 = "lastName1";
    public static final String REQUEST_PARAM_LAST_NAME_2 = "lastName2";
    public static final String REQUEST_PARAM_PHONE = "phoneNumber";
    public static final String REQUEST_PARAM_BORN_DATE = "bornDate";
    public static final String REQUEST_PARAM_GENDER_ID = "genderId";
    public static final String REQUEST_PARAM_LOCALITY_ID = "localityId";
    public static final String REQUEST_PARAM_OLD_PASSWORD = "oldPassword";
    public static final String REQUEST_PARAM_NEW_PASSWORD = "newPassword";
    public static final String REQUEST_PARAM_CONFIRM_NEW_PASSWORD = "confirmNewPassword";
	
	

}
