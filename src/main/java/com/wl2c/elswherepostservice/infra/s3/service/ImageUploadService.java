package com.wl2c.elswherepostservice.infra.s3.service;

import com.wl2c.elswherepostservice.infra.s3.dto.ImageRequest;
import com.wl2c.elswherepostservice.infra.s3.dto.UploadedImage;
import com.wl2c.elswherepostservice.infra.s3.exception.InvalidAccessObjectStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final AWSObjectStorageService s3service;

    public ArrayList<UploadedImage> uploadedImages(List<ImageRequest> images) {
        ArrayList<UploadedImage> postImages = new ArrayList<>();
        for (ImageRequest image : images) {
            postImages.add(uploadImage(image));
        }
        return postImages;
    }

    public UploadedImage uploadImage(ImageRequest image) {
        String originName = image.getOriginalImageName();
        if (originName == null ) originName = "";

        String name = originName.substring(0, originName.lastIndexOf("."));
        String ext = originName.substring(originName.lastIndexOf(".") + 1);
        String imageId = makeImageId(name, ext);
        return upload(image, imageId);
    }

    private UploadedImage upload(ImageRequest image, String objectName) {
        try {
            s3service.uploadImage(image, objectName);
            return new UploadedImage(objectName, image);
        } catch (Throwable e) {
            throw new InvalidAccessObjectStorageException(e);
        }
    }

    private String makeImageId(String name, String ext) {
        return makeObjName(name, UUID.randomUUID() + "." + ext);
    }

    private String makeObjName(String name, String id) {
        return name + "-" + id;
    }
}
