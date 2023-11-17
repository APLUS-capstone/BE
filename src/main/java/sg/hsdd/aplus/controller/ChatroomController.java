package sg.hsdd.aplus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.hsdd.aplus.service.ChatroomService;
import sg.hsdd.aplus.service.vo.ChatroomUidVO;
import sg.hsdd.aplus.service.vo.QuestionVO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatroomController {
    @Autowired
    private ChatroomService chatroomService;

    @GetMapping("/uid/{userUid}")
    public List<ChatroomUidVO> getChatroomUid(@PathVariable(value = "userUid") int userUid){
        return chatroomService.getChatroomUid(userUid);
    }

    @GetMapping("/history/{userUid}/{roomUid}")
    public List<QuestionVO> getChatHistory(@PathVariable(value = "userUid") int userUid,
                                              @PathVariable(value = "roomUid") int roomUid){
        return chatroomService.getChatHistory(userUid, roomUid);
    }
}
