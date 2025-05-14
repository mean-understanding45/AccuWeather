

package service;

import config.ConfigLoader;
import util.HttpClient;

import java.io.IOException;

public class TopCitiesCurrentCondition extends HttpClient {
    public TopCitiesCurrentCondition() throws IOException {
        super(buildUrl());
    }

    private static String buildUrl() throws IOException {
        ConfigLoader configLoader = ConfigLoader.getInstance();
        return "http://dataservice.accuweather.com/currentconditions/v1/topcities/50?apikey="
                + configLoader.get("api.key");
    }
}
