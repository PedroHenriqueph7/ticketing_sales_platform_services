package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases;

import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.exceptions.StorageIntegrationException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.ImageExtensionNotAllowedException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.UploadEventCoverImageUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.FindByEventIdOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.SaveEventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.StorageOutPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadEventCoverImageUseCaseTest {

    @Mock
    private FindByEventIdOutPort findByEventIdOutPort;

    @Mock
    private StorageOutPort storageOutPort;

    @Mock
    private SaveEventOutPort saveEventOutPort;

    @InjectMocks
    private UploadEventCoverImageUseCase useCase;

    @Test
    void uploadFileSuccessfullyUploadsAndSavesCoverUrlWhenParametersAreValid() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        String fileName = "banner.png";
        InputStream fileStream = new ByteArrayInputStream("image-content".getBytes());
        String contentType = "image/png";
        long size = 1024L;
        String uploadedUrl = "https://s3.amazonaws.com/bucket/coverImageEvent/" + eventId + "/cover-test.png";

        EventDomain eventDomain = EventRegistrationFixtures.validDraft();
        when(findByEventIdOutPort.findByEventId(eventId)).thenReturn(Optional.of(eventDomain));
        when(storageOutPort.uploadFile(anyString(), eq(fileStream), eq(contentType), eq(size))).thenReturn(uploadedUrl);

        // Act
        useCase.uploadFile(eventId, fileName, fileStream, contentType, size);

        // Assert
        ArgumentCaptor<String> s3KeyCaptor = ArgumentCaptor.forClass(String.class);
        verify(storageOutPort).uploadFile(s3KeyCaptor.capture(), eq(fileStream), eq(contentType), eq(size));
        String generatedKey = s3KeyCaptor.getValue();
        assertTrue(generatedKey.startsWith("coverImageEvent/" + eventId + "/cover-"));
        assertTrue(generatedKey.endsWith(".png"));

        assertEquals(uploadedUrl, eventDomain.getCoverImageUrl());
        verify(saveEventOutPort).save(eventDomain);
    }

    @Test
    void uploadFileThrowsInputObjectInvalidExceptionWhenSizeIsZeroOrNegative() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        InputStream fileStream = new ByteArrayInputStream(new byte[0]);

        // Act & Assert
        InputObjectInvalidException exception = assertThrows(
                InputObjectInvalidException.class,
                () -> useCase.uploadFile(eventId, "banner.png", fileStream, "image/png", 0L)
        );

        // Assert
        assertEquals("Arquivo da imagem não pode estar vazio!", exception.getMessage());
        verifyNoInteractions(findByEventIdOutPort, storageOutPort, saveEventOutPort);
    }

    @Test
    void uploadFileThrowsInputObjectInvalidExceptionWhenSizeExceedsMaximumAllowed() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        InputStream fileStream = new ByteArrayInputStream(new byte[10]);
        long oversized = (5 * 1024 * 1024) + 1; // 5MB + 1 byte

        // Act & Assert
        InputObjectInvalidException exception = assertThrows(
                InputObjectInvalidException.class,
                () -> useCase.uploadFile(eventId, "banner.png", fileStream, "image/png", oversized)
        );

        // Assert
        assertEquals("O tamanho da imagem excede o limite máximo permitido (5MB).", exception.getMessage());
        verifyNoInteractions(findByEventIdOutPort, storageOutPort, saveEventOutPort);
    }

    @Test
    void uploadFileThrowsImageExtensionNotAllowedExceptionWhenContentTypeIsDisallowed() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        InputStream fileStream = new ByteArrayInputStream("pdf-content".getBytes());
        String disallowedContentType = "application/pdf";
        long size = 500L;

        // Act & Assert
        assertThrows(
                ImageExtensionNotAllowedException.class,
                () -> useCase.uploadFile(eventId, "document.pdf", fileStream, disallowedContentType, size)
        );

        // Assert
        verifyNoInteractions(findByEventIdOutPort, storageOutPort, saveEventOutPort);
    }

    @Test
    void uploadFileThrowsEventNotFoundExceptionWhenEventDoesNotExist() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        InputStream fileStream = new ByteArrayInputStream("image-content".getBytes());
        String contentType = "image/jpeg";
        long size = 500L;

        when(findByEventIdOutPort.findByEventId(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                EventNotFoundException.class,
                () -> useCase.uploadFile(eventId, "photo.jpg", fileStream, contentType, size)
        );

        // Assert
        verify(findByEventIdOutPort).findByEventId(eventId);
        verifyNoInteractions(storageOutPort, saveEventOutPort);
    }

    @Test
    void uploadFilePropagatesExceptionWhenStorageIntegrationFails() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        InputStream fileStream = new ByteArrayInputStream("image-content".getBytes());
        String contentType = "image/webp";
        long size = 500L;

        EventDomain eventDomain = EventRegistrationFixtures.validDraft();
        when(findByEventIdOutPort.findByEventId(eventId)).thenReturn(Optional.of(eventDomain));
        when(storageOutPort.uploadFile(anyString(), eq(fileStream), eq(contentType), eq(size)))
                .thenThrow(new StorageIntegrationException("Falha ao realizar upload para o S3", new RuntimeException("S3 error")));

        // Act & Assert
        assertThrows(
                StorageIntegrationException.class,
                () -> useCase.uploadFile(eventId, "photo.webp", fileStream, contentType, size)
        );

        // Assert
        verify(saveEventOutPort, never()).save(any());
    }
}
