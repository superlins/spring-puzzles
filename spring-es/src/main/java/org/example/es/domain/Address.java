package org.example.es.domain;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "index-address-#{T(java.time.LocalDate).now().toString()}")
public class Address {

    String city, street;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}