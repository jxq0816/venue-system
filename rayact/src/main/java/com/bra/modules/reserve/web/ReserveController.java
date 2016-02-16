package com.bra.modules.reserve.web;

import com.bra.common.utils.Collections3;
import com.bra.common.utils.DateUtils;
import com.bra.common.utils.StringUtils;
import com.bra.common.web.BaseController;
import com.bra.common.web.annotation.Token;
import com.bra.modules.reserve.entity.*;
import com.bra.modules.reserve.entity.form.VenueGiftForm;
import com.bra.modules.reserve.service.*;
import com.bra.modules.reserve.utils.AuthorityUtils;
import com.bra.modules.reserve.utils.TimeUtils;
import com.bra.modules.sys.entity.User;
import com.bra.modules.sys.service.SystemService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 场地预定管理
 * Created by xiaobin on 16/1/5.
 */
@Controller
@RequestMapping(value = "${adminPath}/reserve/field")
public class ReserveController extends BaseController {

    //场馆
    @Autowired
    private ReserveVenueService reserveVenueService;
    //场地
    @Autowired
    private ReserveFieldService reserveFieldService;
    //会员信息
    @Autowired
    private ReserveMemberService reserveMemberService;
    @Autowired
    private ReserveFieldPriceService reserveFieldPriceService;
    @Autowired
    private ReserveVenueConsService reserveVenueConsService;
    @Autowired
    private ReserveVenueConsItemService reserveVenueConsItemService;
    //教练
    @Autowired
    private ReserveTutorService reserveTutorService;
    @Autowired
    private ReserveTutorOrderService reserveTutorOrderService;
    @Autowired
    private ReserveCommodityService reserveCommodityService;
    @Autowired
    private ReserveVenueGiftService reserveVenueGiftService;
    @Autowired
    private SystemService systemService;


    //场地预定
    @RequestMapping(value = "main")
    public String main(Long t, String venueId, Model model) throws ParseException {
        //当前日期
        Date defaultDate = DateUtils.parseDate(DateUtils.getDate(), "yyyy-MM-dd");
        //获取可预定时间段
        Map<String, Long> timeSlot = TimeUtils.getNextDaysMap(defaultDate, 7);
        model.addAttribute("timeSlot", timeSlot);
        //默认时间
        if (t != null) {
            defaultDate = new Date(t);
        }
        model.addAttribute("consDate", defaultDate);
        //获得所有场馆信息
        ReserveVenue venue = new ReserveVenue();
        venue.getSqlMap().put("dsf", AuthorityUtils.getVenueIdSql("a.id"));
        List<ReserveVenue> reserveVenueList = reserveVenueService.findList(venue);
        model.addAttribute("reserveVenueList", reserveVenueList);
        //获取营业时间
        List<String> times = TimeUtils.getTimeSpacListValue("09:00:00", "23:00:00", 30);
        model.addAttribute("times", times);

        //默认场馆的场地
        if (!Collections3.isEmpty(reserveVenueList)) {
            ReserveVenue reserveVenue;
            if (StringUtils.isNoneBlank(venueId)) {
                reserveVenue = reserveVenueService.get(venueId);
            } else {
                reserveVenue = reserveVenueList.get(0);
            }
            //默认场馆
            model.addAttribute("reserveVenue", reserveVenue);
            ReserveField reserveField = new ReserveField();
            reserveField.setReserveVenue(reserveVenue);
            List<ReserveField> reserveFieldList = reserveFieldService.findList(reserveField);
            model.addAttribute("reserveFieldList", reserveFieldList);
            //场地价格
            model.addAttribute("venueFieldPriceList", reserveFieldPriceService.findByDate(reserveVenue.getId(), "1", defaultDate, times));
        }
        return "reserve/saleField/reserveField";
    }

    //预定表单
    @RequestMapping(value = "reserveForm")
    @Token(save = true)
    public String reserveForm(Model model, String fieldId, Long date, String time, String venueId) throws ParseException {
        ReserveField reserveField = reserveFieldService.get(fieldId);
        model.addAttribute("reserveField", reserveField);

        //获取营业时间
        List<String> times = TimeUtils.getTimeSpacList("09:00:00", "23:00:00", TimeUtils.BENCHMARK);
        model.addAttribute("times", times);
        String consDate = DateUtils.formatDate(new Date(date), "yyyy-MM-dd");
        model.addAttribute("date", consDate);

        //会员
        ReserveMember reserveMember = new ReserveMember();
        model.addAttribute("memberList", reserveMemberService.findList(reserveMember));

        //教练
        model.addAttribute("tutors", reserveTutorService.findList(new ReserveTutor()));

        if (StringUtils.isNotBlank(time)) {
            String[] timeStr = time.split("-");
            model.addAttribute("startTime", timeStr[0]);
            model.addAttribute("endTime", timeStr[1]);
        }
        model.addAttribute("venueId", venueId);
        return "reserve/saleField/reserveForm";
    }

    //取消预定
    @RequestMapping(value = "cancelForm")
    @Token(save = true)
    public String cancelForm(Model model, String fieldId, String itemId, String time) {
        ReserveVenueConsItem item = reserveVenueConsItemService.get(itemId);
        ReserveVenueCons cons = reserveVenueConsService.get(item.getConsData().getId());
        model.addAttribute("cos", cons);
        if (StringUtils.isNoneBlank(time)) {
            String startTime = time.split("-")[0];
            String endTime = time.split("-")[1];
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);
        }
        model.addAttribute("item", item);
        List<String> times = TimeUtils.getTimeSpacList("09:00:00", "23:00:00", TimeUtils.BENCHMARK);
        model.addAttribute("times", times);
        //教练订单
        List<ReserveTutorOrder> tutorOrderList = reserveTutorOrderService.findNotCancel(cons.getId(), ReserveVenueCons.MODEL_KEY);
        if (!Collections3.isEmpty(tutorOrderList)) {
            model.addAttribute("tutorOrder", tutorOrderList.get(0));
        }
        return "reserve/saleField/cancelForm";
    }

    /**
     * 取消预定
     *
     * @param itemId
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "cancelReservation")
    @ResponseBody
    @Token(remove = true)
    public List<Map<String, String>> cancelReservation(String itemId, String startTime, String endTime, String tutorOrderId) {
        List<ReserveVenueConsItem> itemList = reserveVenueConsService.cancelReserve(itemId, startTime, endTime, tutorOrderId);
        return getReserveMap(itemList);
    }

    private List<Map<String, String>> getReserveMap(List<ReserveVenueConsItem> itemList) {
        List<Map<String, String>> list = Lists.newArrayList();
        if (itemList == null)
            return list;
        Map<String, String> data;
        for (ReserveVenueConsItem item : itemList) {
            String startTime = item.getStartTime();
            String endTime = item.getEndTime();
            List<String> times = TimeUtils.getTimeSpacListValue(startTime + ":00", endTime + ":00", 30);
            for (String time : times) {
                data = Maps.newConcurrentMap();
                data.put("fieldId", item.getReserveField().getId());
                data.put("time", time);
                data.put("itemId", item.getId());
                list.add(data);
            }
        }
        return list;
    }

    /**
     * 场地预定(保存)
     *
     * @param reserveVenueCons
     * @return
     */
    @RequestMapping(value = "reservation")
    @ResponseBody
    @Token(remove = true)
    public List<Map<String, String>> reservation(ReserveVenueCons reserveVenueCons) {
        reserveVenueCons.setReserveType(ReserveVenueCons.RESERVATION);//预定
        reserveVenueConsService.save(reserveVenueCons);
        List<ReserveVenueConsItem> itemList = reserveVenueCons.getVenueConsList();
        return getReserveMap(itemList);
    }

    /**
     * 结算订单
     *
     * @param itemId
     * @return
     */
    @RequestMapping(value = "settlementForm")
    public String settlementForm(String itemId, Model model) {
        ReserveVenueConsItem consItem = reserveVenueConsItemService.get(itemId);
        ReserveVenueConsItem search = new ReserveVenueConsItem();
        consItem.getConsData().setReserveType("3");
        search.setConsData(consItem.getConsData());
        ReserveVenueCons cons = reserveVenueConsService.get(consItem.getConsData().getId());
        model.addAttribute("cos", cons);
        //教练
        model.addAttribute("tutors", reserveTutorService.findList(new ReserveTutor()));
        //教练订单
        List<ReserveTutorOrder> tutorOrderList = reserveTutorOrderService.findNotCancel(cons.getId(), ReserveVenueCons.MODEL_KEY);
        if (!Collections3.isEmpty(tutorOrderList)) {
            model.addAttribute("tutorOrder", tutorOrderList.get(0));
        }

        List<String> times = TimeUtils.getTimeSpacList("09:00:00", "23:00:00", TimeUtils.BENCHMARK);
        model.addAttribute("times", times);
        List<ReserveVenueConsItem> itemList = reserveVenueConsItemService.findList(search);
        model.addAttribute("itemList", itemList);
        return "reserve/saleField/settlementForm";
    }

    @RequestMapping(value = "settlementDetailForm")
    @Token(save = true)
    public String settlementDetailForm(String cosId,String payType, Double shouldPrice, Double orderPrice, Double discountPrice, Model model) {
        ReserveVenueCons cons = reserveVenueConsService.get(cosId);
        model.addAttribute("cos", cons);
        //教练
        model.addAttribute("tutors", reserveTutorService.findList(new ReserveTutor()));
        //教练订单
        List<ReserveTutorOrder> tutorOrderList = reserveTutorOrderService.findNotCancel(cons.getId(), ReserveVenueCons.MODEL_KEY);
        if (!Collections3.isEmpty(tutorOrderList)) {
            model.addAttribute("tutorOrder", tutorOrderList.get(0));
        }
        ReserveVenueCons venueCons = new ReserveVenueCons(cons.getId());
        ReserveVenueConsItem search = new ReserveVenueConsItem();
        search.setConsData(venueCons);
        List<ReserveVenueConsItem> itemList = reserveVenueConsItemService.findList(search);
        model.addAttribute("itemList", itemList);
        model.addAttribute("shouldPrice", shouldPrice);
        model.addAttribute("orderPrice", orderPrice);
        model.addAttribute("discountPrice", discountPrice);
        model.addAttribute("payType", payType);
        //赠品
        model.addAttribute("giftList", reserveVenueGiftService.findList(new ReserveVenueGift(cons.getId(), ReserveVenueCons.MODEL_KEY)));
        return "reserve/saleField/settlementDetailForm";
    }


    /**
     * 结算订单
     *
     * @param
     * @return
     */
    @RequestMapping(value = "saveSettlement")
    @ResponseBody
    @Token(remove = true)
    public List<Map<String, String>> saveSettlement(ReserveVenueCons cons) {
        ReserveVenueCons venueCons = reserveVenueConsService.saveConsOrder(cons);
        return getReserveMap(venueCons.getVenueConsList());
    }

    //订单详情
    @RequestMapping(value = "details")
    public String details(String itemId, Model model) {
        ReserveVenueConsItem consItem = reserveVenueConsItemService.get(itemId);
        ReserveVenueConsItem search = new ReserveVenueConsItem();
        consItem.getConsData().setReserveType("3");

        search.setConsData(consItem.getConsData());
        ReserveVenueCons cons = reserveVenueConsService.get(consItem.getConsData().getId());
        model.addAttribute("cos", cons);
        List<ReserveVenueConsItem> itemList = reserveVenueConsItemService.findList(search);
        model.addAttribute("itemList", itemList);
        List<String> times = TimeUtils.getTimeSpacList("09:00:00", "23:00:00", TimeUtils.BENCHMARK);
        model.addAttribute("tutorOrderList", reserveTutorOrderService.findNotCancel(cons.getId(), ReserveVenueCons.MODEL_KEY));
        model.addAttribute("times", times);
        //赠品
        model.addAttribute("giftList", reserveVenueGiftService.findList(new ReserveVenueGift(cons.getId(), ReserveVenueCons.MODEL_KEY)));
        return "reserve/saleField/details";
    }

    //退款
    @RequestMapping(value = "refund")
    public String refund(String itemId, Model model) {
       /* ReserveVenueConsItem consItem = reserveVenueConsItemService.get(itemId);
        ReserveVenueConsItem search = new ReserveVenueConsItem();
        consItem.getConsData().setReserveType("3");

        search.setConsData(consItem.getConsData());
        ReserveVenueCons cons = reserveVenueConsService.get(consItem.getConsData().getId());
        model.addAttribute("cos", cons);
        List<ReserveVenueConsItem> itemList = reserveVenueConsItemService.findList(search);
        model.addAttribute("itemList", itemList);
        List<String> times = TimeUtils.getTimeSpacList("09:00:00", "23:00:00", TimeUtils.BENCHMARK);
        model.addAttribute("tutorOrderList", reserveTutorOrderService.findNotCancel(cons.getId(), ReserveVenueCons.MODEL_KEY));
        model.addAttribute("times", times);*/
        return "reserve/saleField/details";
    }

    //赠品
    @RequestMapping(value = "gift")
    @Token(save = true)
    public String gift(String itemId, Model model) {
        ReserveVenueConsItem consItem = reserveVenueConsItemService.get(itemId);
        ReserveVenueConsItem search = new ReserveVenueConsItem();
        consItem.getConsData().setReserveType("3");

        search.setConsData(consItem.getConsData());
        ReserveVenueCons cons = reserveVenueConsService.get(consItem.getConsData().getId());
        model.addAttribute("cos", cons);
        //商品
        model.addAttribute("giftList", reserveCommodityService.findList(new ReserveCommodity()));
        return "reserve/saleField/giftForm";
    }

    //保存赠品
    @RequestMapping(value = "saveGift")
    @ResponseBody
    @Token(remove = true)
    public String saveGift(VenueGiftForm giftForm) {
        reserveVenueGiftService.saveVenueList(giftForm, ReserveVenueCons.MODEL_KEY);
        return "success";
    }

    @RequestMapping(value = "checkUserPwd")
    @ResponseBody
    public User checkUserPwd(String userPwd) {
        User user = systemService.getUserByPwd(userPwd);
        return user;
    }
}