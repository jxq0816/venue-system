<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<form id="settlementDetailFormBean">
    <input type="hidden" name="id" value="${cos.id}"/>
    <input type="hidden" id="settlementDetailToken" name="token" value="${token}"/>

    <div class="content">
        <table class="no-border">
            <tbody class="no-border-y">
            <tr>
                <td>预定人:${cos.userName}(<j:ifelse
                        test="${'1' eq cos.consType}"><j:then>散客</j:then><j:else>会员</j:else></j:ifelse>)
                </td>
                <td>
                    <input type="hidden" name="itemId" value="${item.id}"/>
                    <input type="hidden" name="tutorOrder.id" value="${tutorOrder.id}"/>
                    <input type="hidden" name="tutorOrder.orderPrice" id="tutorPrice" value="${tutorOrder.orderPrice}"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <hr/>
    <div class="content">
        <table>
            <thead>
            <th>场地</th>
            <th>开始时间</th>
            <th>结束时间</th>
            <th>是否半场</th>
            <j:if test="${!empty tutorOrder}">
                <th>
                    教练
                </th>
            </j:if>
            <th>合计</th>
            </thead>
            <tbody>
            <c:forEach items="${itemList}" var="item" varStatus="status">
                <tr>
                    <td>
                            ${item.reserveField.name}
                    </td>
                    <td>
                            ${item.startTime}
                    </td>
                    <td>
                            ${item.endTime}
                    </td>
                    <td>
                        <j:ifelse test="${'1' eq item.halfCourt}"><j:then>是</j:then><j:else>否</j:else></j:ifelse>
                        <input type="hidden" name="venueConsList[${status.index}].halfCourt"
                               value="${item.halfCourt}"/>
                    </td>
                    <j:if test="${!empty tutorOrder}">
                        <td>
                                ${tutorOrder.tutor.name}
                        </td>
                    </j:if>
                    <td>
                        <input type="text" style="width: 60px;" value="${item.consPrice}"
                               class="form-control" readonly="readonly"
                               name="venueConsList[${status.index}].orderPrice"/>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <hr/>
    <div class="content">
        支付方式:
        <j:if test="${'1' eq payType}">
            会员卡
        </j:if>
        <j:if test="${'2' eq payType}">
            现金
        </j:if>
        <j:if test="${'3' eq payType}">
            银行卡
        </j:if>
        <j:if test="${'4' eq payType}">
            微信
        </j:if>
        <j:if test="${'5' eq payType}">
            支付宝
        </j:if>
        <j:if test="${'6' eq payType}">
            其它
        </j:if>
    </div>
    <j:if test="${!empty giftList}">
        <hr/>
        <div class="content">
            赠品
            <table>
                <c:forEach items="${giftList}" var="gift">
                    <tr>
                        <td>${gift.gift.name}</td>
                        <td>${gift.num}${gift.gift.unit}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </j:if>
    <hr/>
    <div class="content">
        <div class="row">
            <div class="col-sm-3">
                <label id="totalPrice">应收(元):<input readonly="readonly" value="${shouldPrice}" type="text"
                                                    id="detailShouldPrice" class="form-control"
                                                    name="shouldPrice"/></label>
            </div>
            <div class="col-sm-3">
                <label>会员优惠(元):<input type="text" readonly="readonly" id="discountPrice" value="${discountPrice}"
                                      class="form-control" name="discountPrice"/></label>
            </div>
            <div class="col-sm-3">
                <label>实收(元):<input type="text" readonly="readonly" id="detailOrderPrice" value="${orderPrice}"
                                    class="form-control" name="orderPrice"/></label>
                <a style="cursor: hand" id="editOrderPrice">
                    <li class="fa fa-edit"></li>
                </a>
            </div>
            <div class="col-sm-3" id="button_userPwd" style="display: none">
                <label>授权码:
                    <input type="password" id="userPwd" class="form-control"/>
                    <button type="button" id="userPwdBtn" class="btn btn-info">确认</button>
                </label>
            </div>
        </div>
    </div>
</form>
<script type="text/javascript">
    $(document).ready(function () {
        $("#editOrderPrice").on('click', function () {
            $("#button_userPwd").show();
        });
        $("#userPwdBtn").on('click', function () {
            var userPwd = $("#userPwd").val();
            jQuery.postItems({
                url: ctx + '/reserve/field/checkUserPwd',
                data: {userPwd: userPwd},
                success: function (result) {
                    if (result!=null && result.id != null) {
                        successLoding("授权码正确!");
                        $("#checkoutId").val(result.id);
                        $("#detailOrderPrice").removeAttr("readonly");
                    }else{
                        errorLoding("授权码不正确!");
                    }
                }
            });
        });
    });
</script>