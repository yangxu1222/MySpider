/**
 *@author ���񣬴�������:2013-5-10
 *
*/
package com.jlu.yangxu.page;

import static net.mindview.util.Print.print;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.jlu.yangxu.util.ConfigUtil;
import com.jlu.yangxu.util.Funcs;

public class PictureDownloader {
	public static Logger logger = Funcs.getLogger();
	private String inUrl;

	private String fileName;

	private String encoding;

	private String content = null;
	private static long count = 0;
	
	public PictureDownloader(String url) {
		this.inUrl = url;
	}

	/**
	 * ���� url ����ҳ����������Ҫ�������ҳ���ļ��� ȥ���� url�з��ļ����ַ�
	 * 
	 * @param url
	 * @param contentType
	 *            ��ҳ����
	 * @return
	 */
	public String getFileNameByUrl(String url, String contentType) {
		url = url.substring(7);// remove http://
		if (contentType.indexOf("html") != -1) {// text/html
			//url = url.replaceAll("[\\?/:*|<>\"]", "_")+".html" ;
			url = url.replaceAll("[\\?/:*|<>\"]", "_")+".html" ;
			return url;
		} else {// ��application/pdf ����html�ļ�
			return url.replaceAll("[\\?/:*|<>\"]", "_")
					+ "."
					+ contentType.substring(contentType.lastIndexOf("/") + 1,
							contentType.indexOf(";"));
		}
	}

	/**
	 * ���� url ָ�����ҳ
	 * 
	 * @return
	 */
	public void downloadFile(String saveName) {
		logger.info("download picture" + saveName);
		try {
			URL url = new URL(inUrl);
			Image src = ImageIO.read(url);
			int width = src.getWidth(null); // �õ�Դͼ��  
            int height = src.getHeight(null); // �õ�Դͼ��  
            BufferedImage tag = new BufferedImage(width, height,  
                    BufferedImage.TYPE_INT_RGB);  
            tag.getGraphics().drawImage(src, 0, 0, width, height, null); // ������С���ͼ  
            //�����ļ���  
            String filepath = ConfigUtil.getProperty("CACHE_PICTURE_PATH")+saveName;  
            FileOutputStream out = new FileOutputStream(filepath);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            encoder.encode(tag); // ��JPEG����  
            // System.out.print(width+ "* "+height);  
            out.close();  
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public String getUrl() {
		return inUrl;
	}

	public void setUrl(String url) {
		this.inUrl = url;
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
		String inUrl = "http://s.cimg.163.com/i/img3.cache.netease.com%2Fphoto%2F0011%2F2013-03-15%2F8Q0G564L53DA0011.jpg.100x70.auto.jpg";
		/*
		PictureDownloader pic = new PictureDownloader(url);
		pic.downloadFile("f:");
		*/
		
		
		
		
	}
}
