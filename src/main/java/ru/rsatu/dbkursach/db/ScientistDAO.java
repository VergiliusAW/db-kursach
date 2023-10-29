package ru.rsatu.dbkursach.db;

import ru.rsatu.dbkursach.db.entity.ScientistView;

import java.time.LocalDate;
import java.util.List;

public class ScientistDAO {
    private final DbManager dbManager;

    public ScientistDAO(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<ScientistView> getScientistViewList() {
        String sql = """
                SELECT s.id, s.first_name, s.second_name, s2.name as subject, c.name as city, s.birthday
                FROM scientist s
                         JOIN conference_schema.city c on c.id = s.city_id
                         JOIN conference_schema.subject s2 on s2.id = s.subject_id
                """;
        return dbManager.executeSqlReturnList(sql, ScientistView.class);
    }

    public List<ScientistView> getScientistViewsForConference(Integer conferenceId) {
        String sql = """
                SELECT s.id, s.first_name, s.second_name, s2.name as subject, c.name as city, s.birthday
                FROM scientist s
                         JOIN conference_schema.participant p on s.id = p.scientist_id
                         JOIN conference_schema.city c on c.id = s.city_id
                         JOIN conference_schema.subject s2 on s2.id = s.subject_id
                WHERE p.conference_id = %s
                """;
        return dbManager.executeSqlReturnList(String.format(sql, conferenceId), ScientistView.class);
    }

    public List<ScientistView> getScientistViewsForSubject(Integer subjectId) {
        String sql = """
                SELECT s.id, s.first_name, s.second_name, s2.name as subject, c.name as city, s.birthday
                from scientist s
                         JOIN conference_schema.city c on c.id = s.city_id
                         JOIN conference_schema.subject s2 on s2.id = s.subject_id
                WHERE subject_id = %s
                """;
        return dbManager.executeSqlReturnList(String.format(sql, subjectId), ScientistView.class);
    }

    public void deleteScientist(Integer id) {
        String sql = """
                DELETE FROM scientist WHERE id = %s
                """;
        dbManager.executeSqlNoReturn(String.format(sql, id));
    }

    public boolean addNewScientist(Integer city_id, Integer subject_id, String first_name, String second_name, LocalDate birthday) {
        String sql = """
                INSERT INTO scientist (city_id, subject_id, first_name, second_name, birthday)
                VALUE (%s, %s, '%s', '%s', '%s')
                """;
        dbManager.executeSqlNoReturn(String.format(sql,
                city_id,
                subject_id,
                first_name,
                second_name,
                birthday));
        return true;
    }
}
