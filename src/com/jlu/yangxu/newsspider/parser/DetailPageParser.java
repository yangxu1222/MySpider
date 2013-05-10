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
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.newsspider.page.FileDownLoader;
import com.jlu.yangxu.newsspider.page.PictureDownloader;
import com.jlu.yangxu.newsspider.util.ConfigUtil;

public class DetailPageParser extends PageParser {
	public DetailPageParser() {
		super();
	}

	/**
	 * 针对detail页做特殊的页面解析操作。 增加提取内容的功能。 这里主要提取图片内容
	 * 
	 * @throws IOException
	 */
	public void extractPage(String url, Integer depth, String dir) {
		logger.info("parse detailPage: " + url + "  depth : " + depth);		
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
	public void extractLinks(FileDownLoader fdl, Integer depth,
			String encoding) {
		try {
			if (fdl.getContent() == null) {
				return;
			}
			Parser parser = new Parser();
			parser.setInputHTML(fdl.getContent());
			parser.setEncoding(encoding);
			NodeList nlist = parser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));// 查处了所有的link标签
			int count = 0;
			for (int i = 0; i < nlist.size(); i++) {
				Node node = nlist.elementAt(i);
				if (node instanceof LinkTag) {
					LinkTag ltag = (LinkTag) node;
					String linkHref = ltag.getLink().trim();
					String[] subsUrl = fdl.getUrl().split("/");
					String mobileBrandName = subsUrl[3];
					String mobileBrandType = subsUrl[4];
					//System.out.println(linkHref);
					if (linkHref.contains("#8B1_")) {
						Node child = ltag.getLastChild();
						if(child instanceof ImageTag){
							String imgsrc = ((ImageTag) child).getAttribute("src");
							String pictureName = mobileBrandName +"_"+ mobileBrandType+"_"+count +".jpg";
							PictureDownloader pictureDownLoader = new PictureDownloader(imgsrc);
							pictureDownLoader.downloadFile(pictureName);
							count ++;
						}
					}
					if(linkHref.contains("param.html#8B2")){
						linkHref = "http://product.mobile.163.com" +"/" + mobileBrandName +"/" + mobileBrandType +"/" + linkHref;

						String fileName = mobileBrandName+"_"+mobileBrandType;
						String dir = ConfigUtil.getProperty("CACHE_PRAMA_PATH");
						FileDownLoader fileDownLoader = new FileDownLoader(linkHref);
						fileDownLoader.downloadFile(fileName,dir);
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
		DetailPageParser parser = new DetailPageParser();
		String url = "http://product.mobile.163.com/Samsung/000BMNBO/#B11";
		Integer depth = 0;
		String dir = "e:";
		parser.extractPage(url, depth, dir);
		System.out.println("finished");
	}

}