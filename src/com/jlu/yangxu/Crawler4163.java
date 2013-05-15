/* @author Xu Yang;
   @time 2013-5-15
 */
package com.jlu.yangxu;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jlu.yangxu.page.PageIterator;
import com.jlu.yangxu.page.PageIterator4163;
import com.jlu.yangxu.page.PageLinkCollector;
import com.jlu.yangxu.parser.BrandIndexPageParser;
import com.jlu.yangxu.parser.DetailPageParser;
import com.jlu.yangxu.parser.ListPageParser;
import com.jlu.yangxu.parser.PageParser;
import com.jlu.yangxu.parser.PicturePageParser;
import com.jlu.yangxu.util.ConfigUtil;
import com.jlu.yangxu.util.Funcs;

public class Crawler4163 extends Crawler {

	public static Logger logger = Funcs.getLogger();
	public static final String startUrl = "http://product.mobile.163.com/brand/";
	public static final String webMarker = "163";
	public static final String webDomain = "product.mobile.163.com";
	public static final Integer maxDepth = Integer.MAX_VALUE;


	public static final String dir = ConfigUtil.getProperty("CACHE_LIST_FILE_PATH");
	public static final Integer threadCount = Integer.valueOf(ConfigUtil
			.getProperty("THREAD_COUNT"));

	public Crawler4163() {
		this(webMarker, maxDepth, webDomain);
	}

	public Crawler4163(String webMarker, Integer maxDepth, String webDomain) {
		super(webMarker, maxDepth, webDomain);
	}
	public  void crawlering(){
		collector.addDealedLink(startUrl, "0");
		parser.extractPage(startUrl, 0, dir);

		List<Thread> threadList = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			Thread t = new Thread(new PageIterator4163(collector,parser,
					lparser,dparser,biparser,pparser));
			t.start();
			threadList.add(t);
		}
		while (threadList.size() > 0) {
			Thread child = (Thread) threadList.remove(0);
			try {
				child.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		collector.clear();
		collector = null;
		
	}

	public static void main(String[] args) {
		 Crawler4163 crawler4163 = new Crawler4163();
		 crawler4163.crawlering();
		 logger.info("finised!");

	}

}
