/**
 *@author ���񣬴�������:2013-5-6
 *
*/
package com.jlu.yangxu.newsspider.page;

import java.io.UnsupportedEncodingException;

import com.jlu.yangxu.newsspider.db.BerkeleyDBDao;
import com.sleepycat.je.DatabaseException;

public class PageLinkCollector {
	private BerkeleyDBDao dao = new BerkeleyDBDao();
	private String toDealDBName;
	private String dealedDBName;
	
	public PageLinkCollector(String identity){
		this.toDealDBName = identity + "ToDealLinkList.db";
		this.dealedDBName = identity + "DealedDBName.db";
		
	}
	
	public PageLinkCollector(BerkeleyDBDao dao){
		this.dao  = dao;
	}
	

	/**
	 * ����һ���������������Ϣ��
	 * 
	 * @param key
	 * @param data
	 */
	public synchronized void add(String key, String data) {
		try {
			if (!dao.isRecordExist(toDealDBName, key, data) && 
					!dao.isRecordExist(dealedDBName, key, data)) {
				dao.saveUrl(toDealDBName, key, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/***
	 * ����һ���������������Ϣ
	 * @param key
	 * @param data
	 */
	public synchronized void addToDealLink(String key,String data){
		try {
			//��������Ӳ��ڴ������������ݿ��У�ͬʱҲ�����Ѵ������ݿ��� ����뵽���������ݿ���
			if (!dao.isRecordExist(toDealDBName, key, data) && !dao.isRecordExist(dealedDBName,  key, data)){
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
	 * @param key
	 * @param data
	 */
	public synchronized void addDealedLink(String key,String data){
		try {
			if (!dao.isRecordExist(dealedDBName, key, data)){
				dao.saveUrl(dealedDBName, key, data);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * ��ȡ��һ���������������Ϣ
	 * @return [url,depth] ����
	 */
	public synchronized String[] getNextToDeal(){
		try {
			String[] result = dao.getNextUrl(toDealDBName);
			if (result != null){
				dao.delete(toDealDBName, result[0]);
				dao.saveUrl(dealedDBName, result[0], result[1]);
			}
			return result;
		} catch (UnsupportedEncodingException | DatabaseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized void clear(){
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
