/**
 *@author ���񣬴�������:2013-5-6
 *
 */
package com.jlu.yangxu.page;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.jlu.yangxu.db.BerkeleyDBDao;
import com.jlu.yangxu.util.Funcs;
import com.sleepycat.je.DatabaseException;

public class PageLinkCollector {
	public static Logger logger = Funcs.getLogger();
	private BerkeleyDBDao dao = new BerkeleyDBDao();
	private String toDealDBName;
	private String dealedDBName;

	public PageLinkCollector(String identity) {
		this.toDealDBName = identity + "ToDealLinkList.db";
		this.dealedDBName = identity + "DealedDBName.db";

	}

	public PageLinkCollector(BerkeleyDBDao dao) {
		this.dao = dao;
	}

	/**
	 * ����һ���������������Ϣ��
	 * 
	 * @param key
	 * @param data
	 */
	public synchronized void add(String key, String data) {

		key = "http://product.mobile.163.com" + key;
		logger.info("toDeal:" + key);
		// if (key.contains("#B21") || Pattern.matches("./\\d*?.html#result",
		// key)) {

		// if (key.contains("#B21")) {
		// key = "http://product.mobile.163.com" + key;
		// }
		// if (Pattern.matches("./\\d*?.html#result", key)) {
		// key = "http://product.mobile.163.com/Samsung"
		// + key.substring(1);
		// }
		try {
			if (!dao.isRecordExist(toDealDBName, key, data)
					&& !dao.isRecordExist(dealedDBName, key, data)) {
				dao.saveUrl(toDealDBName, key, data);
				//System.out.println("toDealDBName:" + key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
	}

	/***
	 * ����һ���������������Ϣ
	 * 
	 * @param key
	 * @param data
	 */
	public synchronized void addToDealLink(String key, String data) {
		key = "http://product.mobile.163.com" + key;
		logger.info("toDeal:" + key);
		try {
			
			// ��������Ӳ��ڴ������������ݿ��У�ͬʱҲ�����Ѵ������ݿ��� ����뵽���������ݿ���
			if (!dao.isRecordExist(toDealDBName, key, data)
					&& !dao.isRecordExist(dealedDBName, key, data)) {
				dao.saveUrl(toDealDBName, key, data);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/***
	 * ����һ���������������Ϣ
	 * 
	 * @param key
	 * @param data
	 */
	public synchronized void addDealedLink(String key, String data) {
		try {
			if (!dao.isRecordExist(dealedDBName, key, data)) {
				dao.saveUrl(dealedDBName, key, data);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		// System.out.println("save to dealed DB :" +key);
		logger.info("to dealed:"+key);
	}

	/***
	 * ��ȡ��һ���������������Ϣ
	 * 
	 * @return [url,depth] ����
	 */
	public synchronized String[] getNextToDeal() {
		try {
			String[] result = dao.getNextUrl(toDealDBName);
			if (result != null) {
				dao.delete(toDealDBName, result[0]);
				dao.saveUrl(dealedDBName, result[0], result[1]);
			}
			return result;
		} catch (UnsupportedEncodingException | DatabaseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void clear() {
		try {
			dao.removeDatabase(toDealDBName);
			dao.removeDatabase(dealedDBName);
			dao.clearLog();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
