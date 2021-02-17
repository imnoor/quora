package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserAdminBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity) {

        String password = userEntity.getPassword();
        if (password == null) {
            userEntity.setPassword("proman@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);

    }

    //check if username exists
    public boolean userNameExists(final String userName) {
        return userDao.getUserByUserName(userName) != null;
    }

    //check if email is already registered
    public boolean emailExists(final String email) {
        return userDao.getUserByEmail(email) != null;
    }

    // get the user by the access token
    public UserEntity getUserByAccessToken(String token) throws SignOutRestrictedException {
        UserAuthTokenEntity authEntity =  userDao.getUserAuthToken(token);
        if (authEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        return authEntity.getUser();
    }

    public UserEntity getUser(String userUuid, String token) throws SignOutRestrictedException,UserNotFoundException {
        UserAuthTokenEntity authEntity =  userDao.getUserAuthToken(token);
        if (authEntity == null) {
            throw new SignOutRestrictedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime =  authEntity.getLogoutAt();
        if (logoutTime == null || logoutTime.isBefore(ZonedDateTime.now())){
            throw new SignOutRestrictedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        UserEntity userEntity = userDao.getUser(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }
        return userEntity;
    }
}
