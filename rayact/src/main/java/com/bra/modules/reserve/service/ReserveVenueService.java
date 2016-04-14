package com.bra.modules.reserve.service;

import com.bra.common.persistence.Page;
import com.bra.common.service.CrudService;
import com.bra.modules.mechanism.web.bean.AttMainForm;
import com.bra.modules.reserve.dao.ReserveVenueDao;
import com.bra.modules.reserve.entity.ReserveField;
import com.bra.modules.reserve.entity.ReserveProject;
import com.bra.modules.reserve.entity.ReserveVenue;
import com.bra.modules.reserve.entity.form.*;
import com.bra.modules.reserve.utils.AuthorityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 场馆管理Service
 *
 * @author 肖斌
 * @version 2015-12-29
 */
@Service
@Transactional(readOnly = true)
public class ReserveVenueService extends CrudService<ReserveVenueDao, ReserveVenue> {

    @Autowired
    private ReserveVenueDao dao;

    @Autowired
    private ReserveFieldService reserveFieldService;

    @Autowired
    private ReserveCardStatementsService reserveCardStatementsService;

    @Autowired
    private ReserveCommoditySellService reserveCommoditySellService;


    public ReserveVenue get(String id) {
        ReserveVenue reserveVenue = super.get(id);
        return reserveVenue;
    }

    public List<ReserveVenue> findList(ReserveVenue reserveVenue) {
        if (reserveVenue != null) {
            if (reserveVenue.getSqlMap().get("dsf") == null)
                reserveVenue.getSqlMap().put("dsf", AuthorityUtils.getDsf("a.id"));
        }
        return super.findList(reserveVenue);
    }

    public Page<ReserveVenue> findPage(Page<ReserveVenue> page, ReserveVenue reserveVenue) {
        return super.findPage(page, reserveVenue);
    }

    @Transactional(readOnly = false)
    public void save(ReserveVenue reserveVenue) {
        super.save(reserveVenue);
    }

    @Transactional(readOnly = false)
    public void save(ReserveVenue reserveVenue, AttMainForm attMainForm) {
        super.save(reserveVenue);
        updateAttMain(reserveVenue, attMainForm);
    }

    @Transactional(readOnly = false)
    public void delete(ReserveVenue reserveVenue) {
        super.delete(reserveVenue);
    }

    //日报表
    public List<ReserveVenueProjectFieldDayReport> dayReport(ReserveVenueProjectFieldIntervalReport fieldReport){

        ReserveVenue venue=fieldReport.getReserveVenue();
        ReserveField field=fieldReport.getReserveField();
        ReserveProject project=fieldReport.getReserveProject();

        ReserveVenueProjectFieldDayReport dayReport=new ReserveVenueProjectFieldDayReport();
        dayReport.setReserveVenue(venue);
        dayReport.setReserveProject(project);
        dayReport.setReserveField(field);

        Date startDate=fieldReport.getStartDate();
        Date endDate=fieldReport.getEndDate();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);


        List<ReserveVenueProjectFieldDayReport> list = new ArrayList<>();

        while(endCal.after(startCal)|| endCal.equals(startCal)){

            Date day =startCal.getTime();
            dayReport.setDay(day);
            List<ReserveVenueProjectFieldDayReport> dayReportList = dao.dayReport(dayReport);
            if (dayReportList == null) {
                continue;
            }
            for (ReserveVenueProjectFieldDayReport report : dayReportList) {

                Double bill = report.getBill();
                if (bill != 0.0) {
                    list.add(report);
                }
            }
            startCal.add(Calendar.DATE, 1);
        }
        return list;
    }
    //场馆收入统计
    public ReserveVenueIncomeIntervalReport reserveVenueIncomeIntervalReport(ReserveVenueProjectIntervalReport venueProjectReport) {
        Double billSum=0.0;
        Double storedCardSum=0.0;
        Double cashSum=0.0;
        Double bankCardSum=0.0;
        Double weiXinSum=0.0;
        Double aliPaySum=0.0;
        Double otherSum=0.0;
        Double dueSum=0.0;
        List<ReserveVenueProjectIntervalReport>  venueProjectList=dao.findVenueProjectList(venueProjectReport);//查询场馆下的所有场地
        List<ReserveVenueProjectIntervalReport> venueProjectBlockReports = dao.reserveVenueProjectBlockIntervalReport(venueProjectReport);//场馆 项目 包场 收入统计
        List<ReserveVenueProjectIntervalReport> venueProjectDividedReports = dao.reserveVenueProjectDividedIntervalReport(venueProjectReport);//场馆 项目 散客 收入统计
        for(ReserveVenueProjectIntervalReport i:venueProjectList){// 遍历 场馆和项目
            ReserveProject project = i.getReserveProject();
            ReserveVenue venue = i.getReserveVenue();
            Double bill=0.0;
            Double storedCard=0.0;
            Double cash=0.0;
            Double bankCard=0.0;
            Double weiXin=0.0;
            Double aliPay=0.0;
            Double other=0.0;
            Double due=0.0;
            //将包场收入加入
            for(ReserveVenueProjectIntervalReport j:venueProjectBlockReports){
                if(j.getReserveProject().getId().equals(project.getId())&&j.getReserveVenue().getId().equals(venue.getId())){
                    bill+=j.getBill();
                    storedCard+=j.getFieldBillStoredCard();
                    cash+=j.getFieldBillCash();
                    bankCard+=j.getFieldBillBankCard();
                    weiXin+=j.getFieldBillWeiXin();
                    aliPay+=j.getFieldBillAliPay();
                    other+=j.getFieldBillOther();
                    due+=j.getFieldBillDue();
                }
            }
            //将散客收入加入
            for(ReserveVenueProjectIntervalReport k:venueProjectDividedReports){
                if(k.getReserveProject().getId().equals(project.getId())&&k.getReserveVenue().getId().equals(venue.getId())){
                    bill+=k.getBill();
                    storedCard+=k.getFieldBillStoredCard();
                    cash+=k.getFieldBillCash();
                    bankCard+=k.getFieldBillBankCard();
                    weiXin+=k.getFieldBillWeiXin();
                    aliPay+=k.getFieldBillAliPay();
                    other+=k.getFieldBillOther();
                    due+=k.getFieldBillDue();
                }
            }
            i.setFieldBillStoredCard(storedCard);
            i.setBill(bill);
            i.setFieldBillCash(cash);
            i.setFieldBillBankCard(bankCard);
            i.setFieldBillWeiXin(weiXin);
            i.setFieldBillAliPay(aliPay);
            i.setFieldBillOther(other);
            i.setFieldBillDue(due);
            //场地 项目总合计 计算
            billSum+=bill;
            storedCardSum+=storedCard;
            cashSum+=cash;
            bankCardSum+=bankCard;
            weiXinSum+=weiXin;
            aliPaySum+=aliPay;
            otherSum+=other;
            dueSum+=due;
        }
        for (ReserveVenueProjectIntervalReport projectIntervalReport : venueProjectBlockReports) {//场馆 项目遍历
            List<ReserveVenueProjectFieldIntervalReport> fieldReports = this.reserveVenueProjectFieldIntervalReport(projectIntervalReport);//场馆 项目 场地 收入统计
            projectIntervalReport.setFieldIntervalReports(fieldReports);//场馆 项目 再精确到几号场地
        }
        //场地 项目 总合计设置
        ReserveVenueIncomeIntervalReport venueReport=new ReserveVenueIncomeIntervalReport();
        venueReport.setBill(billSum);
        venueReport.setStoredCardBill(storedCardSum);
        venueReport.setCashBill(cashSum);
        venueReport.setBankCardBill(bankCardSum);
        venueReport.setWeiXinBill(weiXinSum);
        venueReport.setAliPayBill(aliPaySum);
        venueReport.setOtherBill(otherSum);
        venueReport.setDueBill(dueSum);
        venueReport.setProjectIntervalReports(venueProjectList);//设置场馆项目的统计
        return venueReport;
    }

    /**
     * 场馆 项目 场地 收入统计
     *
     */
    public List<ReserveVenueProjectFieldIntervalReport> reserveVenueProjectFieldIntervalReport(ReserveVenueProjectIntervalReport projectIntervalReport) {

        List<ReserveVenueProjectFieldIntervalReport> filedReports = dao.reserveVenueProjectFieldIntervalReport(projectIntervalReport);
        for(ReserveVenueProjectFieldIntervalReport report:filedReports){

            List<ReserveVenueProjectFieldDayReport> dayReports=this.dayReport(report);
            report.setDayReports(dayReports);
        }
        return filedReports;
    }

    public ReserveVenueTotalIntervalReport totalIncomeReport(ReserveVenueTotalIntervalReport intervalTotalReport) {

        ReserveVenue reserveVenue=intervalTotalReport.getReserveVenue();
        Date startDate=intervalTotalReport.getStartDate();
        Date endDate=intervalTotalReport.getEndDate();
        Double billSum=0.0;
        Double storedCardSum=0.0;
        Double cashSum=0.0;
        Double bankCardSum=0.0;
        Double weiXinSum=0.0;
        Double aliPaySum=0.0;
        Double otherSum=0.0;
        Double dueSum=0.0;
        //场馆收入
        ReserveVenueProjectIntervalReport intervalReport=new ReserveVenueProjectIntervalReport();
        intervalReport.setReserveVenue(reserveVenue);
        intervalReport.setStartDate(startDate);
        intervalReport.setEndDate(endDate);
        List<ReserveVenueProjectIntervalReport> list1 = dao.reserveVenueProjectBlockIntervalReport(intervalReport);
        for (ReserveVenueProjectIntervalReport report : list1) {
            if(report!=null) {
                billSum += report.getBill();
                storedCardSum += report.getFieldBillStoredCard();
                cashSum += report.getFieldBillCash();
                bankCardSum += report.getFieldBillBankCard();
                weiXinSum += report.getFieldBillWeiXin();
                aliPaySum += report.getFieldBillAliPay();
                otherSum += report.getFieldBillOther();
                dueSum += report.getFieldBillDue();
            }
        }
       /* //会员收入
        ReserveMemberIntervalReport intervalMemberReport=new ReserveMemberIntervalReport();
        intervalMemberReport.setReserveVenue(reserveVenue);
        intervalMemberReport.setStartDate(startDate);
        intervalMemberReport.setEndDate(endDate);
        List<ReserveMemberIntervalReport> memberCollectReports=reserveCardStatementsService.memberIncomeCollectReport(intervalMemberReport);
        for(ReserveMemberIntervalReport report:memberCollectReports){
            if(report!=null){
                billSum+=report.getBill();
                storedCardSum+=report.getStoredCardBill();
                cashSum+=report.getCashBill();
                bankCardSum+=report.getBankCardBill();
                weiXinSum+=report.getWeiXinBill();
                aliPaySum+=report.getAliPayBill();
                otherSum+=report.getOtherBill();
                dueSum+=report.getDueBill();
            }

        }*/
        //商品收入
        ReserveCommodityIntervalReport reserveCommodityIntervalReport=new  ReserveCommodityIntervalReport();
        reserveCommodityIntervalReport.setReserveVenue(reserveVenue);
        reserveCommodityIntervalReport.setEndDate(endDate);
        reserveCommodityIntervalReport.setStartDate(startDate);
        List<ReserveCommodityIntervalReport> commodityIntervalReports=reserveCommoditySellService.commodityIncomeCollectReport(reserveCommodityIntervalReport);
        for(ReserveCommodityIntervalReport report:commodityIntervalReports){
            if(report!=null) {
                billSum += report.getBill();
                storedCardSum += report.getStoredCardBill();
                cashSum += report.getCashBill();
                bankCardSum += report.getBankCardBill();
                weiXinSum += report.getWeiXinBill();
                aliPaySum += report.getAliPayBill();
                otherSum += report.getOtherBill();
                dueSum += report.getDueBill();
            }
        }

        intervalTotalReport.setBill(billSum);
        intervalTotalReport.setStoredCardBill(storedCardSum);
        intervalTotalReport.setCashBill(cashSum);
        intervalTotalReport.setBankCardBill(bankCardSum);
        intervalTotalReport.setWeiXinBill(weiXinSum);
        intervalTotalReport.setAliPayBill(aliPaySum);
        intervalTotalReport.setOtherBill(otherSum);
        intervalTotalReport.setDueBill(dueSum);
        return intervalTotalReport;
    }
}