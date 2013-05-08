/**
 *@author 杨旭，创建日期:2013-5-5
 *
 */
package com.jlu.yangxu.newsspider;

import java.util.ArrayList;
import java.util.List;

import com.jlu.yangxu.newsspider.page.PageIterator;
import com.jlu.yangxu.newsspider.page.PageLinkCollector;
import com.jlu.yangxu.newsspider.parser.DetailPageParser;
import com.jlu.yangxu.newsspider.parser.ListPageParser;
import com.jlu.yangxu.newsspider.parser.PageParser;
import com.jlu.yangxu.newsspider.util.ConfigUtil;
import static net.mindview.util.Print.*;

public class Spider {
	public static void main(String[] args) throws InterruptedException {
		String webMarker = "csdn";
		String webDomain = "blog.csdn.net";
		String startUrl = "http://blog.csdn.net/index.html";

		//String listUrlStart = "http://www.ca800.com/news/list.asp";
		String listUrlPattern = "http://blog.csdn.net/.*?/.*?.html";
		String detailUrlPattern = "http://blog.csdn.net/.*?/article/details/[\\d]*";
		String dir =  ConfigUtil.getProperty("CSDN_CACHE_FILE_PATH") ;

		Integer threadCount = Integer.valueOf(ConfigUtil
				.getProperty("THREAD_COUNT"));

		PageLinkCollector collector = new PageLinkCollector(webMarker);

		Integer maxDepth = Integer.valueOf(ConfigUtil.getProperty("MAX_DEPTH"));

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

		// parse start url.
		collector.addDealedLink(startUrl,"0");
		parser.extractPage(startUrl, 0,dir);

		print("线程数：" + threadCount);
		List<Thread> threadList = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			//Thread t = new Thread(new PageIterator(collector, parser, lparser,
			//		dparser, listUrlStart, detailUrlPattern));
			Thread t = new Thread(new PageIterator(collector, parser, lparser,
					dparser, listUrlPattern, detailUrlPattern,dir));
			t.start();
			threadList.add(t);
		}

		while (threadList.size() > 0) {
			Thread child = (Thread) threadList.remove(0);
			child.join();
		}

		collector.clear();
		collector = null;

	}
}
