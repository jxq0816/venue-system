//保存数据
$(document).ready(function () {
    //-------预定---------
    $(".reserveTd").on('mousedown', function () {
        var fieldId = $(this).attr("data-field-id");
        var fieldName = $(this).attr("data-field-name");
        var price = $(this).attr("data-price");
        var time = $(this).attr("data-time");
        var startTime=time.substring(0,5);
        var endTime=time.substring(6,12);
        var time1=time.substring(0,2);
        var time2=time.substring(3,5);
        var time3=time.substring(6,8);
        var time4=time.substring(9,11);
        var timeId=time1+time2+time3+time4;
        var tr_id=fieldId+timeId;
        var order_item_id=tr_id+'item';
        if ($(this).hasClass("access")) {//预定
            $(this).removeClass("access");
            $(this).addClass("unPayed");
            var s='<div id='+tr_id+' class="col-sm-2" style="margin:1%;border: 1px solid #009ff0;border-radius:5px;-moz-border-radius: 5px;-webkit-border-radius: 5px;-o-border-radius: 5px;"> ' +
                '<div class="row text-center" style="background-color:#009ff0;">'+time+'</div>' +
                '<div class="row text-center">'+fieldName+'</div></div>';
            $("#unPayed").append(s);
            var index = $("#orderForm div").length;
            var order_info='<div id='+order_item_id+'><input name="venueConsList['+index+'].reserveField.id" value=\''+fieldId+'\' type="hidden">'
                + '<input name="venueConsList['+index+'].startTime" value=\''+startTime+'\' type="hidden">'
                + '<input name="venueConsList['+index+'].endTime" value=\''+endTime+'\' type="hidden"></div>';
            $("#orderForm").append(order_info);

        }else{//取消预定
            $(this).removeClass("unPayed");
            $(this).addClass("access");
            $("#"+tr_id).remove();
            $("#"+order_item_id).remove();
        }
    });
});
function orderSubmit(){
    var reserveVenueCons = $("#orderForm").serializeArray();
    jQuery.postItems({
        url: ctx+'/app/reserve/field/reservation',
        data: reserveVenueCons,
        success: function (result) {
            if(result.bool){
                alert("预订成功");
            }else{
                alert("预订失败");
            }
            location.reload();
        }
    });
}