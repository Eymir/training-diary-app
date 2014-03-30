<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: vkoba_000
  Date: 12/17/13
  Time: 6:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link href="https://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="http://vitalets.github.io/bootstrap-datepicker/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="http://vitalets.github.io/bootstrap-datepicker/bootstrap-datepicker/css/datepicker.css"
          rel="stylesheet">
</head>

<body>
<script src="//code.jquery.com/jquery-1.9.1.js"></script>
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<script src="http://vitalets.github.io/bootstrap-datepicker/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>

<script type="text/javascript">
    $(function () {
        $('#dp6').datepicker({

        });
    });
</script>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-10" style=" margin-bottom:15px; background:lightgray">
            <div id="logo" style="float: left; margin-left:15px;">
                <img src="https://s3.amazonaws.com/jetstrap-site/images/website/index/what_icon.png"
                     height="60">
            </div>
            <div id="head" style="float: left; margin-left:15px;">
                <h3>
                    Дневник тренировок
                </h3>
            </div>
        </div>
        <div class="col-md-2" style="background:lightgray">

        </div>
    </div>
    <div class="row" style=" margin-left:15px;">
        <div class="col-md-2">
            <div class="sidebar-nav-fixed">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            Календарь
                        </h3>
                    </div>

                    <div class="panel-body">
                        <center>
                            <div id="dp6" data-date="12-02-2012" data-date-format="dd-mm-yyyy"></div>
                        </center>
                    </div>
                </div>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            Тренировки за дату
                        </h3>
                    </div>
                    <div class="panel-body">
                        <div>
                            1. 19:50 - 20:20
                        </div>
                        <div>
                            2.19:50 - 20:20
                        </div>
                        <div>
                            3. 19:50 - 20:20
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-8">
            <table class="table table-striped">
                <tbody>
                <tr>
                    <td>
                        2х15
                        <br>
                        3х 20
                    </td>
                </tr>
                </tbody>
                <thead>
                <tr>
                    <th>
                        Жим лежа
                    </th>
                </tr>
                </thead>
            </table>
            <table class="table table-striped">
                <tbody>
                <tr>
                    <td>
                        2х15
                        <br>
                        3х 20
                    </td>
                </tr>
                </tbody>
                <thead>
                <tr>
                    <th>
                        Становая тяга
                    </th>
                </tr>
                </thead>
            </table>
            <table class="table table-striped">
                <tbody>
                <tr>
                    <td>
                        2х15
                        <br>
                        3х 20
                    </td>
                </tr>
                </tbody>
                <thead>
                <tr>
                    <th>
                        Французский жим
                    </th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
</body>

</html>