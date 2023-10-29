package ru.rsatu.dbkursach.db.entity;

import lombok.Data;

@Data
public class ScientistView {
    Integer id;
    String first_name;
    String second_name;
    String subject;
    String city;
    String birthday;

    @Override
    public String toString() {
        return String.format("%s %s", first_name, second_name);
    }
}
