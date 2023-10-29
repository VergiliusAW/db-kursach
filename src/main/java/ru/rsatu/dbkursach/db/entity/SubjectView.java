package ru.rsatu.dbkursach.db.entity;

import lombok.Data;

@Data
public class SubjectView {
    Integer id;
    String name;
    String domain;
    Integer scientist_count;

    @Override
    public String toString() {
        return String.format("%s. %s, %s", id, name, domain);
    }
}
