package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import java.io.InputStream;

public interface StorageOutPort {

    String uploadFile(String fileName, InputStream inputStream, String contentType, long contentLength);

}
