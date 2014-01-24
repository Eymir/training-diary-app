<%@page contentType="text/html; charset=UTF-8"%>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <title>Регистрация</title>
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.css" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

</head>
<body>
<div class="container">
    <div class="row">
        <div class="span4 offset4 well">
            <legend>Регистрация</legend>
            <div id="errormessage" hidden="true" class="alert alert-error">
                <a class="close"  data-dismiss="alert" href="#">×</a>Incorrect Username or Password!
            </div>
            <form method="POST" action="" accept-charset="UTF-8">
                <input type="text" id="username" class="span4" name="username" placeholder="Username"><br>
                <input type="password" id="password" class="span4" name="password" placeholder="Password">
                <label class="checkbox">
                    <input type="checkbox" name="remember" value="1"> Remember Me
                </label>
                <button type="submit" name="submit" class="btn btn-info btn-block" onclick="alert('Kuu')">Sign in</button>
            </form>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
</body>
</html>