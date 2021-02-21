package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public String createQuestion(QuestionEntity questionEntity) {
        QuestionEntity qe = questionDao.createQuestion(questionEntity);
        return qe.getUuid();
    }
    public String editQuestionContent (QuestionEntity questionEntity) {
        questionDao.editQuestionContent(questionEntity);
        return questionEntity.getUuid();
    }

    public String deleteQuestion(QuestionEntity questionEntity){
        String uuuid= questionEntity.getUuid();
        questionDao.deleteQuestion(questionEntity);
        return uuuid;
    }

    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    public  List<QuestionEntity> getAllQuestionsByUser(UserEntity userEntity) {
        return questionDao.getAllQuestionsByUser(userEntity);
    }

    public QuestionEntity getQuestion(String questionId) {
        return questionDao.getQuestionByUuid(questionId);
    }

}
