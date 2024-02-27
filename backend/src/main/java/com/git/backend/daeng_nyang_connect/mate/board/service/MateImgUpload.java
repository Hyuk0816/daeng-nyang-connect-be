package com.git.backend.daeng_nyang_connect.mate.board.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.EncryptedPutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.mate.board.entity.MateImage;
import com.git.backend.daeng_nyang_connect.mate.board.repository.MateImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class MateImgUpload {

    private final AmazonS3Client amazonS3Client;
    private final MateImageRepository mateImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String buildFileName(String title,String originFileName){
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        return "mate" + "/" + title  + "/" + title+randomString;
    }

    public List<String> uploadMateImgs(Mate mate, String title, List<MultipartFile> multipartFileList){
        List<String> filenameList = new ArrayList<>();

        if(multipartFileList!=null){
            multipartFileList.forEach(file -> {
                try{
                    String fileName = uploadMateImg(mate, title, file);
                    filenameList.add(fileName);
                } catch (FileUploadFailedException e) {
                    throw new RuntimeException(e);
                }
            });
        }else{
            return null;
        }
        return filenameList;
    }

    public String uploadMateImg(Mate mate, String title, MultipartFile multipartFile) throws FileUploadFailedException{

        String fileName = buildFileName(title, Objects.requireNonNull(multipartFile.getOriginalFilename()));

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentDisposition("inline");

        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3Client.putObject(new EncryptedPutObjectRequest(bucketName, fileName, inputStream,objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }catch (Exception e){
            log.error("Amazon S3 파일 업로드 실패: {}", e.getMessage(), e);
            throw new FileUploadFailedException("파일 업로드에 실패했습니다");
        }
        MateImage mateImage = MateImage.builder()
                .mate(mate)
                .url(amazonS3Client.getUrl(bucketName, fileName).toString())
                .build();
        if(multipartFile.isEmpty()){
            return null;
        }
        mateImageRepository.save(mateImage);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    @Transactional
    public List<MateImage > getMateImg(Mate mate){
        List<MateImage> mateImages = mateImageRepository.findByMate(mate)
                .orElseThrow();
        return mateImages;
    }

}
