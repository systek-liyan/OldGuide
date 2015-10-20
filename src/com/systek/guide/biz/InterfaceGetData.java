package com.systek.guide.biz;

import java.util.List;


public interface InterfaceGetData {
	
	<T> List<T>   getDataList(Class<?> entityType);
	
}
