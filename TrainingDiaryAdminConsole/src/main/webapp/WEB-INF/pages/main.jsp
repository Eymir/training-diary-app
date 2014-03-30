<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="ru">

<meta charset="utf-8">
<title>Main</title>
<link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.css" rel="stylesheet">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<title>Admin Panel</title>
<style>
    .rightbutton {
        float: right;
        margin-right: 20px
    }

    .sizetextarea {
        width: 100%;
        height: 1080px;
        resize: none;
    }
</style>
</head>
<body>
<span>Users:${numberOfUsers}</span>
<var></var>

<p>
    <span>Last user:</span></p>
<var></var>

<p>
<hr>
<div>Logging</div>

<form method="GET" action="/TDPAdminConsole/main" name="logs" id="logs">
    <!--  Вместо "URL сервера" вставляете сервак, который обрабатывает данные и выдаёт логи  -->

    <p><span>Count of lines:</span>
        <input name="countOfLines" type="text" size="4px" maxlength="7" title="Enter count of lines" value=${countOfLines}>
        <input type="submit" value="Show" alt="Показать" title="Вывести логи"
               class="rightbutton"><br/>
        <textarea id="logs" class="sizetextarea">${logInfo}</textarea>
</form>
</body>
</html>