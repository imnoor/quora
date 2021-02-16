package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {
    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if (userAdminBusinessService.userNameExists(userEntity.getUserName())) {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }

        if (userAdminBusinessService.emailExists(userEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }

        return userAdminBusinessService.createUser(userEntity);
    }

}
