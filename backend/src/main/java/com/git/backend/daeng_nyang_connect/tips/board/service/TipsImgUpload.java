package com.git.backend.daeng_nyang_connect.tips.board.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.EncryptedPutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.git.backend.daeng_nyang_connect.exception.EmptyFileException;
import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsImage;
import com.git.backend.daeng_nyang_connect.tips.board.repository.TipsImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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
public class TipsImgUpload {

    private final AmazonS3Client amazonS3Client;
    private final TipsImageRepository tipsImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //tips 게시판 이미지 s3 디렉토리 경로 및 파일 이름 생성
    public String buildFileName(String title,String originFileName){
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        return "tips" + "/" + title  + "/" + title+randomString;
    }

    public List<String> uploadTipsImgs(Tips tips, String title,List<MultipartFile> multipartFileList){
        List<String> filenameList = new ArrayList<>();
        if(multipartFileList !=null){
            multipartFileList.forEach(file -> {
                try{
                    String fileName = uploadTipsImg(tips, title, file);
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

    public String uploadTipsImg(Tips tipsId, String title, MultipartFile multipartFile) throws FileUploadFailedException{

        String fileName = buildFileName(title,Objects.requireNonNull(multipartFile.getOriginalFilename()));

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
        TipsImage tipsImage = TipsImage.builder()
                .tips(tipsId)
                .url(amazonS3Client.getUrl(bucketName, fileName).toString())
                .build();

        if(multipartFile.isEmpty()){
            return null;
        }
        tipsImageRepository.save(tipsImage);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    public String uploadModifyTipsImg(Tips tipsId, String title, MultipartFile multipartFile) throws FileUploadFailedException{

        String fileName = buildFileName(title,Objects.requireNonNull(multipartFile.getOriginalFilename()));

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

        if(multipartFile.isEmpty()){
            return null;
        }

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    @Transactional
    public List<TipsImage > getTipsImg(Tips tips){
        List<TipsImage > tipsImgs = tipsImageRepository.findByTips(tips)
                .orElseThrow();
        return tipsImgs;
    }

}
