var days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
var months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
var dtSettings = {
    "paginationType": "full_numbers",
    "dom": "<'row'<'col-sm-4'f><'col-sm-3'B><'col-sm-5'p>><'row'<'col-sm-12'tr>><'row'<'col-sm-4'i><'col-sm-3'l><'col-sm-5'p>>",
    "lengthMenu": [[20, 50, 100, 200, -1], ['20', '50', '100', '200', 'All']],
    "start ": 20,
    "length": 20,
    "processing": true,
    "serverSide": true,
    "autoWidth": false,
    "lengthChange": true,
    "fixedHeader": true,
    "stateSave": true,
    "scrollX": false,
    "deferRender": true,
    "rowId": "counter",
    "ajax": {
        "url": "fetchlist.json",
        "type": "POST",
        "timeout": "10000",
        "error": function () {
            BootstrapDialog.show({
                title: '<span style="font-weight:bolder;">' + $("#title").val() + ' !!!</span>',
                type: BootstrapDialog.TYPE_INFO,
                closable: false,
                draggable: true,
                message: $("#loadError").val() + "<br /><a href='javascript:location.reload();'>" + $("#reload").val() + "</a> " + $("#retry").val(),
                buttons: [{
                        label: '&nbsp;&nbsp;&nbsp;' + $("#btnOk").val() + '&nbsp;&nbsp;&nbsp;',
                        action: function (dialog) {
                            dialog.close();
                        }
                    }]
            });
            return;
        }},
    "columns": [
        {"data": null, "targets": "0", "orderable": false, "searchable": false, "class": "rightAlign", "width": "5%", "render": function (field, type, row, meta) {
                return meta.row + meta.settings._iDisplayStart + 1;
            }},
        {"data": "taskName", "targets": "1", "orderable": true, "searchable": true, "width": "45%", "render": function (field, type, row, meta) {
                var str = '<a href="' + row.serverPath + '" target="_blank">' + field + '</a>';
                if (row.newJob > 0 || row.updateJob > 0) {
                    str += '<span class="circle" id="' + field + '"></span>';
                }
                return str;
            }},
        {"data": "jobCount", "targets": "2", "orderable": true, "searchable": true, "class": "rightAlign", "width": "10%", "render": function (field, type, row, meta) {
                if (field === -1) {
                    return "<div style='color:red; text-align:left;'>" + row.message + "</div>";
                } else if (field === 0) {
                    return "<b style='color:red;'>0</b>";
                }
                return field;
            }},
        {"data": "uploadedOn", "targets": "3", "orderable": true, "searchable": false, "class": "rightAlign", "width": "15%", "render": function (field, type, row, meta) {
                if (field > row.currentTime) {
                    return "<div style='font-weight: bolder; color: green;'>" + formatDate(field) + "</div>";
                }
                return formatDate(field);
            }},
        {"data": "lastModified", "targets": "4", "orderable": true, "searchable": false, "class": "rightAlign", "width": "15%", "render": function (field, type, row, meta) {
                if (field >= row.currentTime) {
                    return "<div style='font-weight: bolder; color: green;'>" + formatDate(field) + "<div>";
                } else if (field < (row.currentTime - 86400000)) {
                    return "<div style='font-weight: bolder; color: red;'>" + formatDate(field) + "</div>";
                } else {
                    return formatDate(field);
                }
            }},
        {"data": "fileSize", "targets": "5", "orderable": true, "searchable": false, "class": "rightAlign", "width": "10%", "render": function (field, type, row, meta) {
                if (field >= 1073741824) {
                    return Number(field / 1073741824).toFixed(2) + " GB";
                } else if (field >= 1048576) {
                    return Number(field / 1048576).toFixed(2) + " MB";
                } else if (field >= 1024) {
                    return Number(field / 1024).toFixed(0) + " KB";
                } else {
                    return field + " Bytes";
                }
            }
        }
    ],
    "order": [[1, "asc"]],
    "buttons": [{"text": "&#9776;", "extend": "colvis", "columns": ":gt(1)", "className": "columnControl"},
        {"extend": "excel", "text": "Export to E<span accesskey='X'><b><u>x</u></b></span>cel"}]
};
var paginate = {"first": "&#10094;&#10094;", "previous": "&#10094;", "next": "&#10095;", "last": "&#10095;&#10095;"};
var language_hi = {"search": "<label accesskey='S'><b>खोजें:</b></label>",
    "processing": "<div><img src='images/loading.gif' alt='कृपया प्रतीक्षा करें' style='height:32px; width:32px;'/><p style='color:black;'>कृपया प्रतीक्षा करें...</p></div>",
    "lengthMenu": "दिखाए _MENU_ कार्य",
    "info": "दिखाए _START_ से _END_ तक _TOTAL_ कार्य में से",
    "zeroRecords": "कोई मिलान कार्य नहीं मिला",
    "infoEmpty": "कोई कार्य नहीं मिला",
    "infoFiltered": "(_MAX_ कुल कार्यों में से छठा हुआ)"};

var language_en = {"search": "<label accesskey='S'><b><u>S</u></b>earch</label>",
    "processing": "<div><img src='images/loading.gif' alt='Please Wait' style='height:32px; width:32px;'/><p style='color:black;'>Please wait...</p></div>",
    "lengthMenu": "Show _MENU_ tasks",
    "info": "Showing _START_ to _END_ of _TOTAL_ task(s)",
    "zeroRecords": "No matching task(s) found",
    "infoEmpty": "No task(s) found",
    "infoFiltered": "(filtered from _MAX_ task(s))"};

$(function () {
    var lang = $("#lang").val();
    if ('hi' === lang) {
        dtSettings.language = language_hi;
    } else {
        dtSettings.language = language_en;
    }

    dtSettings.language.paginate = paginate;
    dtSettings.language.thousands = '';

    var tbl = $("#compList").DataTable(dtSettings);
    $('div.dataTables_filter input').attr('title', 'Press enter to filter');
    $('div.dataTables_filter input').focus();
    $('div.dataTables_filter input').unbind();
    $('div.dataTables_filter input').bind('keyup change', function (e) {
        if (e.keyCode === 13) {
            tbl.search($(this).val()).draw();
        }
    });

    /*$("div.dataTables_filter input").append($('<datalist id="searchresults" />'));
     $('div.dataTables_filter input').attr('list', 'searchresults');
     $("div.dataTables_filter input").on("input", function(e) {
     $.ajax({
     url : 'populateName.json',
     data : "prefix=" + $(this).val(),
     dataType : "json",
     type : "POST",
     contentType : "application/json; charset=utf-8",
     success : function(resp) {
     for (name in resp.filtered) {
     $("#searchresults").append($("<option></option>").attr("value", resp.filtered[name]));
     }
     },
     });
     });*/

    var interval = 0;
    if (typeof (Storage) !== "undefined") {
        if (sessionStorage.autoRefresh) {
            interval = sessionStorage.autoRefresh;
        } else {
            interval = -1;
        }
    }
    $('#aReload').val(interval);
    $("#aReload").on('change', function () {
        sessionStorage.autoRefresh = this.value;
        interval = this.value;
        window.location.reload();
    });

    if (interval > -1) {
        setInterval(function () {
            autoRefresh(interval);
        }, interval);
    }

    setInterval(function () {
        showTime();
    }, 1000);

    $("#refresh").click(function (event) {
        BootstrapDialog.confirm({
            title: '<span style="font-weight:bolder;">' + $("#title").val() + ' !!!</span>',
            type: BootstrapDialog.TYPE_INFO,
            closable: false,
            draggable: true,
            message: $("#message1").val() + "<br />" + $("#message2").val(),
            btnCancelLabel: $("#btnCancel").val(),
            btnCancelIcon: 'glyphicon glyphicon-cancel',
            btnOKLabel: '&nbsp;&nbsp;&nbsp;' + $("#btnOk").val() + '&nbsp;&nbsp;&nbsp;',
            btnOKIcon: 'glyphicon glyphicon-ok',
            callback: function (result) {
                if (result) {
                    window.location.href = "refresh.htm";
                }
                return true;
            }
        });
    });

    $(document).on("click", '.circle', function (event) {
        var taskName = $(this).attr("id");
        $.ajax({
            url: "taskDetail.json",
            type: "POST",
            data: "taskName=" + taskName,
            success: function (resp) {
                if (resp !== "") {
                    BootstrapDialog.show({
                        title: '<span style="font-weight:bolder;">' + taskName + '</span>',
                        type: BootstrapDialog.TYPE_INFO,
                        closable: false,
                        draggable: true,
                        message: $("#total").text() + " " + $("#jobs").val() + ': ' + resp.jobCount + '<br />' + $("#newJob").text() + ': ' + resp.newJob + '<br />' + $("#updateJob").text() + ': ' + resp.updateJob + '<br />' + $("#unchangeJob").text() + ': ' + (resp.jobCount - resp.newJob - resp.updateJob),
                        buttons: [{
                                label: 'Close',
                                action: function (dialog) {
                                    dialog.close();
                                }
                            }]
                    });
                }
            }
        });
    });

});

function showTime() {
    $.ajax({
        url: "timer.htm",
        type: "POST",
        success: function (response) {
            $("#timer").html(response);
        }
    });
}

function formatDate(data) {
    var date = new Date(data);
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    var ampm = hours >= 12 ? "PM" : "AM";
    hours = hours % 12 === 0 ? "12" : hours % 12;
    hours = hours < 10 ? "0" + hours : hours;
    minutes = minutes < 10 ? "0" + minutes : minutes;
    seconds = seconds < 10 ? "0" + seconds : seconds;
    var strTime = hours + ":" + minutes + ":" + seconds + " " + ampm;
    return (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + "-" + (date.getMonth() < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1)) + "-" + date.getFullYear() + " " + strTime;
}

function autoRefresh(intvl) {
    window.location.href = "refresh.htm";
}
