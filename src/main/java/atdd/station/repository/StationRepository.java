package atdd.station.repository;

import atdd.station.model.Station;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StationRepository {
    private final JdbcTemplate jdbcTemplate;

    public StationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Station> save(Station station) {
        int stationId = jdbcTemplate.update("insert into STATION (name) values (?)", station.getName());

        return findById(stationId);
    }

    public Optional<Station> findById(long id) {
        RowMapper<Station> rowMapper = ((rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));

        try {
            return Optional.of(jdbcTemplate.queryForObject("select * from STATION where id = ?",
                    new Object[]{id},
                    rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.ofNullable(null);
        }
    }

    public List findAll() {
        RowMapper<Station> rowMapper = ((rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));

        return jdbcTemplate.query(
                "select * from STATION",
                rowMapper);
    }

    public void deleteById(long id) {
        jdbcTemplate.update("delete from STATION where id = ?", id);
    }
}
