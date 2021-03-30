<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/3/25
  Time: 10:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <c:forEach items="${seckillOrderList}" var="killorder">
        <div class="price">
            <table border="0" style="width:100%; color:#888888;" cellspacing="0" cellpadding="0">
                <tr style="font-family:'宋体';">
                    <td width="33%">创建时间 </td>
                    <td width="33%">售卖者</td>
                    <td width="33%">状态</td>
                </tr>
                <tr>
                    <td style="text-decoration:line-through;">￥${killorder.createTime}</td>
                    <td>￥${killorder.sellerId}</td>
                    <td>￥${killorder.status}</td>
                </tr>
            </table>
            <td>
                <a href="javascript:showPayForm('${killorder.seckillId }');">支付</a>
            </td>
        </div>
    </c:forEach>
    <div id="payDiv" style="display:none;width:500px;height:300px;background-color:white;border:solid 1px red;">
        <form action="/killgoods/killpay" method="post" name="payForm">
            输入联系人:<input type="text" name="receiver"/></br>
            输入联系地址:<input type="text" name="receiverAddress"/></br>
            输入联系电话:<input type="text" name="receiverMobile"/></br>
            <input type="hidden" id="seckillId" name="seckillId"/></br>
            <input type="submit" value="提交" />
        </form>
    </div>
    ${gid}商品,剩余支付时间:<span id="secondSpan"></span>
    <script type="text/javascript">
        function showPayForm( pid ){
            document.getElementById("seckillId").value=pid;
            document.getElementById("payDiv").style.display="block";
        }
    </script>
    <script language="JavaScript">
        var gid = ${gid};
        var second = ${seconds};
        function djs(){
            second--;
            if (second<=0){
                clearInterval();
                location.href="/killgoods/killdelete?gid="+gid;
            }
            document.getElementById("secondSpan").innerHTML=second;
        }
        var nowinter =setInterval(djs,1000);
    </script>
</body>
</html>
