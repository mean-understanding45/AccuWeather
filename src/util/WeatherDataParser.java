package util;

import model.CityWeatherData;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataParser {

    public static List<CityWeatherData> getCombinedData(String citiesJson, String weatherJson) {
        List<CityWeatherData> cityWeatherList = new ArrayList<>();
        int index = 0;
        while ((index = citiesJson.indexOf("\"Key\":\"", index)) != -1) {
            CityWeatherData city = new CityWeatherData();

            int keyStart = index + 7;
            int keyEnd = citiesJson.indexOf("\"", keyStart);
            city.key = citiesJson.substring(keyStart, keyEnd);

            city.name = getValue(citiesJson, "\"LocalizedName\":\"", keyEnd);
            city.country = getValue(citiesJson, "\"Country\":{\"ID", keyEnd, "\"LocalizedName\":\"", "\"");
            city.region = getValue(citiesJson, "\"Region\":{\"ID", keyEnd, "\"LocalizedName\":\"", "\"");
            city.timezone = getValue(citiesJson, "\"TimeZone\":", keyEnd, "\"Name\":\"", "\"");

            int rankMarker = citiesJson.indexOf("\"Rank\":", keyEnd);
            int rankStart = rankMarker + "\"Rank\":".length();
            int rankEnd = citiesJson.indexOf(",", rankStart);
            String rankStr = citiesJson.substring(rankStart, rankEnd).trim();
            city.rank = rankStr.isEmpty() ? 0 : Integer.parseInt(rankStr);

            String latitudeStr = getValue(citiesJson, "\"GeoPosition\":", keyEnd, "\"Latitude\":", ",");
            city.latitude = latitudeStr.isEmpty() ? 0.0 : Double.parseDouble(latitudeStr);

            String longitudeStr = getValue(citiesJson, "\"GeoPosition\":", keyEnd, "\"Longitude\":", ",");
            city.longitude = longitudeStr.isEmpty() ? 0.0 : Double.parseDouble(longitudeStr);

            int matchIndex = weatherJson.indexOf("\"Key\":\"" + city.key + "\"");
            if (matchIndex != -1) {
                city.weatherText = getValue(weatherJson, "\"WeatherText\":\"", matchIndex);
                city.isDayTime = getValue(weatherJson, "\"IsDayTime\":", matchIndex, "", ",").equalsIgnoreCase("true");

                String tempCStr = getValue(weatherJson, "\"Temperature\":", matchIndex, "\"Metric\":{\"Value\":", ",");
                city.tempCelsius = tempCStr.isEmpty() ? 0.0 : Double.parseDouble(tempCStr);

                String tempFStr = getValue(weatherJson, "\"Temperature\":", matchIndex, "\"Imperial\":{\"Value\":", ",");
                city.tempFahrenheit = tempFStr.isEmpty() ? 0.0 : Double.parseDouble(tempFStr);

                city.lastUpdatedAt = getValue(weatherJson, "\"LocalObservationDateTime\":\"", matchIndex);
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
