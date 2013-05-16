/**
 *@author ���񣬴�������:2013-5-7
 *
 */
package com.jlu.yangxu.parser;

import java.io.IOException;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.page.FileDownLoader;
import com.jlu.yangxu.page.PictureDownloader;
import com.jlu.yangxu.util.ConfigUtil;
import static com.jlu.yangxu.util.Funcs.logger;

public class DetailPageParser extends PageParser {
	private static int count = 0;

	public DetailPageParser() {
		super();
	}

	/**
	 * ���detailҳ�������ҳ����������� ������ȡ���ݵĹ��ܡ� ������Ҫ��ȡͼƬ����
	 * 
	 * @throws IOException
	 */
	public void extractPage(String url, Integer depth, String dir) {
		
		FileDownLoader fdl = new FileDownLoader(url);
		fdl.downloadFile("detail",dir);
		
		extractLinks(fdl, depth, fdl.getEncoding());
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
	public void extractLinks(FileDownLoader fdl, Integer depth, String encoding) {
		logger.info("extractor detailPage: " + fdl.getUrl() + "  depth : " + depth);
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
					// processLinkHrefFor163(ltag,fdl,depth,linkHref);
					processLinkHrefForCsdn(fdl, depth, linkHref);

				}
			}
		} catch (ParserException pe) {
			pe.printStackTrace();
		}
	}

	private void processLinkHrefFor163(LinkTag ltag, FileDownLoader fdl,
			Integer depth, String linkHref) {
		String[] subsUrl = fdl.getUrl().split("/");
		String mobileBrandName = subsUrl[3];
		String mobileBrandType = subsUrl[4];
		if (linkHref.contains("#8B1_")) {
			Node child = ltag.getLastChild();
			if (child instanceof ImageTag) {
				String imgsrc = ((ImageTag) child).getAttribute("src");
				String pictureName = mobileBrandName + "_" + mobileBrandType
						+ "_" + count + ".jpg";
				PictureDownloader pictureDownLoader = new PictureDownloader(
						imgsrc);
				pictureDownLoader.downloadFile(pictureName);
				count++;
			}
		}
		if (linkHref.contains("param.html#8B2")) {
			linkHref = "http://product.mobile.163.com" + "/" + mobileBrandName
					+ "/" + mobileBrandType + "/" + linkHref;
			String fileName = mobileBrandName + "_" + mobileBrandType;
			String dir = ConfigUtil.getProperty("CACHE_PRAMA_PATH");
			FileDownLoader fileDownLoader = new FileDownLoader(linkHref);
			fileDownLoader.downloadFile(fileName, dir);
		}
	}

	private void processLinkHrefForCsdn(FileDownLoader fdl, Integer depth,
			String linkHref) {
		String nextDepth = String.valueOf(depth + 1);
		if (Pattern.matches("http://blog.csdn.net/.*?/article/details/\\d*+",linkHref)
				|| Pattern.matches("http://blog.csdn.net/.*?/article/category/\\d*+",linkHref)) {
			//System.out.println(linkHref);
			saveUrl(linkHref, nextDepth);
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
		String url = "http://blog.csdn.net/lance_yan/article/details/8924355";
		Integer depth = 0;
		String dir = "e:";
		parser.extractPage(url, depth, dir);
		System.out.println("finished");
	}

}