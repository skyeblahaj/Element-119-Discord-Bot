package discordbot.core.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DOMGetRequester {

	private Document doc;
	private Elements elements;
	
	public DOMGetRequester(String uri) throws IOException {
		this.doc = Jsoup.connect(uri).get();
	}
	
	public Elements getHTMLElements(String element) {
		this.elements = this.doc.select(element);
		return this.elements;
	}
	
	public List<Element> getHTMLElementsAsList(String element){
		getHTMLElements(element);
		List<Element> ret = new ArrayList<>();
		for (Element e : this.elements) {
			ret.add(e);
		}
		return ret;
	}
}