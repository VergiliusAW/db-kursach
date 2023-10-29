package ru.rsatu.dbkursach.db.entity;

import lombok.Data;

@Data
public class DomainView {
    Integer id;
    String name;

    @Override
    public String toString() {
        return String.format("%s. %s", id, name);
    }
}
