/**
 *@author 杨旭，创建日期:2013-5-7
 *
 */
package com.jlu.yangxu.parser;

import java.io.IOException;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.page.FileDownLoader;
import static com.jlu.yangxu.util.Funcs.logger;
public class ListPageParser extends PageParser {
	public ListPageParser() {
		super();
	}

	/**
	 * 针对list页做特殊的页面解析操作。 增加提取内容的功能。
	 * 
	 * @throws IOException
	 */
	public void extractPage(String url, Integer depth, String dir) {
		if (!checkDepth(url, depth)) {
			return;
		}
		logger.info("parse listPage: " + url + "  depth : " + depth);
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

		try {
			if (fdl.getContent() == null) {
				return;
			}
			Parser parser = new Parser();
			parser.setInputHTML(fdl.getContent());
			parser.setEncoding(encoding);
			NodeList nlist = parser
					.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));// 查处了所有的link标签
			for (int i = 0; i < nlist.size(); i++) {
				Node node = nlist.elementAt(i);
				if (node instanceof LinkTag) {
					LinkTag ltag = (LinkTag) node;
					String linkHref = ltag.getLink().trim();
					//processLinkHrefFor163(fdl, depth, linkHref);
					processLinkHrefForCsdn(fdl, depth, linkHref);
				}
			}
		} catch (ParserException pe) {
			pe.printStackTrace();
		}
	}
	private void processLinkHrefForCsdn(FileDownLoader fdl, Integer depth,
			String linkHref){
		String nextDepth = String.valueOf(depth + 1);
		if(Pattern.matches("http://blog.csdn.net/.*?/article/details/\\d*+", linkHref)
				||Pattern.matches("/.*?/index.html\\?page=\\d*+", linkHref)
				||Pattern.matches("http://blog.csdn.net/.*?/article/category/\\d*+", linkHref)){
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
		String nextDepth = String.valueOf(depth + 1);
		String mobileBrandName = "/" + url.split("/")[3];
		String regex = mobileBrandName + "/.*?/#B\\d{2}?";
		if (Pattern.matches(regex, linkHref)
				|| Pattern.matches("./\\d*?.html#result", linkHref)) {
			if (Pattern.matches("./\\d*?.html#result", linkHref)) {
				linkHref = linkHref.substring(1);
				linkHref = mobileBrandName + linkHref;
				linkHref = "http://product.mobile.163.com" + linkHref;
				//System.out.println(linkHref);
				saveUrl(linkHref, nextDepth);
			}
			//System.out.println(linkHref);
			linkHref = "http://product.mobile.163.com" + linkHref;
			saveUrl(linkHref, nextDepth);
		}
	}

	/**
	 * 继承自父类<code>PageParser</code>。 如果是detail页则不需要校验深度值<code>depth</code>。
	 */
	public boolean checkDepth(String url, int depth) {
		return true;
	}

	public static void main(String[] args) {
		ListPageParser parser = new ListPageParser();
		String url = "http://blog.csdn.net/lance_yan/article/details/8924350";
		String url2 = "http://product.mobile.163.com/BlackBerry/#7BA";
		Integer depth = 0;
		String dir = "e:\\test";
		parser.extractPage(url, depth, dir);
		System.out.println("finished");
	}

}