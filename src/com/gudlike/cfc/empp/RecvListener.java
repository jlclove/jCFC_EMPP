package com.gudlike.cfc.empp;

import org.apache.log4j.Logger;

import com.wondertek.esmp.esms.empp.EMPPObject;
import com.wondertek.esmp.esms.empp.EMPPRecvListener;
import com.wondertek.esmp.esms.empp.EmppApi;

/**
 * @author jail
 *
 * @date 2014年11月26日
 */

public class RecvListener implements EMPPRecvListener {
	private static final long RECONNECT_TIME = 10000L;
	private EmppApi emppApi = null;

	Logger logger = Logger.getLogger(getClass().getName());

	protected RecvListener() {
	}

	public RecvListener(EmppApi empp) {
		this.emppApi = empp;
	}

	public void clear() {
	}

	public void OnClosed(Object arg0) {
	}

	public void OnError(Exception arg0) {
	}


	/* (non-Javadoc)
	 * @see com.wondertek.esmp.esms.empp.EMPPRecvListener#onMessage(com.wondertek.esmp.esms.empp.EMPPObject)
	 */
	@Override
	public void onMessage(EMPPObject arg0) {
		// TODO Auto-generated method stub
		
	}
}