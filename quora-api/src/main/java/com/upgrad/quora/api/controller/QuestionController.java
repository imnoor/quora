package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToPostQuestion(authorization);

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionEntity.getContent());
        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionEntity.setDate(ZonedDateTime.now());
        final String uuid  = questionBusinessService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(uuid).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToGetAllQuestion(authorization);

        List<QuestionEntity> questionEntityList = questionBusinessService.getAllQuestions();
        List<QuestionDetailsResponse> responses = new ArrayList<>();
        for ( QuestionEntity qe : questionEntityList){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(qe.getUuid()).content(qe.getContent());
            responses.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(responses, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@PathVariable("questionId") final String questionId, final QuestionEditRequest questionEditRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity questionEntity= questionBusinessService.getQuestion(questionId);

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToEditQuestion(authorization, questionEntity.getUser().getUuid());

        questionEntity.setUuid(questionId);
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionEntity.setDate(ZonedDateTime.now());
        final String uuid  = questionBusinessService.editQuestionContent(questionEntity);
        QuestionEditResponse questionResponse = new QuestionEditResponse().id(uuid).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity questionEntity= questionBusinessService.getQuestion(questionId);

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        userAdminBusinessService.isAuthorizedToDeleteQuestion(authorization, questionEntity.getUser());
        final String uuid  = questionBusinessService.deleteQuestion(questionEntity);
        QuestionDeleteResponse questionResponse = new QuestionDeleteResponse().id(uuid).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all/{userId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToGetAllUserQuestion(authorization);

        UserEntity userEntity = userAdminBusinessService.safeGetUserByUuid(userId,"USR-001","User with entered uuid whose question details are to be seen does not exist" );

        List<QuestionEntity> questionEntityList = questionBusinessService.getAllQuestionsByUser(userEntity);
        List<QuestionDetailsResponse> responses = new ArrayList<>();
        for ( QuestionEntity qe : questionEntityList){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(qe.getUuid()).content(qe.getContent());
            responses.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(responses, HttpStatus.OK);
    }

}
