import model.CityWeatherData;
import service.WeatherService;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            WeatherService weatherService = WeatherService.getInstance();
            List<CityWeatherData> data = weatherService.getCityWeatherData();
            String googleSheetLink = weatherService.uploadCSVAsGoogleSheet("CityWeatherReport.csv","Weather Report",data);
            weatherService.sendEmail(googleSheetLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}