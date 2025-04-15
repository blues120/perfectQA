package cn.stylefeng.guns.modular.deepseek.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.stylefeng.guns.modular.deepseek.pojo.ChatBoxMessage;
import cn.stylefeng.guns.modular.deepseek.pojo.ChatBoxRequest;
import cn.stylefeng.roses.kernel.auth.api.SessionManagerApi;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.request.SysFileInfoRequest;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import cn.stylefeng.roses.kernel.system.api.UserServiceApi;
import cn.stylefeng.roses.kernel.system.api.pojo.user.SysUserDTO;
import cn.stylefeng.roses.kernel.system.api.pojo.user.request.SysUserRequest;
import cn.stylefeng.roses.kernel.system.modular.user.service.SysUserService;

import okhttp3.*;
import okio.BufferedSource;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParseContext;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * 活动财务预算表控制器
 *
 * @author chanshu
 * @date 2025/01/16 17:31
 */
@RestController
@ApiResource(name = "新增用户")
public class AddUserController {
    @Resource
    private UserServiceApi userServiceApi;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SessionManagerApi sessionManagerApi;


    /**
     * 上传文件
     * <p>
     * 支持上传到数据库，参数fileLocation传递5即可
     * <p>
     * fileLocation传递其他值或不传值，不能决定文件上传到本地还是阿里云或其他地方
     *
     * @author majianguo
     * @date 2020/12/27 13:17
     */
    @PostResource(name = "上传文件", path = "/deepseek/upload", requiredPermission = false)
    public ResponseData<Object> upload(@RequestPart("file") MultipartFile file, @RequestParam(required = true) String type) throws IOException, TikaException {
        String textFile ="";
        if (file.isEmpty()) {
            return new ErrorResponseData<>("401","文件不能为空！");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".docx") || fileName.endsWith(".doc"))) {
            return new ErrorResponseData<>("401","仅支持 Word 文件！");
        }
        try (InputStream inputStream = file.getInputStream()) {
            String content;
            if (fileName.endsWith(".doc")) {
                // 解析 .doc 文件
                HWPFDocument doc = new HWPFDocument(inputStream);
                content = doc.getDocumentText();
            } else if (fileName.endsWith(".docx")) {
                // 解析 .docx 文件
                XWPFDocument docx = new XWPFDocument(inputStream);
                StringBuilder builder = new StringBuilder();
                docx.getParagraphs().forEach(p -> builder.append(p.getText()).append("\n"));


                // 提取表格数据
                for (XWPFTable TableName : docx.getTables()) {
                    for (XWPFTableRow row : TableName.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            builder.append(cell.getText()).append("\t"); // 用制表符分隔单元格内容
                        }
                        builder.append("\n"); // 换行表示新的一行
                    }
                }

                content = builder.toString();
            } else {
                return new ErrorResponseData<>("401","不支持的文件类型！");
            }

            if (content == null || content.trim().isEmpty()) {
                return new ErrorResponseData<>("401","文件内容为空或解析失败！");
            }
            System.out.println("解析成功！内容如下：\n" + content);
            textFile = content;
        } catch (IOException e) {
            e.printStackTrace();
            // 如果 POI 解析失败，尝试使用 Tika
            try (InputStream inputStream = file.getInputStream()) {
                Tika tika = new Tika();
                return new ErrorResponseData<>("401","POI 解析失败，使用 Tika 解析结果：\n" + tika.parseToString(inputStream));
            } catch (IOException | TikaException ex) {
                ex.printStackTrace();
                System.out.println("解析失败：" + ex.getMessage());
            }
        }


        if (ObjectUtil.isEmpty(textFile)) {
            return new ErrorResponseData<>("401","");
        }
// 构建请求的 JSON 数据
        JSONObject jsonObject = new JSONObject();

        // 构建 messages 数组
        JSONObject systemMessage = new JSONObject();
        if ("1".equals(type)) {
            systemMessage.set("content", "我是一名审批局的工作人员，需要从模板中提取签字，解析字段“applyName”," +
                    "然后分析所有解析的字段，如果所有字段成功解析并且有值，提示文件正常，否则提示哪些存在问题，例如申请人没有填写，把分析结果放在json的remark字段中,输出使用json格式。" +
                    "样例输入内容：" +
                    "城市建筑垃圾处置承诺书\n" +
                    "\n" +
                    "\n" +
                    "    根据《国务院对确需保留的行政审批项目设定行政许可的决定》《城市建筑垃圾管理规定》《山东省城镇容貌和环境卫生管理办法》《建设部关于纳入国务院决定的十五项行政许可的条件的规定》，本单位（建设单位）为申请城市建筑垃圾处置核准，作出如下承诺：\n" +
                    "一、本单位已经与运输单位签订建筑垃圾运输合同，明确约定建筑垃圾运输的时间、路线和处置地点，不将建筑垃圾交给个人或者未取得建筑垃圾处置核准的运输单位运输；\n" +
                    "二、运输建筑垃圾的车辆，随车携带建筑垃圾处置核准证件，按照批准的时间、路线、数量，将建筑垃圾运送到指定的处理场所，不擅自丢弃；\n" +
                    "三、建筑垃圾消纳场属于集体所有土地的，已经取得集体经济组织同意；属于国有土地的，已经取得登记的土地使用权人或有关行政主管部门同意；\n" +
                    "四、有消纳场的场地平面图、进场路线图、有相应的摊铺、碾压、除尘、照明等机械和设备，有排水、消防等设施，有健全的环境卫生和安全管理制度并得到有效执行；\n" +
                    "\n" +
                    "共2页，第1页    申请人（签字、盖章）：张伟\n 样例json输出：{applyName:张伟}");
        } else if ("2".equals(type)) {
            systemMessage.set("content", "我是一名审批局的工作人员，需要从模板中提取签字，解析字段“legalPerson”," +
                    "然后分析所有解析的字段，如果所有字段成功解析并且有值，提示文件正常，否则提示哪些存在问题，例如法定代表人没有填写，把分析结果放在json的remark字段中,输出使用json格式。" +
                    "样例输入内容：" +
                    "五、有建筑垃圾分类处置的方案和对废混凝土、金属、木材等回收利用的方案；\n" +
                    "六、有合法的道路运输经营许可证、车辆行驶证；\n" +
                    "七、有健全的运输车辆运营、安全、质量、保养、行政管理制度并得到有效执行；\n" +
                    "八、运输车辆具备全密闭运输机械装置或密闭苫盖装置、安装行驶及装卸记录仪和相应的建筑垃圾分类运输设备；\n" +
                    "九、建筑垃圾出入口地面硬化，运输车辆离开时对轮胎和车体进行冲洗，不在城市道路上抛洒泥土和建筑垃圾，已经采取有效措施避免建筑垃圾运出场地和消纳场地扬尘；\n" +
                    "十、提交的申请书、施工单位与运输单位签订的合同、符合本承诺书第三条内容的同意书，均真实、合法、有效。\n" +
                    "申请人对以上承诺事项负责。如承诺不实或不履行承诺，申请人自愿接受执法部门进行的行政处罚，自愿承担相应的失信惩戒等法律责任和经济赔偿责任。\n" +
                    "法定代表人（委托代理人）签字：张伟\n" +
                    "申请单位：（章）\n" +
                    "                                年     月     日\n" +
                    "\n" +
                    "\n" +
                    "共2页，第2页（第1页、第2页均需签字、盖章） \n样例json输出：{legalPerson:张伟}");
        }else if ("3".equals(type)) {
            systemMessage.set("content", "我是一名审批局的工作人员，需要从模板中提取签字，" +
                    "解析申请日期对应字段applyTime," +
                    "解析申请人（建设或施工单位）\t单位名称对应字段applyOrg," +
                    "解析统一社会信用代码对应字段orgCode," +
                    "解析联系人\t姓   名对应字段applyName," +
                    "解析联系电话对应字段applyMobile," +
                    "解析运输单位1\t单位名称\t对应字段transportOrgName," +
                    "解析联系人对应字段transportName," +
                    "解析联系电话对应字段transportMobile," +
                    "解析申请处置情   况\t建筑垃圾种类及产量（万吨）\t对应字段weight," +
                    "解析产生周期对应字段productRange," +
                    "解析处置单位对应字段deelOrgName," +
                    "解析处置规模\t对应字段deelOrgWeight," +
                    "解析运输时段\t对应字段transportRange," +
                    "解析处置单位对应字段deelOrgName2," +
                    "解析联系人及联系电话对应字段deelOrgMobile2," +
                    "解析（承诺书附后）    法定代表人（委托代理人）签字：对应字段signName," +
                    "解析（承诺书附后）    法定代表人（委托代理人）签字：对应字段signTime," +
                    "然后分析所有解析的字段，如果所有字段成功解析并且有值，提示文件正常，否则提示哪些存在问题，例如法定代表人没有填写，把分析结果放在json的remark字段中,输出使用json格式。" +
                    "样例输入内容：" +
                    "城市建筑垃圾产生核准申请表\n" +
                    "\n" +
                    "申请日期：     2025年  3月  9日                        \n" +
                    "\n" +
                    "申请人（建设或施工单位）\t单位名称\t上海城投公司\t\n" +
                    "\t统一社会信用代码\t123456123\t\n" +
                    "联系人\t姓   名\t王五\t联系电话\t18521367458\t\n" +
                    "运输单位1\t单位名称\t上海浦东建筑运输公司\t\n" +
                    "\t联系人\t李总\t联系电话\t18521367450\t\n" +
                    "运输单位……\t单位名称\t\t\n" +
                    "\t联系人\t\t联系电话\t\t\n" +
                    "申请处置情   况\t建筑垃圾种类及产量（万吨）\t1000\t\n" +
                    "\t产生周期\t3年\t\n" +
                    "\t处置单位\t上海浦东城市中心\t\n" +
                    "\t处置规模\t500\t\n" +
                    "\t运输时段\t18：00-21：00\t\n" +
                    "\t处置单位\t审批中心\t联系人及联系电话\t18521367451\t\n" +
                    "\t\t\t\t\t\n" +
                    "\t\t\t\t\t\n" +
                    "（承诺书附后）    法定代表人（委托代理人）签字：   杜甫        2025  年  3月 9 日    申请单位：（章）" +
                    "样例json输出：{" +
                    "applyTime:2025年  3月  9日" +
                    "applyOrg:上海城投公司" +
                    "orgCode:123456123" +
                    "applyName:王五" +
                    "applyMobile:18521367458" +
                    "transportOrgName:上海浦东建筑运输公司" +
                    "transportName:李总" +
                    "transportMobile:18521367450" +
                    "weight:1000" +
                    "productRange:3年" +
                    "deelOrgName:上海浦东城市中心" +
                    "deelOrgWeight:500" +
                    "transportRange:18：00-21：00" +
                    "deelOrgName2:审批中心" +
                    "deelOrgMobile2:18521367451" +
                    "signName:杜甫" +
                    "signTime:2025  年  3月 9 日" +
                    "}");
        }else if ("4".equals(type)) {
            systemMessage.set("content", "我是一名审批局的工作人员，需要从模板中提取签字，" +
                    "解析企业名称对应字段companyName," +
                    "解析组织机构代码对应字段orgCode," +
                    "解析企业住址对应字段companyAddress," +
                    "解析工程项目名称对应字段projectName," +
                    "解析地址：对应字段address," +
                    "解析项目占地面积对应字段area," +
                    "解析施工方式对应字段constructionStyle," +
                    "解析建设单位对应字段buildOrgName," +
                    "解析建设单位联系人对应字段buildUserName," +
                    "解析建设单位联系人对应字段buildUserMobile," +
                    "解析施工单位对应字段constructionOrgName," +
                    "解析施工单位联系人对应字段constructionUserName," +
                    "解析施工单位联系人对应字段constructionUserMobile," +
                    "解析监理单位对应字段supervisionOrgName," +
                    "解析监理单位联系人对应字段supervisionUserName," +
                    "解析监理单位联系人对应字段supervisionUserMobile," +
                    "解析运输单位对应字段transportOrgName," +
                    "解析运输单位联系人对应字段transportUserName," +
                    "解析运输单位联系人对应字段transportUserMobile," +
                    "然后分析所有解析的字段，如果所有字段成功解析并且有值，提示文件正常，否则提示哪些存在问题，例如法定代表人没有填写，把分析结果放在json的remark字段中,输出使用json格式。" +
                    "样例输入内容：一、企业信息（填写工程施工单位信息）\n" +
                    "1.企业名称: 上海量化金融\n" +
                    "2.组织机构代码：98756234\n" +
                    "3.企业住址: 上海浦东秀妍路\n" +
                    "二、工程概况\n" +
                    "1.工程项目名称：上海周浦瑞禾路养老院工程\n" +
                    "2.地址：上海周浦瑞禾路128号\n" +
                    "3.项目占地面积：3000平\n" +
                    "4.施工方式：绿色施工\n" +
                    "三、建筑垃圾产生情况\n" +
                    ".................." +
                    "九、工程各方联系人\n" +
                    "建设单位：上海绿地集团\t联系人：王总\t电话：18521367457\n" +
                    "施工单位：上海立方体公司\t联系人：李总\t电话：18521367456\n" +
                    "监理单位：上海明月公司\t联系人：张总\t电话：18521367451\n" +
                    "运输单位：上海保安大队\t联系人：何总\t电话：18521367452\n" +
                    "十、其他需要说明的情况\n" +
                    "样例json输出：{" +
                    "companyName:上海量化金融" +
                    "orgCode:98756234" +
                    "companyAddress:上海浦东秀妍路" +
                    "projectName:上海周浦瑞禾路养老院工程" +
                    "address:上海周浦瑞禾路128号" +
                    "area:3000平" +
                    "constructionStyle:绿色施工" +
                    "buildOrgName:上海绿地集团" +
                    "buildUserName:王总" +
                    "buildUserMobile:18521367457" +
                    "constructionOrgName:上海立方体公司" +
                    "constructionUserName:李总" +
                    "constructionUserMobile:18521367456" +
                    "supervisionOrgName:上海明月公司" +
                    "supervisionUserName:张总" +
                    "supervisionUserMobile:18521367451" +
                    "transportOrgName:上海保安大队" +
                    "transportOrgName:何总" +
                    "transportUserMobile:18521367452" +
                    "}");
        }




        systemMessage.set("role", "system");

        JSONObject userMessage = new JSONObject();
        userMessage.set("content", textFile);
        userMessage.set("role", "user");

        jsonObject.set("messages", new Object[]{systemMessage, userMessage});
        jsonObject.set("model", "deepseek-chat");
        jsonObject.set("frequency_penalty", 0);
        jsonObject.set("max_tokens", 2048);
        jsonObject.set("presence_penalty", 0);
        jsonObject.set("response_format", new JSONObject().set("type", "json_object"));
        jsonObject.set("stop", null);
        jsonObject.set("stream", false);
        jsonObject.set("stream_options", null);
        jsonObject.set("temperature", 1);
        jsonObject.set("top_p", 1);
        jsonObject.set("tools", null);
        jsonObject.set("tool_choice", "none");
        jsonObject.set("logprobs", false);
        jsonObject.set("top_logprobs", null);

        // 构建 HTTP 请求
        HttpResponse response = HttpRequest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer sk-7e68165c2bb84ca995adbfe6824e3ff6")
                .body(jsonObject.toString())
                .execute();

        // 获取响应结果
        if (response.isOk()) {
            String result = response.body();
            System.out.println("111111");
            System.out.println(result);
            System.out.println("111111");
            JSONObject resultObj = JSONUtil.parseObj(result);

            // 获取choices数组中的第一个元素
            JSONObject choicesItem = resultObj.getJSONArray("choices").getJSONObject(0);
            // 获取message对象
            JSONObject messageObject = choicesItem.getJSONObject("message");
            // 获取content字符串
            String contentStr = messageObject.getStr("content");
            // 解析content字符串
            JSONObject contentObject = JSONUtil.parseObj(contentStr);
            // 获取signName的值
//            String signName = contentObject.getStr("signName");
//            System.out.println("signName的值为: " + signName);

            return new SuccessResponseData<>(contentObject);
        } else {
            String result = response.body();
            System.out.println(result);
            System.out.println("请求失败，状态码：" + response.getStatus());
//            System.out.println("请求失败，状态码：" + response.bo());
        }

        return new SuccessResponseData<>();
    }

    public String parseWordFile(InputStream inputStream) throws IOException, TikaException, SAXException {
        Parser parser = new OfficeParser(); // 强制使用 Word 解析器
        ContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();

        parser.parse(inputStream, handler, metadata,parseContext);
        return handler.toString();
    }
    public static String getLastUserContent(ChatBoxRequest request) {
        List<ChatBoxMessage> messages = request.getMessages();
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        // 倒序遍历消息列表
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatBoxMessage message = messages.get(i);
            if ("user".equals(message.getRole())) {
                return message.getContent();
            }
        }
        return null;
    }
    @PostResource(name = "上传文件", path = "/deepseek/smartAsk", requiredPermission = false,requiredLogin = false)
    public ResponseData<Object> smartAsk(@RequestBody(required = true) ChatBoxRequest chatBoxRequest) throws IOException, TikaException {

        String userQuestion = getLastUserContent(chatBoxRequest);
        if (ObjectUtil.isEmpty(userQuestion)) {
            return new ErrorResponseData<>("408","messages不能为空，且用户有需要咨询的问题");
        }
// 构建请求的 JSON 数据
        JSONObject jsonObject = new JSONObject();

        // 构建 messages 数组
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("content", "审批局里面有很多办理事项，需要开发智能客服提供政策咨询和办理事项咨询服务能力并且通过引导式对话，主动回复和反问客户信息，\n" +
                "需要实现场景样例如下，以下对话交互：\n" +
                "当用户询问 “我想问建筑垃圾办理的流程” 时，智能客服回复 “有企业提交待材料，审批局办公人员接收和审批材料，然后现场勘测，且审核通过，就发放运营牌照” 。\n" +
                "当用户继续询问 “企业需要提交哪些材料呢” 时，智能客服能给出相应准确的回复（目前该问题回复内容未给出，需补充完善相关知识内容以便智能客服准确回答）。\n" +
                "\n" +
                "\n" +
                "1、当用户问第一个问题，根据的问题进行回答，当不清楚客户问题，就回复“有什么地方可以帮助你！”\n" +
                "2、针对用户下一个问题生成的逻辑主要如下：\n" +
                "基于流程图谱：根据节点顺序推荐下一步问题（如选址后→营业执照）。  \n" +
                "基于用户画像：根据用户身份（个人/企业）调整问题优先级。  \n" +
                "基于缺失信息：需要提交哪些审批材料\n" +
                "3、城市建筑垃圾事项办理流程主要有企业提交待材料，审批局办公人员接收和审批材料，然后现场勘测，且审核通过，就发放运营牌照\n" +
                "4、当超出问题范围，在AI无法回答时，提供人工客服联系方式。\n" +
                "下面是城市建筑垃圾产生核准首次申请完整信息\n" +
                "（一）办理条件：\n" +
                "1.提交书面申请（施工、建设单位与运输单位签订的合同、与处置单位签订的合同）；\n" +
                "2.具有建筑垃圾分类处置的方案和对废混凝土、金属、木材等回收利用的方案。\n" +
                "（二）申请材料名称、来源、数量及介质要求：\n" +
                "1.城市建筑垃圾产生核准申请表（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "2.建筑垃圾运输、处置合同（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "3.法定代表人（机构负责人）身份证（原件0份，复印件1份，纸质；来源：申请人自备）\n" +
                "4.防止环境污染和控制突发事件的应急预案（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "5.处置设施名称、业主单位受纳情况清单（含接收量、受纳余量）（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "6.受纳地县级以上行政主管部门意见（原件1份，复印件0份，纸质；来源：聊城市城市管理局）\n" +
                "7.分类处置和回收利用方案（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "（三）办理方式：聊城市政务服务中心或山东政务服务网（聊城站）办理\n" +
                "（四）受理窗口：山东省聊城市东昌府区昌润路153号聊城市政务服务中心1楼统一受理窗口\n" +
                "（五）受理窗口工作时间：\n" +
                "3月至9月上午8:30-11:30，下午14:00-17:30；10月至次年2月上午8:30-11:30，下午13:30-17:00。（节假日除外） \n" +
                "（六）法定期限：20个工作日\n" +
                "（七）承诺期限：1工作日\n" +
                "（八）是否收费：不收费\n" +
                "（九）收费依据及标准：\n" +
                "（十）受理窗口电话：0635-8903506 \n" +
                "业务咨询电话：0635-8902662\n" +
                "（十一）办理进程和结果查询：\n" +
                "聊城市政务服务网\n" +
                "http://zwfw.sd.gov.cn/col/col1562/index.html\n" +
                "（十二）监督电话：0635-12345\n" +
                "详版请登陆山东政务服务网（聊城站点）查询或下载。\n" +
                "\n" +
                "审批局还提供了政策文件和事项办理流程文件。\n" +
                "上述背景，我希望通过调用AI的能力，让AI实现智能客服实现友好的交互。\n" +
                "输出使用json格式。\n" +
                "分析用户办理事项对应字段 deelType 对应可以为空\n" +
                "生成下一个用户的问题对应字段 nextQuestion\n" +
                "回答当前的问题对应字段 answer，\n" +
                "用户推荐问题列表 nextQuestionList\n" +
                "返回json样例\n" +
                "{\n" +
                "answer：\n" +
                "deelType：\n" +
                "nextQuestion：还有什么可以帮你的吗\n" +
                "nextQuestionList：[]\n" +
                "}");


        systemMessage.set("role", "system");

        JSONObject userMessage = new JSONObject();
        userMessage.set("content", userQuestion);
        userMessage.set("role", "user");

        jsonObject.set("messages", new Object[]{systemMessage, userMessage});
        jsonObject.set("model", "deepseek-chat");
        jsonObject.set("frequency_penalty", 0);
        jsonObject.set("max_tokens", 2048);
        jsonObject.set("presence_penalty", 0);
        jsonObject.set("response_format", new JSONObject().set("type", "json_object"));
        jsonObject.set("stop", null);
        jsonObject.set("stream", false);
        jsonObject.set("stream_options", null);
        jsonObject.set("temperature", 1);
        jsonObject.set("top_p", 1);
        jsonObject.set("tools", null);
        jsonObject.set("tool_choice", "none");
        jsonObject.set("logprobs", false);
        jsonObject.set("top_logprobs", null);

        // 构建 HTTP 请求
        HttpResponse response = HttpRequest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer sk-7e68165c2bb84ca995adbfe6824e3ff6")
                .body(jsonObject.toString())
                .execute();

        // 获取响应结果
        if (response.isOk()) {
            String result = response.body();
            System.out.println("111111");
            System.out.println(result);
            System.out.println("111111");
            JSONObject resultObj = JSONUtil.parseObj(result);

            // 获取choices数组中的第一个元素
            JSONObject choicesItem = resultObj.getJSONArray("choices").getJSONObject(0);
            // 获取message对象
            JSONObject messageObject = choicesItem.getJSONObject("message");
            // 获取content字符串
            String contentStr = messageObject.getStr("content");
            // 解析content字符串
            JSONObject contentObject = JSONUtil.parseObj(contentStr);
            // 获取signName的值
//            String signName = contentObject.getStr("signName");
//            System.out.println("signName的值为: " + signName);

            return new SuccessResponseData<>(contentObject);
        } else {
            String result = response.body();
            System.out.println(result);
            System.out.println("请求失败，状态码：" + response.getStatus());
//            System.out.println("请求失败，状态码：" + response.bo());
        }

        return new SuccessResponseData<>();
    }




    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostResource(name = "上传文件", path = "/deepseek/smartAskByStream", requiredPermission = false, requiredLogin = false,produces = "text/event-stream")
    public ResponseEntity<ResponseBodyEmitter> smartAskByStream(@RequestBody(required = true) ChatBoxRequest chatBoxRequest) {
        String userQuestion = getLastUserContent(chatBoxRequest);
        if (ObjectUtil.isEmpty(userQuestion)) {
            // 这里可以根据实际情况返回错误响应
            return ResponseEntity.badRequest().body(null);
        }

        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        executorService.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = buildRequest(userQuestion);

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            processStreamResponse(responseBody, emitter);
                        }
                    } else {
                        String result = Objects.requireNonNull(response.body()).string();
                        System.err.println("请求失败，状态码：" + response.code() + "，响应内容：" + result);
                        emitter.completeWithError(new IOException("请求 DeepSeek API 失败，状态码：" + response.code()));
                    }
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });

        return ResponseEntity.ok(emitter);
    }

    private Request buildRequest(String userQuestion) {
        JSONObject jsonObject = buildRequestJson(userQuestion);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
        return new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer sk-7e68165c2bb84ca995adbfe6824e3ff6")
                .post(body)
                .build();
    }

    private JSONObject buildRequestJson(String userQuestion) {
        JSONObject jsonObject = new JSONObject();

        JSONObject systemMessage = new JSONObject();
        systemMessage.set("content", "审批局里面有很多办理事项，需要开发智能客服提供政策咨询和办理事项咨询服务能力并且通过引导式对话，主动回复和反问客户信息，\n" +
                "需要实现场景样例如下，以下对话交互：\n" +
                "当用户询问 “我想问建筑垃圾办理的流程” 时，智能客服回复 “有企业提交待材料，审批局办公人员接收和审批材料，然后现场勘测，且审核通过，就发放运营牌照” 。\n" +
                "当用户继续询问 “企业需要提交哪些材料呢” 时，智能客服能给出相应准确的回复（目前该问题回复内容未给出，需补充完善相关知识内容以便智能客服准确回答）。\n" +
                "\n" +
                "\n" +
                "1、当用户问第一个问题，根据的问题进行回答，当不清楚客户问题，就回复“有什么地方可以帮助你！”\n" +
                "2、针对用户下一个问题生成的逻辑主要如下：\n" +
                "基于流程图谱：根据节点顺序推荐下一步问题（如选址后→营业执照）。  \n" +
                "基于用户画像：根据用户身份（个人/企业）调整问题优先级。  \n" +
                "基于缺失信息：需要提交哪些审批材料\n" +
                "3、城市建筑垃圾事项办理流程主要有企业提交待材料，审批局办公人员接收和审批材料，然后现场勘测，且审核通过，就发放运营牌照\n" +
                "4、当超出问题范围，在AI无法回答时，提供人工客服联系方式。\n" +
                "下面是城市建筑垃圾产生核准首次申请完整信息\n" +
                "（一）办理条件：\n" +
                "1.提交书面申请（施工、建设单位与运输单位签订的合同、与处置单位签订的合同）；\n" +
                "2.具有建筑垃圾分类处置的方案和对废混凝土、金属、木材等回收利用的方案。\n" +
                "（二）申请材料名称、来源、数量及介质要求：\n" +
                "1.城市建筑垃圾产生核准申请表（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "2.建筑垃圾运输、处置合同（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "3.法定代表人（机构负责人）身份证（原件0份，复印件1份，纸质；来源：申请人自备）\n" +
                "4.防止环境污染和控制突发事件的应急预案（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "5.处置设施名称、业主单位受纳情况清单（含接收量、受纳余量）（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "6.受纳地县级以上行政主管部门意见（原件1份，复印件0份，纸质；来源：聊城市城市管理局）\n" +
                "7.分类处置和回收利用方案（原件1份，复印件0份，纸质；来源：申请人自备）\n" +
                "（三）办理方式：聊城市政务服务中心或山东政务服务网（聊城站）办理\n" +
                "（四）受理窗口：山东省聊城市东昌府区昌润路153号聊城市政务服务中心1楼统一受理窗口\n" +
                "（五）受理窗口工作时间：\n" +
                "3月至9月上午8:30-11:30，下午14:00-17:30；10月至次年2月上午8:30-11:30，下午13:30-17:00。（节假日除外） \n" +
                "（六）法定期限：20个工作日\n" +
                "（七）承诺期限：1工作日\n" +
                "（八）是否收费：不收费\n" +
                "（九）收费依据及标准：\n" +
                "（十）受理窗口电话：0635-8903506 \n" +
                "业务咨询电话：0635-8902662\n" +
                "（十一）办理进程和结果查询：\n" +
                "聊城市政务服务网\n" +
                "http://zwfw.sd.gov.cn/col/col1562/index.html\n" +
                "（十二）监督电话：0635-12345\n" +
                "详版请登陆山东政务服务网（聊城站点）查询或下载。\n" +
                "\n" +
                "审批局还提供了政策文件和事项办理流程文件。\n" +
                "上述背景，我希望通过调用AI的能力，让AI实现智能客服实现友好的交互。\n" +
                "输出使用json格式。\n" +
                "分析用户办理事项对应字段 deelType 对应可以为空\n" +
                "生成下一个用户的问题对应字段 nextQuestion\n" +
                "回答当前的问题对应字段 answer，\n" +
                "用户推荐问题列表 nextQuestionList\n" +
                "返回json样例\n" +
                "{\n" +
                "answer：\n" +
                "deelType：\n" +
                "nextQuestion：还有什么可以帮你的吗\n" +
                "nextQuestionList：[]\n" +
                "}");
        systemMessage.set("role", "system");

        JSONObject userMessage = new JSONObject();
        userMessage.set("content", userQuestion);
        userMessage.set("role", "user");

        jsonObject.set("messages", new Object[]{systemMessage, userMessage});
        jsonObject.set("model", "deepseek-chat");
        jsonObject.set("frequency_penalty", 0);
        jsonObject.set("max_tokens", 2048);
        jsonObject.set("presence_penalty", 0);
        jsonObject.set("response_format", new JSONObject().set("type", "json_object"));
        jsonObject.set("stop", null);
        jsonObject.set("stream", true);
        jsonObject.set("stream_options", null);
        jsonObject.set("temperature", 1);
        jsonObject.set("top_p", 1);
        jsonObject.set("tools", null);
        jsonObject.set("tool_choice", "none");
        jsonObject.set("logprobs", false);
        jsonObject.set("top_logprobs", null);
        return jsonObject;
    }

    private void processStreamResponse(ResponseBody responseBody, ResponseBodyEmitter emitter) throws IOException {
        try (BufferedSource source = responseBody.source()) {
            String line;
            while ((line = source.readUtf8Line()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    if (!"[DONE]".equals(data)) {
                        JSONObject resultObj = JSONUtil.parseObj(data);
                        JSONArray choices = resultObj.getJSONArray("choices");
                        if (choices != null && !choices.isEmpty()) {
                            JSONObject choicesItem = choices.getJSONObject(0);
                            JSONObject messageObject = choicesItem.getJSONObject("message");
                            if (messageObject != null) {
                                String deltaContent = messageObject.getStr("content");
                                if (deltaContent != null) {
                                    emitter.send(deltaContent);
                                }
                            }
                        }
                    }
                }
            }
        }
        emitter.complete();
    }




    public static void main(String[] args) {
// 构建请求的 JSON 数据
        JSONObject jsonObject = new JSONObject();

        // 构建 messages 数组
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("content", "我是一名审批局的工作人员，需要从模板中提取签字。下面是模板文件内容，城市建筑垃圾处置承诺书\n" +
                "\n" +
                "\n" +
                "    根据《国务院对确需保留的行政审批项目设定行政许可的决定》《城市建筑垃圾管理规定》《山东省城镇容貌和环境卫生管理办法》《建设部关于纳入国务院决定的十五项行政许可的条件的规定》，本单位（建设单位）为申请城市建筑垃圾处置核准，作出如下承诺：\n" +
                "一、本单位已经与运输单位签订建筑垃圾运输合同，明确约定建筑垃圾运输的时间、路线和处置地点，不将建筑垃圾交给个人或者未取得建筑垃圾处置核准的运输单位运输；\n" +
                "二、运输建筑垃圾的车辆，随车携带建筑垃圾处置核准证件，按照批准的时间、路线、数量，将建筑垃圾运送到指定的处理场所，不擅自丢弃；\n" +
                "三、建筑垃圾消纳场属于集体所有土地的，已经取得集体经济组织同意；属于国有土地的，已经取得登记的土地使用权人或有关行政主管部门同意；\n" +
                "四、有消纳场的场地平面图、进场路线图、有相应的摊铺、碾压、除尘、照明等机械和设备，有排水、消防等设施，有健全的环境卫生和安全管理制度并得到有效执行；\n" +
                "\n" +
                "共2页，第1页    申请人（签字、盖章）：\n");
        systemMessage.set("role", "system");

        JSONObject userMessage = new JSONObject();
        userMessage.set("content", "Hi");
        userMessage.set("role", "user");

        jsonObject.set("messages", new Object[]{systemMessage, userMessage});
        jsonObject.set("model", "deepseek-chat");
        jsonObject.set("frequency_penalty", 0);
        jsonObject.set("max_tokens", 2048);
        jsonObject.set("presence_penalty", 0);
        jsonObject.set("response_format", new JSONObject().set("type", "json_object"));
        jsonObject.set("stop", null);
        jsonObject.set("stream", false);
        jsonObject.set("stream_options", null);
        jsonObject.set("temperature", 1);
        jsonObject.set("top_p", 1);
        jsonObject.set("tools", null);
        jsonObject.set("tool_choice", "none");
        jsonObject.set("logprobs", false);
        jsonObject.set("top_logprobs", null);

        // 构建 HTTP 请求
        HttpResponse response = HttpRequest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer sk-7e68165c2bb84ca995adbfe6824e3ff6")
                .body(jsonObject.toString())
                .execute();

        // 获取响应结果
        if (response.isOk()) {
            String result = response.body();
            System.out.println(result);
        } else {
            System.out.println("请求失败，状态码：" + response.getStatus());
        }
    }
}
