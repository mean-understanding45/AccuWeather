package service;

import config.ConfigLoader;
import model.CityWeatherData;
import util.WeatherDataParser;

import java.io.IOException;
import java.util.List;

public class WeatherService {
    private static WeatherService instance;
    private ConfigLoader configLoader;
    private GoogleDriveUploader googleDriveUploader;
    private SMTPMailer smtpMailer;
    private List<CityWeatherData> data;
    private WeatherService() throws IOException {
//        this.csvGenerator = new CSVGenerator();
        this.configLoader= ConfigLoader.getInstance();
        this.googleDriveUploader = new GoogleDriveUploader();
        this.smtpMailer=new SMTPMailer();
    }

    public static WeatherService getInstance() throws IOException {
        if(instance==null){
            instance=new WeatherService();
        }
        return instance;
    }

    public List<CityWeatherData> getCityWeatherData() throws Exception {
        TopCitiesWeather topCitiesWeather = new TopCitiesWeather();
        TopCitiesCurrentCondition topCitiesCurrentCondition = new TopCitiesCurrentCondition();
        String citiesJson = topCitiesWeather.fetch();
        String weatherJson = topCitiesCurrentCondition.fetch();
        this.data = WeatherDataParser.getCombinedData(citiesJson, weatherJson);
        return data;
    }

    public String uploadCSVAsGoogleSheet(String outputFile, String sheetName,List<CityWeatherData> data) throws IOException {
        return googleDriveUploader.uploadCSVAsGoogleSheet(outputFile,sheetName,data);
    }

    public void sendEmail(String googleSheetLink) {
        smtpMailer.sendEmail(googleSheetLink);
    }
}
