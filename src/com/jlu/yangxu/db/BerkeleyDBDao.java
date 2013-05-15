/**
 *@author 杨旭，创建日期:2013-5-5
 *
*/
package com.jlu.yangxu.db;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.jlu.yangxu.util.ConfigUtil;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class BerkeleyDBDao {
	private String encoding = "UTF-8";
	
	public BerkeleyDBDao(){
		
	}
	public BerkeleyDBDao(String encoding){
		this.encoding = encoding;
	}
	
	private Environment getEnvironment() throws DatabaseException{
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(false);
		Environment env = new Environment(new File(ConfigUtil.getProperty("BDB_EVN_PATH")),envConfig);
		return env;
	}
	
	private Database getDatabase(Environment env,String databaseName) throws DatabaseException{
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTransactional(false);
		//打开一个名字问databaseName的数据库，数据库的配置为dbConfig
		Database db = env.openDatabase(null, databaseName, dbConfig);
		return db;
	}
	
	private void closeEnvironment(Environment env) throws DatabaseException{
		if(env != null){
			env.cleanLog();
			env.close();
		}
		env = null;
	}
	
	private void closeDatabase(Database db) throws DatabaseException{
		if (db != null){
			db.close();
		}
		db = null;
	}
	/**
	 * save 一个待处理的url
	 * @param databaseName
	 * @param key
	 * @param data
	 * @throws DatabaseException
	 * @throws UnsupportedEncodingException
	 */
	public void saveUrl(String databaseName,String key,String data) throws DatabaseException, UnsupportedEncodingException{
		Environment env = getEnvironment();
		Database db = getDatabase(env,databaseName);
		
		DatabaseEntry theKey = new DatabaseEntry(key.getBytes(encoding));
		DatabaseEntry theData = new DatabaseEntry(data.getBytes(encoding));
		try{
			db.put(null,theKey, theData);
		}finally{
			closeDatabase(db);
			closeEnvironment(env);
		}
	}
	
	/**
	 * 根据key获取value
	 * @param databaseName
	 * @param key
	 * @return
	 * @throws DatabaseException
	 */
	public String getData(String databaseName,String key) throws DatabaseException {
		Environment env = null;
		Database db = null;
		try {
			env= getEnvironment();
			db = getDatabase(env,databaseName);
			
			DatabaseEntry theKey = new DatabaseEntry(key.getBytes(encoding));
			DatabaseEntry theData = new DatabaseEntry();
			
			if(db.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS){
				return new String(theData.getData(),encoding);
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}finally{
			closeDatabase(db);
			closeEnvironment(env);
		}
		return null;
	}
	/***
	 * 
	 * @param databaseName
	 * @return
	 * @throws DatabaseException
	 * @throws UnsupportedEncodingException
	 */
	public String[] getNextUrl(String databaseName) throws DatabaseException, UnsupportedEncodingException{
		Environment env = getEnvironment();
		Database db = getDatabase(env,databaseName);
		Cursor cursor = null;
		try{
			cursor = db.openCursor(null, null);
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();
			if (cursor.getLast(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS){
				String[] result = new String[2];
				result[0] = new String(foundKey.getData(),encoding);
				result[1] = new String(foundData.getData(),encoding);
				return result;
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
			closeDatabase(db);
			closeEnvironment(env);
		}
		return null;
	}
	
	/**
	 * 判断<code>key</code>所对应的记录是否存在。
	 * 
	 * @param databaseName
	 * @param key
	 * @param data
	 * @return 如果存在返回true，否则返回false
	 * @throws DatabaseException
	 * @throws UnsupportedEncodingException
	 */
	public boolean isRecordExist(String databaseName, String key, String data) throws DatabaseException,UnsupportedEncodingException {
		Environment env = getEnvironment();
		Database db = getDatabase(env, databaseName);
		
		try {
			DatabaseEntry theKey = new DatabaseEntry(key.getBytes(encoding));
			DatabaseEntry theData = new DatabaseEntry(data.getBytes(encoding));
			// db.getSearchBoth(null, theKey, theData, LockMode.DEFAULT); 获取同时满足key和data的记录。
			if (db.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				return true;
			}
		} finally {
			closeDatabase(db);
			closeEnvironment(env);
		}				
		return false;
	}
	
	/**
	 * 根据<code>key</code>删除记录。
	 * 
	 * @param databaseName
	 * @param key
	 * @throws DatabaseException
	 * @throws UnsupportedEncodingException
	 */
	public void delete(String databaseName, String key) throws DatabaseException,UnsupportedEncodingException {
		Environment env = getEnvironment();
		Database db = getDatabase(env, databaseName);
		
		try {
			DatabaseEntry theKey = new DatabaseEntry(key.getBytes(encoding));
			db.delete(null, theKey);
		} finally {
			closeDatabase(db);
        	closeEnvironment(env);
		}
	}
	
	/**
	 * 删除数据库<code>databaseName</code>中的所有记录。
	 * 
	 * @param databaseName
	 * @return 删除的记录数目
	 * @throws DatabaseException
	 */
	public long truncateDatabase(String databaseName) throws DatabaseException {
		Environment env = getEnvironment();
		
		try {
			return env.truncateDatabase(null, databaseName, true);
		} finally {
        	closeEnvironment(env);
		}
	}
	
	/**
	 * 删除数据库<code>databaseName</code>。
	 * 
	 * @param databaseName
	 * @throws DatabaseException
	 */
	public void removeDatabase(String databaseName) throws DatabaseException {
		Environment env = getEnvironment();
		
		try {
			env.removeDatabase(null, databaseName);
		} finally {
        	closeEnvironment(env);
		}
	}
	
	/**
	 * 清除当前数据库环境中的日志信息，释放空间。
	 * 
	 * @throws DatabaseException
	 */
	public void clearLog() throws DatabaseException {
		Environment env = getEnvironment();
		
		try {
			env.cleanLog();
		} finally {
        	closeEnvironment(env);
		}
	}
	public static void main(String[] args) {
		

	}

}
