package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCardEventsUseCaseTest {

    @Mock
    private EventOutPort eventOutPort;

    @Test
    void delegatesFiltersAndPaginationToOutputPort() {
        Long categoryId = 42L;
        PageRequest pageable = PageRequest.of(0, 12);
        Page<EventCard> expected = Page.empty(pageable);
        when(eventOutPort.listCardEvents("São Paulo", categoryId, pageable)).thenReturn(expected);

        Page<EventCard> result = new ListCardEventsUseCase(eventOutPort).execute("São Paulo", categoryId, pageable);

        assertSame(expected, result);
        verify(eventOutPort).listCardEvents("São Paulo", categoryId, pageable);
    }
}
