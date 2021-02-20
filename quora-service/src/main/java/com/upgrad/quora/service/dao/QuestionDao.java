package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public void editQuestionContent (QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    public String deleteQuestion(QuestionEntity questionEntity){
        String uuid = questionEntity.getUuid();
        entityManager.remove(questionEntity);
        return uuid;
    }

    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery(QuestionEntity.FIND_ALL, QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public  List<QuestionEntity> getAllQuestionsByUser(UserEntity userEntity) {
        try {
            return entityManager.createNamedQuery("questionsByUser", QuestionEntity.class).setParameter("id", userEntity.getId()).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
