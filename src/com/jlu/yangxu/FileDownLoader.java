/**
 *@author ���񣬴�������:2013-5-5
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
	 * ���� url ����ҳ����������Ҫ�������ҳ���ļ��� ȥ���� url �з��ļ����ַ�
	 */
	private static String getFileNameByUrl(String url, String contentType) {
		url = url.substring(7);// remove http://
		if (contentType.indexOf("html") != -1)// text/html
		{
			url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
			return url;
		} else// ��application/pdf
		{
			return url.replaceAll("[\\?/:*|<>\"]", "_") + "."
					+ contentType.substring(contentType.lastIndexOf("/") + 1);
		}
	}

	/**
	 * ������ҳ�ֽ����鵽�����ļ� filePath ΪҪ������ļ�����Ե�ַ
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

	/* ���� url ָ�����ҳ */
	public static String downloadFile(String url) {
		logger.log(Level.INFO, "downloadFile..."+url);
		String filePath = null;
		/* 1.���� HttpClinet �������ò��� */
		HttpClient httpClient = new HttpClient();
		// ���� Http ���ӳ�ʱ 5s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		/* 2.���� GetMethod �������ò��� */
		GetMethod getMethod = new GetMethod(url);
		// ���� get ����ʱ 5s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		// �����������Դ���
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.setFollowRedirects(false);

		/* 3.ִ�� HTTP GET ���� */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// �жϷ��ʵ�״̬��
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
				filePath = null;
			}

			/* 4.���� HTTP ��Ӧ���� */
			// byte[] responseBody = getMethod.getResponseBody();//��ȡΪ�ֽ�����
			InputStream responseBody = getMethod.getResponseBodyAsStream();
			// ������ҳ url ���ɱ���ʱ���ļ���
			filePath = "F:\\temp\\"
					+ getFileNameByUrl(url,
							getMethod.getResponseHeader("Content-Type")
									.getValue());
			saveToLocal(responseBody, filePath);
		} catch (HttpException e) {
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			System.out.println("Please check your provided httpaddress!");
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

	// ���Ե� main ����
	public static void main(String[] args) {
		FileDownLoader.downloadFile("http://www.jlu.edu.cn");
	}
}
