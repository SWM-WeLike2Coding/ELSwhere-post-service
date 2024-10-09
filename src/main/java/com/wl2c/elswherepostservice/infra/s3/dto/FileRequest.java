package com.wl2c.elswherepostservice.infra.s3.dto;

import com.wl2c.elswherepostservice.infra.s3.exception.InvalidFileContentTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileRequest {
    private final String originalFileName;
    private final MediaType contentType;
    private final InputStreamSupplier inputStreamSupplier;
    private final Long size;

    public FileRequest(MultipartFile file) {
        this.originalFileName = file.getOriginalFilename();
        this.inputStreamSupplier = file::getInputStream;
        this.size = file.getSize();

        String fileMimeType = file.getContentType();
        if (fileMimeType == null) {
            this.contentType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            try {
                this.contentType = MediaType.parseMediaType(file.getContentType());
            } catch (InvalidMediaTypeException e) {
                throw new InvalidFileContentTypeException(e);
            }
        }
    }

    public InputStream getInputStream() throws Exception {
        return inputStreamSupplier.get();
    }

    public static List<FileRequest> ofList(List<MultipartFile> files) {
        List<FileRequest> fileRequests = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                fileRequests.add(new FileRequest(file));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return fileRequests;
    }

    @FunctionalInterface
    public interface InputStreamSupplier {
        InputStream get() throws Exception;
    }
}
