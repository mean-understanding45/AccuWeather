import config.ConfigLoader;
import model.CityWeatherData;
import service.MinimalGoogleDriveUploader;
import service.SMTPMailer;
import service.WeatherService;
import util.CSVGenerator;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            WeatherService weatherService = WeatherService.getInstance();
            List<CityWeatherData> data = weatherService.getCityWeatherData();
            weatherService.generateCSV(data,"CityWeatherReport.csv");
            String googleSheetLink = weatherService.uploadCSVAsGoogleSheet("CityWeatherReport.csv","Weather Report");
            weatherService.sendEmailWithAttachment("CityWeatherReport.csv",googleSheetLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}