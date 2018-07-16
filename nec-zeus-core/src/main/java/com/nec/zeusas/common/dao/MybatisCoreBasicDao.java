package com.nec.zeusas.common.dao;

import java.io.Serializable;

import com.nec.zeusas.common.mybatis.MybatisBasicDao;
import com.nec.zeusas.core.entity.IEntity;

public class MybatisCoreBasicDao <T extends IEntity, PK extends Serializable> extends MybatisBasicDao<T, PK>{

}
