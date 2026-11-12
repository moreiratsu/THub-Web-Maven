package com.alvaro.thub.utils;

/**
 * Clase de utilidad que contiene:
 * Las constantes compartidas por la aplicación web.
 */

public final class ConstantsUtils {
	
	private ConstantsUtils() {
	}
	
	/*------------------------------------------------------------------------------
	 * ENTIDADES
	 -----------------------------------------------------------------------------*/
	
	/** Nombre del atributo de sesión con el usuario autenticado. */
	public static final String USER_SESSION_ATTRIBUTE = "usuario";

	/** Tipo de entidad usado por el CMS para identificar contenido asociado a un usuario. */
	public static final String USER_ENTITY_TYPE = "usuario";
	/** Tipo de entidad usado por el CMS para identificar contenido asociado a un evento. */
	public static final String EVENT_ENTITY_TYPE = "evento";
	/** Tipo de recurso del CMS para la foto de perfil del usuario. */
	public static final String USER_PROFILE_RESOURCE_TYPE = "perfil";
	public static final String EVENT_LINEUP_RESOURCE_TYPE = "cartel";

	public static final String EVENT = "event";
		
	
	
	/* ------------------------------------------------------------------------------------
	 VISTAS JSP
	---------------------------------------------------------------------------------------*/
	
	public static final String INDEX = "/index.jsp";
	public static final String ERROR_404 = "/html/common/error404.jsp";
	
	public static final String USER_LOGIN = "/html/public/user/login.jsp";
	public static final String USER_REGISTER = "/html/public/user/register.jsp";
	public static final String USER_PROFILE = "/html/private/user/profile.jsp";
	
	public static final String EVENT_SEARCH = "/html/public/event/EventSearch.jsp";
	public static final String EVENT_DETAIL = "/html/public/event/EventDetail.jsp";
	public static final String EVENT_NEW = "/html/private/event/NewEvent.jsp";
	public static final String EVENT_EDIT = "/html/private/event/EditEvent.jsp";

	
	/*------------------------------------------------------------------------------
	 * SERVLETS 
	 -----------------------------------------------------------------------------*/
	// IMPORTANTISIMO!!!!!! EN LOS @WebServlet de los servlets,
	// hay que poner exactamente el mismo valor que en estas constantes
	public static final String PUBLIC_USER_SERVLET = "/Public/User";
	public static final String PRIVATE_USER_SERVLET = "/Private/User";
	public static final String PRIVATE_EVENT_SERVLET = "/Private/Event";
	public static final String PUBLIC_EVENT_SERVLET = "/Public/Event";

	
	/*------------------------------------------------------------------------------
	 * CONSTANTES DE LOS PARAMETROS ED LAs REQUEST DE LOS SERVLETS (los parametros que vienen en la URL o en los formularios)
	 -----------------------------------------------------------------------------*/

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
    public static final String REQUEST_PARAM_COUNTRY_ID = "countryId";
    public static final String REQUEST_PARAM_PROVINCE_ID = "provinceId";
    public static final String REQUEST_PARAM_OLD_PASSWORD = "oldPassword";
    public static final String REQUEST_PARAM_NEW_PASSWORD = "newPassword";
    public static final String REQUEST_PARAM_CONFIRM_NEW_PASSWORD = "confirmNewPassword";
    public static final String REQUEST_PARAM_STATUS_ID = "statusId";
    public static final String REQUEST_PARAM_FORMAT_ID = "formatId";
    public static final String REQUEST_PARAM_START_DATE = "startDate";
    public static final String REQUEST_PARAM_END_DATE = "endDate";
    public static final String REQUEST_PARAM_USER_ID = "userId";
    
    /* ------------------------------------------------------------------------------
     * ACCIONES DE LOS SERVLETS 
     ---------------------------------------------------------------------------------*/
	public static final String REQUEST_PARAM_ACTION = "action";
    // acciones de eventos
	public static final String REQUEST_EVENT_ACTION_SEARCH = "search";
	public static final String REQUEST_EVENT_ACTION_DETAIL = "detail";
	public static final String REQUEST_EVENT_ACTION_DELETE = "delete";
	public static final String REQUEST_EVENT_ACTION_CREATE = "create";
	public static final String REQUEST_EVENT_ACTION_UPDATE = "update";
	public static final String REQUEST_EVENT_ACTION_UPLOAD_LINEUP = "uploadLineup";
	public static final String REQUEST_EVENT_ACTION_SHOW_SEARCH = "showSearch";
	public static final String REQUEST_EVENT_ACTION_SHOW_LINEUP = "showLineup";
	public static final String REQUEST_EVENT_ACTION_SHOW_CREATE = "showCreate";
	public static final String REQUEST_EVENT_ACTION_SHOW_MY_EVENTS = "showMyEvents";

	
	/* ------------------------------------------------------------------------------------
	 TIPOS DE ESTADOS
	---------------------------------------------------------------------------------------*/
	public static final String ESTADO_PROGRAMADO = "Programado";
	public static final String ESTADO_CANCELADO = "Cancelado";
	public static final String ESTADO_EN_CURSO = "En curso";
	public static final String ESTADO_FINALIZADO = "Finalizado";

}
