package com.ml.nlp.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.ml.db.MongoDB;
import com.ml.model.News;
import com.ml.util.Constants;

/**
 * 用于对新浪网站上的新闻进行抓取
 */
public class SinaNewsParser extends AbstractNewsParser {
	
	/**
     * 获得新闻的标题
     * <h1 itemprop="headline">...<\/h1>
     */
	protected String getTitle(NodeFilter titleFilter, Parser parser) throws ParserException {
        String titleName = "";
        NodeList titleNodeList = (NodeList) parser.parse(titleFilter);
        for (int i = 0; i < titleNodeList.size(); i++) {
            HeadingTag title = (HeadingTag) titleNodeList.elementAt(i);
            titleName = title.getStringText();
        }
        return titleName;
    }

    /**
     * 获得新闻的责任编辑，也就是作者。
     */
    protected String getAuthor(NodeFilter authorFilter, Parser parser) throws ParserException {
        String newsAuthor = "";
        return newsAuthor;

    }

    /**
     * 获得新闻的日期
     * <span id="pub_date">2013年10月10日 07:21</span>
     */
    protected String getDate(NodeFilter dateFilter, Parser parser) throws ParserException {
        String newsDate = "";
        NodeList dateList = (NodeList) parser.parse(dateFilter);
        for (int i = 0; i < dateList.size(); i++) {
        	Span dateTag = (Span) dateList.elementAt(i);
            newsDate = dateTag.getStringText();
        }
        NodeFilter dateFilter2 = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("class", "t2_time"));
        if(newsDate.equals("")) {
        	parser.reset();
        	NodeList dateList2 = (NodeList) parser.parse(dateFilter2);
            for (int i = 0; i < dateList2.size(); i++) {
            	Span dateTag = (Span) dateList2.elementAt(i);
                newsDate = dateTag.getStringText();
            }
        }
        return newsDate;
    }

    /**
     * 获取新闻的内容
     * <div class="BSHARE_POP blkContainerSblkCon clearfix blkContainerSblkCon_14" id="artibody">
     */
    protected String getContent(NodeFilter contentFilter, Parser parser) throws ParserException {
        StringBuilder builder = new StringBuilder();
        NodeList contentList = (NodeList) parser.parse(contentFilter);
        for (int i = 0; i < contentList.size(); i++) {
            Div newsContenTag = (Div) contentList.elementAt(i);
            builder = builder.append(newsContenTag.getStringText());
        }
        
        String content = builder.toString();  //转换为String 类型。
        if (content != null) {
            parser.reset();
            parser = Parser.createParser(content, "gb2312");
            StringBean sb = new StringBean();
            sb.setCollapse(true);
            parser.visitAllNodesWith(sb);
            content = sb.getStrings();
            //content = content.replaceAll("\\\".*[a-z].*\\}", "");

        } else {
           System.out.println("没有得到新闻内容！");
        }

        return content;
    }

    /**
     * 获取新闻的图片url
     * content body中的第一个图片，如果没有则为空
     */
	protected String getImg(NodeFilter imgFilter, Parser parser) throws ParserException {
		String url = "";
		
		StringBuilder builder = new StringBuilder();
        NodeList contentList = (NodeList) parser.parse(imgFilter);
        for (int i = 0; i < contentList.size(); i++) {
            Div newsContenTag = (Div) contentList.elementAt(i);
            builder = builder.append(newsContenTag.getStringText());
        }
        String content = builder.toString();  //转换为String 类型。
        if (content != null) {
            parser.reset();
            parser = Parser.createParser(content, "gb2312");
            NodeFilter filter = new TagNameFilter("img");
            NodeList imgList = parser.extractAllNodesThatMatch(filter);
            for (int i = 0; i < imgList.size(); i++) {
            	ImageTag imgNode = (ImageTag) imgList.elementAt(i);
                url = imgNode.getImageURL();
            }

        }
		return url;
	}
	
	/**
     * 获取新闻的来源
     * <span id="media_name">法制文萃报&nbsp;</span>
     */
	protected String getSource(NodeFilter sourceFilter, Parser parser) throws ParserException {
		String source = "";
        NodeList sourceList = (NodeList) parser.parse(sourceFilter);
        for (int i = 0; i < sourceList.size(); i++) {
            Span sourceTag = (Span) sourceList.elementAt(i);
            Node node = sourceTag.childAt(0);
            source = node.toPlainTextString();
            source = source.replace("&nbsp;", "");
        }
        return source;
	}

	protected NewsNodeFilters getNodeFilters() {
        NodeFilter titleFilter = new AndFilter(new TagNameFilter("h1"), new HasAttributeFilter("id", "artibodyTitle"));
        NodeFilter contentFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "artibody"));
        NodeFilter dateFilter = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("id", "pub_date"));
        //NodeFilter authorFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "editer"));
        NodeFilter imgFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "artibody"));
        NodeFilter sourceFilter = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("id", "media_name"));
        
        NewsNodeFilters nodeFilters = new NewsNodeFilters(titleFilter, contentFilter, dateFilter, null, imgFilter, sourceFilter);
		return nodeFilters;
	}


    

    //单个文件测试网页
    public static void main(String[] args) throws ParserException, FileNotFoundException, IOException {
        SinaNewsParser sina = new SinaNewsParser();
        News news = sina.parse("http://tech.sina.com.cn/it/2013-10-15/10128817508.shtml");
        System.out.println(news.toString());
        
        String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		mongodb.save(news, "news3");
		List<News> list = mongodb.findAll(News.class, "news3");
		System.out.println(list.toString());
    }

}
