package com.bra.modules.reserve.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bra.modules.reserve.dao.ReserveVenueConsItemDao;
import com.bra.modules.reserve.entity.*;
import com.bra.modules.reserve.event.venue.VenueCancelEvent;
import com.bra.modules.reserve.event.venue.VenueCheckoutEvent;
import com.bra.modules.reserve.event.venue.VenueReserveEvent;
import com.bra.modules.reserve.utils.AuthorityUtils;
import com.bra.modules.reserve.utils.TimeUtils;
import com.bra.modules.reserve.web.form.SaleVenueLog;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bra.common.persistence.Page;
import com.bra.common.service.CrudService;
import com.bra.modules.reserve.dao.ReserveVenueConsDao;

/**
 * 场地预定主表Service
 *
 * @author 肖斌
 * @version 2016-01-11
 */
@Service
@Transactional(readOnly = true)
public class ReserveVenueConsService extends CrudService<ReserveVenueConsDao, ReserveVenueCons> {

    @Autowired
    private ReserveVenueConsItemDao reserveVenueConsItemDao;
    @Autowired
    private ReserveFieldPriceService reserveFieldPriceService;

    public List<Map<String, Object>> findOrderLog(SaleVenueLog venueLog) {
        return dao.findOrderLog(venueLog);
    }

    public List<Map<String, Object>> findPriceGroupProject(ReserveVenueCons venueCons) {
        return dao.findPriceGroupProject(venueCons);
    }

    public List<Map<String, Object>> findPriceGroupProjectReport(ReserveVenueCons venueCons) {
        venueCons.getSqlMap().put("dsf", AuthorityUtils.getDsf("a.venue_id"));
        return dao.findPriceGroupProjectReport(venueCons);
    }

    /**
     * 结账
     *
     * @param cons
     */
    @Transactional(readOnly = false)
    public ReserveVenueCons saveConsOrder(ReserveVenueCons cons) {
        ReserveVenueCons reserveVenueCons = get(cons.getId());
        Double total = cons.getOrderPrice();
        reserveVenueCons.setPayType(cons.getPayType());
        reserveVenueCons.setOrderPrice(total);
        reserveVenueCons.setShouldPrice(cons.getShouldPrice());
        reserveVenueCons.setCheckOutUser(cons.getCheckOutUser());
        for (ReserveVenueConsItem item : cons.getVenueConsList()) {
            ReserveVenueConsItem consItem = reserveVenueConsItemDao.get(item.getId());
            consItem.setOrderPrice(item.getOrderPrice());
            reserveVenueConsItemDao.update(consItem);
        }

        //ConsType:2:已预定;payType:1:会员卡;
        if ("1".equals(reserveVenueCons.getReserveType())) {
            reserveVenueCons.setReserveType("4");
            reserveVenueCons.preUpdate();
            dao.update(reserveVenueCons);
            reserveVenueCons.setVenueConsList(cons.getVenueConsList());
            //会员扣款;结算教练(事件通知)
            VenueCheckoutEvent venueCheckoutEvent = new VenueCheckoutEvent(reserveVenueCons);
            applicationContext.publishEvent(venueCheckoutEvent);
            return reserveVenueCons;
        }
        return null;
    }

    public ReserveVenueCons get(String id) {
        return super.get(id);
    }

    public List<Map<String,Object>> sellOfChart(ReserveVenueCons venueCons){
        return dao.sellOfChart(venueCons);
    }

    public BigDecimal sellMonthOfChart(ReserveVenueCons venueCons){
        return dao.sellMonthOfChart(venueCons);
    }

    public List<ReserveVenueCons> findList(ReserveVenueCons reserveVenueCons) {
        return super.findList(reserveVenueCons);
    }

    public Page<ReserveVenueCons> findPage(Page<ReserveVenueCons> page, ReserveVenueCons reserveVenueCons) {
        return super.findPage(page, reserveVenueCons);
    }

    public List<ReserveVenueCons> findListOrder(ReserveVenueCons venueCons) {
        return dao.findListOrder(venueCons);
    }

    /**
     * 查询空场率
     *
     * @param venueCons
     * @return
     */
    public List<Map<String, Object>> findOpenRateReport(ReserveVenueCons venueCons) {
        return dao.findOpenRateReport(venueCons);
    }

    /**
     * 存储预定单据
     *
     * @param reserveVenueCons
     */
    @Transactional(readOnly = false)
    public void save(ReserveVenueCons reserveVenueCons) {

        String halfCourt=reserveVenueCons.getHalfCourt();
        String frequency=reserveVenueCons.getFrequency();


        List<Date> reserveDateList = Lists.newArrayList(new Date());
        if ("2".equals(reserveVenueCons.getFrequency())) {//每天
            reserveDateList = TimeUtils.getDayBettwnDays(reserveVenueCons.getEndDate());
        } else if ("3".equals(reserveVenueCons.getFrequency())) {
            reserveDateList = TimeUtils.getWeekBettwnDays(reserveVenueCons.getEndDate());
        }

        //1:散客;2:会员
        //String consType = reserveVenueCons.getConsType();

        for (Date date : reserveDateList) {
            reserveVenueCons.setConsDate(date);
            reserveVenueCons.preInsert();
            dao.insert(reserveVenueCons);
            List<ReserveVenueConsItem> itemList = reserveVenueCons.getVenueConsList();
            Double orderPrice = 0D;
            Date consDate = reserveVenueCons.getConsDate();
            String consWeek = TimeUtils.getWeekOfDate(consDate);

            for (ReserveVenueConsItem item : itemList) {
                item.setConsData(reserveVenueCons);
                item.setConsWeek(consWeek);
                item.setHalfCourt(halfCourt);//设置半场
                item.setFrequency(frequency);//设置频率
                Double price = reserveFieldPriceService.getPrice(item.getReserveField(), reserveVenueCons.getConsType(),
                        reserveVenueCons.getConsDate(), item.getStartTime(), item.getEndTime());
                item.setConsPrice(price);
                orderPrice += price;
                item.preInsert();
                reserveVenueConsItemDao.insert(item);
            }

            //查询会员打折信息
            if ("2".equals(reserveVenueCons.getConsType())) {//会员预定
                Double rate = reserveFieldPriceService.getMemberDiscountRate(reserveVenueCons.getMember());
                if (rate != null && rate != 0) {
                    orderPrice = orderPrice - (orderPrice * rate / 100);
                    reserveVenueCons.setCosOrderPrice((double) Math.round(orderPrice));
                    reserveVenueCons.setDiscountPrice((double) Math.round(orderPrice * rate / 100));
                }
            }
            dao.update(reserveVenueCons);
            //预定教练
            List<String> timeList = TimeUtils.getTimeSpace(itemList.get(0).getStartTime(), itemList.get(0).getEndTime());
            applicationContext.publishEvent(new VenueReserveEvent(reserveVenueCons, timeList));
        }
    }

    /**
     * 取消预定
     *
     * @param itemId
     * @param startTime
     * @param endTime
     * @param tutorOrderId
     * @return
     */
    @Transactional(readOnly = false)
    public List<ReserveVenueConsItem> cancelReserve(String itemId, String startTime, String endTime, String
            tutorOrderId) {
        if (startTime.equals(endTime))
            return null;
        List<ReserveVenueConsItem> consItemList = Lists.newArrayList();
        ReserveVenueConsItem item = reserveVenueConsItemDao.get(itemId);
        ReserveVenueCons venueCons = dao.get(item.getConsData().getId());
        if (item.getStartTime().equals(startTime) && endTime.equals(item.getEndTime())) {
            venueCons.setReserveType("3");//已经取消
            venueCons.preUpdate();
            dao.update(venueCons);
        } else if (TimeUtils.compare(startTime, item.getStartTime()) < 0 || TimeUtils.compare(endTime, item.getEndTime()) > 0) {
            return null;
        } else if (TimeUtils.compare(startTime, endTime) > 0) {
            return null;
        } else if (TimeUtils.compare(startTime, item.getStartTime()) == 0) {
            item.setStartTime(endTime);
            Double price = reserveFieldPriceService.getPrice(item.getReserveField(), venueCons.getConsType(), venueCons.getConsDate(), item.getStartTime(), item.getEndTime());
            item.setConsPrice(price);
            item.preUpdate();
            reserveVenueConsItemDao.update(item);
        } else if (TimeUtils.compare(endTime, item.getEndTime()) == 0) {
            item.setEndTime(startTime);
            Double price = reserveFieldPriceService.getPrice(item.getReserveField(), venueCons.getConsType(), venueCons.getConsDate(), item.getStartTime(), item.getEndTime());
            item.setConsPrice(price);
            item.preUpdate();
            reserveVenueConsItemDao.update(item);
        } else {
            reserveVenueConsItemDao.delete(item);
            ReserveVenueConsItem item1 = new ReserveVenueConsItem();
            item1.setConsData(item.getConsData());
            item1.setConsPrice(item.getConsPrice());
            item1.setStartTime(item.getStartTime());
            item1.setEndTime(startTime);
            item1.setReserveVenue(item.getReserveVenue());
            item1.setReserveField(item.getReserveField());
            item1.setId(null);
            Double price = reserveFieldPriceService.getPrice(item1.getReserveField(), venueCons.getConsType(), venueCons.getConsDate(), item1.getStartTime(), item1.getEndTime());
            item1.setConsPrice(price);
            item1.preInsert();

            ReserveVenueConsItem item2 = new ReserveVenueConsItem();
            item2.setConsData(item.getConsData());
            item2.setConsPrice(item.getConsPrice());
            item2.setStartTime(endTime);
            item2.setEndTime(item.getEndTime());
            item2.setId(null);
            item2.setReserveVenue(item.getReserveVenue());
            item2.setReserveField(item.getReserveField());
            Double price2 = reserveFieldPriceService.getPrice(item2.getReserveField(), venueCons.getConsType(), venueCons.getConsDate(), item2.getStartTime(), item2.getEndTime());
            item2.setConsPrice(price2);
            item2.preInsert();

            consItemList.add(item1);
            consItemList.add(item2);
            reserveVenueConsItemDao.insert(item1);
            reserveVenueConsItemDao.insert(item2);
        }
        //修改教练订单
        applicationContext.publishEvent(new VenueCancelEvent(venueCons, tutorOrderId));
        return consItemList;
    }

    @Transactional(readOnly = false)
    public void delete(ReserveVenueCons reserveVenueCons) {
        super.delete(reserveVenueCons);
    }

}