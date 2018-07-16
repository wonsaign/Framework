package com.nec.zeusas.common.service;

import java.util.List;

import com.nec.zeusas.common.entity.Dictionary;
import com.nec.zeusas.core.service.IService;
import com.nec.zeusas.exception.ServiceException;

/**
 * 业务字典管理服务
 *
 */
public interface DictionaryService //
		extends IService<Dictionary, String> {
	List<Dictionary> getChildren(String DID) throws ServiceException;
}
