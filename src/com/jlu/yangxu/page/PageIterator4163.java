/* @author Xu Yang;
   @time 2013-5-15
*/
package com.jlu.yangxu.page;

import java.util.regex.Pattern;

import com.jlu.yangxu.parser.BrandIndexPageParser;
import com.jlu.yangxu.parser.DetailPageParser;
import com.jlu.yangxu.parser.ListPageParser;
import com.jlu.yangxu.parser.PageParser;
import com.jlu.yangxu.parser.PicturePageParser;
import com.jlu.yangxu.util.ConfigUtil;

public class PageIterator4163 implements  Runnable{

	private PageLinkCollector collector;

	private PageParser parser;

	private ListPageParser lparser;

	private DetailPageParser dparser;
	
	private BrandIndexPageParser biparser ;
	
	private PicturePageParser pparser;

	private static final String listUrlPattern = "http://product.mobile.163.com/.*?/\\d*+.html#result";
	private static final String detailUrlPattern = "http://product.mobile.163.com/.*?/.*?/#B\\d{2}?";
	private static final String brandIndexPattern = "http://product.mobile.163.com/.*?/#7BA";
	private static final String picturePattern = "http://product.mobile.163.com/.*?/.*?/img.html#8B3";
		
	private static String detailDir = ConfigUtil.getProperty("CACHE_DETAIL_FILE_PATH");
	private static String listDir = ConfigUtil.getProperty("CACHE_LIST_FILE_PATH");

	public PageIterator4163(PageLinkCollector collector, PageParser parser,
			ListPageParser lparser, DetailPageParser dparser,
			BrandIndexPageParser biparser,PicturePageParser pparser) {
		this.collector = collector;
		this.parser = parser;
		this.lparser = lparser;
		this.dparser = dparser;
		this.pparser = pparser;
		this.biparser = biparser;
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
		if (Pattern.matches(listUrlPattern, url)){
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
		if (Pattern.matches(brandIndexPattern, url)){
			return true;
		}
		return false;
	}
	public boolean ispicturePage(String url) {
		if (Pattern.matches(picturePattern, url)){
			return true;
		}
		return false;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
