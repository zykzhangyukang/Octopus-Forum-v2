
package com.zhangyu.coderman.web.controller;

import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.web.cache.HotTagCache;
import com.zhangyu.coderman.web.dao.TopicMapper;
import com.zhangyu.coderman.web.dao.UserMapper;
import com.zhangyu.coderman.web.dto.NewUserDTO;
import com.zhangyu.coderman.web.dto.QuestionDTO;
import com.zhangyu.coderman.web.dto.QuestionQueryDTO;
import com.zhangyu.coderman.web.dto.ResultTypeDTO;
import com.zhangyu.coderman.web.exception.CustomizeException;
import com.zhangyu.coderman.web.modal.Question;
import com.zhangyu.coderman.web.modal.User;
import com.zhangyu.coderman.web.modal.UserExample;
import com.zhangyu.coderman.web.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.web.myenums.UserType;
import com.zhangyu.coderman.web.provider.AliYunCodeProivder;
import com.zhangyu.coderman.web.service.QuestionService;
import com.zhangyu.coderman.web.service.UserService;
import com.zhangyu.coderman.web.utils.CookieUtils;
import com.zhangyu.coderman.web.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    protected static final Logger logger = LoggerFactory.getLogger(IndexController.class);


    @Value("${chatId}")
    private String chatId;

    @Value("${chatKey}")
    private String chatKey;


    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private HotTagCache hotTagCache;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AliYunCodeProivder aliYunCodeProivder;

    @Autowired
    private TopicMapper topicMapper;

    /**
     * 验证用户名是否被占用
     * @return
     */
    @GetMapping("/ajaxNameUsed")
    @ResponseBody
    public ResultTypeDTO ajaxNameUsed(@RequestParam("username") String username){
        if(username!=null&&username!=""){
            UserExample example = new UserExample();
            example.createCriteria().andNameEqualTo(username.trim());
            List<User> userList = userMapper.selectByExample(example);
            if(userList.size()>0){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.USERNAME_IS_USED);
            }
        }
        return new ResultTypeDTO().okOf();
    }

    /**
     * 手机验证码
     * @param phone
     * @param request
     * @return
     */
    @PostMapping("/sendPhone")
    @ResponseBody
    public ResultTypeDTO sendCodeToPhone(@RequestParam("phone") String phone,HttpServletRequest request){
        String code = null;
        try {
            code = aliYunCodeProivder.sendCodeToPhone(phone);
        } catch (Exception e) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.SEND_CODE_ERROR);
        }
        request.getSession().setAttribute("phoneCode",code);
        return new ResultTypeDTO().okOf().addMsg("msg","成功发送");
    }
    /**
     * 用户登入
     * @param username
     * @param password
     * @param response
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultTypeDTO login(@RequestParam("username") String username,@RequestParam("password") String password
                               ,@RequestParam("code") String code,
                               HttpServletResponse response,HttpServletRequest request){
        String loginCode= (String) request.getSession().getAttribute("loginCode");
        if(username==null&&username.trim()==""&&password!=null&&password==""){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.NAME_AND_PASSWORD_CANT_EMPTY);
        }
        if(code==null||"".equals(code)){
            return new ResultTypeDTO().errorOf("验证码不能为空");
        }else if(!code.equalsIgnoreCase(loginCode)){
            return new ResultTypeDTO().errorOf("验证码错误");
        }
        UserExample example = new UserExample();
        String encodeByMd5pw="";
        try {
            encodeByMd5pw= encodeByMd5(password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //判断该用户名是否存在

        example.createCriteria().andNameEqualTo(username).andPasswordEqualTo(encodeByMd5pw);
        List<User> userList = userMapper.selectByExample(example);
        if(userList.size()>0){
            User user=userList.get(0);
            //登入成功
            //logger.info("用户登入成功:{}",githubUser);
            //注册聊天
            long time = System.currentTimeMillis();
            HttpSession session = request.getSession();
            session.setAttribute("user",user);
            session.setAttribute("xlm_wid", chatId);
            session.setAttribute("xlm_uid", user.getId());
            session.setAttribute("xlm_name", user.getName());
            session.setAttribute("xlm_avatar", user.getAvatarUrl());
            session.setAttribute("xlm_time", time);
            String string = 14876 + "_" + user.getId() + "_" + time + "_" + chatKey;
            string = AuthorizeController.encryptPasswordWithSHA512(string).toLowerCase();
            session.setAttribute("xlm_hash", string);
            //聊天cookie
            Cookie[] cookies = request.getCookies();
            Cookie chatCookie = CookieUtils.findCookieByName(cookies, "JSESSIONID");
            if(chatCookie!=null){
                chatCookie.setMaxAge(3600 * 24);
                session.setMaxInactiveInterval(3600 * 24);
                response.addCookie(chatCookie);
            }
            return new ResultTypeDTO().okOf();
        }
        return new ResultTypeDTO().errorOf(CustomizeErrorCode.LOGIN_FAIL);
    }

    /**
     * 注册
     * @param name
     * @param password1
     * @param phone
     * @param password2
     * @param code
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public ResultTypeDTO register(@RequestParam(value = "name") String name,
                                  @RequestParam("password1") String password1,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("password2") String password2,
                                  @RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response){
        String regCode = (String) request.getSession().getAttribute("regCode");
        if(name.trim()==null&&name.trim()==""){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NAME_NOT_EMPTY);
        }
        if(!password1.equals(password2)){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.PASSWORD_TWICE_EQ);
        }
        if(phone==null||"".equals(phone.trim())){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.PHONE_NOT_EMPTY);
        }
        if(regCode==null||!regCode.equalsIgnoreCase(code)){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.CODE_NOT_SUCCESS);
        }
        //验证用户名是否可用
        UserExample example1 = new UserExample();
        example1.createCriteria().andNameEqualTo(name);
        List<User> userList1 = userMapper.selectByExample(example1);
        if(userList1.size()>0){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USERNAME_IS_USED);
        }
        //判断该电话号码已被注册
        UserExample example = new UserExample();
        example.createCriteria().andAccountIdEqualTo(phone);
        List<User> userList = userMapper.selectByExample(example);
        if(userList.size()>0){
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.THIS_PHONE_USED);
        }
        //注册用户
        User user=new User();
        user.setUserType(UserType.PHONE.getCode());
        user.setName(name);
        user.setAccountId(phone);
        user.setAvatarUrl("https://www.zykcoderman.xyz/resources/res/images/avatar/default.png");
        user.setAccountId(phone);
        user.setGmtCreate(System.currentTimeMillis());
        try {
            String password = encodeByMd5(password1);
            user.setPassword(password);
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            System.out.println("login error");
        }
        user.setGmtModified(System.currentTimeMillis());
        //token
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        userMapper.insertSelective(user);
        Cookie c = new Cookie("token", token);
        c.setMaxAge(3600 * 24);//设置cookie的时长为24小时(登入的token)
        response.addCookie(c);
        //注册成功
        //logger.info("用户登入成功:{}",githubUser);
        //注册聊天
        long time = System.currentTimeMillis();
        HttpSession session = request.getSession();
        session.setAttribute("xlm_wid", chatId);
        session.setAttribute("xlm_uid", user.getId());
        session.setAttribute("xlm_name", user.getName());
        session.setAttribute("xlm_avatar", user.getAvatarUrl());
        session.setAttribute("xlm_time", time);
        String string = 14876 + "_" + user.getId() + "_" + time + "_" + chatKey;
        string = AuthorizeController.encryptPasswordWithSHA512(string).toLowerCase();
        session.setAttribute("xlm_hash", string);
        //聊天cookie
        Cookie[] cookies = request.getCookies();
        Cookie chatCookie = CookieUtils.findCookieByName(cookies, "JSESSIONID");
        if(chatCookie!=null){
            chatCookie.setMaxAge(3600 * 24);
            session.setMaxInactiveInterval(3600 * 24);
            response.addCookie(chatCookie);
        }
        return new ResultTypeDTO().okOf();
    }
    //贴子分类
    @ResponseBody
    @GetMapping("/explore/{action}")
    private String explore(@PathVariable("action") String action,
                           @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize,
                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                           HttpServletRequest request,
                           Map<String, Object> map) {
        String[] split = action.split("-");
        Integer categoryVal = 0;
        try {
            categoryVal = Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.NOT_FOND_CATEGORY);
        }
        PageInfo<Question> questionPageInfo;
        if (categoryVal == 0) {
            request.getSession().setAttribute("category", categoryVal);
            return "forward:/";
        }
        questionPageInfo = questionService.findQuestionsByCategory(pageNo, pageSize, categoryVal);
        //最新用户
        List<NewUserDTO> userList = userService.findNewsUsers(8);
        //热门标签
        //List<String> tags = hotTagCache.updateTags();

        //最新问题
        List<QuestionDTO> NewQuestions = questionService.findNewQuestion(6);

        map.put("sort", "all");//全部
        request.getSession().setAttribute("category", categoryVal);
        map.put("newQuestions", NewQuestions);
        map.put("users", userList);
        map.put("page", questionPageInfo);
        map.put("navLi", "find");
       // map.put("hotTags", tags);
        return "redirect:/index.html";
    }
    //首页
    @RequestMapping("/")
    public String index(@RequestParam(value = "tag", required = false) String tag,
                        @RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "category", defaultValue = "0") String category,
                        Map<String, Object> map) {
        map.put("tag", tag);
        map.put("search", search);
        map.put("category", category);
        map.put("navLi","find");
        return "index";
    }
    //ajax加载问题列表
    @ResponseBody
    @GetMapping("/loadQuestionList")
    public ResultTypeDTO listQuestion(@RequestParam(name = "sortby", defaultValue = "ALL") String sort,
                                      @RequestParam(name = "search", required = false) String search,
                                      @RequestParam(name = "tag", required = false) String tag,
                                      @RequestParam(name = "pageSize", defaultValue = "35") Integer pageSize,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "category", defaultValue = "0") String categoryStrVal,
                                      HttpServletRequest request) {
        Integer category = null;
        try {
            category = Integer.parseInt(categoryStrVal);
        } catch (NumberFormatException e) {

        }
        //获取首页列表数据
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setSort(sort);
        questionQueryDTO.setTag(tag);
        questionQueryDTO.setCategory(category);
        questionQueryDTO.setPageNo(pageNo);
        questionQueryDTO.setPageSize(pageSize);
        PageInfo<Question> questionPageInfo = questionService.getPageBySearch(questionQueryDTO);
        request.getSession().setAttribute("category", category);//全部
        return new ResultTypeDTO().okOf().addMsg("page", questionPageInfo);
    }
    //加载右边的数据
    @ResponseBody
    @GetMapping("/loadRightList")
    public ResultTypeDTO loadRightList() {
        //最新用户
        List<NewUserDTO> userList = userService.findNewsUsers(6);
        //热门标签
        List<String> hot = hotTagCache.updateTags();
        //List<String> hot = redisTemplate.opsForList().range("hot", 0, -1);
        //最新问题
        List<QuestionDTO> NewQuestions = questionService.findNewQuestion(7);
        //问题推荐
        int pageno = 1;
        int pagesize = 10;
        List<QuestionDTO> RecommendQuestions = questionService.findRecommendQuestions(pageno, pagesize);
        return new ResultTypeDTO().okOf().addMsg("userList", userList)
                .addMsg("tags", hot)
                .addMsg("newQuestions", NewQuestions)
                .addMsg("recommend", RecommendQuestions);
                //.addMsg("hotTopic",topicPageInfo);
    }

    /**
     * MD5加密
     * @param string
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String encodeByMd5(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder base64Encoder = Base64.getEncoder();
        // 加密字符串
        return base64Encoder.encodeToString(md5.digest(string.getBytes("utf-8")));
    }
}