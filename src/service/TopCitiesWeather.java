package service;

import config.ConfigLoader;
import util.HttpClient;

import java.io.IOException;

public class TopCitiesWeather extends HttpClient {
    public TopCitiesWeather() throws IOException {
        super(buildUrl());
    }

    private static String buildUrl() throws IOException {
            ConfigLoader configLoader = ConfigLoader.getInstance();
            return "http://dataservice.accuweather.com/locations/v1/topcities/50?apikey="
                    + configLoader.get("api.key");
        }
}
