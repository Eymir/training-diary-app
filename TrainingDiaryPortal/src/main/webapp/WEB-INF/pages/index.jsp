<%@page contentType="text/html; charset=UTF-8"%>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <title>Template &middot; Bootstrap</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <script type="text/javascript">
        //<![CDATA[
        try{if (!window.CloudFlare) {var CloudFlare=[{verbose:0,p:0,byc:0,owlid:"cf",bag2:1,mirage2:0,oracle:0,paths:{cloudflare:"/cdn-cgi/nexp/acv=4125811108/"},atok:"b3da29c137d13f50c39d6d48c905e2a3",petok:"c5cdae1bcd0aeb0581fa29a962a3a038236392a6-1387096163-1800",zone:"bootstrap-ru.com",rocket:"a",apps:{"ga_key":{"ua":"UA-12784007-23","ga_bs":"2"}}}];document.write('<script type="text/javascript" src="//ajax.cloudflare.com/cdn-cgi/nexp/acv=616370821/cloudflare.min.js"><'+'\/script>');}}catch(e){};
        //]]>
    </script>
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">body{padding-top:20px;padding-bottom:40px;}.container-narrow{margin:0 auto;max-width:700px;}.container-narrow>hr{margin:30px 0;}.jumbotron{margin:60px 0;text-align:center;}.jumbotron h1{font-size:72px;line-height:1;}.jumbotron .btn{font-size:21px;padding:14px 24px;}.marketing{margin:60px 0;}.marketing p+h4{margin-top:28px;}</style>
    <link href="../assets/css/bootstrap-responsive.css" rel="stylesheet">

    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->


    <script type="text/javascript">
        /* <![CDATA[ */
        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-12784007-23']);
        _gaq.push(['_trackPageview']);

        (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();

        (function(b){(function(a){"__CF"in b&&"DJS"in b.__CF?b.__CF.DJS.push(a):"addEventListener"in b?b.addEventListener("load",a,!1):b.attachEvent("onload",a)})(function(){"FB"in b&&"Event"in FB&&"subscribe"in FB.Event&&(FB.Event.subscribe("edge.create",function(a){_gaq.push(["_trackSocial","facebook","like",a])}),FB.Event.subscribe("edge.remove",function(a){_gaq.push(["_trackSocial","facebook","unlike",a])}),FB.Event.subscribe("message.send",function(a){_gaq.push(["_trackSocial","facebook","send",a])}));"twttr"in b&&"events"in twttr&&"bind"in twttr.events&&twttr.events.bind("tweet",function(a){if(a){var b;if(a.target&&a.target.nodeName=="IFRAME")a:{if(a=a.target.src){a=a.split("#")[0].match(/[^?=&]+=([^&]*)?/g);b=0;for(var c;c=a[b];++b)if(c.indexOf("url")===0){b=unescape(c.split("=")[1]);break a}}b=void 0}_gaq.push(["_trackSocial","twitter","tweet",b])}})})})(window);
        /* ]]> */
    </script>
</head>
<body>
<div class="container-narrow">
    <div class="masthead">
        <ul class="nav nav-pills pull-right">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#">About</a></li>
            <li><a href="#">Contact</a></li>
        </ul>
        <h3 class="muted">Training Diary Portal</h3>
    </div>
    <hr>
    <div class="jumbotron">
        <h1>Gym faping portal!</h1>
        <p class="lead">Дневник тренировок. Версия 0.1.0a</p>
        <a class="btn btn-large btn-success" href="#">Зарегестрируйся!</a>
    </div>
    <hr>
    <div class="row-fluid marketing">
        <div class="span6">
            <h4>Увеличь свой жим</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>
            <h4>Будь всегда на связи</h4>
            <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum.</p>
            <h4>Блаблабла</h4>
            <p>Maecenas sed diam eget risus varius blandit sit amet non magna.</p>
        </div>

    </div>
    <hr>
    <div class="footer">
        <p>&copy; Training Diary Portal 2013</p>
    </div>
</div>


<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
</body>
</html>



