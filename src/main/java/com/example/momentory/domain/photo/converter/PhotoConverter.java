package com.example.momentory.domain.photo.converter;

import com.example.momentory.domain.photo.dto.PhotoRequestDto;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.entity.Visibility;
import com.example.momentory.domain.user.entity.User;

public class PhotoConverter {

    public static Photo uploadToPhoto(PhotoRequestDto.PhotoUpload req, User user) {
        return Photo.builder()
                .user(user)
                .imageName(req.getImageName())
                .imageUrl(req.getImageUrl())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .address(req.getCityName())
                .memo(req.getMemo())
                .visibility(req.getVisibility() != null && req.getVisibility() ? Visibility.PUBLIC : Visibility.PRIVATE)
                .build();
    }

    public static PhotoReseponseDto.PhotoResponse toPhotoResponse(Photo photo) {
        return PhotoReseponseDto.PhotoResponse.builder()
                .photoId(photo.getPhotoId())
                .imageName(photo.getImageName())
                .imageUrl(photo.getImageUrl())
                .latitude(photo.getLatitude())
                .longitude(photo.getLongitude())
                .address(photo.getAddress())
                .memo(photo.getMemo())
                .visibility(photo.getVisibility())
                .createdAt(photo.getCreatedAt())
                .build();
    }
}
