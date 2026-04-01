package com.peopleground.moida.user.infrastructure;

import com.peopleground.moida.global.exception.ApiErrorCode;
import com.peopleground.moida.global.exception.AppException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GoogleGeocodingClient implements GeocodingClient {

    @Value("${google.maps.api-key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create(
        "https://maps.googleapis.com"
    );

    @Override
    public GeoPoint convert(String address) {

        Map response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/maps/api/geocode/json")
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .build())
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        if (response == null) {
            throw new AppException(ApiErrorCode.EXTERNAL_API_ERROR);
        }

        if (!"OK".equals(response.get("status"))) {
            throw new AppException(ApiErrorCode.ADDRESS_CONVERT_FAILED);
        }

        List results = (List) response.get("results");
        Map first =  (Map) results.get(0);
        Map geometry = (Map) first.get("geometry");
        Map location = (Map) geometry.get("location");

        double lat = ((Number) location.get("lat")).doubleValue();
        double lng = ((Number) location.get("lng")).doubleValue();

        return new GeoPoint(lat, lng);
    }
}
