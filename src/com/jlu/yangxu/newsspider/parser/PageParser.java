/**
 *@author ���񣬴�������:2013-5-6
 *
 */
package com.jlu.yangxu.newsspider.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.jlu.yangxu.newsspider.page.FileDownLoader;
import com.jlu.yangxu.newsspider.page.PageLinkCollector;

public class PageParser {

	private String webDomain;
	private boolean limitDomain = true;
	private String protocol = "http";
	protected PageLinkCollector collector;
	private Integer maxDepth;

	public PageParser() {

	}

	public void extractPage(String url, Integer depth, String dir) {
		if (!checkDepth(url, depth)) {
			return;
		}
		System.out.println("parse : " + url + "  depth : " + depth);

		FileDownLoader fdl = getPageContent(url, dir);

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

					if (Pattern.matches("/.*?/#7BA", linkHref)) {
						// System.out.println("brand moblie: " + linkHref);
						saveUrl(linkHref, nextDepth);
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
	 * �����ҳ�����ݡ�
	 * 
	 * @param url
	 * @return ������ҳ��Ϣ����
	 * @see FileDownLoader
	 */
	public FileDownLoader getPageContent(String url, String dir) {
		FileDownLoader fdl = new FileDownLoader(url);
		fdl.downloadFile(dir);
		return fdl;
	}

	/**
	 * �ж�<code>url</code>��<code>depth</code>�Ƿ������������������� ������Լ̳и÷����Ծ��崦��ĳЩurl��
	 * 
	 * @param url
	 * @param depth
	 * @return ������㷵��true�����򷵻�false
	 */
	public boolean checkDepth(String url, int depth) {
		return depth < maxDepth;
	}

	/**
	 * У��<code>linkHref</code>�Ƿ��Ǹ��Ϸ���Ч�����ӡ�
	 * 
	 * @param linkHref
	 * @return ����Ƿ���true�����򷵻�false
	 */
	public boolean checkUrl(String linkHref) {
		// ����������ҳ���������Լ������ʼ��͵���js�����ӡ�
		if (linkHref.indexOf("#") > -1) {
			return false;
		}
		if (linkHref.indexOf("mailto:") > -1) {
			return false;
		}
		if (linkHref.toLowerCase().indexOf("javascript") > -1) {
			return false;
		}

		// linkHref = removeJsessionId(linkHref);
		// ����ֻ����ץȡhttpЭ��
		if (!linkHref.startsWith(protocol + "://")) {
			return false;
		}
		// �����´���Ϳ����������Լ���domian������
		if (limitDomain) {
			try {
				URL httpUrl = new URL(linkHref);
				if (httpUrl.getHost().indexOf(webDomain) < 0) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	/***
	 * ��ʽ��url
	 * 
	 * @param parentLink
	 * @param link
	 * @return
	 * @throws MalformedURLException
	 */
	private String formatUrl(String parentLink, String link)
			throws MalformedURLException {
		URL parentUrl = new URL(parentLink);
		StringBuffer res = new StringBuffer();
		if (link.trim().length() > 0 && link.indexOf("://") == -1) {
			if (link.charAt(0) == '/') {// ������Ե�
				res.append("http://").append(parentUrl.getHost());
				if (parentUrl.getPort() != -1) {
					res.append(":").append(parentUrl.getPort());
				}
				res.append(link);
			} else {
				if (link.charAt(0) == '?') {// ���������ַ
					res.append("http://").append(parentUrl.getHost());
					if (parentUrl.getPort() != -1) {
						res.append(":").append(parentUrl.getPort());
					}
					res.append(parentUrl.getPath()).append(link);
				} else {
					String file = parentUrl.getFile();
					if (file.indexOf('/') == -1) {// ������Ե�ַ
						res.append("http://").append(parentUrl.getHost());
						if (parentUrl.getPort() != -1) {
							res.append(":").append(parentUrl.getPort());
						}
						res.append("/").append(link);
					} else {
						String path = file.substring(0,
								file.lastIndexOf('/') + 1);
						res.append("http://").append(parentUrl.getHost());
						if (parentUrl.getPort() != -1) {
							res.append(":").append(parentUrl.getPort());
						}
						res.append(path).append(link);
					}
				}
			}
		}

		return res.toString();
	}

	/**
	 * ���洦�����url�� ������չΪһ���ӿڣ���ʵ�ֲ�ͬ�ı��淽ʽ��
	 * 
	 * @param linkHref
	 * @param nextDepth
	 */
	protected void saveUrl(String linkHref, String nextDepth) {
		collector.add(linkHref, nextDepth);
	}

	/**
	 * ����Ƕ�̬��վ�Ļ���url�кܿ��ܳ���jessionid��������� ȥ���Է�ֹurl�ظ�����
	 * 
	 * @param url
	 * @return ȥ��jessionid��url
	 */
	public String removeJsessionId(String url) {
		if (url.indexOf(";jsessionid=") > -1) {
			StringBuffer newUrl = new StringBuffer(url.substring(0,
					url.indexOf(";jsessionid=")));
			int andPos = url.substring(url.indexOf(";jsessionid=") + 1)
					.indexOf("?");
			if (andPos > -1) {
				newUrl.append(url.substring(url.indexOf(";jsessionid=") + 1)
						.substring(andPos));
			}
			return newUrl.toString();
		}

		return url;
	}

	public String getWebDomain() {
		return webDomain;
	}

	public void setWebDomain(String webDomain) {
		this.webDomain = webDomain;
	}

	public boolean isLimitDomain() {
		return limitDomain;
	}

	public void setLimitDomain(boolean limitDomain) {
		this.limitDomain = limitDomain;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public PageLinkCollector getCollector() {
		return collector;
	}

	public void setCollector(PageLinkCollector collector) {
		this.collector = collector;
	}

	public Integer getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}

	public static void main(String[] args) {
		PageParser pp = new PageParser();
		pp.extractPage("http://product.mobile.163.com/", 0, "F:\\crawler\\temp");
		System.out.println("finised!");
	}
}
