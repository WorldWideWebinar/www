package com.ssafy.globalcc.domain.meetingCC.controller;

import com.deepl.api.*;
import com.ssafy.globalcc.aop.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/translation")
public class MeetingCCController {
    private final Translator translator;
    public MeetingCCController(@Value("${deepl.api.key}") String apiKey) {
        translator = new Translator(apiKey);
    }

    @GetMapping
    public ResponseEntity<?> getTranslated(
        @RequestParam("CountryCode") String code,
        @RequestParam(value = "SourceCountryCode" , required = false) String src,
        @RequestParam("SourceText") String text,
        @RequestParam(value = "Context", required = false) String context
    ) throws DeepLException, InterruptedException {
        log.info("Translate '{}' to '{}' from '{}' in '{}'" , text, code, src, context);
        TextTranslationOptions options = new TextTranslationOptions();
        options.setContext(context);
        TextResult result = translator.translateText(text,src,code,options);
        return new ResponseEntity<>(ApiResponse.success(result,"번역 성공!"), HttpStatus.OK);
    }
    @ExceptionHandler({DeepLException.class, InterruptedException.class})
    public ResponseEntity<?> handleDeepLException(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ApiResponse.fail(null,"번역 실패"),HttpStatus.BAD_REQUEST);
    }
}
