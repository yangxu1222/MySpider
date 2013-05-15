/**
 *@author 杨旭，创建日期:2013-5-7
 *
 */
package com.jlu.yangxu.page;

import java.util.regex.Pattern;

import com.jlu.yangxu.parser.BrandIndexPageParser;
import com.jlu.yangxu.parser.DetailPageParser;
import com.jlu.yangxu.parser.ListPageParser;
import com.jlu.yangxu.parser.PageParser;
import com.jlu.yangxu.parser.PicturePageParser;
import com.jlu.yangxu.util.ConfigUtil;

public class PageIterator implements Runnable {
	private PageLinkCollector collector;

	private PageParser parser;

	private ListPageParser lparser;

	private DetailPageParser dparser;
	
	private BrandIndexPageParser biparser ;
	
	private PicturePageParser pparser;

	private String listUrlStart;

	private String detailUrlPattern;
	
	private String brandIndexPattern;
	
	private String picturePattern;
	
	private static String detailDir = ConfigUtil.getProperty("CACHE_DETAIL_FILE_PATH");
	
	private static String listDir = ConfigUtil.getProperty("CACHE_LIST_FILE_PATH");

	public PageIterator(PageLinkCollector collector, PageParser parser,
			ListPageParser lparser, DetailPageParser dparser,
			BrandIndexPageParser biparser,PicturePageParser pparser,
			String listUrlStart, String detailUrlPattern,
			String brandIndexPattern,String picturePattern) {
		this.collector = collector;
		this.parser = parser;
		this.lparser = lparser;
		this.dparser = dparser;
		this.pparser = pparser;
		this.biparser = biparser;
		this.listUrlStart = listUrlStart;
		this.detailUrlPattern = detailUrlPattern;
		this.brandIndexPattern = brandIndexPattern;
		this.picturePattern = picturePattern;
		this.brandIndexPattern = brandIndexPattern;
	}

	@Override
	public void run() {
		String[] toDealLink = null;//[url,depth]
		while ((toDealLink = collector.getNextToDeal()) != null) {
			if (isDetailPage(toDealLink[0])) {
				dparser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),detailDir);
			} else if (isListPage(toDealLink[0])) {
				lparser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),listDir);
			} else if (isbrandIndexPage(toDealLink[0])){
				biparser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),listDir);
			}else if (ispicturePage(toDealLink[0])){
				pparser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),listDir);
			}else{
				parser.extractPage(toDealLink[0],
						Integer.valueOf(toDealLink[1]),listDir);
			}

		}

	}

	public boolean isListPage(String url) {
		//if (url.startsWith(listUrlStart)) {
	//		return true;
		//}
		if (Pattern.matches(listUrlStart, url)){
			return true;
		}
		return false;
	}

	public boolean isDetailPage(String url) {
		if (Pattern.matches(detailUrlPattern, url)) {
			return true;
		}
		return false;
	}

	public boolean isbrandIndexPage(String url) {
		//if (url.startsWith(listUrlStart)) {
	//		return true;
		//}
		if (Pattern.matches(brandIndexPattern, url)){
			return true;
		}
		return false;
	}
	public boolean ispicturePage(String url) {
		//if (url.startsWith(listUrlStart)) {
	//		return true;
		//}
		if (Pattern.matches(picturePattern, url)){
			return true;
		}
		return false;
	}
	public static void main(String[] args) {

	}

}
