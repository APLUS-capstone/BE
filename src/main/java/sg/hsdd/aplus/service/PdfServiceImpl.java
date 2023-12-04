package sg.hsdd.aplus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import sg.hsdd.aplus.controller.dto.OptionDTO;
import sg.hsdd.aplus.entity.Chatroom;
import sg.hsdd.aplus.entity.Question;
import sg.hsdd.aplus.entity.UserInfo;
import sg.hsdd.aplus.exception.NotFoundException;
import sg.hsdd.aplus.repository.ChatroomRepository;
import sg.hsdd.aplus.repository.QuestionRepository;
import sg.hsdd.aplus.repository.UserInfoRepository;
import sg.hsdd.aplus.service.vo.PdfStringVO;
import sg.hsdd.aplus.service.vo.QuestionListVO;
import sg.hsdd.aplus.service.vo.QuestionVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService{

    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Value("${aplus.chatgpt.url}")
    private String uri;

    @Override
    public PdfStringVO extractText(MultipartFile multipartFile, int userUid) throws IOException {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByUserUid(userUid);

        if(userInfoOptional.isPresent()) {
//        File file = new File("/Users/hong/" + multipartFile.getOriginalFilename());
//        multipartFile.transferTo(file);
//        File file = new File("/Users/hong/Downloads/"+"test.pdf");
            File file = new File(multipartFile.getOriginalFilename());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            PDDocument document;
            document = PDDocument.load(file);

            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            PDFTextStripper Tstripper = new PDFTextStripper();
//            Tstripper.setStartPage(18);
//            Tstripper.setEndPage(18);
            String summaryText = Tstripper.getText(document);
//        log.info("{extracted String} ==>" + summaryText);

            if (document != null) {
                document.close();
            }

            Chatroom chatroom = Chatroom.builder()
                    .userUid(userUid)
                    .pdfString(summaryText)
                    .build();

            Chatroom savedChatroom = chatroomRepository.save(chatroom);
            int roomUid = savedChatroom.getRoomUid();

            PdfStringVO pdfStringVO = PdfStringVO.builder()
                    .roomUid(roomUid)
                    .pdfString(summaryText)
                    .build();

            return pdfStringVO;
        }
        else{
            throw new NotFoundException("{ERROR} =====> USER_NOT_FOUND");
        }
    }

    @Override
    public QuestionListVO generateQuestion(OptionDTO optionDTO){
        Optional<Chatroom> chatroomOptional = chatroomRepository.findByRoomUid(optionDTO.getRoomUid());

        if(chatroomOptional.isPresent()) {
            //TODO: 외부 api request
            String pdfString = chatroomOptional.get().getPdfString();
            int questionNum = optionDTO.getNumber();
            String questionType = "subjective";
            String language = "English";
            int choiceNum = optionDTO.getChoice();

            if(optionDTO.getType() == 1){
                questionType = "multiple choice";
            }
            else if(optionDTO.getType() == 2){
                questionType = "subjective";
                choiceNum = 0;
            }
            else if(optionDTO.getType() == 3){
                questionType = "short answer";
                choiceNum = 0;
            }

            if(optionDTO.getLanguage() == 1){
                language = "Korean";
            }
            else if(optionDTO.getLanguage() == 2){
                language = "English";
            }
//            String pdfString = "- 1 -\n한일 협상 조약(을사조약) / 1905.11.17. " +
//                    "\n일본국 정부와 한국 정부는 두 나라를 결합하는 이해(利害) 공통의 주의를 공고히 하기 위하" +
//                    "\n여 한국의 부강의 실(實)을 인정할 수 있을 때까지 이 목적을 위하여 아래에 열거한 조관(條\n款)을 약정한다." +
//                    "\n제1조 일본국 정부는 도쿄에 있는 외무성을 통하여 금후 한국의 외국과의 관계 및 사무를 관" +
//                    "\n리감독⋅지휘하고, 일본국의 외교 대표자 및 영사(領事)는 외국에 있는 한국의 신민 및 그 이\n익을 보호한다." +
//                    "\n제2조 일본국 정부는 한국과 타국 사이에 현존하는 조약의 실행을 완수하는 책임을 지며 한국 " +
//                    "\n정부는 금후 일본국 정부의 중개를 거치지 않고서는 국제적 성질을 가진 어떠한 조약이나 약\n속을 하지 않을 것을 약속한다." +
//                    "\n제3조 일본국 정부는 그 대표자로서 한국 황제폐하의 아래에 1명의 통감(統監)을 두되, 통감\n은 오로지 외교에 관한 사항을 관리하기 위하여 서울에 주재하고, 직접 한국 황제 폐하를 궁" +
//                    "\n중에서 알현할 권리를 가진다. 일본국 정부는 또 한국의 각 개항장과 기타 일본국 정부가 필\n요하다고 인정하는 지역에 이사관(理事官)을 두는 권리를 가지되, 이사관은 통감의 지휘 아래 " +
//                    "\n종래 재한국 일본 영사에게 속했던 일체 직권을 집행하고 아울러 본 협약의 조관을 완전히 실\n행하기 위하여 필요한 일체 사무를 맡아 처리한다.\n제4조 일본국과 한국 사이에 현존하는 조약 및 약속은 본 협약의 조관에 저촉하는 것을 제외" +
//                    "\n하고는 모두 그 효력이 계속되는 것으로 한다.\n제5조 일본 정부는 한국 황실의 안녕과 존엄을 유지함을 보증한다.\n이상의 증거로써 아래의 사람들은 각기 자기 나라 한국 정부에서 상당한 위임을 받아 본 협약\n에 이름을 적고 조인한다." +
//                    "\n광무 9년(1905) 11월 17일 외부대신 박제순(朴齊純)\n메이지(明治) 38년 11월 17일\n특명전권공사 하야시 곤스케(林權助)\n-『고종실록』권46, 42년(광무 9년) 11월 17일\n  이 사료는 대한제국이 사실상 일본의 식민지가 되었던 첫 단계라 할 수 있는 외교권 박탈과 " +
//                    "\n통감부 설치를 주요 골자로 1905년(고종 42년) 11월 17일 일본의 강압에 의해 체결된 조약이 \n실린 〈관보〉의 일부이다.\n  1904년(고종 41년) 2월에 시작된 러⋅일 간 전쟁에서 승리한 일본은 열강들로부터 한국에 " +
//                    "\n대한 독점적 지배를 승인받아 본격적으로 식민지화를 추진하였다. 1905년 11월 15일 일본 특\n파대신 이토 히로부미(伊藤博文)는 고종 황제를 알현하여 한일 협약안을 제시하면서 조약 체\n- 2 -" +
//                    "\n결을 강압적으로 요구하였다. 이때 조선 왕궁은 하야시 곤스케(林權助) 일본 공사와 하세가와 \n요시미치(長谷川好道) 주한 일본군 사령관이 궁궐 내외에 물샐 틈 없는 경계망을 펴고 호위함\n으로써 공포 분위기에 싸여 있었다." +
//                    "\n  일본은 조선의 외교권을 빼앗는 내용의 5개조 조약을 미리 준비하여 한국 측에 제시하고 \n그 체결을 강요하였다. 고종은 이토의 강요에도 불구하고 조약의 승인을 거부하였다. 이에 일" +
//                    "\n본은 조선 조정 대신들을 상대로 매수⋅위협을 가한 끝에 11월 17일 어전 회의를 열도록 하\n였다. 고종은 의견 개진 없이 대신들에게 결정토록 하고 이를 지켜보았다. 5시간이 지나도 결" +
//                    "\n론이 나지 않자 초조해진 이토는 하세가와와 헌병대장을 대동하고 수십 명의 헌병의 호위를 \n받으며 궐내로 들어가 대신들에게 노골적으로 위협과 공갈을 자행하였다.\n  이토는 살기를 띤 모습으로 직접 메모 용지에 연필을 들고 대신들에게 ‘가(可)’냐 ‘부(否)’냐" +
//                    "\n를 따져 물었다. 참정대신 한규설(韓圭卨), 탁지부대신 민영기(閔泳綺), 법부대신 이하영(李夏\n榮)만이 무조건 ‘불가’를 썼고, 학부대신 이완용(李完用), 군부대신 이근택(李根澤), 내부대신 \n이지용(李址鎔), 외부대신 박제순(朴齊純), 농상공부대신 권중현(權重顯)은 찬성하였다. 이토는 " +
//                    "\n각료 8대신 가운데 5명이 찬성했으니 조약 안건은 가결되었다고 선언하고 궁내부대신 이재극\n(李載克)을 통해 그날 밤 황제의 칙재(勅裁)를 강요하였다. 이들 다섯 명을 ‘을사오적’이라고 \n부른다. 그리고 같은 날짜로 외부대신 박제순과 일본 공사 하야시 간에 이른바 ‘을사조약’(일" +
//                    "\n본에서는 ‘제2차 일한 협약’이라 부름)이 체결되었다.\n  이 조약이 체결됨으로써 한국의 외교권은 일본으로 넘어가 통감부(統監府)가 이를 장악하였\n다. 대한제국의 외부(外部)는 폐지하여 의정부 외사국(外事局)으로 축소하고, 한국 주재 각국 " +
//                    "\n공사관이 철수하고, 후일 청과의 외교적 현안이던 간도 문제에 통감부가 자임하여 나서게 되\n었다. 또 내정 개혁을 빌미로 통감이 사실상 회의를 주재하고 정부 대신들이 참여하는 ‘시정\n개선협의회’가 열려 대한제국의 내정 개혁을 통제하였다. 그리고 각 개항장에서 일본인의 활" +
//                    "\n동을 강화하기 위해 이사청(理事廳)이 설치되었다.\n  그런데 ‘을사늑약’은 그 체결 과정에 일본의 군사적 강박이 있었고, 조약 체결 시 갖추어야 \n할 제반 절차, 즉 협상 대표에게 내리는 전권 위임장, 합의 후 국가 원수의 비준, 〈관보〉에 게\n재 등이 결여되어 있었다. 따라서 ‘을사늑약’은 일제의 강압에 의해 ‘법적 형식’이 결여된 불" +
//                    "\n법적인 조약이라 하겠다. 그래서 합법성을 띠는 의미의 ‘조약’이라는 용어보다 ‘늑약’이라는 \n용어를 사용하여 그 불법성과 강제성을 강조하기도 한다. 그러나 일본의 조선 식민지화 과정\n에서 ‘을사늑약’은 이후 효력을 얻어 조선의 외교권을 빼앗고 향후 병합으로 가는 가교 역할\n을 하게 되었다.\n- 국사편찬위원회, 〈우리역사넷〉 중\n";

//            String pdfString = "\"CSE4120 (Compiler Construction)\\nChapter 5. Type Checking\\nProf. Jaeseung Choi\\nDept. of Computer Science and Engineering\\nSogang University\\nTopics\\n■ General background on type system\\n▪ Static typing vs. Dynamic typing\\n" +
//                    "▪ Explicit typing vs. Implicit typing\\n■ Key ideas for type checking in C-like language\\n▪ Scope\\n▪ Symbol table\\n▪ Decomposition of program into subcomponents\\n2\\nFront-end Until Now\\n■ Lexical analysis\\n▪ Detects lexical error (invalid token) in the source program\\n• Ex) int 2way = 1;\\n▪ If there is no such error, outputs stream of valid tokens\\n■ Syntax analysis (parsing)\\n" +
//                    "▪ Detects syntax-level error (such as a token that appears at the \\nunexpected position in the stream)\\n• Ex) int x y = 1;\\n▪ If there is no such error, outputs abstract syntax tree (AST)\\n3\\nType Checking\\n■ Also known as semantic analysis in compiler\\n■ Detects obvious (but not all) semantic errors from AST\\n▪ Type mismatch\\n▪ Use of undeclared identifier\\n▪ Redeclaration of identifier\\n▪ And more\\n■ If there is no such error found, the input AST is simply \\npassed to the IR generator\\n4\\n" +
//                    "What is Type?\\n■ A name that represents set of values\\n▪ Ex) unsigned int value can be one of { 0, 1, 2, … 2^32-1 }\\n▪ Ex) bool value can be one of  { true, false }\\n■ Usually, each operator is allowed only for specific type(s)\\n▪ Ex) Adding int with int is okay\\n▪ Ex) But adding string with float is not allowed: error\\n▪ Detailed rules are decided by the programming language design\\n▪ This makes type a helpful concept for detecting program errors\\n5\\nVarious Design Choice for Type\\n■ Static typing vs. Dynamic typing\\n▪ Static typing: type error is detected before the runtime (usually \\nat compile time)\\n▪ Dynamic typing: type error is caught at runtime\\n■ Explicit typing vs. Implicit typing\\n▪ Explicit typing: must specify types explicitly (like in C)\\n• Ex) int x = 0;\\n▪ Implicit typing: do not have to specify types (like in OCaml)\\n• Ex) let x = 0\\n▪ Some languages take a hybrid approach (C++ with auto)\\n6\\nStatic Typing\\n■ Advantage of static typing\\n▪ If your program passes type checking, it is guaranteed to be \\nsafe from type error\\n• But only if the language adopts sound & strong type system\\n• C allows unsafe type conversions (e.g., pointer casting), so \\nprograms can encounter type error at runtime\\n■ Disadvantage of static typing\\n▪ May be restrictive: some correct programs can be rejected\\n7\\nWe will focus on static type checking at compile time\\nExplicit Typing\\n■ If type of each variable or function is explicit declared, \\ntype errors can be easily detected with such information\\n▪ We will see more details in the following pages\\n■ If type is not explicitly declared (i.e., implicit typing):\\n▪ Type of each expression must be automatically inferred while \\nchecking for type errors\\n▪ Especially challenging in the existence of polymorphic type\\n• Ex) List.length [1;2;3] + List.length [\\\"ABC\\\";\\\"123\\\"]\\n8\\nWe will focus on languages with explicit typing \\nSide-Note: Type Inference\\n■ Type inference is one of the challenging topics in PL\\n▪ You may have a chance to learn about this in graduate course\\n■ There is even textbook dedicated to this topic\\n▪ Types and Programming Languages, Benjamin C. Pierce\\nwww.cis.upenn.edu/~bcpierce/tapl/ 9\\nExample Language: Simplified C\\n■ From now on, let’s consider a simple C-like language as \\na concrete example to discuss type checking\\n■ In the course project, you will work with a language \\nsimilar to this one\\n■ In this lecture, we will discuss at high level, but in the \\nproject the program will be given in the AST form\\n10\\nProgram in Simplified C\\n■ A program consists of (1) global variable declarations \\nand (2) function definitions\\n▪ One of the functions (usually the last one) must be main()\\n■ To make the syntax simple, let’s assume the followings\\n▪ Variables cannot be declared and initialized in one line\\n▪ Declaration of function is not needed\\n11\\nint v; // No \\\"int v = 0;\\\"\\nint f(void) { g(); ... }\\nvoid g(bool v, int n) { if(v)... } \\nint main(void) { v = 0; ... }\\ng() used without declaration\\nMapping Name to Type\\n■ In explicitly typed languages, we can easily identify the \\ntype of each variable or function\\n▪ Ex) (Global variable) v is int, g is bool × int→ void, ...\\n■ But we have to be careful in distinguishing the global \\nvariable v from the local variable v\\n12\\nint v;\\nint f(void) { g(); ... }\\nvoid g(bool v, int n) { if(v)... } \\nint main(void) { v = 0; ... }\\nScope\\n■ Each name (identifier) has a scope (range) in which that \\nname is valid\\n▪ Ex) Global variable is valid in all the parts of a program\\n▪ Ex) Argument and local variable are valid only in that function\\n■ Usually, each name refers to its closest definition\\n13\\nint v;\\nint f(void) { g(); ... }\\nvoid g(bool v, int n) { if(v)... } \\nint main(void) { v = 0; ... }\\nScope of \\nint v\\nScope of \\nbool v\\nSymbol Table\\n■ Traditionally, a mapping that records information about \\neach name is called a symbol table\\n▪ Type and other useful information will be stored here\\n▪ Note: The table must be updated when entering each function\\n■ Symbol table is used not only in type checking, but also \\nin other steps of a compiler\\n14\\nv (Global, int)\\nf (Func, void→ int)\\ng (Func, bool × int→ void)\\nmain (Func, int→ void)\\nv (Arg, bool)\\nf (Func, void→ int)\\ng (Func, bool × int→ void)\\nmain (Func, int→ void)\\nn (Arg, int)\\nAfter examining the globals After entering g()\\nType Checking for Program\\n■ Now, let’s sketch the type checking process in top-\\ndown manner\\n■ First, examine the global variables declarations and \\nfunction definitions to construct a symbol table\\n■ Next, we will iterate through each function code and \\ncheck for type errors\\n15\\nint v1;\\nbool v2;\\nint f(void) { ... }\\nvoid g(bool v, int n) { ... } \\n...\\nType Checking for Function\\n■ Then how should we run type checking on a function?\\n■ A function consists of statements\\n▪ Thus, we iterate through statements and check for type errors\\n■ The language can have various kinds of statements\\n▪ Ex) Assignment \\\"x = \uD835\uDC6C;\\\" : perform type checking on \\nexpression \uD835\uDC6C and check if the types of x and \uD835\uDC6Cmatch\\n▪ Ex) Conditional statement \\\"if(\uD835\uDC6C) then { \uD835\uDC7A\uD835\uDFCF } else { \uD835\uDC7A\uD835\uDFD0 }\\\" : \\nperform type checking on \uD835\uDC6C, \uD835\uDC7A\uD835\uDFCF, \uD835\uDC7A\uD835\uDFD0, and then check if the type \\nof \uD835\uDC6C is bool\\n16\\nType Checking for Expression\\n■ Lastly, how should we run type checking on expression?\\n■ We know that an expression is also inductively defined:\\n▪ Base case: constant number, variable name, ...\\n▪ Inductive cases: addition, subtraction, function call, …\\n■ Thus, we recurse into the subcomponents if exist, and \\nthen decide the type of expression itself\\n▪ Ex) Variable name \\\"x\\\": no subcomponent, so just lookup the \\nsymbol table and resolve the type of this variable\\n▪ Ex) Addition \\\"\uD835\uDC6C\uD835\uDFCF + \uD835\uDC6C\uD835\uDFD0\\\": perform type checking on \uD835\uDC6C\uD835\uDFCF and \uD835\uDC6C\uD835\uDFD0, \\nand then check if both are int types (the result is also int type)\\n• We are assuming that float type does not exist, so + is only \\nallowed for int types\\n17\\nType Checking: Wrap-up\\n■ As we have seen, a program can be decomposed into \\nsmaller subcomponents\\n■ Therefore, the basic idea is to traverse into those \\nsubcomponents\\n▪ If all the subcomponent are free from errors, then the whole \\n" +
//                    "program is also free from errors\\n■ Although not explicitly explained, other semantic errors \\ncan be easily detected during this process as well\\n▪ Ex) Use of undeclared identifier\\n▪ Ex) Redeclaration of identifier\\n18\\n\"";



            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("number", questionNum);
            bodyMap.put("type", questionType);
            bodyMap.put("choice", choiceNum);
            bodyMap.put("language", language);
            bodyMap.put("pdfString", pdfString);

            // webClient 기본 설정
            WebClient webClient =
                    WebClient
                            .builder()
//                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .baseUrl(uri)
                            .build();

            // api 요청
            String response = webClient
                            .post()
                            .uri("/options")
                            .contentType(MediaType.APPLICATION_JSON)
                            //.body(Mono.just(jsonObject.toString()), JSONObject.class)
                            .bodyValue(bodyMap)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();


            // 결과 확인
            log.info("{RESPONSE} ======> " + response);
            List<QuestionVO> questionVOS = new ArrayList<>();

            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject object = (JSONObject) jsonParser.parse(response);
                int number =  Integer.parseInt(String.valueOf(object.get("number")));
                log.info("{number} ======> " + number);
                JSONArray jsonArray = (JSONArray) object.get("list");
                for(int i=0;i<number;i++){
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    String question = (String) jsonObject.get("question");
                    String answer = (String) jsonObject.get("answer");
                    String solution = (String) jsonObject.get("solution");

                    log.info("{question} ======> " + question);
                    log.info("{answer} ======> " + answer);
                    log.info("{solution} ======> " + solution);
                    log.info("-------------------------------------------------");

                    Question question1 = Question.builder()
                                    .roomUid(optionDTO.getRoomUid())
                                    .typeUid(optionDTO.getType())
                                    .languageUid(optionDTO.getLanguage())
                                    .choiceNum(optionDTO.getChoice())
                                    .question(question)
                                    .answer(answer)
                                    .solution(solution)
                                    .build();

                    questionRepository.save(question1);

                    QuestionVO questionVO = QuestionVO.builder()
                            .question(question)
                            .answer(answer)
                            .solution(solution)
                            .build();
                    questionVOS.add(questionVO);


                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }




//            int userUid=1;
//            String string11 ="AVCX";
//            List<Chatroom> questions = chatroomRepository.findAllByUserUid(userUid);
//            List<QuestionVO> questionVOS = questions.stream().map(x -> QuestionVO.builder()
//                            .question(string11)
//                            .answer(string11)
//                            .solution(string11)
//                            .build())
//                    .collect(Collectors.toList());

            QuestionListVO questionListVO = QuestionListVO.builder()
                    .number(optionDTO.getNumber())
                    .type(optionDTO.getType())
                    .list(questionVOS)
                    .build();

            return questionListVO;
        }
        else{
            throw new NotFoundException("{ERROR} =====> USER_NOT_FOUND");
        }
    }
}

