package atdd.station.repository;

import atdd.station.model.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StationRepository {
    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public void setDataSource(final DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("ID");
    }

    public StationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        final Map parameters = new HashMap<>();
        parameters.put("NAME", station.getName());

        final Long stationId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();

        return findById(stationId);
    }

    public Station findById(long id) {
        try {
            return jdbcTemplate.queryForObject("select * from STATION where id = ?",
                    new Object[]{id},
                    ((rs, rowNum) -> new Station(
                            rs.getLong("id"),
                            rs.getString("name"))));

        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public List findAll() {
        return jdbcTemplate.query(
                "select * from STATION",
                (rs, rowNum) -> new Station(
                        rs.getLong("id"),
                        rs.getString("name")));
    }

    public void deleteById(long id) {
        jdbcTemplate.update("delete from STATION where id = ?", id);
    }
}
