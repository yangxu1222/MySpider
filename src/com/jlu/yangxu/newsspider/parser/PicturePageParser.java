/**
 *@author 杨旭，创建日期:2013-5-10
 *
*/
package com.jlu.yangxu.newsspider.parser;

import java.io.IOException;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.newsspider.page.FileDownLoader;

public class PicturePageParser extends PageParser {
	public PicturePageParser(){
		super();
	}
	
	/**
	 * 针对detail页做特殊的页面解析操作。
	 * 增加提取内容的功能。  这里主要提取图片内容
	 * @throws IOException 
	 */
	public void extractPage(String url, Integer depth,String dir) {
		System.out.println("parse : " + url + "  depth : " + depth);
		String mobileBrand = "/"+url.split("/")[3]+"/"+url.split("/")[4];
		FileDownLoader fdl = getPageContent(url,dir);
	
		// extract content
		System.out.println("extract content : " + url);
		
		extractLinks(fdl, depth, fdl.getEncoding(),mobileBrand);
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
	public void extractLinks(FileDownLoader fdl, Integer depth, String encoding,String mobileBrand) {
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
					// String linkText = ltag.getLinkText().trim();
					// print(linkText);
					// Pattern pattern = Pattern.compile("./\\d*?.html#result");

					try {
						//linkHref = formatUrl(fdl.getUrl(), linkHref);
						//linkHref = "http://product.mobile.163.com"+linkHref;
						//System.out.println(linkHref);
					//	if (checkUrl(linkHref)) {
						if (linkHref == "img.html#8B3"){
							linkHref = mobileBrand+linkHref;
							saveUrl(linkHref, nextDepth);
						}
					//	}
					} catch (Exception e) {
						System.err.println("Error when format " + linkHref);
					}

				}
			}
		} catch (ParserException pe) {
			pe.printStackTrace();
		}
	}
	
	
	
	/**
	 * 继承自父类<code>PageParser</code>。
	 * 如果是detail页则不需要校验深度值<code>depth</code>。
	 */
	public boolean checkDepth(String url, int depth) {
		return true;
	}

}
