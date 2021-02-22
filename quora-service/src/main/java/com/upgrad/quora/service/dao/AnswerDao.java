package com.upgrad.quora.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.upgrad.quora.service.entity.AnswerEntity;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswer(final Integer id) {
        try {
            return entityManager.createNamedQuery("id", AnswerEntity.class).setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateAnswer(final AnswerEntity updatedAnswerEntity) {
        entityManager.merge(updatedAnswerEntity);
    }

    public Integer deleteAnswer(AnswerEntity delAnswer) {
        Integer id = delAnswer.getId();
        entityManager.remove(delAnswer);
        return id;
    }

    public List<AnswerEntity> getAllAnswers(Integer questionId) {
        return entityManager.createNamedQuery("answerByQuestion", AnswerEntity.class).setParameter("question_id", questionId).getResultList();
    }
}