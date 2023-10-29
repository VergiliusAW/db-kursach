package ru.rsatu.dbkursach.db.entity;

import lombok.Data;

@Data
public class ConferenceView {
    Integer id;
    String name;
    String city;
    String subject;
    String start_date;
    String finish_date;

    @Override
    public String toString() {
        return String.format("%s, %s, %s - %s", name, city, start_date, finish_date);
    }
}
