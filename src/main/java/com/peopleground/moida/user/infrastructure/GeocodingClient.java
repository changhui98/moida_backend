package com.peopleground.moida.user.infrastructure;

public interface GeocodingClient {

    record GeoPoint(double latitude, double longitude){

    }

    GeoPoint convert(String address);

}
