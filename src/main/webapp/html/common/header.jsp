<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.alvaro.thub.model.*"%>    
<%@ page import="com.alvaro.thub.utils.ConstantsUtils" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
 <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<title>THUB</title>
</head>
<body>
<% UserDTO usuario = (UserDTO) request.getSession().getAttribute(ConstantsUtils.USER_SESSION_ATTRIBUTE); %>
<!-- Area de contenido -->

