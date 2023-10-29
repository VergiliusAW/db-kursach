package ru.rsatu.dbkursach.db;

import ru.rsatu.dbkursach.db.entity.ConferenceView;

import java.time.LocalDate;
import java.util.List;

public class ConferenceDAO {
    private final DbManager dbManager;

    public ConferenceDAO(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<ConferenceView> getConferenceViewList() {
        String sql = """
                SELECT con.id, con.name, c.name AS city, s.name AS subject, con.start_date, con.finish_date
                FROM conference con
                         JOIN conference_schema.city c on con.city_id = c.id
                         JOIN conference_schema.subject s on s.id = con.subject_id
                """;
        List<ConferenceView> view = dbManager.executeSqlReturnList(sql, ConferenceView.class);
        return view;
    }

    public List<ConferenceView> getConferenceViewsForScientist(Integer scientistId) {
        String sql = """
                SELECT con.id, con.name, c.name AS city, s.name AS subject, con.start_date, con.finish_date
                FROM conference con
                         JOIN conference_schema.participant p on con.id = p.conference_id
                         JOIN conference_schema.city c on con.city_id = c.id
                         JOIN conference_schema.subject s on s.id = con.subject_id
                WHERE p.scientist_id = %s
                """;
        return dbManager.executeSqlReturnList(String.format(sql, scientistId), ConferenceView.class);
    }

    public List<ConferenceView> getConferenceViewsForCity(Integer cityId) {
        String sql = """
                SELECT con.id, con.name, c.name AS city, s.name AS subject, con.start_date, con.finish_date
                FROM conference con
                         JOIN conference_schema.city c on con.city_id = c.id
                         JOIN conference_schema.subject s on s.id = con.subject_id
                WHERE con.city_id = %s
                """;
        return dbManager.executeSqlReturnList(String.format(sql, cityId), ConferenceView.class);
    }

    public void deleteConference(Integer id) {
        String sql = """
                DELETE FROM conference WHERE id = %s
                """;
        dbManager.executeSqlNoReturn(String.format(sql, id));
    }

    public boolean addNewConference(Integer city_id,
                                    Integer subject_id,
                                    String name,
                                    LocalDate start_date,
                                    LocalDate finish_date) {
        String sql = """
                INSERT INTO conference (city_id, subject_id, name, start_date, finish_date)
                VALUE (%s, %s, '%s', '%s', '%s')
                """;
        dbManager.executeSqlNoReturn(String.format(sql,
                city_id,
                subject_id,
                name,
                start_date,
                finish_date));
        return true;
    }

    public List<ConferenceView> getAvailableConferencesForScientist(Integer id) {
        String sql = """
                SELECT con.id, con.name, c.name AS city, s.name AS subject, con.start_date, con.finish_date
                FROM conference con
                    JOIN conference_schema.city c on con.city_id = c.id
                    JOIN conference_schema.subject s on s.id = con.subject_id
                    JOIN conference_schema.scientist s2 on s.id = s2.subject_id
                WHERE s2.id = %s and s2.id not in (select p.scientist_id from participant p where p.conference_id = con.id)
                """;
        return dbManager.executeSqlReturnList(String.format(sql, id), ConferenceView.class);
    }
}
