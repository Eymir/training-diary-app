<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
  Created by IntelliJ IDEA.
  User: vkoba_000
  Date: 12/17/13
  Time: 6:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Главная страница</title>
</head>
<body>
   Приветствую, ${username}
   <a href="<c:url value="/j_spring_security_logout" />" > Logout</a>
</body>
</html>