package ru.rsatu.dbkursach.db.entity;

import lombok.Data;

@Data
public class CityView {
    Integer id;
    String name;
    String region;

    @Override
    public String toString() {
        return String.format("%s. %s, %s", id, name, region);
    }
}
