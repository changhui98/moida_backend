package com.peopleground.moida.place.application;

import com.peopleground.moida.place.infrastructure.GooglePlaceClient;
import com.peopleground.moida.place.presentation.dto.response.PlaceSuggestionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final GooglePlaceClient googlePlaceClient;

    public List<PlaceSuggestionResponse> searchAutocomplete(String query) {
        String keyword = query == null ? "" : query.trim();
        if (keyword.length() < 2) {
            return List.of();
        }
        return googlePlaceClient.autocomplete(keyword);
    }
}
