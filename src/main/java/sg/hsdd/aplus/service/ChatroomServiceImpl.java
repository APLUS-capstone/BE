package sg.hsdd.aplus.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.hsdd.aplus.entity.Chatroom;
import sg.hsdd.aplus.entity.Question;
import sg.hsdd.aplus.entity.UserInfo;
import sg.hsdd.aplus.exception.NotFoundException;
import sg.hsdd.aplus.repository.ChatroomRepository;
import sg.hsdd.aplus.repository.QuestionRepository;
import sg.hsdd.aplus.repository.UserInfoRepository;
import sg.hsdd.aplus.service.vo.ChatroomUidVO;
import sg.hsdd.aplus.service.vo.QuestionVO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatroomServiceImpl implements ChatroomService{

    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private QuestionRepository questionRepository;

    public List<ChatroomUidVO> getChatroomUid(int userUid){
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByUserUid(userUid);

        if(userInfoOptional.isPresent()){
            List<Chatroom> chatrooms = chatroomRepository.findAllByUserUid(userUid);
            List<ChatroomUidVO> chatroomUidVOS = chatrooms.stream().map(x -> ChatroomUidVO.builder()
                        .chatroomUid(x.getRoomUid())
                        .build())
                    .collect(Collectors.toList());

            return chatroomUidVOS;
        }
        else{
            throw new NotFoundException("{ERROR} =====> USER_UID_NOT_FOUND");
        }
    }

    public List<QuestionVO> getChatHistory(int userUid, int roomUid){
        Optional<Chatroom> chatroomOptional = chatroomRepository.findByRoomUid(roomUid);

        if(chatroomOptional.isPresent() && chatroomOptional.get().getUserUid() == userUid){
            List<Question> questions = questionRepository.findAllByRoomUid(roomUid);
            List<QuestionVO> questionVOS = questions.stream().map(x -> QuestionVO.builder()
                        .question(x.getQuestion())
                        .answer(x.getAnswer())
                        .solution(x.getSolution())
                        .build())
                    .collect(Collectors.toList());

            return questionVOS;
        }
        else{
            throw new NotFoundException("{ERROR} =====> ROOM_UID_OR_USER_UID_NOT_FOUND");
        }


    }
}
