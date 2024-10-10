import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        String lat = "52.37125";
        String lon = "4.89388";
        int limit = 5;

        HttpClient client = HttpClient.newBuilder().build();

        HttpResponse<String> response = client.send(getHttpRequest(lat, lon), HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseLim = client.send(getHttpRequest(lat, lon, limit),
                HttpResponse.BodyHandlers.ofString());

        showTodayTemperature(response.body());
        System.out.println();
        showLimForecast(responseLim.body(), limit);
    }

    private static HttpRequest getHttpRequest(String lat, String lon) throws URISyntaxException {
        String key = "503150e4-194d-4c37-a786-4cfb0e09758b";
        String uri = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon;
        return HttpRequest.newBuilder()
                .uri(new URI(uri))
                .headers("X-Yandex-Weather-Key", key)
                .GET()
                .build();
    }

    private static HttpRequest getHttpRequest(String lat, String lon, int limit) throws URISyntaxException {
        String key = "503150e4-194d-4c37-a786-4cfb0e09758b";
        String uri = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat +
                "&lon=" + lon + "&limit=" + limit;
        return HttpRequest.newBuilder()
                .uri(new URI(uri))
                .headers("X-Yandex-Weather-Key", key)
                .GET()
                .build();
    }

    private static void showLimForecast(String jsonResponse, int limit) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray forecasts = jsonObject.getJSONArray("forecasts");
        double averageTemp = countAverageTemp(forecasts);
        System.out.println("Средняя температура за " + limit + " дней: " + averageTemp);
    }

    private static void showTodayTemperature(String jsonResponse) {
        System.out.println("JSON-ответ: " + jsonResponse);
        JSONObject jsonObject = new JSONObject(jsonResponse);
        int temperature = jsonObject.getJSONObject("fact").getInt("temp");
        System.out.println("Температура: " + temperature);
    }

    private static double countAverageTemp(JSONArray forecasts) {
        double totalTemp = 0;
        int count = 0;

        for (int i = 0; i < forecasts.length(); i++) {
            JSONObject day = forecasts.getJSONObject(i);
            JSONObject parts = day.getJSONObject("parts");
            JSONObject dayPart = parts.getJSONObject("day");
            totalTemp += dayPart.getDouble("temp_avg");
            count++;
        }
        return totalTemp / count;
    }
}