package com.git.backend.daeng_nyang_connect.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.EncryptedPutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsImage;
import com.git.backend.daeng_nyang_connect.user.entity.MyPage;
import com.git.backend.daeng_nyang_connect.user.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class ProfileImgService {

    private final AmazonS3Client amazonS3Client;
    private final MyPageRepository myPageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String buildFileName(String nickName,String originFileName){
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        return "User Profile" + "/" + nickName  + "/" + nickName+randomString;
    }

    public String uploadUserProfile(MyPage page, String nickName, MultipartFile multipartFile) throws FileUploadFailedException {

        String fileName = buildFileName(nickName, Objects.requireNonNull(multipartFile.getOriginalFilename()));

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
        page.setImg(amazonS3Client.getUrl(bucketName, fileName).toString());
        myPageRepository.save(page);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

}
