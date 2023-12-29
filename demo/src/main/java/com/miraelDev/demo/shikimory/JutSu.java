package com.miraelDev.demo.shikimory;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


public class JutSu {
    public String link;
    public HttpClient client;

    public JutSu(String link) throws IOException, InterruptedException {
        this.link = link;
        this.client = HttpClient.newBuilder()
                .build();
    }

    public HttpClient getClient() {
        return this.client;
    }

    public List<Episode> getAllEpisodes() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Document doc = Jsoup.parse(response.body());

        Elements episodes = doc.select("a.short-btn");

        List<Episode> episodeList = new ArrayList<>();

        for (int i = 0; i < episodes.size(); i++) {

            Element episode = episodes.get(i);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("https://jut.su" + episode.attr("href")))
                    .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                    .build();


            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            Document doc2 = Jsoup.parse(response2.body());

            String seriesName = doc2.select("div.video_plate_title").select("h2").text();

            episodeList.add(new Episode(seriesName, episode.attr("href")));
        }

        return episodeList;
    }


    public String getDownloadLink(String href, String res) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jut.su" + "/" + href))
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Document doc = Jsoup.parse(response.body());

        Element source = doc.selectFirst("source[res=" + res + "]");
        if (source == null) {
            source = doc.selectFirst("source");
        }

        return source != null ? source.attr("src") : null;
    }
}




