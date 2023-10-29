package ru.rsatu.dbkursach.db.entity;

import lombok.Data;

@Data
public class Subject {
    Integer id;
    Integer domain_id;
    String name;
}
