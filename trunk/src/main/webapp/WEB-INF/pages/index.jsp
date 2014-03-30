<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <title>Template &middot; Bootstrap</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">


    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet">
    <%--<style type="text/css">body{padding-top:20px;padding-bottom:40px;}.container-narrow{margin:0 auto;max-width:700px;}.container-narrow>hr{margin:30px 0;}.jumbotron{margin:60px 0;text-align:center;}.jumbotron h1{font-size:72px;line-height:1;}.jumbotron .btn{font-size:21px;padding:14px 24px;}.marketing{margin:60px 0;}.marketing p+h4{margin-top:28px;}</style>--%>


</head>
<body>
<div class="container-narrow">

    <div class="jumbotron">
        <h1>Консоль администратора</h1>
        <p class="lead">Версия 0.1.0</p>

        <form action="<c:url value='j_spring_security_check'/>" method="post" class="form">
            <div class="get-in-touch">
                <div class="form-group">
                    <input type="text" class="form-control" name="j_username" placeholder="Имя пользователя" oninvalid="setCustomValidity('Введите имя пользователя')" required/>
                </div>
                <div class="form-group">
                    <input type="password" class="form-control" name="j_password" placeholder="Пароль"   required/>
                </div>
                <input class="btn btn-large btn-success" type="submit" value="Войти">
                <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <strong>Ошибка!</strong> Неправильное имя пользователя, или пароль
                </div>
                </c:if>
            </div>
        </form>
    </div>


</div>


<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
</body>
</html>



