package service;

import config.ConfigLoader;
import model.CityWeatherData;
import parser.WeatherParser;
import util.CSVGenerator;
import util.HttpClient;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class WeatherService {
    private static WeatherService instance;
    private CSVGenerator csvGenerator;
    private ConfigLoader configLoader;
    private MinimalGoogleDriveUploader minimalGoogleDriveUploader;
    private SMTPMailer smtpMailer;
    private List<CityWeatherData> data;
    private WeatherService() throws IOException {
        this.csvGenerator = new CSVGenerator();
        this.configLoader= ConfigLoader.getInstance();
        this.minimalGoogleDriveUploader = new MinimalGoogleDriveUploader();
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
        this.data = WeatherParser.getCombinedData(citiesJson, weatherJson);
        return data;
    }

    public void generateCSV(List<CityWeatherData> data,String fileName){
        csvGenerator.generateCSV(data,fileName);
    }

    public String uploadCSVAsGoogleSheet(String outputFile, String sheetName) throws IOException {
        return minimalGoogleDriveUploader.uploadCSVAsGoogleSheet(outputFile,sheetName);
    }

    public void sendEmailWithAttachment(String outputFile, String googleSheetLink) {
         smtpMailer.sendEmailWithAttachment(outputFile,googleSheetLink);
    }
}
