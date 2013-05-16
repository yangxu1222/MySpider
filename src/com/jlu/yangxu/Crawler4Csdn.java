/* @author Xu Yang;
   @time 2013-5-15
*/
package com.jlu.yangxu;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jlu.yangxu.page.PageIterator4Csdn;
import com.jlu.yangxu.util.ConfigUtil;
import static com.jlu.yangxu.util.Funcs.logger;;

public class Crawler4Csdn extends Crawler {
	//public static final String startUrl ="http://blog.csdn.net/";
	public static final String[] seeds = {"http://blog.csdn.net/"
		//"http://blog.csdn.net/all/column/list.html"
		};
	public static final String webMarker = "csdn";
	public static final String webDomain = "blog.csdn.net";
	public static final Integer maxDepth = Integer.MAX_VALUE;
	public static final String brandIndexPattern = "http://blog.csdn.net/.*?/index.html";
	public static final String listUrlPattern = "http://blog.csdn.net/.*?/index.html\\?page=\\d*+";
	public static final String detailUrlPattern = "http://blog.csdn.net/.*?/article/details/\\d*+";
	public static final String categoryPattern = "http://blog.csdn.net/.*?/article/category/\\d*+";
	//public static final String columnPattern = "http://blog.csdn.net/all/column/list.html";
	//public static final String picturePattern = "http://product.mobile.163.com/.*?/.*?/img.html#8B3";
	
	public static final String dir = ConfigUtil.getProperty("CSDN_LIST_FILE_PATH");
	public static final Integer threadCount = Integer.valueOf(ConfigUtil
			.getProperty("THREAD_COUNT"));

	public Crawler4Csdn() {
		this(webMarker, maxDepth, webDomain);
	}

	public Crawler4Csdn(String webMarker, Integer maxDepth, String webDomain) {
		super(webMarker, maxDepth, webDomain);
	}
	public  void crawlering(){
		logger.info("Starting crawler...");
		for(String startUrl:seeds){
			collector.addDealedLink(startUrl, "0");
			parser.extractPage(startUrl, 0, dir);
		}

		List<Thread> threadList = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			Thread t = new Thread(new PageIterator4Csdn(collector, parser,
					lparser, dparser,biparser,pparser));
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
		
		Crawler4Csdn crawler = new Crawler4Csdn();
		
		crawler.crawlering();
		logger.info("finished!");
	}

}
