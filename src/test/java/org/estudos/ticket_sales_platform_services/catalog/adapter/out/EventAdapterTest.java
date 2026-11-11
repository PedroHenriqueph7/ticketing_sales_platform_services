package org.estudos.ticket_sales_platform_services.catalog.adapter.out;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.CategoryEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.CategoryRepository;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.EventRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class EventAdapterTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private EventAdapter adapter;

    @Test
    void listCardEventsDelegatesFiltersAndPagination() {
        Long categoryId = 42L;
        PageRequest pageable = PageRequest.of(1, 12);
        Page<EventCard> expected = Page.empty(pageable);
        when(eventRepository.findPublishedEventsForCatalog("São Paulo", categoryId, pageable)).thenReturn(expected);

        assertSame(expected, adapter.listCardEvents("São Paulo", categoryId, pageable));
        verify(eventRepository).findPublishedEventsForCatalog("São Paulo", categoryId, pageable);
    }

    @Test
    void registerAssignsCategoryProxyPersistsAndReturnsGeneratedId() {
        EventDomain domain = EventRegistrationFixtures.validDraft();
        CategoryEntity categoryProxy = org.mockito.Mockito.mock(CategoryEntity.class);
        UUID generatedId = UUID.randomUUID();
        when(categoryRepository.getReferenceById(domain.getCategoryId())).thenReturn(categoryProxy);
        when(eventRepository.save(any(EventEntity.class))).thenAnswer(invocation -> {
            EventEntity entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", generatedId);
            return entity;
        });
        UUID result = adapter.register(domain);
        ArgumentCaptor<EventEntity> captor = ArgumentCaptor.forClass(EventEntity.class);
        verify(eventRepository).save(captor.capture());
        assertSame(categoryProxy, captor.getValue().getCategory());
        assertEquals(EventStatus.DRAFT, captor.getValue().getStatus());
        assertEquals(generatedId, result);
    }

    @Test
    void registerDoesNotPersistWhenCategoryReferenceIsNull() {
        EventDomain domain = EventRegistrationFixtures.validDraft();
        when(categoryRepository.getReferenceById(domain.getCategoryId())).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> adapter.register(domain));
        verify(eventRepository, never()).save(any());
    }
}