package sg.hsdd.aplus.service;

import sg.hsdd.aplus.service.vo.ChatroomUidVO;
import sg.hsdd.aplus.service.vo.QuestionVO;

import java.util.List;

public interface ChatroomService {
    List<ChatroomUidVO> getChatroomUid(int userUid);
    List<QuestionVO> getChatHistory(int userUid, int roomUid);
}
