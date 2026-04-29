package com.peopleground.moida.place.application.port;

import com.peopleground.moida.place.presentation.dto.response.PlaceSuggestionResponse;
import java.util.List;

public interface PlaceAutocompletePort {

    List<PlaceSuggestionResponse> autocomplete(String query);
}
