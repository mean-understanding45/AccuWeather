package model;

public class CityWeatherData {
    public String key;
    public String name;
    public String country;
    public String region;
    public String timezone;
    public int rank;
    public double latitude;
    public double longitude;
    public String weatherText;
    public boolean isDayTime;
    public double tempCelsius;
    public double tempFahrenheit;
    public String lastUpdatedAt;

    public String toString() {
        return String.format(
                "Name: %s | Country: %s | Region: %s | Timezone: %s | Rank: %d\nLatitude: %.2f | Longitude: %.2f\nWeather: %s | Daytime: %s\nTemp: %.1f°C / %.1f°F | Last Updated: %s\n",
                name, country, region, timezone, rank, latitude, longitude, weatherText, isDayTime, tempCelsius, tempFahrenheit, lastUpdatedAt
        );
    }
}
