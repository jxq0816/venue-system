package com.bra.modules.reserve.entity.form;


import com.bra.common.persistence.SaasEntity;
import com.bra.modules.reserve.entity.*;

import java.util.Date;
import java.util.List;

/**
 * 场馆收入统计Entity
 *
 * @author jiangxingqi
 * @version 2015-12-29
 */
public class ReserveCommodityDayReport extends SaasEntity<ReserveCommodityDayReport> {

    private static final long serialVersionUID = 1L;

    private Double  storedCardBill;// 储值卡

    private Double  cashBill;//现金

    private Double  bankCardBill;//银行卡

    private Double  weiXinBill;//微信

    private Double  aliPayBill;//支付宝

    private Double  dueBill;// 欠账

    private Double  otherBill;// 其它

    private ReserveVenue reserveVenue;//场馆

    private ReserveCommodityType reserveCommodityType;//商品类型

    private Date day;//日期

    public ReserveCommodityType getReserveCommodityType() {
        return reserveCommodityType;
    }

    public void setReserveCommodityType(ReserveCommodityType reserveCommodityType) {
        this.reserveCommodityType = reserveCommodityType;
    }

    public Double getStoredCardBill() {
        return storedCardBill;
    }

    public void setStoredCardBill(Double storedCardBill) {
        this.storedCardBill = storedCardBill;
    }

    public Double getCashBill() {
        return cashBill;
    }

    public void setCashBill(Double cashBill) {
        this.cashBill = cashBill;
    }

    public Double getBankCardBill() {
        return bankCardBill;
    }

    public void setBankCardBill(Double bankCardBill) {
        this.bankCardBill = bankCardBill;
    }

    public Double getWeiXinBill() {
        return weiXinBill;
    }

    public void setWeiXinBill(Double weiXinBill) {
        this.weiXinBill = weiXinBill;
    }

    public Double getAliPayBill() {
        return aliPayBill;
    }

    public void setAliPayBill(Double aliPayBill) {
        this.aliPayBill = aliPayBill;
    }

    public Double getDueBill() {
        return dueBill;
    }

    public void setDueBill(Double dueBill) {
        this.dueBill = dueBill;
    }

    public Double getOtherBill() {
        return otherBill;
    }

    public void setOtherBill(Double otherBill) {
        this.otherBill = otherBill;
    }

    public ReserveVenue getReserveVenue() {
        return reserveVenue;
    }

    public void setReserveVenue(ReserveVenue reserveVenue) {
        this.reserveVenue = reserveVenue;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }



}