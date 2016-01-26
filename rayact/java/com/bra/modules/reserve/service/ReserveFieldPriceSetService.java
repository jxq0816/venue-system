package com.bra.modules.reserve.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.bra.common.utils.Collections3;
import com.bra.common.utils.StringUtils;
import com.bra.modules.reserve.entity.form.TimePrice;
import com.bra.modules.reserve.utils.TimeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bra.common.persistence.Page;
import com.bra.common.service.CrudService;
import com.bra.modules.reserve.entity.ReserveFieldPriceSet;
import com.bra.modules.reserve.dao.ReserveFieldPriceSetDao;

/**
 * 场地价格Service
 *
 * @author 肖斌
 * @version 2016-01-06
 */
@Service
@Transactional(readOnly = true)
public class ReserveFieldPriceSetService extends CrudService<ReserveFieldPriceSetDao, ReserveFieldPriceSet> {

    public ReserveFieldPriceSet get(String id) {
        return super.get(id);
    }

    public List<ReserveFieldPriceSet> findList(ReserveFieldPriceSet reserveFieldPriceSet) {
        return super.findList(reserveFieldPriceSet);
    }

    public Page<ReserveFieldPriceSet> findPage(Page<ReserveFieldPriceSet> page, ReserveFieldPriceSet reserveFieldPriceSet) {
        return super.findPage(page, reserveFieldPriceSet);
    }

    @Transactional(readOnly = false)
    public void save(ReserveFieldPriceSet reserveFieldPriceSet) {
        super.save(reserveFieldPriceSet);
    }

    @Transactional(readOnly = false)
    public void delete(ReserveFieldPriceSet reserveFieldPriceSet) {
        super.delete(reserveFieldPriceSet);
    }

    public List<ReserveFieldPriceSet> findListByField(ReserveFieldPriceSet reserveFieldPriceSet) {

        List<ReserveFieldPriceSet> fieldPriceSetList = findList(reserveFieldPriceSet);
        if (Collections3.isEmpty(fieldPriceSetList)) {
            fieldPriceSetList = Lists.newArrayList();
            Map<String, String> weekMap = Maps.newConcurrentMap();
            weekMap.put("1", "周一至周五");
            weekMap.put("2", "周六");
            weekMap.put("3", "周日");

            Map<String, String> memberMap = Maps.newConcurrentMap();
            memberMap.put("1", "散客");
            memberMap.put("2", "会员");
            memberMap.put("3", "团体");

            ReserveFieldPriceSet fieldPriceSet;
            for (String weekKey : weekMap.keySet()) {
                for (String memberKey : memberMap.keySet()) {
                    fieldPriceSet = new ReserveFieldPriceSet();
                    //fieldPriceSet.setVenueId(reserveFieldPriceSet.getVenueId());
                    //fieldPriceSet.setFieldId(reserveFieldPriceSet.getFieldId());
                    fieldPriceSet.setWeek(weekKey);
                    fieldPriceSet.setConsType(memberKey);
                    fieldPriceSet.setTimePriceList(getTimePrice());
                    fieldPriceSetList.add(fieldPriceSet);
                }
            }
            return fieldPriceSetList;
        } else {
            return fieldPriceSetList;
        }
    }

    private List<TimePrice> getTimePrice() {
        List<TimePrice> timePriceList = Lists.newArrayList();
        try {
            List<String> times = TimeUtils.getTimeSpacList("09:00:00", "23:00:00", 60);
            TimePrice timePrice;
            for (String time : times) {
                timePrice = new TimePrice();
                timePrice.setTime(time);
                timePriceList.add(timePrice);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("时间获取异常");
        }
        return timePriceList;
    }

}