<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="base.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>rda</title>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Content-Language" content="zh-cn" />

</head>
<script type="text/javascript" src="${jsBasePath}/base/sea.js"></script>
<script type="text/javascript" src="${jsBasePath}/base/sea-config.js"></script>
<body>
	<!--header-->
	<tiles:insertAttribute name="header" />
	<!--end header-->
	
	<!--middle content-->
	<tiles:insertAttribute name="content" />
	<!--end middle-->
	<!--footer-->
<%-- 	<tiles:insertAttribute name="footer" /> --%>
	<!--end footer-->

</body>
</html>