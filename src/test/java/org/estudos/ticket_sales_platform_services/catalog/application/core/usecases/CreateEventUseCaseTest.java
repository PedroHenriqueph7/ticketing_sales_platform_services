package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.CategoryNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.CreateEventUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.CheckProducerExistsOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.CategoryIdOutPort;
import org.estudos.ticket_sales_platform_services.identity.application.core.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class CreateEventUseCaseTest {

    @Mock
    private CategoryIdOutPort categoryIdOutPort;
    @Mock
    private CheckProducerExistsOutPort checkProducerExistsOutPort;
    @Mock
    private EventOutPort eventOutPort;
    @InjectMocks
    private CreateEventUseCase useCase;

    @Test
    void executeRegistersAndReturnsIdWhenCategoryAndProducerExist() {
        EventDomain event = EventRegistrationFixtures.validDraft();
        UUID eventId = UUID.randomUUID();

        when(categoryIdOutPort.existsByCategoryId(event.getCategoryId())).thenReturn(true);
        when(checkProducerExistsOutPort.existsByProducerId(event.getProducerId())).thenReturn(true);
        when(eventOutPort.register(event)).thenReturn(eventId);

        UUID result = useCase.execute(event);
        assertEquals(eventId, result);
        verify(eventOutPort).register(event);
    }

    @Test
    void executeDoesNotCheckProducerOrPersistWhenCategoryIsMissing() {
        EventDomain event = EventRegistrationFixtures.validDraft();

        when(categoryIdOutPort.existsByCategoryId(event.getCategoryId())).thenReturn(false);
        assertThrows(CategoryNotFoundException.class, () -> useCase.execute(event));

        verify(checkProducerExistsOutPort, never()).existsByProducerId(event.getProducerId());
        verify(eventOutPort, never()).register(event);
    }

    @Test
    void executeDoesNotPersistWhenProducerIsMissing() {
        EventDomain event = EventRegistrationFixtures.validDraft();

        when(categoryIdOutPort.existsByCategoryId(event.getCategoryId())).thenReturn(true);
        when(checkProducerExistsOutPort.existsByProducerId(event.getProducerId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(event));
        verify(eventOutPort, never()).register(event);
    }
    @Test
    void executeRejectsNullEventBeforeCallingPorts() {
        assertThrows(InputObjectInvalidException.class, () -> useCase.execute(null));
        verifyNoInteractions(categoryIdOutPort, checkProducerExistsOutPort, eventOutPort);
    }
}