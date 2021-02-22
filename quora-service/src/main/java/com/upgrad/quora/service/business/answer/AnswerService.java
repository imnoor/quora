package com.upgrad.quora.service.business.answer;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    
    @Autowired
    AnswerDao answerDao;

    public void addAnswerToAQuestionService(AnswerEntity answerEntity) {
        answerDao.createAnswer(answerEntity);
    }

    public String editAnAnswerService(String answerId, String newAnswer) {
        AnswerEntity exAnswerEntity = answerDao.getAnswer(answerId);
        exAnswerEntity.setAns(newAnswer);
        return answerDao.updateAnswer(exAnswerEntity);
    }

    public String deleteAnswerService(String answerId) {
        AnswerEntity exAnswerEntity = answerDao.getAnswer(answerId);
        return answerDao.deleteAnswer(exAnswerEntity);
    }

    public List<AnswerEntity> getAllAnswersForAQuestionService(Integer questionId) {
        return answerDao.getAllAnswers(questionId);
    }
}