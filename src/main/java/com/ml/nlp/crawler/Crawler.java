package com.ml.nlp.crawler;

import java.util.Set;

public class Crawler {
	
	public Set<String> crawlingNews(String url, String pattern) {
		LinkFilter linkFilter = new CustomLinkFilter(pattern);
		Set<String> links = HtmlParser.extracLinks(url, linkFilter);
		return links;
	}

	class CustomLinkFilter implements LinkFilter {
		
		private String pattern;
		
		CustomLinkFilter(String pattern) {
			this.pattern = pattern;
		}
		public boolean accept(String url) {
			if (url.matches(pattern)) {
				return true;
			} else {
				return false;
			}
		}
		
	}
}