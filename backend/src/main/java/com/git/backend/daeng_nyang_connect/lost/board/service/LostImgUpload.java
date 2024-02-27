package com.git.backend.daeng_nyang_connect.lost.board.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.EncryptedPutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.lost.board.entity.LostImage;
import com.git.backend.daeng_nyang_connect.lost.board.repository.LostImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
public class LostImgUpload {
    private final AmazonS3Client amazonS3Client;
    private final LostImgRepository lostImgRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //lost 게시판 이미지 s3 디렉토리 경로 및 파일 이름 생성
    public String buildFileName(String text,String originFileName){
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        return "lost" + "/" + text  + "/" + text+randomString;
    }

    public List<String> uploadLostImgs(Lost lost, String text, List<MultipartFile> multipartFileList){
        List<String> filenameList = new ArrayList<>();
        if(multipartFileList!=null){
            multipartFileList.forEach(file -> {
                try{
                    String fileName = uploadLostImg(lost, text, file);
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

    public String uploadLostImg(Lost lostId, String text, MultipartFile multipartFile) throws FileUploadFailedException{

        String fileName = buildFileName(text, Objects.requireNonNull(multipartFile.getOriginalFilename()));

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
        LostImage lostImage = LostImage.builder()
                .lost(lostId)
                .url(amazonS3Client.getUrl(bucketName, fileName).toString())
                .build();
        if(multipartFile.isEmpty()){
            return null;
        }
        lostImgRepository.save(lostImage);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    @Transactional
    public List<LostImage> getLostImg(Lost lost){
        List<LostImage> lostImages = lostImgRepository.findByLost(lost)
                .orElseThrow();
        return lostImages;
    }
}
