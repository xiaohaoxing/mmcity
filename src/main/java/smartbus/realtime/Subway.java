package smartbus.realtime;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.transit.realtime.GtfsRealtime;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Subway {

    private static final String url = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs-ace";
    private static final String key = "7OS7e9OggM1VKJS8zBWLnBCsalwkPF8vZIhqing0";

    public static void main(String[] args) throws Exception {
        URI uri = new URI(url);
        URL url = uri.toURL();
        HttpURLConnection hc = (HttpURLConnection) url.openConnection();
        hc.setRequestProperty("x-api-key", key);
        GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(hc.getInputStream());
//        String json = new Gson().toJson(feed.getEntity(0), GtfsRealtime.FeedEntity.class);
        System.out.println(feed.getEntity(0));
    }
}
