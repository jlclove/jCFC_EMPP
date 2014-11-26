package com.gudlike.cfc.empp;

import java.util.Date;

import org.dom4j.DocumentException;

import EcpOpen.Model.SimpleReference;
import EcpOpen.Model.Timer;
import EcpOpen.constant.Constants;
import EcpOpen.http.Extend.MCSM.sendSms;

/**
 * @author jail
 *
 * @date 2014年11月25日
 */
public class SendSms {
	public static void main(String[] args) {
		// 设置Constants中的常量值，web工程中应保证这些值能持久化
		Constants.extendUrl = "http://open.ecplive.cn:8080/EcpOpen/services/ExtendService/extendService";
		Constants.spid = "22487930570003";
		Constants.serviceKey = "bec67614807546f48846f976a5ecf681";
		Constants.appid = "01156021";
		Constants.appkey = "a9c35c476cc94182a46e4d49e6e54da1";
		Constants.type = "1";
//		SimpleReference simpleRef = new SimpleReference();
//		simpleRef.setEndpoint("http://127.0.0.1:8080/");
//		simpleRef.setInterfaceName("notifySmsDeliveryStatus");
//		simpleRef.setCorrelator("");
		// 实例化sendSms接口类
		sendSms sendSmsExam = new sendSms();
		try {
			sendSmsExam.init();
			// 添加必要参数，详见各接口API文档 
			// 22487930570003   38949020
			sendSmsExam.setAccount("38949020");// 05723333333
			sendSmsExam.setDestinationAddresses("18621807761");
			sendSmsExam.setMessage("test123");
			// sendSmsExam.setTimer(t);
			// sendSmsExam.setReceiptRequest(simpleRef);
			// 调用invokeClient方法发起调用请求
			String response = sendSmsExam.invokeClient(true);
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
