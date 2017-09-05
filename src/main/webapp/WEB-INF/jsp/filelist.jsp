<%@ page language="java" contentType="text/html;  charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="./include.jsp"%>
    </head>
    <body>
        <%@include file="./hidden.jsp" %>
        <!-- <select id="aReload">
                        <option value="-1">Auto Refresh</option>
                        <option value="600000">10 min</option>
                        <option value="1200000">20 min</option>
                        <option value="1800000">30 min</option>
                </select> -->
        <div class="container-fluid">
            <div style="position: fixed; left: 5px; top: 2px; font-weight: bolder; font-size: 2px; color: white; opacity: 0;" id="refresh">
                <span accesskey="R">R</span>efresh
            </div>
            <nav class="navbar navbar-default">
                <div class="container-fluid">
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar">
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <%-- <a class="navbar-brand" style="color: black; font-weight: bolder; font-size: 18px;">${statusDetail.clientName}</a> --%>
                    </div>
                    <div class="collapse navbar-collapse" id="navbar">
                        <ul class="nav navbar-nav" style="font-weight: bolder; font-size: 18px; padding-left: 6%;">
                            <li class="dropdown">
                                <a data-toggle="dropdown" style="color: black; padding-right: 45px;"><span id="total"><spring:message code="label.Total" text="Total" /></span>: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.taskCount}"/>(<c:choose><c:when test="${statusDetail.jobCount gt 0}"><fmt:formatNumber groupingUsed = "false" value="${statusDetail.jobCount}"/></c:when><c:otherwise><fmt:formatNumber value="0"/></c:otherwise></c:choose>)</a>
                                        <ul class="dropdown-menu" style="width: 100%;">
                                                <li><a style="color: black;"><span id="newJob"><spring:message code="label.newJobCount" text="New Jobs" /></span>: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.newJob}"/></a></li>
                                    <li><a style="color: black;"><span id="updateJob"><spring:message code="label.updatedJobCount" text="Update Jobs" /></span>: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.updateJob}"/></a></li>
                                    <li><a style="color: black;"><span id="unchangeJob"><spring:message code="label.unchangedJobCount" text="Unchanged Jobs" /></span>: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.unchangeJob}"/></a></li>
                                </ul>
                            </li>
                            <li><a id="completed" style="color: darkgreen; padding-right: 45px;"><spring:message code="label.Completed" text="Completed" />: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.doneCount}"/></a></li>
                            <li><a id="pending" style="color: red; padding-right: 45px;"><spring:message code="label.Pending" text="Pending" />: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.pendingCount}"/></a></li>
                            <li><a id="error" style="color: #8b0000; padding-right: 45px;"><spring:message code="label.Error" text="Error" />: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.errorCount}"/></a></li>
                            <li><a id="zeros" style="color: grey; padding-right: 45px;"><spring:message code="label.ZeroJob" text="Zero Job" />: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.zeroCount}"/></a></li>
                            <li><a id="today" style="color: blue; padding-right: 45px;"><spring:message code="label.TodayStatus" text="Today's status" />: <fmt:formatNumber groupingUsed = "false" value="${statusDetail.todayTaskCount}"/>(<fmt:formatNumber groupingUsed = "false" value="${statusDetail.todayJobCount}"/>)</a></li>
                        </ul>
                        <ul class="nav navbar-nav navbar-right" style="font-weight: bolder; font-size: 14px; padding-left: 6%;">
                            <li><a id="timer" style="color: black;"></a></li>
                        </ul> 
                    </div>
                </div>
            </nav>
            <div class="container-fluid" style="width: 80%;">
                <table id="compList" class="table table-striped table-bordered dt-responsive nowrap">
                    <thead>
                        <tr>
                            <th class="noWapColumn" style="text-align: center; width: 5%;"><spring:message code="label.SrNo" text="Sr. No" /></th>
                            <th class="noWapColumn" style="text-align: center; width: 45%;"><spring:message code="label.TaskName" text="Task Name" /></th>
                            <th class="noWapColumn" style="text-align: center; width: 10%;"><spring:message code="label.JobCount" text="Job Count" /></th>
                            <th class="noWapColumn" style="text-align: center; width: 15%;"><spring:message code="label.UploadedOn" text="Uploaded On" /></th>
                            <th class="noWapColumn" style="text-align: center; width: 15%;"><spring:message code="label.LastModified" text="Last Modified" /></th>
                            <th class="noWapColumn" style="text-align: center; width: 10%;"><spring:message code="label.Size" text="Size" /></th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
        <div style="position: fixed; bottom: 0px; width: 100%; text-align: center; font-size: 10px;"><jsp:useBean id="now" class="java.util.Date" />&copy; <fmt:formatDate pattern="yyyy" value="${now}" /> <spring:message code="label.CopyRight" text="All rights reserved." /></div>
    </body>
</html>