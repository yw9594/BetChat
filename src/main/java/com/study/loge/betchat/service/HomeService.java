package com.study.loge.betchat.service;

import com.study.loge.betchat.component.KeyGenerator;
import com.study.loge.betchat.entity.User;
import com.study.loge.betchat.enums.ResultState;
import com.study.loge.betchat.exception.UserLoginException;
import com.study.loge.betchat.model.MessageHeader;
import com.study.loge.betchat.model.request.UserLoginRequest;
import com.study.loge.betchat.model.response.UserLoginResponse;
import com.study.loge.betchat.repository.UserRepository;
import com.study.loge.betchat.utils.validation.HomeValidationChecker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// 홈 페이지 로직을 정의하는 클래스입니다.
@AllArgsConstructor
@Service
public class HomeService {
    private final KeyGenerator keyGenerator;
    private UserRepository userRepository;
    private HomeValidationChecker homeValidationChecker;

    // 유저의 로그인 요청을 처리합니다.
    public MessageHeader<UserLoginResponse> userLogin(MessageHeader<UserLoginRequest> request){
        ResultState resultState = null;
        String userKey = null;

        String userName = request.getData().getUserName();
        try {
            // 로그인 요청의 유효성을 검사합니다.
            homeValidationChecker.check(request);

            // response에 필요한 값을 생성합니다.
            resultState = ResultState.OK;
            userKey = keyGenerator.generateKey();

            // DB에 저장합니다.
            User user = User.builder()
                    .userName(userName)
                    .userKey(userKey)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        }
        catch(UserLoginException e){
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