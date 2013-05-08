/**
 *@author 杨旭，创建日期:2013-5-5
 *
 */
package com.jlu.yangxu;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class FileDownLoader {
	private static Logger logger = Logger.getLogger(FileDownLoader.class.getName());

	/**
	 * 根据 url 和网页类型生成需要保存的网页的文件名 去除掉 url 中非文件名字符
	 */
	private static String getFileNameByUrl(String url, String contentType) {
		url = url.substring(7);// remove http://
		if (contentType.indexOf("html") != -1)// text/html
		{
			url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
			return url;
		} else// 如application/pdf
		{
			return url.replaceAll("[\\?/:*|<>\"]", "_") + "."
					+ contentType.substring(contentType.lastIndexOf("/") + 1);
		}
	}

	/**
	 * 保存网页字节数组到本地文件 filePath 为要保存的文件的相对地址
	 */
	private static void saveToLocal(InputStream responseBody, String filePath) {
		try {
			// DataOutputStream out = new DataOutputStream(new FileOutputStream(
			// new File(filePath)));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					responseBody));
			FileWriter writer = new FileWriter(new File(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				writer.append(line + "\n");
			}
			writer.flush();
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 下载 url 指向的网页 */
	public static String downloadFile(String url) {
		logger.log(Level.INFO, "downloadFile..."+url);
		String filePath = null;
		/* 1.生成 HttpClinet 对象并设置参数 */
		HttpClient httpClient = new HttpClient();
		// 设置 Http 连接超时 5s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		/* 2.生成 GetMethod 对象并设置参数 */
		GetMethod getMethod = new GetMethod(url);
		// 设置 get 请求超时 5s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		// 设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setFollowRedirects(false);

		/* 3.执行 HTTP GET 请求 */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// 判断访问的状态码
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
				filePath = null;
			}

			/* 4.处理 HTTP 响应内容 */
			// byte[] responseBody = getMethod.getResponseBody();//读取为字节数组
			InputStream responseBody = getMethod.getResponseBodyAsStream();
			// 根据网页 url 生成保存时的文件名
			filePath = "F:\\temp\\"
					+ getFileNameByUrl(url,
							getMethod.getResponseHeader("Content-Type")
									.getValue());
			saveToLocal(responseBody, filePath);
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided httpaddress!");
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

	// 测试的 main 方法
	public static void main(String[] args) {
		FileDownLoader.downloadFile("http://www.jlu.edu.cn");
	}
}
