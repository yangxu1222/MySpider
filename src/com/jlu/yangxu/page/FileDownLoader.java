/**
 *@author 杨旭，创建日期:2013-5-7
 *
 */
package com.jlu.yangxu.page;

import static net.mindview.util.Print.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;

import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.jlu.yangxu.util.ConfigUtil;
import com.jlu.yangxu.util.Funcs;
import com.jlu.yangxu.util.URLUtil;


public class FileDownLoader {
	
	public static Logger logger = Funcs.getLogger();
	private String url;

	private String fileName;

	private String encoding;

	private String content = null;
	private static long detailCount = 0;
	private static long listCount = 0;
	private static long count = 0;
	public FileDownLoader(String url) {
		this.url = url;
	}

	/**
	 * 根据 url 和网页类型生成需要保存的网页的文件名 去除掉 url中非文件名字符
	 * 
	 * @param url
	 * @param contentType
	 *            网页类型
	 * @return
	 */
	public String getFileNameByUrl(String url) {
		url = url.substring(30);// remove http://
			//url = url.replaceAll("[\\?/:*|<>\"]", "_")+".html" ;
		url = url.replaceAll("/", "_");
		return url;
	}

	/**
	 * 下载 url 指向的网页
	 * 
	 * @return
	 */
	public String downloadFile(String dir) {
		logger.info("downloading..." + url);
		//print("downloading..." + url);
		String filePath = null;
		InputStream responseBody;
		
		// 设置http header信息
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Accept",
				"text/html, application/xhtml+xml, */*"));
		headers.add(new Header("User-Agent",
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"));
		headers.add(new Header("Connection", "Keep-Alive"));

		// 设置Cookie，解决cookie reject问题
		DefaultHttpParams.getDefaultParams().setParameter(
				"http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		// 建立一个httpClient对象
		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().getParams()
				.setParameter("http.default-headers", headers);
		// 设置 Http 连接超时 20s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(20000);
		/* 2.生成 GetMethod 对象并设置参数 */

		try {
			url = URLUtil.enCodeZh(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetMethod getMethod = new GetMethod(url);
		// 设置 get 请求超时 20s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
		// 设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		/* 3.执行 HTTP GET 请求 */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// 判断访问的状态码
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
				filePath = null;
			}
			//只下载html文件 不是html文件 则直接返回
			if (getMethod.getResponseHeader("Content-Type").getValue().indexOf("html") == -1){
				this.setContent(content);
				return null;
				
			}
			/* 4.处理 HTTP 响应内容 */
			responseBody = getMethod.getResponseBodyAsStream();// 读取为字节数组
			String fileName = String.valueOf(count++);
			filePath = dir + fileName;
			String charset = getMethod.getResponseCharSet();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					responseBody, charset));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(new File(filePath)), charset);
			StringBuilder sb = new StringBuilder();//
			
			String line;
			while ((line = reader.readLine()) != null) {
				writer.append(line + "\n");
				sb.append(line + "\n");
			}
			content = sb.toString();
			writer.flush();
			writer.close();
			reader.close();
			this.setFileName(fileName);
			// this.setEncoding(getCharset(responseBody, getMethod));
			this.setEncoding(charset);
			this.setContent(content);
			
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("访问" + url + "失败！");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return filePath;
	}
	
	/**
	 * 下载 url 指向的网页
	 * 
	 * @return
	 */
	public String downloadFile(String fileName,String dir) {
		logger.info("downloading..." + url);
		String filePath = null;
		InputStream responseBody;
		
		// 设置http header信息
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Accept",
				"text/html, application/xhtml+xml, */*"));
		headers.add(new Header("User-Agent",
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"));
		headers.add(new Header("Connection", "Keep-Alive"));

		// 设置Cookie，解决cookie reject问题
		DefaultHttpParams.getDefaultParams().setParameter(
				"http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		// 建立一个httpClient对象
		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().getParams()
				.setParameter("http.default-headers", headers);
		// 设置 Http 连接超时 20s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(20000);
		/* 2.生成 GetMethod 对象并设置参数 */

		try {
			url = URLUtil.enCodeZh(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetMethod getMethod = new GetMethod(url);
		// 设置 get 请求超时 20s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
		// 设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		/* 3.执行 HTTP GET 请求 */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// 判断访问的状态码
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
				filePath = null;
			}
			//只下载html文件 不是html文件 则直接返回
			if (getMethod.getResponseHeader("Content-Type").getValue().indexOf("html") == -1){
				this.setContent(content);
				return null;
				
			}
			/* 4.处理 HTTP 响应内容 */
			responseBody = getMethod.getResponseBodyAsStream();// 读取为字节数组
			if(fileName=="detail"){
				filePath = dir + fileName + detailCount;
				detailCount++;
			}
			if(fileName == "list"){
				filePath = dir + fileName + listCount;
				listCount++;
			}
			
			
			String charset = getMethod.getResponseCharSet();

			// saveToLocal(responseBody, filePath);//暂时不保存临时文件
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					responseBody, charset));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(new File(filePath)), charset);
			StringBuilder sb = new StringBuilder();//
			
			String line;
			while ((line = reader.readLine()) != null) {
				writer.append(line + "\n");
				sb.append(line + "\n");
			}
			content = sb.toString();
			writer.flush();
			writer.close();
			reader.close();
			this.setFileName(fileName);
			// this.setEncoding(getCharset(responseBody, getMethod));
			this.setEncoding(charset);
			this.setContent(content);
			
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("访问" + url + "失败！");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return filePath;
	}
	

	/*
	 * /** 获得字符集
	 * 
	 * @param responseBody
	 * 
	 * @param getMethod
	 * 
	 * @return
	 * 
	 * private String getCharset(InputStream responseBody, GetMethod getMethod)
	 * { HeaderElement values[] = getMethod.getResponseHeader("Content-Type")
	 * .getElements(); String charset = getMethod.getResponseCharSet(); if
	 * (values.length == 1) { NameValuePair param =
	 * values[0].getParameterByName("charset"); if (param != null) { charset =
	 * param.getValue(); } else {// http头信息中无charset // <meta
	 * http-equiv=Content-Type // content="text/html;charset=gb2312"> // <meta
	 * http-equiv="content-type" content="text/html; // charset=UTF-8" /> String
	 * htmlContent = new String(responseBody); Pattern pt =
	 * Pattern.compile("<meta(.*?)charset=(.*?)\""); Matcher mc =
	 * pt.matcher(htmlContent); if (mc.find()) { charset = mc.group(2).trim(); }
	 * else {// 否则是<?xml version="1.0" encoding="gb2312"?>格式的 pt =
	 * Pattern.compile("<?xml(.*?)encoding=\"(.*?)\""); mc =
	 * pt.matcher(htmlContent); if (mc.find()) { charset = mc.group(2).trim(); }
	 * }
	 * 
	 * } }// end if return charset; }
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public static void main(String[] args){
		String url = "http://product.mobile.163.com/Samsung/9.html#result";
		String url2 = "http://product.mobile.163.com/Samsung/000BCcIW/param.html#8B2";
		String url3 = "http://product.mobile.163.com/acer/#7BA";
		FileDownLoader loader = new FileDownLoader(url2);
		print(loader.getFileNameByUrl(url2));

	}
}
