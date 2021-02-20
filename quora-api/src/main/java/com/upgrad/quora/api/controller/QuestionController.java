package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;


    @RequestMapping(method = RequestMethod.POST, path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();

        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.safeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        userAdminBusinessService.safeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to post a question" );

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionEntity.getContent());
        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionEntity.setDate(ZonedDateTime.now());
        final String uuid  = questionBusinessService.createQuestion(questionEntity);
        QuestionResponse userResponse = new QuestionResponse().id(uuid).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(userResponse, HttpStatus.CREATED);
    }

}
