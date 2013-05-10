/**
 *@author 杨旭，创建日期:2013-5-9
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

public class BrandIndexPageParser extends PageParser {

	public BrandIndexPageParser(){
		super();
	}
	
	/**
	 * 针对BranPageIndex页做特殊的页面解析操作。
	 * 增加提取内容的功能。
	 * @throws IOException 
	 */
	public void extractPage(String url, Integer depth,String dir) {
		System.out.println("parse BrandPage: " + url + "  depth : " + depth);
		
		FileDownLoader fdl = getPageContent(url,dir);
		// extract content
		//System.out.println("extract content : " + url);
		
		extractLinks(fdl, depth, fdl.getEncoding());
		//collector.addDealedLink(url, String.valueOf(depth));
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
		String[] subs = url.split("/");
		String mobileBrandName = "/"+subs[3]  ;
		
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

				//	try {
						//linkHref = formatUrl(fdl.getUrl(), linkHref);
						//linkHref = "http://product.mobile.163.com"+linkHref;
						//System.out.println(linkHref);
					//	if (checkUrl(linkHref)) {
						String regex = mobileBrandName + "/.*?/#B\\d{2}?";
						//System.out.println(mobileBrandName);
						if (Pattern.matches("./\\d*?.html#result", linkHref) || Pattern.matches(regex, linkHref)){
							if(Pattern.matches("./\\d*?.html#result", linkHref)){
								//System.out.println("save list:"+ linkHref);
								linkHref = linkHref.substring(1);
								linkHref =  mobileBrandName +linkHref;
							}
							//System.out.println("detail:" + linkHref);
							saveUrl(linkHref, nextDepth);
							
						}
					//	}
				//	} catch (Exception e) {
				//		System.err.println("Error when format " + linkHref);
				//	}

				}
			}
		} catch (ParserException pe) {
			pe.printStackTrace();
		}
	}
	public static void main(String[] args){
		BrandIndexPageParser bip = new BrandIndexPageParser();
		String url1 = "http://product.mobile.163.com/TSD/#7BA";
		String url2 = "http://product.mobile.163.com/BlackBerry/#7BA";
		Integer depth = 0;
		String dir = "F:";
		bip.extractPage(url1, depth, dir);
		System.out.println("finished");
	}
}
