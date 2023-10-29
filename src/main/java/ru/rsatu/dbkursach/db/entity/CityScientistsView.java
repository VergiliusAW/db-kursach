package ru.rsatu.dbkursach.db.entity;

import lombok.Data;

@Data
public class CityScientistsView {
    String city;
    Integer scientist_count;
}
