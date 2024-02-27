package com.git.backend.daeng_nyang_connect.animal.service;

import com.git.backend.daeng_nyang_connect.animal.dto.request.AnimalRequestDTO;
import com.git.backend.daeng_nyang_connect.animal.dto.response.AnimalResponseDTO;
import com.git.backend.daeng_nyang_connect.animal.entity.*;
import com.git.backend.daeng_nyang_connect.animal.repository.AdoptedAnimalRepository;
import com.git.backend.daeng_nyang_connect.animal.repository.AnimalImageRepository;
import com.git.backend.daeng_nyang_connect.animal.repository.AnimalRepository;
import com.git.backend.daeng_nyang_connect.animal.repository.AnimalScrapRepository;
import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@EnableCaching
public class AnimalServiceImpl  implements AnimalService{
    private final AnimalRepository animalRepository;
    private final AnimalImageRepository animalImageRepository;
    private final AnimalScrapRepository animalScrapRepository;
    private final AnimalImageService animalImageService;
    private final AdoptedAnimalRepository adoptedAnimalRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public Animal addAnimal(AnimalRequestDTO animalRequestDTO, List<MultipartFile> files, String token) {
        // 1. 토큰으로 유저 확인
        User user = checkUserByToken(token);

        // 2. 댕냥이 DB에 저장
        Animal newAnimal = Animal.builder()
                                .user(user)
                                .animalName(animalRequestDTO.getAnimalName())
                                .age(animalRequestDTO.getAge())
                                .gender(animalRequestDTO.getGender())
                                .disease(animalRequestDTO.getDisease())
                                .training(animalRequestDTO.getTraining())
                                .neutering(animalRequestDTO.getParseNeutering())
                                .textEtc(animalRequestDTO.getTextEtc())
                                .textReason(animalRequestDTO.getTextReason())
                                .textEtc(animalRequestDTO.getTextEtc())
                                .healthCheck(animalRequestDTO.getHealthCheck())
                                .breed(animalRequestDTO.getBreed())
                                .kind(animalRequestDTO.getKind())
                                .nurturePeriod(animalRequestDTO.getParseNurturePeriod())
                                .city(animalRequestDTO.getCity())
                                .adoptionStatus(AdoptionStatus.PROGRESS)
                                .createdAt(nowDate())
                                .build();
        animalRepository.save(newAnimal);

        // 3. 이미지를 DB에 저장
        if(files!=null){
            uploadImage(newAnimal, files);
        }

        // 4. return 새로운 댕냥이
        return newAnimal;
    }

    @Override
    public AdoptedAnimal completeAnimal(Long animalId, Long adoptedUserId, String token) {
        if(checkAnimalStatus(animalId).equals(AdoptionStatus.COMPLETED)) {
            throw new IllegalStateException("이미 입양된 동물입니다.");
        }
        // 1. 토큰으로 유저 확인
        User user = checkUserByToken(token);

        User adoptedUser = userRepository.findById(adoptedUserId).orElseThrow(
                ()-> new NoSuchElementException("없는 유저입니다.")
        );

        // 2. 내가 작성한 글이 맞는지 확인
        Animal myAnimal = checkMyBoard(animalId, user);

        // 3. 입양 진행 상태(adoption_status)를 COMPLETED로 변경
        myAnimal.updateAdoptionStatus(AdoptionStatus.COMPLETED);
        animalRepository.save(myAnimal);

        // 4. 해당 댕냥이와 입양한 유저를 함께 입양된 동물(AdoptedAnimal) DB에 추가
        AdoptedAnimal adoptedAnimal = AdoptedAnimal.builder()
                                                    .animal(myAnimal)
                                                    .user(adoptedUser)
                                                    .build();
        adoptedAnimalRepository.save(adoptedAnimal);

        return adoptedAnimal;
    }

    @Override
    public void deleteAnimal(Long animalId, String token) {
        // 1. 토큰으로 유저 확인
        User user = checkUserByToken(token);

        // 2. 내가 작성한 글이 맞는지 확인
        Animal myAnimal = checkMyBoard(animalId, user);

        // 3. 해당 댕냥이 게시글을 DB에서 삭제
        animalRepository.delete(myAnimal);
    }


    @Transactional
    @Override
    public Animal updateAnimal(Long animalId, AnimalRequestDTO animalRequestDTO, String token) {
        // 1. 토큰으로 유저 확인
        User user = checkUserByToken(token);

        // 2. 내가 작성한 글이 맞는지 확인
        Animal myAnimal = checkMyBoard(animalId, user);

        // 3. 댕냥이 게시글 정보를 DB에서 수정
        animalRequestDTO.checkUpdateList(animalRequestDTO, myAnimal);
        Animal updateAnimal = Animal.builder()
                                    .animalId(myAnimal.getAnimalId())
                                    .user(myAnimal.getUser())
                                    .animalName(animalRequestDTO.getAnimalName())
                                    .age(animalRequestDTO.getAge())
                                    .gender(animalRequestDTO.getGender())
                                    .disease(animalRequestDTO.getDisease())
                                    .training(animalRequestDTO.getTraining())
                                    .neutering(animalRequestDTO.getParseNeutering())
                                    .textEtc(animalRequestDTO.getTextEtc())
                                    .textReason(animalRequestDTO.getTextReason())
                                    .textEtc(animalRequestDTO.getTextEtc())
                                    .healthCheck(animalRequestDTO.getHealthCheck())
                                    .breed(animalRequestDTO.getBreed())
                                    .kind(animalRequestDTO.getKind())
                                    .nurturePeriod(animalRequestDTO.getParseNurturePeriod())
                                    .city(animalRequestDTO.getCity())
                                    .adoptionStatus(myAnimal.getAdoptionStatus())
                                    .createdAt(myAnimal.getCreatedAt())
                                    .build();
        animalRepository.save(updateAnimal);

        // 4. 이미지를 DB에 저장
//        if(animalRequestDTO.getFiles()!=null){
//            uploadImage(myAnimal, animalRequestDTO.getFiles());
//        }
        // 5. 수정된 댕냥이 게시글을 반환
        return updateAnimal;
    }


    @Override
    public List<Animal> findAllAnimal() {
        // DB에 저장된 댕냥이 리스트 반환, 없다면 null 반환
        return animalRepository.findAll();
    }

    @Override
    public List<Animal> findAnimalByKind(Kind kind) {
        return animalRepository.findAnimalByKind(kind);
    }

    @Override
    public List<Animal> findAnimalByCity(String city) {
        return animalRepository.findAnimalByCity(city);
    }

    @Override
    public List<Animal> findAnimalByAdoptionStatus(AdoptionStatus adoptionStatus) {
        return animalRepository.findAnimalByAdoptionStatus(adoptionStatus);
    }

    @Override
    public Map<String, String> scrapAnimal(Long animalId, String token) {
        // 1. 토큰으로 유저 확인
        User user = checkUserByToken(token);

        // 2. 게시글 존재 유무 확인
        Animal animalBoard = animalRepository.findById(animalId).orElseThrow(
                () -> new NoSuchElementException("없는 게시글입니다.")
        );

        // 3. 해당 유저가 해당 댕냥이를 이미 스크랩 했는지 확인
        Map<String,String> message = new HashMap<>();

        if(animalScrapRepository.findMyScrapList(user, animalBoard).isPresent()){
            // 3-1. 만약 해당 유저가 해당 댕냥이를 이미 스크랩 했다면 (제거)
            animalScrapRepository.deleteByUser(user);
            message.put("message", animalBoard.getAnimalName() + "이 스크랩 목록에서 삭제되었습니다.");
            return message;
        }

        // 3-2. 해당 유저가 해당 댕냥이를 처음 스크랩 했다면 (추가)
        AnimalScrap myScrap = AnimalScrap.builder()
                .animal(animalBoard)
                .user(user)
                .build();
        animalScrapRepository.save(myScrap);

        message.put("message", animalBoard.getAnimalName() + " 이 스크랩 목록에 추가되었습니다.");
        return message;
    }

    @Override
    public Animal checkMyBoard(Long animalId, User user){
        // 1. 게시글 존재 유무 확인
        Animal myAnimal = animalRepository.findById(animalId).orElseThrow(
                () -> new NoSuchElementException("없는 게시글입니다.")
        );

        // 2. 내가 작성한 게시글이 맞는지 확인
        if(!myAnimal.getUser().equals(user)){
            throw new IllegalArgumentException("다른 유저의 작성 게시글입니다.");
        }
        // 3. 내가 작성한 게시글 반환
        return myAnimal;
    }

    @Override
    public Timestamp nowDate(){
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        return Timestamp.valueOf(currentDateTime);
    }

    @Override
    public void uploadImage(Animal animal, List<MultipartFile> multipartFileList) {
        List<String> imageUrlList = animalImageService.uploadImages(animal.getAnimalName(), multipartFileList);
//        if(animal.getImages() != null && !animal.getImages().isEmpty()) {
//            animalImageRepository.deleteByAnimal(animal);
//        }

        for (String imageUrl : imageUrlList) {
            AnimalImage animalImage = AnimalImage.builder()
                                                .animal(animal)
                                                .url(imageUrl)
                                                .build();
            animalImageRepository.save(animalImage);
        }
    }

    @Override
    public User checkUserByToken(String token){
        String email = tokenProvider.getEmailBytoken(token);
        return userRepository.findByEmail(email).orElseThrow(
                ()-> new NoSuchElementException("없는 유저입니다.")
        );
    }

    @Override
    public AdoptionStatus checkAnimalStatus(Long animalId) {
        Animal animal = animalRepository.findById(animalId).orElseThrow(
                () -> new NullPointerException("없는 동물입니다.")
        );
        return animal.getAdoptionStatus();
    }

    @Override
    public AnimalResponseDTO response(Animal animal) {
        List<AnimalImage> animalImages = animalImageRepository.findByAnimal(animal);
        return new AnimalResponseDTO(animal, animalImages);
    }

    @Override
    public AnimalResponseDTO response(AdoptedAnimal adoptedAnimal) {
        List<AnimalImage> animalImages = animalImageRepository.findByAnimal(adoptedAnimal.getAnimal());
        return new AnimalResponseDTO(adoptedAnimal, animalImages);
    }

    @Override
    public List<AnimalResponseDTO> responseList(List<Animal> animalList) {
        List<AnimalResponseDTO> responseList = new ArrayList<>();

        for (Animal animal : animalList){
            responseList.add(response(animal));
        }

        return responseList;
    }
}
