package util;

import model.CityWeatherData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVGenerator {
    public void generateCSV(List<CityWeatherData> data, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Name,Country,Region,Timezone,Rank,Latitude,Longitude,WeatherText,IsDayTime,TempCelsius,TempFahrenheit,LastUpdatedAt");
            writer.newLine();
            for (CityWeatherData city : data) {
                writer.write(String.format("%s,%s,%s,%s,%d,%.6f,%.6f,%s,%b,%.2f,%.2f,%s",
                        city.name, city.country, city.region, city.timezone, city.rank,
                        city.latitude, city.longitude, city.weatherText, city.isDayTime,
                        city.tempCelsius, city.tempFahrenheit, city.lastUpdatedAt));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
