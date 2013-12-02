<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<h2>Statistic</h2>

<h3>Requests by device:</h3>
<table border="1">
    <tr>
        <th>Device Id</th>
        <th>Count</th>
    </tr>
    <c:forEach var="dcm" items="${deviceCountMap}">
        <tr>
            <td>
                    ${dcm.key}
            </td>
            <td>
                    ${dcm.value}
            </td>
        </tr>
    </c:forEach>
</table>

<h3>Total request: ${total}</h3>
</body>
</html>
