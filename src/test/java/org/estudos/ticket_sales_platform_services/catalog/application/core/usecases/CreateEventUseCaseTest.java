package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.CategoryNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.CheckProducerExistsOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.FindByCategoryIDOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.RegisterEventOutPort;
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
    private FindByCategoryIDOutPort findByCategoryIDOutPort;
    @Mock
    private CheckProducerExistsOutPort checkProducerExistsOutPort;
    @Mock
    private RegisterEventOutPort registerEventOutPort;
    @InjectMocks
    private CreateEventUseCase useCase;

    @Test
    void executeRegistersAndReturnsIdWhenCategoryAndProducerExist() {
        EventDomain event = EventRegistrationFixtures.validDraft();
        UUID eventId = UUID.randomUUID();

        when(findByCategoryIDOutPort.existsByCategoryId(event.getCategoryId())).thenReturn(true);
        when(checkProducerExistsOutPort.existsByProducerId(event.getProducerId())).thenReturn(true);
        when(registerEventOutPort.register(event)).thenReturn(eventId);

        UUID result = useCase.execute(event);
        assertEquals(eventId, result);
        verify(registerEventOutPort).register(event);
    }

    @Test
    void executeDoesNotCheckProducerOrPersistWhenCategoryIsMissing() {
        EventDomain event = EventRegistrationFixtures.validDraft();

        when(findByCategoryIDOutPort.existsByCategoryId(event.getCategoryId())).thenReturn(false);
        assertThrows(CategoryNotFoundException.class, () -> useCase.execute(event));

        verify(checkProducerExistsOutPort, never()).existsByProducerId(event.getProducerId());
        verify(registerEventOutPort, never()).register(event);
    }

    @Test
    void executeDoesNotPersistWhenProducerIsMissing() {
        EventDomain event = EventRegistrationFixtures.validDraft();

        when(findByCategoryIDOutPort.existsByCategoryId(event.getCategoryId())).thenReturn(true);
        when(checkProducerExistsOutPort.existsByProducerId(event.getProducerId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(event));
        verify(registerEventOutPort, never()).register(event);
    }
    @Test
    void executeRejectsNullEventBeforeCallingPorts() {
        assertThrows(InputObjectInvalidException.class, () -> useCase.execute(null));
        verifyNoInteractions(findByCategoryIDOutPort, checkProducerExistsOutPort, registerEventOutPort);
    }
}