package io.javaweb.community.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.javaweb.community.entity.AbsIpInfoEntity;

public class IpUtils {
	
	private static final String TAOBAO_SERVICE = "http://ip.taobao.com/service/getIpInfo.php?ip=";
	
	private static final Integer DEFAULT_TIME_OUT = 2000;
	
	/**
	 * 根据IP读取所属地址,运营商等信息,该API访问频率限制  < 10qps
	 * @param ip
	 * @return
	 * @throws IOException
	 */
	public static JSONObject getIpInfo(String ip) throws IOException {
		return JSON.parseObject(SimpleHttpUtils.getString(TAOBAO_SERVICE + ip, DEFAULT_TIME_OUT, DEFAULT_TIME_OUT));
	}
	
	/**
	 * 设置IP地址信息
	 * @param entity
	 * @throws IOException
	 */
	public static void setIpInfo(AbsIpInfoEntity entity) throws IOException {
		String ip = entity.getIp();
		if(!GeneralUtils.isEmpty(ip)) {
			JSONObject jsonObject = getIpInfo(ip);
			Integer code = jsonObject.getInteger("code");
			if(code == 0) {
				JSONObject data = jsonObject.getJSONObject("data");
				String country = data.getString("country");		//国
				String region = data.getString("region");		//省
				String city = data.getString("city");			//市
				String isp = data.getString("isp");				//运营商
				if(city.equals(region)) {
					region = "";		//直辖市,木有省
				}
				entity.setAddress(country + region + city);
				entity.setOperator(isp);
			}
		}
	}

	/**
	 *
	 * 鸭式辩型的多态,Emmmmm,Python的感觉
	 * 实例对象必须具备三个bean属性
	 * 	1,ip:从该属性读取需要查询的ip
	 * 	2,operator:从该属性写入ip运营商洗洗
	 * 	3,address:从该属性写入ip的地址信息
	 * @param bean
	 */
	public static <T> void setIpInfo(T bean) {
		try {
			String ip = null;
			Method operator = null;
			Method address = null;
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			if(propertyDescriptors != null) {
				for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					if(propertyDescriptor.getName().equals("ip")) {
						ip = (String) propertyDescriptor.getReadMethod().invoke(bean);
					}else if(propertyDescriptor.getName().equals("operator")) {
						operator = propertyDescriptor.getWriteMethod();
					}else if(propertyDescriptor.getName().equals("address")) {
						address = propertyDescriptor.getWriteMethod();
					}
				}
			}
			if(ip != null) {
				JSONObject jsonObject = getIpInfo(ip);
				Integer code = jsonObject.getInteger("code");
				if(code == 0) {
					JSONObject data = jsonObject.getJSONObject("data");
					String country = data.getString("country");		//国
					String region = data.getString("region");		//省
					String city = data.getString("city");			//市
					String sip = data.getString("isp");				//运营商
					if(city.equals(region)) {
						region = "";		//直辖市,木有省
					}
					if(operator != null) {
						operator.invoke(bean, country + region + city);
					}
					if(address != null) {
						address.invoke(bean, sip);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		AbsIpInfoEntity infoEntity = new AbsIpInfoEntity() {
			private static final long serialVersionUID = 1L;
		};
		infoEntity.setIp("59.110.167.11");
		setIpInfo(infoEntity);
		System.out.println(infoEntity.getAddress() +" - " + infoEntity.getOperator());
	}
}
