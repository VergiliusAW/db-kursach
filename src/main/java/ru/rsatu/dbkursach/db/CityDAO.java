package ru.rsatu.dbkursach.db;

import ru.rsatu.dbkursach.db.entity.CityScientistsView;
import ru.rsatu.dbkursach.db.entity.CityView;

import java.util.List;

public class CityDAO {
    private final DbManager dbManager;

    public CityDAO(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<CityView> getAllCities() {
        String sql = """
                SELECT id, name, region
                FROM city
                """;
        return dbManager.executeSqlReturnList(sql, CityView.class);
    }

    public void deleteCity(Integer id) {
        String sql = """
                DELETE FROM city where id = %s
                """;
        dbManager.executeSqlNoReturn(String.format(sql, id));
    }

    public boolean addNewCity(String name, String region) {
        String sql = """
                INSERT INTO city (name, region)
                VALUE ('%s', '%s')
                """;
        dbManager.executeSqlNoReturn(String.format(sql,
                name,
                region));
        return true;
    }

    public List<CityScientistsView> getCityScientistsViews() {
        String sql = """
                SELECT c.name as city, COUNT(s.id) as scientist_count
                FROM city c
                    JOIN conference_schema.scientist s on c.id = s.city_id
                GROUP BY c.id
                HAVING scientist_count > 0
                """;
        return dbManager.executeSqlReturnList(sql, CityScientistsView.class);
    }
}
