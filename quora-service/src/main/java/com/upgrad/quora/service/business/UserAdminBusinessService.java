package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
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

    // get the access token from DB based on request access token
    public UserAuthTokenEntity getUsersAccessToken(String token) {
        UserAuthTokenEntity authEntity =  userDao.getUserAuthToken(token);
        return authEntity;
    }



    // handle exception cases
    private UserAuthTokenEntity pvtSafeGetUserByAccessToken(String token, String errorCode, String errorMessage)  throws AuthorizationFailedException {
        UserAuthTokenEntity authEntity =  userDao.getUserAuthToken(token);
        if (authEntity == null) {
            //throw new SignOutRestrictedException(errorCode, errorMessage);
            throw new AuthorizationFailedException(errorCode, errorMessage);
        }
        return authEntity;
    }

    // handle exception cases
    public void safeIsUserLoggedOut(ZonedDateTime logoutTime, String code, String message) throws SignOutRestrictedException {
        if (logoutTime != null && logoutTime.isBefore(ZonedDateTime.now())){
            throw new SignOutRestrictedException(code, message);
        }
    }

    //handle exception case
    private void pvtSafeIsUserSignedIn(UserAuthTokenEntity userAuthTokenEntity, String code, String message) throws AuthorizationFailedException {
        ZonedDateTime logoutTime = userAuthTokenEntity.getLogoutAt();
        if (logoutTime != null && logoutTime.isBefore(ZonedDateTime.now())){
            throw new AuthorizationFailedException(code, message);
        }
    }

    // handle exception cases
    public UserEntity safeGetUserByUuid(String userUuid, String code, String message) throws UserNotFoundException {
        UserEntity userEntity = userDao.getUser(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException(code,message);
        }
        return userEntity;
    }

    // handle exception cases
    private void pvtSafeIsUserAdmin(UserEntity userEntity, String code, String message) throws AuthorizationFailedException {
        if (userEntity.getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException(code,message);
        }
    }

    public UserEntity getUserProfile(String userUuid, String token) throws SignOutRestrictedException,UserNotFoundException, AuthorizationFailedException {

        UserEntity userEntity = this.safeGetUserByUuid(userUuid,"USR-001","User with entered uuid does not exist" );

        UserAuthTokenEntity authEntity =  this.pvtSafeGetUserByAccessToken(token,"ATHR-001", "User has not signed in" );

        ZonedDateTime logoutTime =  authEntity.getLogoutAt();
        this.safeIsUserLoggedOut(logoutTime,"ATHR-002", "User is signed out.Sign in first to get user details");

        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(String userUuid, String token) throws SignOutRestrictedException, AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity authEntity =  this.pvtSafeGetUserByAccessToken(token,"ATHR-001", "User has not signed in" );

        ZonedDateTime logoutTime =  authEntity.getLogoutAt();
        this.safeIsUserLoggedOut(logoutTime,"ATHR-002","User is signed out");

        UserEntity userEntity = authEntity.getUser();

        this.pvtSafeIsUserAdmin(userEntity,"ATHR-003","Unauthorized Access, Entered user is not an admin");

        UserEntity delUser = this.safeGetUserByUuid(userUuid, "USR-001", "User with entered uuid to be deleted does not exist");

        return userDao.deleteUser(delUser);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void signoutUser(UserAuthTokenEntity token){

        token.setLogoutAt(ZonedDateTime.now());
        userDao.updateSignout(token);

    }

    public UserAuthTokenEntity isAuthorizedToPostQuestion(String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to post a question" );
        return userAuthTokenEntity;

    }
    public UserAuthTokenEntity isAuthorizedToGetAllQuestion(String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to get all questions" );
        return userAuthTokenEntity;

    }

    public UserAuthTokenEntity isAuthorizedToEditQuestion(String authorization, String userUuid) throws AuthorizationFailedException{

        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");

        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to edit the question" );

        if ( !userAuthTokenEntity.getUser().getUuid().equals(userUuid) ){
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        return userAuthTokenEntity;
    }

    public UserAuthTokenEntity isAuthorizedToDeleteQuestion(String authorization, UserEntity user)  throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");

        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to edit the question" );

        if (user.getRole().equals("nonadmin")) {
            if (!userAuthTokenEntity.getUser().getUuid().equals(user.getUuid())) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }

        return userAuthTokenEntity;

    }

    public UserAuthTokenEntity isAuthorizedToGetAllUserQuestion(String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user" );
        return userAuthTokenEntity;
    }

    public UserAuthTokenEntity isAuthorizedToPostAnswer(String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to post an answer" );
        return userAuthTokenEntity;

    }

    public UserAuthTokenEntity isAuthorizedToDeleteAnswer(String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to post an answer" );
        return userAuthTokenEntity;

    }

    public UserAuthTokenEntity isAuthorizedToEditAnswer(String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to post an answer" );
        return userAuthTokenEntity;

    }

    public UserAuthTokenEntity isAuthorizedToGetAllAnswersForAQuestion(String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.pvtSafeGetUserByAccessToken(authorization, "ATHR-001", "User has not signed in");
        this.pvtSafeIsUserSignedIn(userAuthTokenEntity,"ATHR-002","User is signed out.Sign in first to post an answer" );
        return userAuthTokenEntity;

    }
}
