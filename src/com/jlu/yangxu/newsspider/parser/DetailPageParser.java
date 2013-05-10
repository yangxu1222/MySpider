/**
 *@author ���񣬴�������:2013-5-7
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

public class DetailPageParser extends PageParser {
	private static int count =0;
	public DetailPageParser() {
		super();
	}

	/**
	 * ���detailҳ�������ҳ����������� ������ȡ���ݵĹ��ܡ� ������Ҫ��ȡͼƬ����
	 * 
	 * @throws IOException
	 */
	public void extractPage(String url, Integer depth, String dir) {
		System.out.println("parse detailPage: " + url + "  depth : " + depth);
		FileDownLoader fdl = getPageContent(url, dir);

		// extract content
		// System.out.println("detail not extract : " + url);

		 extractLinks(fdl, depth, fdl.getEncoding());
		//collector.addDealedLink(url, String.valueOf(depth));
		fdl = null;
	}

	/**
	 * ������ҳ��������Ҫ�����url�������档
	 * 
	 * @param responseBody
	 *            ��ҳ��Ϣ
	 * @param depth
	 *            ���ֵ
	 * @param encoding
	 *            ����
	 * @throws IOException
	 */
	public void extractLinks(FileDownLoader fdl, Integer depth,
			String encoding) {
		//String nextDepth = String.valueOf(depth + 1);
		try {
			if (fdl.getContent() == null) {
				return;
			}
			Parser parser = new Parser();
			parser.setInputHTML(fdl.getContent());
			parser.setEncoding(encoding);
			NodeList nlist = parser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));// �鴦�����е�link��ǩ
			for (int i = 0; i < nlist.size(); i++) {
				Node node = nlist.elementAt(i);
				if (node instanceof LinkTag) {
					LinkTag ltag = (LinkTag) node;
					String linkHref = ltag.getLink().trim();
					// String linkText = ltag.getLinkText().trim();
					// print(linkText);
					// Pattern pattern = Pattern.compile("./\\d*?.html#result");

					// try {
					// linkHref = formatUrl(fdl.getUrl(), linkHref);
					// linkHref = "http://product.mobile.163.com"+linkHref;
					// System.out.println(linkHref);
					// if (checkUrl(linkHref)) {
					String[] subsUrl = fdl.getUrl().split("/");
					String mobileBrandName = subsUrl[3];
					String mobileBrandType = subsUrl[4];
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

				}
			}
		} catch (ParserException pe) {
			pe.printStackTrace();
		}
	}

	/**
	 * �̳��Ը���<code>PageParser</code>�� �����detailҳ����ҪУ�����ֵ<code>depth</code>��
	 */
	public boolean checkDepth(String url, int depth) {
		return true;
	}

	public static void main(String[] args) {
		DetailPageParser parser = new DetailPageParser();
		String url = "http://product.mobile.163.com/Samsung/000BMNBO/#B11";
		Integer depth = 0;
		String dir = "F:";
		parser.extractPage(url, depth, dir);
		System.out.println("finished");
	}

}