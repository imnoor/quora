package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswer(final String uuid) {
        try {
            return entityManager.createNamedQuery("answerById", AnswerEntity.class).setParameter("id", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public String updateAnswer(final AnswerEntity updatedAnswerEntity) {
        entityManager.merge(updatedAnswerEntity);
        return updatedAnswerEntity.getUuid();
    }

    public String deleteAnswer(AnswerEntity delAnswer) {
        String uuid = delAnswer.getUuid();
        entityManager.remove(delAnswer);
        return uuid;
    }

    public List<AnswerEntity> getAllAnswers(String questionId) {
        return entityManager.createNamedQuery("answerByQuestion", AnswerEntity.class).setParameter("question_id", questionId).getResultList();
    }
}