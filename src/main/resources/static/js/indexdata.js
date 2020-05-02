


$(function () {
    //总记录数
    var totalpageo;
    //当前页
    var currentpage;
    //到第几页,默认到第一页
    to_page(1);
    //加载右边的数据
    build_right_list();
});



//构建最新用户
function build_right_list() {
    $.ajax({
        type: "GET",
        url: "/loadRightList",
        data: {
            "time": new Date()
        },
        success: function (data) {
            if (data.code == "1000") {
                build_user_list(data);
                build_hot_tags(data);
                build_new_questions(data);
                build_recommend_questions(data);
            } else {

            }
        }
    })
}

function build_new_questions(data) {
    $("#newquestions_wrapper").empty();
    $.each(data.extend.newQuestions, function (index, item) {
        var li = $("<li class='list-group-item'  class=\"\"><a title='查看全文' href='/question/" + item.id + "'>" + item.title + "</a></li>");
        li.appendTo($("#newquestions_wrapper"));
    });
}

function build_recommend_questions(data) {
    $("#recommend_wrapper").empty();
    $.each(data.extend.recommend, function (index, item) {
        var li = $("<li class='list-group-item'  class=\"\"><span id='hit_no_"+index+"'>"+(index+1)+"</span><a  title='查看全文' href='/question/" + item.id + "' >" + item.title + "</a>" + "</li>");
        li.appendTo($("#recommend_wrapper"));
    });
}

function build_hot_tags(data) {
    $(".tag_wrapper").empty();
    $.each(data.extend.tags, function (index, item) {
        var hot_tag = $("  <div class=\"hot_tag\">\n" +
            "            <a  class='tagname' id='" + item + "' onclick='setTag(this)' ><span>" + item + "</span></a>\n" +
            "        </div>");
        hot_tag.appendTo($(".tag_wrapper"));
    });
}

function setTag(e) {
    $("#tag_param").val(e.id);
    $(".tagname").css({color: "#fff"})
    e.style.color = "#337ab7";
    to_page(1);
    return false;
}

function build_user_list(data) {
    //清空
    $("#userlist").empty();
    var users = data.extend.userList;
    $.each(users, function (index, item) {
        var userli = $("<li class=\"list-group-item\" style=\"list-style: none;\">\n" +
            "             <a style='margin-right: 5px;' href=\"/people?id=" + item.id + "\"><img class=\"img-rounded\" width=\"30\" src=\"" + item.avatarUrl + "\"></a>\n" +
            "                 <small style='font-size: 13px;color: #303030;'>" + item.name + "</small>\n" +
            "                  <small></small>\n" +
            "              <small  style='float: right;font-size: 11px;color: #666;'>问题:" + item.questionCount + "&nbsp;&nbsp; <span class='iconfont icon-fensi2'></span>:(" + item.fansCount + ")</small>\n" +
            "         </li>\n")
        userli.appendTo("#userlist");
    });
}

//到问题页第几页
function to_page(pageno) {
    $("#no_quetions").css({
        display:"none",
    });
    //加载完成之后,发送请求到服务器,拿到jason数据,构建列表数据
    var url = "/loadQuestionList";
    $.ajax({
        type: "GET",
        url: url,
        data: {
            "pageNo": pageno,
            "pageSize": 24,
            "tag": $("#tag_param").val(),
            "search": $("#search_param").val(),
            "sortby": $("#sortby_param").attr("sortby"),
            "category": $("#category_param").val(),
            contentType: "application/json;charset=UTF-8"
        },
        beforeSend: function () {
            //loadingIndex = layer.msg('加载数据~~', {icon: 16});
            NProgress.start();
        },
        success: function (data) {
            //layer.close(loadingIndex);
            NProgress.done();
            if (data.code == "1000") {
                if(data.extend.page.total>0){
                    //构建问题列表信息
                    build_question_list(data);
                    //构建分页信息
                    //build_page_nav(data);
                    $("#page").bootstrapPaginator({
                        bootstrapMajorVersion: 4, //对应的bootstrap版本
                        currentPage: data.extend.page.pageNum, //当前页数
                        numberOfPages: 5, //每次显示页数
                        totalPages: data.extend.page.pages, //总页数
                        shouldShowPage: true, //是否显示该按钮
                        useBootstrapTooltip: true,
                        onPageClicked: function(event, originalEvent, type, page) {
                            to_page(page);
                        }
                    });

                }else{
                    $("#question_wrapper").empty();
                    $('.page_info-area').empty();
                    $(".pagination").empty();
                    $("#no_quetions").css({
                        display:"block",
                    });
                }
                $("html,body").animate({scrollTop: 0}, 0);//回到顶端
            } else {
                layer.msg(data.extend.msg, {time: 2000, icon: 5, shift: 6}, function () {
                });
            }
        }
    });

}



//构建questions列表
function build_question_list(data) {
    //清空
    $("#question_wrapper").empty();
    var questions = data.extend.page.list;
    $.each(questions, function (index, item) {
        var rank="";
        switch (item.user.rank) {
            case 0:rank="<font style='color: grey'>青铜</font>"
                break;
            case 1:rank="<font style='color: burlywood'>白银</font>"
                break;
            case 2:rank="<font style='color: #ffad13'>黄金</font>"
                break;
            case 3:rank="<font style='color: #303030'>钻石</font>"
                break;
            case 4:rank="<font style='color: green'>星耀</font>"
                break;
            case 5:rank="<font style='color: darkorchid'>王者</font>"
                break;
            case 6:rank="<font style='color: darkorange'>最强王者</font>"
                break;
        }
        var top=' ';
        switch (item.top) {
            case 0:
                break;
            case 1:
                top="<span class='label label-danger'>置顶</span>";
                break;
        }
        var question = $("<div  class=\"question media\">\n" +
            "  <div class=\"  media-left \">\n" +
            "    <a href=\"/people?id=" + item.creator + "\">\n" +
            "      <img style='width: 50px;margin-right: 20px;' class='img-rounded u_img' src=\" " + item.user.avatarUrl + " \" alt=\"...\">\n" +
            "    </a>\n" +
            "  </div>\n" +
            "  <div class=\"media-body\">\n" +
            "    <a  href='/question/" + item.id + "' class=\"media-heading question_title\">" + item.title + "</a>&nbsp;"+top+"\n" +
            "  <br>  <span style=\"font-size: 12px;\">\n" +
            "                         <span class='question_type_tag'>" + item.typeName + "</span> • \n" +
            "                  <span >"+item.user.name +"</span> 【"+rank+"】 •  <span style=\"font-size: 11px;\" class=\"iconfont icon-pinglun1\">" + item.commentCount + "</span>人评论 •\n" +
            "                     <span><small style='font-size: 11px;' class='iconfont icon-liulan1'></small>" + item.viewCount + "</span>次浏览 •\n" +
            "                        <span>" + item.likeCount + "</span>人点赞 •\n" +
            "                        发布于:<spanid=\"publish_time\"><span id='clock' class='iconfont icon-zuijingengxin' ></span>" + item.showTime + "</span>\n" +
            "            </span>\n" +
            "    <span style=\"float: right;color: #999999;font-size: 10px !important;\">\n" +
            "      <small class=\"question_type_right\">" + item.typeName + "</small>\n\n" +
            "  </div>\n" +
            "</div>")
        question.appendTo("#question_wrapper");
    })
}

function siteTime() {
    window.setTimeout("siteTime()", 1000);
    var seconds = 1000;
    var minutes = seconds * 60;
    var hours = minutes * 60;
    var days = hours * 24;
    var years = days * 365;
    var today = new Date();
    var todayYear = today.getFullYear();
    var todayMonth = today.getMonth() + 1;
    var todayDate = today.getDate();
    var todayHour = today.getHours();
    var todayMinute = today.getMinutes();
    var todaySecond = today.getSeconds();
    /* Date.UTC() -- 返回date对象距世界标准时间(UTC)1970年1月1日午夜之间的毫秒数(时间戳)
    year - 作为date对象的年份，为4位年份值
    month - 0-11之间的整数，做为date对象的月份
    day - 1-31之间的整数，做为date对象的天数
    hours - 0(午夜24点)-23之间的整数，做为date对象的小时数
    minutes - 0-59之间的整数，做为date对象的分钟数
    seconds - 0-59之间的整数，做为date对象的秒数
    microseconds - 0-999之间的整数，做为date对象的毫秒数 */
    var t1 = Date.UTC(2019, 6, 01, 00, 00, 00); //北京时间2016-12-1 00:00:00
    var t2 = Date.UTC(todayYear, todayMonth, todayDate, todayHour, todayMinute, todaySecond);
    var diff = t2 - t1;
    var diffYears = Math.floor(diff / years);
    var diffDays = Math.floor((diff / days) - diffYears * 365);
    var diffHours = Math.floor((diff - (diffYears * 365 + diffDays) * days) / hours);
    var diffMinutes = Math.floor((diff - (diffYears * 365 + diffDays) * days - diffHours * hours) / minutes);
    var diffSeconds = Math.floor((diff - (diffYears * 365 + diffDays) * days - diffHours * hours - diffMinutes * minutes) / seconds);
    document.getElementById("sitetime").innerHTML = "本站已苟活：" + diffYears + " 年 " + diffDays + " 天 " + diffHours + " 小时 " + diffMinutes + " 分钟 " + diffSeconds + " 秒";
}
siteTime();

