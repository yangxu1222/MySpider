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

public class DetailPageParser extends PageParser {

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
		String mobileBrand = "/" + url.split("/")[3] + "/" + url.split("/")[4];
		FileDownLoader fdl = getPageContent(url, dir);

		// extract content
		// System.out.println("detail not extract : " + url);

		// extractLinks(fdl, depth, fdl.getEncoding(),mobileBrand);
		collector.addDealedLink(url, String.valueOf(depth));
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
			String encoding, String mobileBrand) {
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
					// String linkText = ltag.getLinkText().trim();
					// print(linkText);
					// Pattern pattern = Pattern.compile("./\\d*?.html#result");

					// try {
					// linkHref = formatUrl(fdl.getUrl(), linkHref);
					// linkHref = "http://product.mobile.163.com"+linkHref;
					// System.out.println(linkHref);
					// if (checkUrl(linkHref)) {
					if (linkHref == "img.html#8B3") {
						linkHref = mobileBrand + linkHref;
						// saveUrl(linkHref, nextDepth);
						// System.out.println("save picture: " + linkHref);
					}
					// }
					// } catch (Exception e) {
					// System.err.println("Error when format " + linkHref);
					// }

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
		String url = "http://product.mobile.163.com/Nokia/000BLXOY/#B11";
		String url2 = "http://product.mobile.163.com/SonyEricss/000BHdWV/img.html#8B1_10";
		Integer depth = 0;
		String dir = "F:";
		parser.extractPage(url, depth, dir);
		System.out.println("finished");
	}

}