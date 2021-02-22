package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.business.answer.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {
    @Autowired
    AnswerService answerService;

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @Autowired
    QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> addAnswerToAQuestion(final AnswerRequest answerRequest, @PathVariable("questionId") String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();
        QuestionEntity questionEntity = questionBusinessService.getQuestion(questionId);
        if(questionEntity==null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToPostAnswer(authorization);
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setQuestion_id(questionEntity);
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerService.addAnswerToAQuestionService(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnAnswer(final AnswerEditRequest answerRequest, @PathVariable("answerId") String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = answerService.getAnswerByUuid(answerId);
        if(answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToEditAnswer(authorization);
        if(answerEntity.getUser().getId() != userAuthTokenEntity.getUser().getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        String uuid = answerService.editAnAnswerService(answerId, answerRequest.getContent());
        AnswerEditResponse answerResponse = new AnswerEditResponse().id(uuid).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnAnswer(@PathVariable("answerId") String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = answerService.getAnswerByUuid(answerId);
        if(answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToDeleteAnswer(authorization);
        if(userAuthTokenEntity.getUser().getRole().equals("nonadmin") || answerEntity.getUser().getId() != userAuthTokenEntity.getUser().getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        String uuid = answerService.deleteAnswerService(answerId);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(uuid).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersForAQuestion(@PathVariable("questionId") String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        //send the string to service and replace the data in db
        QuestionEntity questionEntity = questionBusinessService.getQuestion(questionId);
        if(questionEntity==null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }
        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToGetAllAnswersForAQuestion(authorization);

        List<AnswerEntity> answerEntities = answerService.getAllAnswersForAQuestionService(questionId);
        List<AnswerDetailsResponse> responses = new ArrayList<>();
        for ( AnswerEntity ae : answerEntities){
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(ae.getUuid())
                    .questionContent(questionEntity.getContent())
                    .answerContent(ae.getAns());
            responses.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(responses, HttpStatus.OK);
    }
}