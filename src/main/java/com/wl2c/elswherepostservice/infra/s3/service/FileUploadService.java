package com.wl2c.elswherepostservice.infra.s3.service;

import com.wl2c.elswherepostservice.infra.s3.dto.FileRequest;
import com.wl2c.elswherepostservice.infra.s3.dto.UploadedFile;
import com.wl2c.elswherepostservice.infra.s3.exception.InvalidAccessObjectStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AWSObjectStorageService s3service;

    public ArrayList<UploadedFile> uploadedFiles(List<FileRequest> files) {
        ArrayList<UploadedFile> postFiles = new ArrayList<>();
        for (FileRequest file : files) {
            postFiles.add(uploadFile(file));
        }
        return postFiles;
    }

    public UploadedFile uploadFile(FileRequest file) {
        String originName = file.getOriginalFileName();
        if (originName == null) originName = "";

        String name = originName.substring(0, originName.lastIndexOf("."));
        String ext = originName.substring(originName.lastIndexOf(".") + 1);
        String fileId = makeFileId(name, ext);
        return upload(file, fileId);
    }

    private UploadedFile upload(FileRequest file, String objectName) {
        try {
            s3service.uploadFile(file, objectName);
            return new UploadedFile(objectName, file);
        } catch (Throwable e) {
            throw new InvalidAccessObjectStorageException(e);
        }
    }

    private String makeFileId(String name, String ext) {
        return makeObjName(name, UUID.randomUUID() + "." + ext);
    }

    private String makeObjName(String name, String id) {
        return name + "-" + id;
    }
}