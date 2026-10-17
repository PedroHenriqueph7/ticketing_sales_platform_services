package org.estudos.ticket_sales_platform_services.catalog.application.ports.in;

import java.io.InputStream;
import java.util.UUID;

public interface UploadCoverImageInPort {

    void uploadFile(UUID eventId, String fileName, InputStream fileStream, String contentType, long size);
}
