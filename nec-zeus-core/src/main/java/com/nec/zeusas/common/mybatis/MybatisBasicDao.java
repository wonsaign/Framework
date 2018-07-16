package com.nec.zeusas.common.mybatis;

import java.io.Serializable;

import com.nec.zeusas.core.dao.BasicDao;
import com.nec.zeusas.core.entity.IEntity;

public abstract  class MybatisBasicDao <T extends IEntity, PK extends Serializable> extends BasicDao<T,PK> {


}
