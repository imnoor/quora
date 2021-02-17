package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

}
