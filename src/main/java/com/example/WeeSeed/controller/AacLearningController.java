package com.example.WeeSeed.controller;


import com.example.WeeSeed.dto.AacDto;
import com.example.WeeSeed.service.AacLearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AacLearningController {
    private final AacLearningService aaclearningService;

//    @PostMapping("/findSimilars")
//    public ResponseEntity<Object> findSimilarImage(
//            @RequestParam("cardId") long cardId,
//            @RequestParam("childCode") String childCode
//    ) {
//        System.out.println("아동 코드 : " + childCode);
//        try {
//            List<AacDto> aacDto = aaclearningService.findSimilarImage(cardId, childCode);
//            return new ResponseEntity<>(aacDto, HttpStatus.OK);
//        } catch (IOException e) {
//            return new ResponseEntity<>("Filed to find most similar aac card.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }
   // public List<AacDto> findSimilarImage(S)


