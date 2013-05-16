/**
 *@author 杨旭，创建日期:2013-5-9
 *
 */
package com.jlu.yangxu.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.page.FileDownLoader;
import static com.jlu.yangxu.util.Funcs.logger;
public class BrandIndexPageParser extends PageParser {

	public BrandIndexPageParser() {
		super();
	}

	/**
	 * 针对BranPageIndex页做特殊的页面解析操作。 增加提取内容的功能。
	 * 
	 * @throws IOException
	 */
	public void extractPage(String url, Integer depth, String dir) {
		//if (!checkDepth(url, depth)) {
	//		return;
	//	}
		
		FileDownLoader fdl = new FileDownLoader(url);
		fdl.downloadFile("list",dir);
		extractLinks(fdl, depth, fdl.getEncoding());
	    collector.addDealedLink(url, String.valueOf(depth));
		fdl = null;
	}

	/**
	 * 解析出页面所有需要处理的url，并保存。
	 * 
	 * @param responseBody
	 *            网页信息
	 * @param depth
	 *            深度值
	 * @param encoding
	 *            编码
	 * @throws IOException
	 */
	public void extractLinks(FileDownLoader fdl, Integer depth, String encoding) {
		logger.info("extractor BrandPage: " + fdl.getUrl() + "  depth : " + depth);
		try {
			if (fdl.getContent() == null) {
				return;
			}
			Parser parser = new Parser();
			parser.setInputHTML(fdl.getContent());
			parser.setEncoding(encoding);

			/*
			 * // 获取main_center 节点下的link NodeFilter temp_filter = new
			 * AndFilter(new TagNameFilter("a"), new
			 * HasAttributeFilter("name")); NodeFilter list_filter = new
			 * AndFilter(temp_filter, new LinkRegexFilter(
			 * "http://blog.csdn.net/.*?/article/details/\\d*+"));
			 * 
			 * NodeList blog_list =
			 * parser.extractAllNodesThatMatch(list_filter);
			 * System.out.println(blog_list.size()); Node[] detailNodes =
			 * blog_list.toNodeArray(); for (Node node : detailNodes) {
			 * System.out.println(node.getText()); }
			 * 
			 * NodeFilter page_filter =new AndFilter(new TagNameFilter("a"),new
			 * LinkRegexFilter( "/.*?/index.html\\?page=\\d*+")); NodeList
			 * page_list = parser.extractAllNodesThatMatch(page_filter);
			 * System.out.println(page_list.size());
			 * 
			 * Node[] pageNodes = page_list.toNodeArray(); for (Node node :
			 * pageNodes) { System.out.println(node.getText()); }
			 */

			NodeList nlist = parser
					.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));// 查处了所有的link标签
			for (int i = 0; i < nlist.size(); i++) {
				Node node = nlist.elementAt(i);
				if (node instanceof LinkTag) {
					LinkTag ltag = (LinkTag) node;
					String linkHref = ltag.getLink().trim();								
				  //processLinkHrefFor163(fdl,depth,linkHref);
					processLinkHrefForCsdn( depth, linkHref);
				}
			}

		} catch (ParserException pe) {
			pe.printStackTrace();
		}
	}

	private void processLinkHrefForCsdn( Integer depth,
			String linkHref) {
		String nextDepth = String.valueOf(depth + 1);
		if(Pattern.matches("http://blog.csdn.net/.*?/article/details/\\d*+", linkHref)
				||Pattern.matches("/.*?/index.html\\?page=\\d*+", linkHref)){
			if(Pattern.matches("/.*?/index.html\\?page=\\d*+", linkHref)){
				linkHref = "http://blog.csdn.net"+linkHref;
			}
			//System.out.println(linkHref);
			saveUrl(linkHref, nextDepth);
		}
	}

	private void processLinkHrefFor163(FileDownLoader fdl, Integer depth,
			String linkHref) {
		String url = fdl.getUrl();
		String[] subs = url.split("/");
		String mobileBrandName = "/" + subs[3];
		String nextDepth = String.valueOf(depth + 1);
		String regex = mobileBrandName + "/.*?/#B\\d{2}?";
		if (Pattern.matches("./\\d*?.html#result", linkHref)
				|| Pattern.matches(regex, linkHref)) {
			if (Pattern.matches("./\\d*?.html#result", linkHref)) {
				linkHref = linkHref.substring(1);
				linkHref = mobileBrandName + linkHref;
				linkHref = "http://product.mobile.163.com" + linkHref;
				saveUrl(linkHref, nextDepth);
			}
			linkHref = "http://product.mobile.163.com" + linkHref;
			saveUrl(linkHref, nextDepth);

		}

	}

	public static void main(String[] args) {
		BrandIndexPageParser bip = new BrandIndexPageParser();
		String url1 = "http://blog.csdn.net/web/index.html?page=3";
		String url2 = "http://product.mobile.163.com/BlackBerry/#7BA";
		Integer depth = 0;
		String dir = "e:\\test";
		bip.extractPage(url1, depth, dir);
		System.out.println("finished");
	}
}
