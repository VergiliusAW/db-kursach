package ru.rsatu.dbkursach.db;

import lombok.SneakyThrows;
import ru.rsatu.dbkursach.db.entity.Subject;
import ru.rsatu.dbkursach.db.entity.SubjectView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    private final DbManager dbManager;

    public SubjectDAO(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public void printSubjectTable() {
        ResultSet rs = dbManager.executeSql("SELECT * FROM subject");
        try {
            while (rs.next()) {
                System.out.printf("%s, %s, %s\n",
                        rs.getInt("id"),
                        rs.getInt("domain_id"),
                        rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<Subject> getAllSubjects() {
        ResultSet rs = dbManager.executeSql("SELECT * FROM subject");
        List<Subject> subjects = new ArrayList<>();
        while (rs.next()) {
            subjects.add(dbManager.rsToObject(rs, Subject.class));
        }
        return subjects;
    }

    public List<SubjectView> getSubjectViewList() {
        String sql = """
                SELECT s.id,
                       s.name,
                       d.name                             as domain,
                       COALESCE((SELECT COUNT(s2.id)
                                 FROM subject
                                          JOIN conference_schema.scientist s2 on subject.id = s2.subject_id
                                 WHERE subject.id = s.id
                                 GROUP BY subject.id), 0) AS scientist_count
                FROM subject s
                         JOIN conference_schema.domain d on d.id = s.domain_id
                """;
        return dbManager.executeSqlReturnList(sql, SubjectView.class);
    }

    public void deleteSubject(Integer id) {
        String sql = """
                DELETE FROM subject WHERE id = %s
                """;
        dbManager.executeSqlNoReturn(String.format(sql, id));
    }

    public boolean addNewSubject(String name, Integer domain_id) {
        String sql = """
                INSERT INTO subject (domain_id, name)
                VALUE (%s, '%s')
                """;
        dbManager.executeSqlNoReturn(String.format(sql,
                domain_id,
                name));
        return true;
    }
}
