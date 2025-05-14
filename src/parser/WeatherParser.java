package parser;

import model.CityWeatherData;

import java.util.ArrayList;
import java.util.List;

public class WeatherParser {

    public static List<CityWeatherData> getCombinedData(String api1Json, String api2Json) {
        List<CityWeatherData> cityWeatherList = new ArrayList<>();
        int index = 0;
        while ((index = api1Json.indexOf("\"Key\":\"", index)) != -1) {
            CityWeatherData city = new CityWeatherData();

            int keyStart = index + 7;
            int keyEnd = api1Json.indexOf("\"", keyStart);
            city.key = api1Json.substring(keyStart, keyEnd);

            city.name = getValue(api1Json, "\"LocalizedName\":\"", keyEnd);
            city.country = getValue(api1Json, "\"Country\":{\"ID", keyEnd, "\"LocalizedName\":\"", "\"");
            city.region = getValue(api1Json, "\"Region\":{\"ID", keyEnd, "\"LocalizedName\":\"", "\"");
            city.timezone = getValue(api1Json, "\"TimeZone\":", keyEnd, "\"Name\":\"", "\"");

            int rankMarker = api1Json.indexOf("\"Rank\":", keyEnd);
            int rankStart = rankMarker + "\"Rank\":".length();
            int rankEnd = api1Json.indexOf(",", rankStart);
            String rankStr = api1Json.substring(rankStart, rankEnd).trim();
            city.rank = rankStr.isEmpty() ? 0 : Integer.parseInt(rankStr);

            String latitudeStr = getValue(api1Json, "\"GeoPosition\":", keyEnd, "\"Latitude\":", ",");
            city.latitude = latitudeStr.isEmpty() ? 0.0 : Double.parseDouble(latitudeStr);

            String longitudeStr = getValue(api1Json, "\"GeoPosition\":", keyEnd, "\"Longitude\":", ",");
            city.longitude = longitudeStr.isEmpty() ? 0.0 : Double.parseDouble(longitudeStr);

            int matchIndex = api2Json.indexOf("\"Key\":\"" + city.key + "\"");
            if (matchIndex != -1) {
                city.weatherText = getValue(api2Json, "\"WeatherText\":\"", matchIndex);
                city.isDayTime = getValue(api2Json, "\"IsDayTime\":", matchIndex, "", ",").equalsIgnoreCase("true");

                String tempCStr = getValue(api2Json, "\"Temperature\":", matchIndex, "\"Metric\":{\"Value\":", ",");
                city.tempCelsius = tempCStr.isEmpty() ? 0.0 : Double.parseDouble(tempCStr);

                String tempFStr = getValue(api2Json, "\"Temperature\":", matchIndex, "\"Imperial\":{\"Value\":", ",");
                city.tempFahrenheit = tempFStr.isEmpty() ? 0.0 : Double.parseDouble(tempFStr);

                city.lastUpdatedAt = getValue(api2Json, "\"LocalObservationDateTime\":\"", matchIndex);
            }

            cityWeatherList.add(city);
            index = keyEnd;
        }
        return cityWeatherList;
    }

    private static String getValue(String json, String marker, int startFrom) {
        int start = json.indexOf(marker, startFrom);
        if (start == -1) return "";
        start += marker.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private static String getValue(String json, String parentMarker, int startFrom, String marker, String endChar) {
        int parent = json.indexOf(parentMarker, startFrom);
        if (parent == -1) return "";
        int start = json.indexOf(marker, parent);
        if (start == -1) return "";
        start += marker.length();
        int end = json.indexOf(endChar, start);
        return json.substring(start, end).replaceAll("\"", "").trim();
    }
}
