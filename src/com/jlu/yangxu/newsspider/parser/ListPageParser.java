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
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.newsspider.page.FileDownLoader;

public class ListPageParser extends PageParser {
	public ListPageParser() {
		super();
	}

	/**
	 * ���listҳ�������ҳ����������� ������ȡ���ݵĹ��ܡ�
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
					.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));// �鴦�����е�link��ǩ
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
	 * �̳��Ը���<code>PageParser</code>�� �����detailҳ����ҪУ�����ֵ<code>depth</code>��
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