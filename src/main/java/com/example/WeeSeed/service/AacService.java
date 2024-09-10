package com.example.WeeSeed.service;


import com.example.WeeSeed.FileName;
//import com.example.WeeSeed.ImageAi.ImageChecker;
import com.example.WeeSeed.ImageAi.ImageLoader;
import com.example.WeeSeed.SFTPService;
import com.example.WeeSeed.dto.AacDto;
import com.example.WeeSeed.dto.DefaultImageDto;
import com.example.WeeSeed.entity.AacCard;
import com.example.WeeSeed.entity.DefaultImage;
import com.example.WeeSeed.repository.AacRepository;
import com.example.WeeSeed.repository.ChildRepository;
import com.example.WeeSeed.repository.DefaultImageRepository;
import com.example.WeeSeed.repository.UserInfoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.ProCamTransformer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.ResNet50;
import org.hibernate.boot.model.internal.XMLContext;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AacService {
    private static final int IMAGE_WIDTH = 28;
    private static final int IMAGE_HEIGHT = 28;
    private static final int IMAGE_CHANNELS = 3;

    private final AacRepository aacRepository;

    private final UserInfoRepository userInfoRepository;



    @Value("${upload.directory}")
    private String uploadDirectory;
    @Value("${raspberry.pi.url}")
    private String raspberryPiUrl;
    private final SFTPService sftpService;

    @PostConstruct
    public void init() throws IOException {

    }

    public String saveAACCard(MultipartFile image, String cardName, MultipartFile audio,
                            String color, String childCode,String constructorId,int share)
            throws IOException {
        String SuitableState ="";
        // Create directory if not exists
        String imageFormat = "";
        String imageFileName = image.getOriginalFilename();
        int imageI = imageFileName.lastIndexOf('.');
        if (imageI > 0) {
            imageFormat = imageFileName.substring(imageI + 1);
        }

        String voiceFormat = "";
        String voiceFileName = audio.getOriginalFilename();
        int voiceI = voiceFileName.lastIndexOf('.');
        if (voiceI > 0) {
            voiceFormat = voiceFileName.substring(voiceI + 1);
        }

//        try {
//            // MultipartFile image을 File imageFile로 변경
//            File tempFile = File.createTempFile("upload", image.getOriginalFilename());
//            image.transferTo(tempFile);
//            INDArray imageAi = ImageLoader.loadImage(tempFile, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_CHANNELS);
//            boolean isSuitable = ImageChecker.isSuitable(imageAi);
//
//            if (!isSuitable) {
//                System.out.println("이미지 부적합");
//                SuitableState = "not suitable";
//            } else {
//                System.out.println("이미지 적합");
//                SuitableState = "suitable";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Error loading or preprocessing the image: " + e.getMessage());
//        }


        FileName imageName = new FileName(image.getOriginalFilename());
        FileName audioName = new FileName(audio.getOriginalFilename());
        String imageUrl = imageName.getFileName()+"."+imageFormat;
        String voiceUrl = audioName.getFileName()+"."+voiceFormat;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");
        String formattedDate = now.format(formatter); //현재시간을 String 형으로


        String userState  = userInfoRepository.getUserState(constructorId);
        AacCard aacCard  = AacCard.builder().
                cardName(cardName).
                creationTime(formattedDate).
                color(color).
                childId(childCode).
                constructorId(constructorId).
                imageUrl(imageUrl).
                voiceUrl(voiceUrl).
                share(share).
                clickCnt(0).
                state(userState).
                build();
        aacRepository.aacCardSave(aacCard);




        try {
            byte[] bytes = image.getBytes();
            String remoteFilePath = uploadDirectory +imageUrl;
            sftpService.uploadFile(bytes, remoteFilePath);

            byte[] audioBytesBytes = audio.getBytes();
            String audioRemoteFilePath = uploadDirectory+voiceUrl;
            sftpService.uploadFile(audioBytesBytes, audioRemoteFilePath);

           // return new ResponseEntity<>("File uploaded successfully and sent via WebSocket", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("라즈베리파이 + :"+e);
            //return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Path uploadPath = Paths.get(UPLOAD_DIR);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//        // Save image file
//        if (!image.isEmpty()) {
//            Path imagePath = uploadPath.resolve(image.getOriginalFilename());
//            Files.copy(image.getInputStream(), imagePath);
//        }
//
//        // Save audio file
//        if (!audio.isEmpty()) {
//            Path audioPath = uploadPath.resolve(audio.getOriginalFilename());
//            Files.copy(audio.getInputStream(), audioPath);
//        }

        // Save other data (for demonstration purposes, we're printing it)
        System.out.println("Card Name: " + cardName);
        System.out.println("Color: " + color);
        System.out.println("Child Code: " + childCode);
        return SuitableState;
    }
    public List<AacDto> getAacCard(String childCode, String constructorId){
        System.out.println("childcode + constructorId " + childCode+" + "+constructorId);

        String userState = userInfoRepository.getUserState(constructorId);

        List<AacCard> aacCardList;
        if (userState.equals("Nok")){
            aacCardList= aacRepository.getNokAacCardList(childCode);
        }else{
           aacCardList= aacRepository.getPathAacCardList(childCode,constructorId);
        }

        List<AacDto> aacDtoList = new ArrayList<>(aacCardList.size());
        for (AacCard aacCard:aacCardList){
            try {
                System.out.println("이미지 이름: " + aacCard.getImageUrl());
                String imageUrl = raspberryPiUrl + aacCard.getImageUrl();

                String voiceUrl = raspberryPiUrl + aacCard.getVoiceUrl();
                System.out.println("오디오 이름: " + aacCard.getVoiceUrl());
                RestTemplate voiceTemplate = new RestTemplate();
                byte[] voiceBytes = voiceTemplate.getForObject(URI.create(voiceUrl), byte[].class);
//                InputStream voiceInputStream = new ByteArrayInputStream(voiceBytes);
//                InputStreamResource voice = new InputStreamResource(voiceInputStream);


                AacDto aacDto = AacDto.builder().
                        aacCardId(aacCard.getAacCardId()).
                        cardName(aacCard.getCardName()).
                        creationTime(aacCard.getCreationTime()).
                        color(aacCard.getColor()).
                        childId(aacCard.getChildId()).
                        constructorId(aacCard.getConstructorId()).
                        image(imageUrl).
                        voice(voiceUrl).
//                        image(imageBytes).
//                        voice(voiceBytes).
                        build();

                aacDtoList.add(aacDto);
            }catch (Exception e){
                System.out.println("aac 카드 목록 조회 에러: "+e);
            }
        }

        return aacDtoList;
    }

    public  void  removeAacCard(String childCode,String constructorId,String cardName){
            AacCard aacCard = aacRepository.findByName(childCode,constructorId,cardName);
            aacRepository.removeAacCard(aacCard);
    }
}
