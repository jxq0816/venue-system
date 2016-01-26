/**
 * Copyright &copy; 2012-2014 <a href="https://github.com.bra.>JeeSite</a> All rights reserved.
 */
package com.bra.modules.cms.dao;

import com.bra.modules.cms.entity.Comment;
import com.bra.common.persistence.CrudDao;
import com.bra.common.persistence.annotation.MyBatisDao;

/**
 * 评论DAO接口
 *
 * @version 2013-8-23
 */
@MyBatisDao
public interface CommentDao extends CrudDao<Comment> {

}
