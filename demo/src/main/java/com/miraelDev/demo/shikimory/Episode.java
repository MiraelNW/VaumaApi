package com.miraelDev.demo.shikimory;

import lombok.ToString;

@ToString
public class Episode {
    public String name;
    public String href;
    public String season;

    public Episode(String episode_name, String href) {

        this.name = episode_name;
        this.href = href;
        this.season = href.contains("season") ? href.split("/")[2] : "season-1";
    }
}