<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/html/common/header.jsp"%>

<%
	EventDTO event = (EventDTO) request.getAttribute("evento");
	out.println("<h1>" + event.getName() + "</h1>");
	out.println("<p>" + event.getStatusName() + "</p>");
	%>
<%@ include file="/html/common/footer.jsp"%>