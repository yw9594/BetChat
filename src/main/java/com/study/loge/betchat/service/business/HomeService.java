package com.study.loge.betchat.service.business;

import com.study.loge.betchat.service.dao.UserDao;
import com.study.loge.betchat.utils.enums.ResultState;
import com.study.loge.betchat.utils.exception.LoginException;
import com.study.loge.betchat.model.MessageHeader;
import com.study.loge.betchat.model.request.UserLoginRequest;
import com.study.loge.betchat.model.response.UserLoginResponse;
import com.study.loge.betchat.utils.validation.messageheader.LoginValidationChecker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

// 홈 페이지 로직을 정의하는 클래스입니다.
@AllArgsConstructor
@Service
public class HomeService {
    private UserDao userDao;
    private LoginValidationChecker loginValidationChecker;

    // 유저의 로그인 요청을 처리합니다.
    public MessageHeader<UserLoginResponse> userLogin(MessageHeader<UserLoginRequest> request){
        ResultState resultState = null;
        String userKey = null;

        String userName = request.getData().getUserName();
        try {
            // 로그인 요청의 유효성을 검사합니다.
            loginValidationChecker.check(request);

            // response에 필요한 값을 생성합니다.
            resultState = ResultState.OK;
            userKey = userDao.create(userName);
        }
        catch(LoginException e){
            resultState = ResultState.ERROR;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            // response를 생성한 뒤, 반환합니다.
            UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                    .userKey(userKey)
                    .build();
            return MessageHeader.makeMessage(resultState, userLoginResponse);
        }
    }
}
