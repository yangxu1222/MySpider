/**
 *@author ���񣬴�������:2013-5-5
 *
 */
package com.jlu.yangxu;

import java.util.Set;

public class Crawler {

	/* ʹ������ url ��ʼ�� URL ���� */
	private void initCrawlerWithSeeds(String[] seeds) {
		for (int i = 0; i < seeds.length; i++)
			LinkDB.addUnvisitedUrl(seeds[i]);
	}

	/* ��ȡ���� */
	public void crawling(String[] seeds) {
		LinkFilter filter = new LinkFilter() {
			// ��ȡ�� http://www.twt.edu.cn ��ͷ������
			public boolean accept(String url) {
				if (url.startsWith("http://www.jlu.edu.cn"))
					return true;
				else
					return false;
			}
		};
		// ��ʼ�� URL ����
		initCrawlerWithSeeds(seeds);
		// ѭ����������ץȡ�����Ӳ�����ץȡ����ҳ������ 1000
		while (!LinkDB.unVisitedUrlsEmpty()
				&& LinkDB.getVisitedUrlNum() <= 100) {
			// ��ͷ URL ����
			String visitUrl = LinkDB.unVisitedUrlDeQueue();
			if (visitUrl == null)
				continue;
			// ������ҳ
			FileDownLoader.downloadFile(visitUrl);
			// �� url ���뵽�ѷ��ʵ� URL ��
			LinkDB.addVisitedUrl(visitUrl);
			// ��ȡ��������ҳ�е� URL
			Set<String> links = HtmlParserTool.extracLinks(visitUrl, filter);
			// �µ�δ���ʵ� URL ���
			for (String link : links) {
				LinkDB.addUnvisitedUrl(link);
			}
		}
	}
	// main �������
	public static void main(String[] args) {
		Crawler crawler = new Crawler();
		crawler.crawling(new String[] { "http://www.jlu.edu.cn" });
		System.out.println("finised!");
	}

}
