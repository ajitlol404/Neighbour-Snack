package com.akn.ns.neighbour_snack_be.service;

import com.akn.ns.neighbour_snack_be.dto.FileUploadResponseDto;
import com.akn.ns.neighbour_snack_be.utility.ImageCategory;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadResponseDto storeFile(ImageCategory imageCategory, MultipartFile file);

    void deleteFile(ImageCategory imageCategory, String fileId);

}
