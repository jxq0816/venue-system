package com.bra.modules.reserve.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bra.common.persistence.Page;
import com.bra.common.service.CrudService;
import com.bra.modules.reserve.entity.ReserveCommodity;
import com.bra.modules.reserve.dao.ReserveCommodityDao;

/**
 * 商品Service
 * @author jiangxingqi
 * @version 2016-01-07
 */
@Service
@Transactional(readOnly = true)
public class ReserveCommodityService extends CrudService<ReserveCommodityDao, ReserveCommodity> {

	public ReserveCommodity get(String id) {
		return super.get(id);
	}

	public List<ReserveCommodity> checkCommodityId(ReserveCommodity commodity) {
		List<ReserveCommodity> list=dao.checkCommodityId(commodity);
		return list;
	}
	
	public List<ReserveCommodity> findList(ReserveCommodity commodity) {
		return super.findList(commodity);
	}
	
	public Page<ReserveCommodity> findPage(Page<ReserveCommodity> page, ReserveCommodity commodity) {
		return super.findPage(page, commodity);
	}
	
	@Transactional(readOnly = false)
	public void save(ReserveCommodity commodity) {
		super.save(commodity);
	}
	
	@Transactional(readOnly = false)
	public void delete(ReserveCommodity commodity) {
		super.delete(commodity);
	}
	
}