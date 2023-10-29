package ru.rsatu.dbkursach.db;

import ru.rsatu.dbkursach.db.entity.DomainView;

import java.util.List;

public class DomainDAO {
    private final DbManager dbManager;

    public DomainDAO(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<DomainView> getDomainViewList() {
        String sql = """
                SELECT id, name FROM domain
                """;
        return dbManager.executeSqlReturnList(sql, DomainView.class);
    }
}
