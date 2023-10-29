package ru.rsatu.dbkursach.db;

public class ParticipantDAO {
    private final DbManager dbManager;

    public ParticipantDAO(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean registerScientistToConference(Integer scientist_id, Integer conference_id, Integer count_reports) {
        String sql = """
                INSERT INTO participant (conference_id, scientist_id, count_reports)
                VALUE (%s, %s, %s)
                """;
        dbManager.executeSqlNoReturn(String.format(sql,
                conference_id,
                scientist_id,
                count_reports));
        return true;
    }
}
