package com.study.loge.betchat.component;

import com.study.loge.betchat.entity.Participate;
import com.study.loge.betchat.entity.Room;
import com.study.loge.betchat.entity.User;
import com.study.loge.betchat.exception.SubscribeException;
import com.study.loge.betchat.repository.ParticipateRepository;
import com.study.loge.betchat.repository.RoomRepository;
import com.study.loge.betchat.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// StompInboudChannel의 Interceptor 클래스입니다.
@AllArgsConstructor
@Component
public class StompInboundChannelInterceptor implements ChannelInterceptor {
    private UserRepository userReposotory;
    private RoomRepository roomRepository;
    private ParticipateRepository participateRepository;

    // Message를 Controller 또는 Broker로 보내기 전, Dkem전처리를 수행합니다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = (StompHeaderAccessor) StompHeaderAccessor.getAccessor(message);
        SimpMessageType messageType = stompHeaderAccessor.getMessageType();

        try {
            // message type에 따라 특정 로직을 수행합니다.
            switch(messageType){
                // subscribe 요청 시 Participate table에 등록합니다.
                case SUBSCRIBE:
                    processParticipate(message);
                    break;
                case DISCONNECT:
                case UNSUBSCRIBE:
                    processExit(message);
                    break;
            }
        }
        catch(Exception e){
            // 예외가 발생한 경우 subscribe 시키지 않습니다.
            e.printStackTrace();
            message = null;
        }
        finally {
            return message;
        }
    }
    // 유저가 채팅방에 참가하는 행위를 처리합니다.
    private void processParticipate(Message<?> message) throws SubscribeException {
        MessageHeaders headers = message.getHeaders();
        StompHeaderAccessor stompHeaderAccessor = (StompHeaderAccessor) StompHeaderAccessor.getAccessor(message);

        String userKey = stompHeaderAccessor.getNativeHeader("user_key").get(0);
        String roomKey = stompHeaderAccessor.getNativeHeader("room_key").get(0);
        String simpSessionId = stompHeaderAccessor.getSessionId();

        User user = userReposotory.findByUserKey(userKey);
        Room room = roomRepository.findByRoomKey(roomKey);

        // 유효하지 않은 user 또는 room일 경우 예외를 발생시킵니다.
        if(user==null || room==null)
            throw new SubscribeException("유효하지 않은 user key 또는 room key입니다.");

        // 유저가 채팅방에 참가한 것을 DB에 저장합니다.
        Participate participate = Participate.builder()
                .user(user)
                .room(room)
                .isJoined(1)
                .joinedAt(LocalDateTime.now())
                .simpSessionId(simpSessionId)
                .build();

        participateRepository.save(participate);
    }

    // 유저가 채팅방에서 나가는 행위를 처리합니다.
    private void processExit(Message<?> message) {
        StompHeaderAccessor stompHeaderAccessor = (StompHeaderAccessor) StompHeaderAccessor.getAccessor(message);
        String simpSessionId = stompHeaderAccessor.getSessionId();

        Participate participate = participateRepository.findBySimpSessionId(simpSessionId);
        participate.setIsJoined(0);
        participate.setExitedAt(LocalDateTime.now());

        participateRepository.save(participate);
    }

}
