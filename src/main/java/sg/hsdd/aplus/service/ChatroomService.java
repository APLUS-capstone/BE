package sg.hsdd.aplus.service;

import sg.hsdd.aplus.service.vo.ChatroomUidListVO;
import sg.hsdd.aplus.service.vo.ChatroomUidVO;
import sg.hsdd.aplus.service.vo.QuestionHistoryVO;
import sg.hsdd.aplus.service.vo.QuestionVO;

import java.util.List;

public interface ChatroomService {
    ChatroomUidListVO getChatroomUid(int userUid);
    QuestionHistoryVO getChatHistory(int userUid, int roomUid);
}
