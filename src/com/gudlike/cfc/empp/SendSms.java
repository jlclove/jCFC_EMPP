package com.gudlike.cfc.empp;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jcx.db.talk;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.wondertek.esmp.esms.empp.exception.EMPPObjectException;

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
	
	private static final String sqlSelect = "select top 50 S.id, S.Msg_Content, S.Dest_Terminal_ID from Empp20_submit S ";
	static talk dbconn = DbUtil.getTalk("dooiooERP");
	private static final SimpleDateFormat sd = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static Logger logger = Logger.getLogger(SendSms.class);
	
	public  static void main(String[] args){
		Constants.extendUrl = "http://open.ecplive.cn:8080/EcpOpen/services/ExtendService/extendService";
		Constants.spid = "22487930570003";
		Constants.serviceKey = "bec67614807546f48846f976a5ecf681";
		Constants.appid = "01156021";
		Constants.appkey = "a9c35c476cc94182a46e4d49e6e54da1";
		Constants.type = "0";
//		SimpleReference simpleRef = new SimpleReference();
//		simpleRef.setEndpoint("http://127.0.0.1:8080/");
//		simpleRef.setInterfaceName("notifySmsDeliveryStatus");
//		simpleRef.setCorrelator("");
		// 实例化sendSms接口类
		sendSms sendSmsExam = new sendSms();
		try {
			sendSmsExam.init();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		sendSmsExam.setAccount("02138949020");// 05723333333
		
		String[][] rets = (String[][]) null;
		try {
			while (true) {
				rets = dbconn
						.queryFromPool("select top 50 S.id, S.Msg_Content, S.Dest_Terminal_ID from Empp20_submit S ");
				System.out.println(sd.format(Calendar.getInstance()
						.getTime()) + "  ===>db read...");
				for (int i = 0; i < rets.length; i++) {
					String id = rets[i][0].trim();
					String content = rets[i][1].trim();
					String mobile = rets[i][2].trim();
					if ((!content.isEmpty()) && (mobile.length() >= 11)) {
						try {
							
							// 添加必要参数，详见各接口API文档 
							// 22487930570003   38949020
							sendSmsExam.setDestinationAddresses(mobile);
							sendSmsExam.setMessage(content);
							// sendSmsExam.setTimer(t);4
							// sendSmsExam.setReceiptRequest(simpleRef);
							// 调用invokeClient方法发起调用请求
							String response = sendSmsExam.invokeClient(true);
							System.out.println(sd.format(Calendar
									.getInstance().getTime())
									+ " ===>send suss...id:" + id);
							removeSussRecord(id);
						} catch (Exception e) {
							logger.info(e);
							System.out.println(sd.format(Calendar
									.getInstance().getTime())
									+ " ===>send failed...id:" + id);
							removeFalseRecord(id);
						}
						
					} else {
						removeFalseRecord(id);
					}
					Thread.sleep(10L);
				}
				Thread.sleep(2000L);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeSussRecord(String id) {
		String sqlSuss = "insert into FinancialDB.dbo.EMPP20_SUBMIT_HISTORY ( [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id]) select [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id] from empp20_submit where id="
				+ id + ";";
		String sqlDelSubmit = "delete from Empp20_Submit where id=" + id + ";";
		logger.info("发送成功,id:" + id);
		try {
			dbconn.execFromPool(new String[] { sqlSuss, sqlDelSubmit });
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeFalseRecord(String id) {
		String sqlError = "insert into FinancialDB.dbo.empp20_error ( [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id]) select [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id] from empp20_submit where id="
				+ id + ";";
		String sqlDelSubmit = "delete from Empp20_Submit where id=" + id + ";";
		logger.info("发送失败,id:" + id);
		try {
			dbconn.execFromPool(new String[] { sqlError, sqlDelSubmit });
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	public static void main(String[] args) {
		// 设置Constants中的常量值，web工程中应保证这些值能持久化
		Constants.extendUrl = "http://open.ecplive.cn:8080/EcpOpen/services/ExtendService/extendService";
		Constants.spid = "22487930570003";
		Constants.serviceKey = "bec67614807546f48846f976a5ecf681";
		Constants.appid = "01156021";
		Constants.appkey = "a9c35c476cc94182a46e4d49e6e54da1";
		Constants.type = "0";
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
			sendSmsExam.setAccount("02138949020");// 05723333333
			sendSmsExam.setDestinationAddresses("18621807761");
			sendSmsExam.setMessage("test123");
			// sendSmsExam.setTimer(t);4
			// sendSmsExam.setReceiptRequest(simpleRef);
			// 调用invokeClient方法发起调用请求
			String response = sendSmsExam.invokeClient(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
