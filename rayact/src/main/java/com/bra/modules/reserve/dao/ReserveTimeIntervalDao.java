package com.bra.modules.reserve.dao;

import com.bra.common.persistence.CrudDao;
import com.bra.common.persistence.annotation.MyBatisDao;
import com.bra.modules.reserve.entity.ReserveTimeInterval;

/**
 * 教练DAO接口
 * @author jiangxingqi
 * @version 2016-01-15
 */
@MyBatisDao
public interface ReserveTimeIntervalDao extends CrudDao<ReserveTimeInterval> {
	
}