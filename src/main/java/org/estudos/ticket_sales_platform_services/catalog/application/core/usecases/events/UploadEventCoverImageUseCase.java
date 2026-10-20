package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.ImageExtensionNotAllowedException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.UploadCoverImageInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.FindByEventIdOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.SaveEventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.StorageOutPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class UploadEventCoverImageUseCase implements UploadCoverImageInPort {

    private static final Logger log = LoggerFactory.getLogger(UploadEventCoverImageUseCase.class);

    private final FindByEventIdOutPort findByEventIdOutPort;
    private final StorageOutPort storageOutPort;
    private final SaveEventOutPort saveEventOutPort;

    public UploadEventCoverImageUseCase(FindByEventIdOutPort findByEventIdOutPort, StorageOutPort storageOutPort, SaveEventOutPort saveEventOutPort) {
        this.findByEventIdOutPort = findByEventIdOutPort;
        this.storageOutPort = storageOutPort;
        this.saveEventOutPort = saveEventOutPort;
    }

    private final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    @Override
    public void uploadFile(UUID eventId, String fileName, InputStream fileStream, String contentType, long size) {

        if (size <= 0) { throw new InputObjectInvalidException("Arquivo da imagem não pode estar vazio!");
        }

        if (size > MAX_FILE_SIZE_BYTES) {throw new InputObjectInvalidException("O tamanho da imagem excede o limite máximo permitido (5MB).");}

        validateImageExtension(contentType);

        EventDomain eventDomain = findByEventIdOutPort.findByEventId(eventId).orElseThrow(EventNotFoundException::new);

        String oldImageUrl = eventDomain.getCoverImageUrl();

        String extension = getFileExtension(fileName);
        log.info("Extensão do arquivo identificado!");

        String safeFileName = generateS3ImagePath(eventId, extension);
        log.info("Gerado o Path da image para o S3!");

        String coverUrl = storageOutPort.uploadFile(safeFileName, fileStream, contentType, size);

        eventDomain.setCoverImageUrl(coverUrl);

        try {
            saveEventOutPort.save(eventDomain);
            log.info("Url da imagem salva no S3, persistida no banco com sucesso!");
        } catch (Exception e) {
            storageOutPort.deleteFile(safeFileName);
            throw e;
        }

        deletarOldImage(oldImageUrl);
    }

    private String getFileExtension(String filename) {

        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    private String generateS3ImagePath(UUID eventId, String extension) {
        return String.format("coverImageEvent/%s/cover-%s%s", eventId, UUID.randomUUID(), extension);
    }

    private void validateImageExtension(String contentType) {

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ImageExtensionNotAllowedException();
        }
        log.info("Validado a extensão do arquivo!");
    }

    private void deletarOldImage(String oldImageUrl) {
        if (oldImageUrl != null) {
            try {
                log.info("Apagando Imagem da Capa antiga do evento do bucket S3");
                storageOutPort.deleteFile(oldImageUrl);

            } catch (Exception e) {
                log.warn("Falha não-bloqueante ao excluir imagem anterior do S3: {}", oldImageUrl, e);
            }
        }
    }
}
