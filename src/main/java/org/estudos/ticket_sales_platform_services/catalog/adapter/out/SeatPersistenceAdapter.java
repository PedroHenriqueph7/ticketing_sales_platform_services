package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.SeatDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.SaveSeatsOutPort;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class SeatPersistenceAdapter implements SaveSeatsOutPort {

    private final JdbcTemplate jdbcTemplate;

    public SeatPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveAllInBatches(List<SeatDomain> seats) {

        String sql = "INSERT INTO tb_seat (id, ticket_batch_id, code, status) VALUES (?, ?, ?, ?)";

        int batchSize = 500;

        for (int i = 0; i < seats.size(); i += batchSize) {

            List<SeatDomain> batchChunk = seats.subList(i, Math.min(i + batchSize, seats.size()));

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    SeatDomain seat = batchChunk.get(i);
                    ps.setObject(1, seat.getId()); // UUIDv7 gerado na aplicação
                    ps.setObject(2, seat.getTicketBatchId());
                    ps.setString(3, seat.getCode());
                    ps.setString(4, seat.getStatus().name());
                }

                @Override
                public int getBatchSize() {
                    return batchChunk.size();
                }
            });
        }
    }
}
