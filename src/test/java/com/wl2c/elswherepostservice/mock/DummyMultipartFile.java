package com.wl2c.elswherepostservice.mock;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DummyMultipartFile implements MultipartFile {

    private final String parameterName;

    private final String originalFileName;

    private final String contentType;

    public DummyMultipartFile(@NotNull String parameterName, String originalFileName, String contentType) {
        this.parameterName = parameterName;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return parameterName;
    }

    @Override
    public String getOriginalFilename() {
        return originalFileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return InputStream.nullInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }
}
