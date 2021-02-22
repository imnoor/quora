package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.business.answer.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
public class AnswerController {
    @Autowired
    AnswerService answerService;

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> addAnswerToAQuestion(final AnswerRequest answerRequest, @PathVariable("questionId") Integer questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        AnswerEntity answerEntity = new AnswerEntity();

        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.isAuthorizedToPostAnswer(authorization);

        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setQuestion_id(questionId);
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerService.addAnswerToAQuestionService(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnAnswer(final AnswerEditRequest answerRequest, @PathVariable("answerId") Integer answerId) {
        answerService.editAnAnswerService(answerId, answerRequest.getContent());
        return null; //dummy
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnAnswer(@PathVariable("answerId") Integer answerId) {
        answerService.deleteAnswerService(answerId);
        return null; //dummy
    }

    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswersForAQuestion(@PathVariable("questionId") Integer questionId) {
        //send the string to service and replace the data in db
        answerService.getAllAnswersForAQuestionService(questionId);
        return null; //dummy
    }

}