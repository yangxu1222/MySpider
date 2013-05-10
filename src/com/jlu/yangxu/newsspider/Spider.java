/**
 *@author 杨旭，创建日期:2013-5-5
 *
 */
package com.jlu.yangxu.newsspider;

import java.util.ArrayList;
import java.util.List;

import com.jlu.yangxu.newsspider.page.PageIterator;
import com.jlu.yangxu.newsspider.page.PageLinkCollector;
import com.jlu.yangxu.newsspider.parser.BrandIndexPageParser;
import com.jlu.yangxu.newsspider.parser.DetailPageParser;
import com.jlu.yangxu.newsspider.parser.ListPageParser;
import com.jlu.yangxu.newsspider.parser.PageParser;
import com.jlu.yangxu.newsspider.parser.PicturePageParser;
import com.jlu.yangxu.newsspider.util.ConfigUtil;
import static net.mindview.util.Print.*;

public class Spider {
	public static final String webMarker = "163";
	public static final String webDomain = "product.mobile.163.com";
	public static final String startUrl = "http://product.mobile.163.com/brand/";
	public static ArrayList<String> startUrlList = new ArrayList<String>();
	public static final String listUrlPattern = "http://product.mobile.163.com/.*?/[\\d*?].html#result";
	public static final String detailUrlPattern = "http://product.mobile.163.com/.*?/.*?/#B\\d{2}?";
	public static final String brandIndexPattern = "http://product.mobile.163.com/.*?/#7BA";
	public static final String picturePattern = "http://product.mobile.163.com/.*?/.*?/img.html#8B3";

	public static final String dir = ConfigUtil.getProperty("CACHE_FILE_PATH");
	public static final Integer threadCount = Integer.valueOf(ConfigUtil
			.getProperty("THREAD_COUNT"));
	public static final Integer maxDepth = Integer.MAX_VALUE;

	public static void main(String[] args) throws InterruptedException {
		/*
		 * String webMarker = "csdn"; String webDomain = "blog.csdn.net"; String
		 * startUrl = "http://blog.csdn.net/index.html";
		 * 
		 * //String listUrlStart = "http://www.ca800.com/news/list.asp"; String
		 * listUrlPattern = "http://blog.csdn.net/.*?/.*?.html"; String
		 * detailUrlPattern = "http://blog.csdn.net/.*?/article/details/[\\d]*";
		 * String dir = ConfigUtil.getProperty("CSDN_CACHE_FILE_PATH") ;
		 */
		PageLinkCollector collector = new PageLinkCollector(webMarker);
		PageParser parser = new PageParser();
		parser.setCollector(collector);
		parser.setMaxDepth(maxDepth);
		parser.setWebDomain(webDomain);
		ListPageParser lparser = new ListPageParser();
		lparser.setCollector(collector);
		lparser.setMaxDepth(maxDepth);
		lparser.setWebDomain(webDomain);

		DetailPageParser dparser = new DetailPageParser();
		dparser.setCollector(collector);
		dparser.setMaxDepth(maxDepth);
		dparser.setWebDomain(webDomain);
		
		BrandIndexPageParser biparser = new BrandIndexPageParser();
		biparser.setCollector(collector);
		biparser.setMaxDepth(maxDepth);
		biparser.setWebDomain(webDomain);
		
		PicturePageParser pparser = new PicturePageParser();
		pparser.setCollector(collector);
		pparser.setMaxDepth(maxDepth);
		pparser.setWebDomain(webDomain);

		// parse start url.
		collector.addDealedLink(startUrl, "0");
		parser.extractPage(startUrl, 0, dir);

		print("线程数：" + threadCount);
		List<Thread> threadList = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			Thread t = new Thread(new PageIterator(collector, parser, lparser,
					dparser,biparser,pparser, listUrlPattern, detailUrlPattern,
					brandIndexPattern, picturePattern));
			t.start();
			threadList.add(t);
		}

		while (threadList.size() > 0) {
			Thread child = (Thread) threadList.remove(0);
			child.join();
		}

		collector.clear();
		collector = null;
		System.out.println("finished!");
	}

}
