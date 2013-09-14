<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<title><tiles:getAsString name="title"/></title>
	
	<tiles:insertAttribute name="meta"/>
	<tiles:insertAttribute name="stylesheets"/>
</head>
<body>
	<tiles:insertAttribute name="header"/>
	<div id="pjax-container">
		<tiles:insertAttribute name="content"/>
	</div>
	<tiles:insertAttribute name="footer"/>
	<tiles:insertAttribute name="js"/>
</body>
</html>
