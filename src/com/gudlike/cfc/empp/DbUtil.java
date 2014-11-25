package com.gudlike.cfc.empp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jcx.db.talk;

/**
 * @author jail
 *
 * @date 2014年11月26日
 */
public class DbUtil {
	public static synchronized talk getTalk(String db) {
		String profilepath = db + ".properties";
		String dbprotocal = "mssql";
		String dbhost = "";
		String dbuser = "";
		String dbpwd = "";
		String dbdatabase = "";

		Properties dbcfgs = new Properties();
		try {
			dbcfgs.load(new FileInputStream(profilepath));
			dbhost = dbcfgs.getProperty("host").trim();
			System.out.println(dbhost);
			dbuser = dbcfgs.getProperty("user").trim();
			dbpwd = dbcfgs.getProperty("password").trim();
			dbdatabase = dbcfgs.getProperty("database").trim();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			System.exit(-1);
		}
		talk dbconn = null;
		if (dbconn == null) {
			dbconn = new talk(dbprotocal, dbhost, dbuser, dbpwd, dbdatabase, 10);
			talk.debug = false;
		}

		return dbconn;
	}
}