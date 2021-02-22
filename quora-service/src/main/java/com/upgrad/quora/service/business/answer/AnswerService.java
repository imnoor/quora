package com.upgrad.quora.service.business.answer;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    
    @Autowired
    AnswerDao answerDao;

    public void addAnswerToAQuestionService(AnswerEntity answerEntity) {
        answerDao.createAnswer(answerEntity);
    }

    public void editAnAnswerService(Integer answerId, String newAnswer) {
        AnswerEntity exAnswerEntity = answerDao.getAnswer(answerId);
        exAnswerEntity.setAns(newAnswer);
        answerDao.updateAnswer(exAnswerEntity);
    }

    public void deleteAnswerService(Integer answerId) {
        AnswerEntity exAnswerEntity = answerDao.getAnswer(answerId);
        answerDao.deleteAnswer(exAnswerEntity);
    }

    public void getAllAnswersForAQuestionService(Integer questionId) {
        answerDao.getAllAnswers(questionId);
    }
}