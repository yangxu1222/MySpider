/**
 *@author ���񣬴�������:2013-5-7
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
	 * ���� url ����ҳ����������Ҫ�������ҳ���ļ��� ȥ���� url�з��ļ����ַ�
	 * 
	 * @param url
	 * @param contentType
	 *            ��ҳ����
	 * @return
	 */
	public String getFileNameByUrl(String url) {
		url = url.substring(30);// remove http://
			//url = url.replaceAll("[\\?/:*|<>\"]", "_")+".html" ;
		url = url.replaceAll("/", "_");
		return url;
	}

	/**
	 * ���� url ָ�����ҳ
	 * 
	 * @return
	 */
	public String downloadFile(String dir) {
		logger.info("downloading..." + url);
		//print("downloading..." + url);
		String filePath = null;
		InputStream responseBody;
		
		// ����http header��Ϣ
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Accept",
				"text/html, application/xhtml+xml, */*"));
		headers.add(new Header("User-Agent",
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"));
		headers.add(new Header("Connection", "Keep-Alive"));

		// ����Cookie�����cookie reject����
		DefaultHttpParams.getDefaultParams().setParameter(
				"http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		// ����һ��httpClient����
		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().getParams()
				.setParameter("http.default-headers", headers);
		// ���� Http ���ӳ�ʱ 20s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(20000);
		/* 2.���� GetMethod �������ò��� */

		try {
			url = URLUtil.enCodeZh(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetMethod getMethod = new GetMethod(url);
		// ���� get ����ʱ 20s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
		// �����������Դ���
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		/* 3.ִ�� HTTP GET ���� */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// �жϷ��ʵ�״̬��
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
				filePath = null;
			}
			//ֻ����html�ļ� ����html�ļ� ��ֱ�ӷ���
			if (getMethod.getResponseHeader("Content-Type").getValue().indexOf("html") == -1){
				this.setContent(content);
				return null;
				
			}
			/* 4.���� HTTP ��Ӧ���� */
			responseBody = getMethod.getResponseBodyAsStream();// ��ȡΪ�ֽ�����
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
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			System.out.println("����" + url + "ʧ�ܣ�");
			e.printStackTrace();
		} catch (IOException e) {
			// ���������쳣
			e.printStackTrace();
		} finally {
			// �ͷ�����
			getMethod.releaseConnection();
		}
		return filePath;
	}
	
	/**
	 * ���� url ָ�����ҳ
	 * 
	 * @return
	 */
	public String downloadFile(String fileName,String dir) {
		logger.info("downloading..." + url);
		String filePath = null;
		InputStream responseBody;
		
		// ����http header��Ϣ
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Accept",
				"text/html, application/xhtml+xml, */*"));
		headers.add(new Header("User-Agent",
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"));
		headers.add(new Header("Connection", "Keep-Alive"));

		// ����Cookie�����cookie reject����
		DefaultHttpParams.getDefaultParams().setParameter(
				"http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		// ����һ��httpClient����
		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().getParams()
				.setParameter("http.default-headers", headers);
		// ���� Http ���ӳ�ʱ 20s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(20000);
		/* 2.���� GetMethod �������ò��� */

		try {
			url = URLUtil.enCodeZh(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetMethod getMethod = new GetMethod(url);
		// ���� get ����ʱ 20s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
		// �����������Դ���
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		/* 3.ִ�� HTTP GET ���� */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// �жϷ��ʵ�״̬��
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
				filePath = null;
			}
			//ֻ����html�ļ� ����html�ļ� ��ֱ�ӷ���
			if (getMethod.getResponseHeader("Content-Type").getValue().indexOf("html") == -1){
				this.setContent(content);
				return null;
				
			}
			/* 4.���� HTTP ��Ӧ���� */
			responseBody = getMethod.getResponseBodyAsStream();// ��ȡΪ�ֽ�����
			if(fileName=="detail"){
				filePath = dir + fileName + detailCount;
				detailCount++;
			}
			if(fileName == "list"){
				filePath = dir + fileName + listCount;
				listCount++;
			}
			
			
			String charset = getMethod.getResponseCharSet();

			// saveToLocal(responseBody, filePath);//��ʱ��������ʱ�ļ�
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
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			System.out.println("����" + url + "ʧ�ܣ�");
			e.printStackTrace();
		} catch (IOException e) {
			// ���������쳣
			e.printStackTrace();
		} finally {
			// �ͷ�����
			getMethod.releaseConnection();
		}
		return filePath;
	}
	

	/*
	 * /** ����ַ���
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
	 * param.getValue(); } else {// httpͷ��Ϣ����charset // <meta
	 * http-equiv=Content-Type // content="text/html;charset=gb2312"> // <meta
	 * http-equiv="content-type" content="text/html; // charset=UTF-8" /> String
	 * htmlContent = new String(responseBody); Pattern pt =
	 * Pattern.compile("<meta(.*?)charset=(.*?)\""); Matcher mc =
	 * pt.matcher(htmlContent); if (mc.find()) { charset = mc.group(2).trim(); }
	 * else {// ������<?xml version="1.0" encoding="gb2312"?>��ʽ�� pt =
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
