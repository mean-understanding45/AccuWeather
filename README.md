# AccuWeather Reporter
A Java application that fetches weather data from the AccuWeather API, generates CSV reports, and uploads them to Google Drive as Google Sheets.

## Features
- Fetches weather data for top cities.
- Generates a CSV report.
- Uploads the report to Google Sheets.
- Sends the report via email with a link to the Google Sheet.


# AccuWeather API
api.key=YOUR_ACCUWEATHER_API_KEY

# Output
output.file=CityWeatherReport.csv

# SMTP Configuration (optional)
smtp.server=smtp.example.com
smtp.port=587
smtp.username=your_email@example.com
smtp.password=your_password
smtp.to=recipient@example.com
smtp.subject=Weather Report
smtp.body=Please find attached the latest weather report.
Compile the Java files or build with your preferred build tool
Usage
Run the application with:

bash
CopyInsert in Terminal
java -cp ".:./resources" Main
Or use the provided shell script: