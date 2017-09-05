<%@ page language="java" contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="./include.jsp"%>
    </head>
    <body style="overflow-y: scroll;">
        <div>${exception.message}</div>
        <div style="text-align: center;"><a onclick="window.location.reload();" href="#" style="text-decoration: none;">Retry</a></div>
    </body>
</html>