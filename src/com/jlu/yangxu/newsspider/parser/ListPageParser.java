/**
 *@author 杨旭，创建日期:2013-5-7
 *
 */
package com.jlu.yangxu.newsspider.parser;

import java.io.IOException;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.newsspider.page.FileDownLoader;

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
		logger.info("parse listPage: " + url + "  depth : " + depth);
		FileDownLoader fdl = getPageContent(url, dir);
		extractLinks(fdl, depth, fdl.getEncoding());

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
		String url = fdl.getUrl();
		String mobileBrandName = "/" + url.split("/")[3];
		String nextDepth = String.valueOf(depth + 1);
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
					String regex = mobileBrandName + "/.*?/#B\\d{2}?";
					if (Pattern.matches(regex, linkHref)
							|| Pattern.matches("./\\d*?.html#result", linkHref)) {
						if (Pattern.matches("./\\d*?.html#result", linkHref)) {
							linkHref = linkHref.substring(1);
							linkHref = mobileBrandName + linkHref;
						}
						 saveUrl(linkHref, nextDepth);
					}

				}
			}
		} catch (ParserException pe) {
			pe.printStackTrace();
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
		String url = "http://product.mobile.163.com/BlackBerry/2.html#result";
		String url2 = "http://product.mobile.163.com/BlackBerry/#7BA";
		Integer depth = 0;
		String dir = "F:";
		parser.extractPage(url, depth, dir);
		System.out.println("finished");
	}

}