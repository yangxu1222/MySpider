/**
 *@author 杨旭，创建日期:2013-5-6
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
	 * 获得网页的内容。
	 * 
	 * @param url
	 * @return 含有网页信息的类
	 * @see FileDownLoader
	 */
	public FileDownLoader getPageContent(String url, String dir) {
		FileDownLoader fdl = new FileDownLoader(url);
		fdl.downloadFile(dir);
		return fdl;
	}

	/**
	 * 判断<code>url</code>的<code>depth</code>是否满足继续处理的条件。 子类可以继承该方法以具体处理某些url。
	 * 
	 * @param url
	 * @param depth
	 * @return 如果满足返回true，否则返回false
	 */
	public boolean checkDepth(String url, int depth) {
		return depth < maxDepth;
	}

	/**
	 * 校验<code>linkHref</code>是否是个合法有效的链接。
	 * 
	 * @param linkHref
	 * @return 如果是返回true，否则返回false
	 */
	public boolean checkUrl(String linkHref) {
		// 跳过链到本页面内链接以及发送邮件和调用js的链接。
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
		// 这里只考虑抓取http协议
		if (!linkHref.startsWith(protocol + "://")) {
			return false;
		}
		// 以下下代码就可以限制在自己的domian里面找
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
	 * 格式化url
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
			if (link.charAt(0) == '/') {// 处理绝对地
				res.append("http://").append(parentUrl.getHost());
				if (parentUrl.getPort() != -1) {
					res.append(":").append(parentUrl.getPort());
				}
				res.append(link);
			} else {
				if (link.charAt(0) == '?') {// 处理参数地址
					res.append("http://").append(parentUrl.getHost());
					if (parentUrl.getPort() != -1) {
						res.append(":").append(parentUrl.getPort());
					}
					res.append(parentUrl.getPath()).append(link);
				} else {
					String file = parentUrl.getFile();
					if (file.indexOf('/') == -1) {// 处理相对地址
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
	 * 保存处理过的url。 可以扩展为一个接口，以实现不同的保存方式。
	 * 
	 * @param linkHref
	 * @param nextDepth
	 */
	protected void saveUrl(String linkHref, String nextDepth) {
		collector.add(linkHref, nextDepth);
	}

	/**
	 * 如果是动态网站的话，url中很可能出现jessionid这个参数。 去掉以防止url重复处理。
	 * 
	 * @param url
	 * @return 去掉jessionid的url
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
