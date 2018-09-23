package gr.papadogiannis.stefanos.newsreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

class RssFeedHandler {

	private static final String KEY_ITEM = "item";
    private static String imageLink;
    private static String url;

    RssFeedHandler(String url) {
        RssFeedHandler.url =url;
    }

	void processFeed(Element element, HashMap<String, Object> map) {
        if(url.equals("yada")) {
            imageLink = getImageLink2(element, MainActivity.KEY_ENCLOSURE);
            map.put(MainActivity.KEY_ENCLOSURE, getBitmap(imageLink));
            map.put("newspapers","1");
            map.put(MainActivity.KEY_IMAGE_LINK, imageLink);
        } else {
            map.put(MainActivity.KEY_TITLE, getTitle(element, MainActivity.KEY_TITLE));
            map.put(MainActivity.KEY_LINK, getNewsItemLink(element, MainActivity.KEY_LINK));
            imageLink = getImageLink2(element, MainActivity.KEY_ENCLOSURE);
            map.put(MainActivity.KEY_ENCLOSURE, getBitmap(imageLink));
            map.put(MainActivity.KEY_IMAGE_LINK, imageLink);
            if (url.equals("yada2"))
                map.put(MainActivity.KEY_VIDEO_LINK,getVideoLink((String)map.get(MainActivity.KEY_LINK)));
        }
    }

    private String getVideoLink(String keyLink) {
        String feed=keyLink;
        Document document=null;
        Elements elements;
        org.jsoup.nodes.Element element;
        if (feed.startsWith("/")) {
            feed = "yada" + feed;
        }
        for (int i = 0; i < 10 && document == null; i++) {
            try {
                document = Jsoup.connect(feed).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (document != null) {
            elements = document.select("iframe[class=media-youtube-player]");
            if (elements.size() > 0) {
                element = elements.get(0);
                if (element != null) {
                    feed = element.attr("src");
                }
            }
        }
        return feed;
    }

    private String getImageLink2(Element element,String inputUrl) {
        String textContent="yada";
		NodeList nodeList=element.getElementsByTagName(inputUrl);
        if(nodeList!=null){
            Node node=nodeList.item(0);
            if(node!=null){
                NamedNodeMap attributes=node.getAttributes();
                if(attributes!=null){
                    node=attributes.getNamedItem("url");
                    if(node!=null){
                        textContent=node.getNodeValue();
                        if(textContent!=null) {
                            if(!url.equals("yada")) {
                                if (textContent.contains("MediaV2")) {
                                    textContent = textContent.replace("MediaV2", "yada");
                                } else {
                                    textContent = textContent.replace("default/files", "yada");
                                }
                            }
                        }
                    }
                }
            }
        }
        return textContent;
	}

	private String getNewsItemLink(Element element,String inputUrl) {
        return element.getElementsByTagName(inputUrl).item(0).getTextContent();
	}

	private Bitmap getBitmap(String url) {
        String url2="";
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			input.close();
			connection.disconnect();
			return bitmap;
		} catch(IOException e) {
            try {
                url2=url.replace("grid-4", "grid-8");
                HttpURLConnection connection = (HttpURLConnection) new URL(url2).openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input=connection.getInputStream();
                Bitmap bitmap=BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
                imageLink = url2;
                return bitmap;
            } catch(IOException e2) {
                try {
                    url2 = url2.replace("grid-8", "grid-2");
                    HttpURLConnection connection=(HttpURLConnection) new URL(url2).openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input=connection.getInputStream();
                    Bitmap bitmap=BitmapFactory.decodeStream(input);
                    input.close();
                    connection.disconnect();
                    imageLink = url2;
                    return bitmap;
                } catch(IOException e3) {
                    return null;
                }
            }
		}
	}

	private String getTitle(Element element,String string) {
		String description=element.getElementsByTagName(string).item(0).getTextContent();
		description=description.replace("&nbsp;"," ");
   	 	description=description.replace("&quot;","\"");
   	 	description=description.replace("&#039;","'");
		return description;
	}

}
