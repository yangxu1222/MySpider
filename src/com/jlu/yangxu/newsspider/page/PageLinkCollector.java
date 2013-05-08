/**
 *@author 杨旭，创建日期:2013-5-6
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
	 * 保存一个待处理的链接信息。
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
	 * 保存一个待处理的连接信息
	 * @param key
	 * @param data
	 */
	public synchronized void addToDealLink(String key,String data){
		try {
			//如果此连接不在待处理连接数据库中，同时也不在已处理数据库中 则加入到待处理数据库中
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
	 * 保存一个处理过的连接信息
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
	 * 获取下一个待处理的连接信息
	 * @return [url,depth] 数组
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
