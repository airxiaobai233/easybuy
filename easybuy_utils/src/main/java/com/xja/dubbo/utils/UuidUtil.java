package com.xja.dubbo.utils;

import java.util.UUID;

/** 
* @author 作者 :lixiaosheng 
* @date 创建时间：2019年11月14日 上午9:04:00 
* @version 1.0 
*/
public class UuidUtil {
	public static String getUUID(){
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;
	}
 
}
