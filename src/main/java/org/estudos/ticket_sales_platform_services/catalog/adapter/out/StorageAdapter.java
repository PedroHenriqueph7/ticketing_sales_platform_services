package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import lombok.extern.slf4j.Slf4j;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.exceptions.StorageIntegrationException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.StorageOutPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Slf4j
@Component
public class StorageAdapter implements StorageOutPort {

    private final S3Client s3Client;
    private final String bucketName;

    public StorageAdapter(S3Client s3Client, @Value("${aws.s3.bucket.name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String contentType, long contentLength) {

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            log.info("Upload Concluido com Sucesso no bucket S3");

            return s3Client.utilities()
                    .getUrl(b -> b.bucket(bucketName).key(fileName))
                    .toExternalForm();

        } catch (Exception e) {
            log.error("Erro detalhado no S3: ", e);
            throw new StorageIntegrationException("Falha ao realizar upload para o S3", e);

        }
    }

    @Override
    public void deleteFile(String fileName) {

        String key = extractKey(fileName);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Arquivo excluído do S3 com sucesso: {}", key);

        } catch (Exception e) {
            log.error("Erro ao excluir arquivo no S3 (key={}): ", key, e);
            throw new StorageIntegrationException("Falha ao excluir arquivo no S3", e);
        }
    }

    private String extractKey(String fileKeyOrUrl) {

        if (fileKeyOrUrl == null) return null;

        if (fileKeyOrUrl.contains("coverImageEvent/")) {
            return fileKeyOrUrl.substring(fileKeyOrUrl.indexOf("coverImageEvent/"));
        }
        return fileKeyOrUrl;
    }
}
