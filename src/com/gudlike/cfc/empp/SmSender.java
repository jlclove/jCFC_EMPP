package com.gudlike.cfc.empp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Properties;

import org.apache.log4j.Logger;

import jcx.db.talk;

import com.wondertek.esmp.esms.empp.EMPPConnectResp;
import com.wondertek.esmp.esms.empp.EmppApi;
import com.wondertek.esmp.esms.empp.exception.EMPPObjectException;
import com.wondertek.esmp.esms.empp.exception.HeaderIncompleteException;
import com.wondertek.esmp.esms.empp.exception.InvalidEMPPObjectException;
import com.wondertek.esmp.esms.empp.exception.MessageIncompleteException;
import com.wondertek.esmp.esms.empp.exception.NotEnoughDataInByteBufferException;
import com.wondertek.esmp.esms.empp.exception.UnknownCommandIdException;
import com.wondertek.esmp.esms.empp.exception.ValueNotSetException;

/**
 * @author jail
 *
 * @date 2014年11月26日
 */

public class SmSender {
	public static final String profilepath = "EMPP.properties";
	private static String empphost = "211.136.163.68";
	private static int emppport = 9981;
	private static String emppaccountId_deyou = "10657109093918";
	private static String empppassword_deyou = "Caifucity88";
	private final String sqlSelect = "select top 50 S.id, S.Msg_Content, S.Dest_Terminal_ID from Empp20_submit S ";

	private final SimpleDateFormat sd = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	EmppApi emppApi_deyou = null;
	RecvListener listener_deyou = null;

	talk dbconn = DbUtil.getTalk("dooiooERP");

	public Logger logger = Logger.getLogger(getClass().getName());
	public HashSet<String> liantongs;

	public SmSender() {
		Properties emppCfgs = new Properties();
		try {
			emppCfgs.load(new FileInputStream("EMPP.properties"));
			empphost = emppCfgs.getProperty("host").trim();
			emppport = Integer.parseInt(emppCfgs.getProperty("port").trim());
			empppassword_deyou = emppCfgs.getProperty("password").trim();
			emppaccountId_deyou = emppCfgs.getProperty("account").trim();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			System.exit(-1);
		}
		initialize();
	}

	public HashSet<String> loadHaoduan(String filename) {
		HashSet results = new HashSet();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String s;
			while ((s = in.readLine()) != null) {
			     s = s.trim();
				results.add(s);
				System.out.println(s);
			}
			in.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		return results;
	}

	public void removeFalseRecord(String id) {
		String sqlError = "insert into FinancialDB.dbo.empp20_error ( [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id]) select [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id] from empp20_submit where id="
				+ id + ";";
		String sqlDelSubmit = "delete from Empp20_Submit where id=" + id + ";";
		this.logger.info("发送失败,id:" + id);
		try {
			this.dbconn.execFromPool(new String[] { sqlError, sqlDelSubmit });
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeSussRecord(String id) {
		String sqlSuss = "insert into FinancialDB.dbo.EMPP20_SUBMIT_HISTORY ( [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id]) select [ID],[MT_Date],[Dest_terminal_Id],[Msg_Content],[Service_Id] from empp20_submit where id="
				+ id + ";";
		String sqlDelSubmit = "delete from Empp20_Submit where id=" + id + ";";
		this.logger.info("发送成功,id:" + id);
		try {
			this.dbconn.execFromPool(new String[] { sqlSuss, sqlDelSubmit });
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initialize() {
		this.emppApi_deyou = null;
		this.listener_deyou = null;
		this.emppApi_deyou = new EmppApi();
		this.listener_deyou = new RecvListener(this.emppApi_deyou);
		connect(this.emppApi_deyou, this.listener_deyou, emppaccountId_deyou,
				empppassword_deyou);
	}

	public void connect(EmppApi empp, RecvListener listener,
			String emppaccountId, String pwd) {
		try {
			EMPPConnectResp response = empp.connect(empphost, emppport,
					emppaccountId, pwd, this.listener_deyou);
			System.out.println(response);
			if (response == null) {
				return;
			}
			if (!empp.isConnected())
				;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public int[] send(EmppApi empp, String content, String[] mobiles, String id)
			throws EMPPObjectException {
		int[] res = (int[]) null;
		try {
			res = empp.submitMsgAsync(content, mobiles, "");
		} catch (MessageIncompleteException e) {
			e.printStackTrace();
		} catch (UnknownCommandIdException e) {
			e.printStackTrace();
		} catch (InvalidEMPPObjectException e) {
			e.printStackTrace();
		} catch (ValueNotSetException e) {
			e.printStackTrace();
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
		} catch (HeaderIncompleteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			initialize();
		}
		return res;
	}

	public void sendSM() {
		String[][] rets = (String[][]) null;
		try {
			while (this.emppApi_deyou.isSubmitable()) {
				rets = this.dbconn
						.queryFromPool("select top 50 S.id, S.Msg_Content, S.Dest_Terminal_ID from Empp20_submit S ");
				System.out.println(this.sd.format(Calendar.getInstance()
						.getTime()) + "  ===>db read...");
				for (int i = 0; i < rets.length; i++) {
					String id = rets[i][0].trim();
					String content = rets[i][1].trim();
					String mobile = rets[i][2].trim();
					if ((!content.isEmpty()) && (mobile.length() >= 11)) {
						try {
							send(this.emppApi_deyou, content,
									new String[] { mobile }, id);
							System.out.println(this.sd.format(Calendar
									.getInstance().getTime())
									+ " ===>send suss...id:" + id);
							removeSussRecord(id);
						} catch (EMPPObjectException ee) {
							this.logger.info(ee);
							System.out.println(this.sd.format(Calendar
									.getInstance().getTime())
									+ " ===>send failed...id:" + id);
							removeFalseRecord(id);
						} catch (Exception e) {
							e.printStackTrace();
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

	public void reConnect(EmppApi empp, RecvListener listener) {
		listener.clear();
		try {
			empp.reConnect(listener);
		} catch (MessageIncompleteException e) {
			e.printStackTrace();
		} catch (UnknownCommandIdException e) {
			e.printStackTrace();
		} catch (InvalidEMPPObjectException e) {
			e.printStackTrace();
		} catch (ValueNotSetException e) {
			e.printStackTrace();
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
		} catch (HeaderIncompleteException e) {
			e.printStackTrace();
		} catch (EMPPObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SmSender sender = new SmSender();
		while (sender.emppApi_deyou.isSubmitable()) {
			sender.sendSM();
			sender.reConnect(sender.emppApi_deyou, sender.listener_deyou);
			sender.initialize();
		}
	}
}