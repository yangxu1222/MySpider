/* @author Xu Yang;
   @time 2013-5-15
 */
package com.jlu.yangxu;

import com.jlu.yangxu.page.PageLinkCollector;
import com.jlu.yangxu.parser.BrandIndexPageParser;
import com.jlu.yangxu.parser.DetailPageParser;
import com.jlu.yangxu.parser.ListPageParser;
import com.jlu.yangxu.parser.PageParser;
import com.jlu.yangxu.parser.PicturePageParser;


public class Crawler {
	
	protected PageLinkCollector collector;
	protected PageParser parser ;
	protected ListPageParser lparser;
	protected DetailPageParser dparser;
	protected BrandIndexPageParser biparser;
	protected PicturePageParser pparser ;
	
	public Crawler(){
		this(null,null,null);
	}

	public Crawler(String webMarker,Integer maxDepth,String webDomain) {
		initialize(webMarker,maxDepth,webDomain);
	}

	public void initialize(String webMarker,Integer maxDepth,String webDomain) {
		PageLinkCollector collector = new PageLinkCollector(webMarker);
		this.collector = collector;
		PageParser parser = new PageParser();
		parser.setCollector(collector);
		parser.setMaxDepth(maxDepth);
		parser.setWebDomain(webDomain);
		this.parser = parser;
		
		ListPageParser lparser = new ListPageParser();
		lparser.setCollector(collector);
		lparser.setMaxDepth(maxDepth);
		lparser.setWebDomain(webDomain);
		this.lparser = lparser;
		
		DetailPageParser dparser = new DetailPageParser();
		dparser.setCollector(collector);
		dparser.setMaxDepth(maxDepth);
		dparser.setWebDomain(webDomain);
		this.dparser = dparser;
		
		BrandIndexPageParser biparser = new BrandIndexPageParser();
		biparser.setCollector(collector);
		biparser.setMaxDepth(maxDepth);
		biparser.setWebDomain(webDomain);
		this.biparser = biparser;
		
		PicturePageParser pparser = new PicturePageParser();
		pparser.setCollector(collector);
		pparser.setMaxDepth(maxDepth);
		pparser.setWebDomain(webDomain);
		this.pparser = pparser;
	}
	
	public PageLinkCollector getCollector() {
		return collector;
	}

	public void setCollector(PageLinkCollector collector) {
		this.collector = collector;
	}

	public PageParser getParser() {
		return parser;
	}

	public void setParser(PageParser parser) {
		this.parser = parser;
	}

	public ListPageParser getLparser() {
		return lparser;
	}

	public void setLparser(ListPageParser lparser) {
		this.lparser = lparser;
	}

	public DetailPageParser getDparser() {
		return dparser;
	}

	public void setDparser(DetailPageParser dparser) {
		this.dparser = dparser;
	}

	public BrandIndexPageParser getBiparser() {
		return biparser;
	}

	public void setBiparser(BrandIndexPageParser biparser) {
		this.biparser = biparser;
	}

	public PicturePageParser getPparser() {
		return pparser;
	}

	public void setPparser(PicturePageParser pparser) {
		this.pparser = pparser;
	}

}
