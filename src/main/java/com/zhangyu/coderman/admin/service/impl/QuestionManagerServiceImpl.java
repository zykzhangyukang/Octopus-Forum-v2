package com.zhangyu.coderman.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.admin.service.QuestionManagerService;
import com.zhangyu.coderman.admin.vo.PageVO;
import com.zhangyu.coderman.admin.vo.QuestionVO;
import com.zhangyu.coderman.web.dao.QuestionExtMapper;
import com.zhangyu.coderman.web.dao.QuestionMapper;
import com.zhangyu.coderman.web.modal.Question;
import com.zhangyu.coderman.web.modal.QuestionExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/12 11:06
 * @Version 1.0
 **/
@Service
public class QuestionManagerServiceImpl implements QuestionManagerService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;


    @Override
    public PageVO<Question> list(QuestionVO QuestionVO) {
        PageHelper.startPage(QuestionVO.getPage(),QuestionVO.getLimit());
        QuestionExample example = new QuestionExample();

        example.setOrderByClause("gmt_create desc");
        if(QuestionVO.getTitle()!=null&&!"".equalsIgnoreCase(QuestionVO.getTitle())){
            example.createCriteria().andTitleLike("%"+QuestionVO.getTitle()+"%");
        }
        if(QuestionVO.getTag()!=null&&!"".equals(QuestionVO.getTag())){
            example.createCriteria().andTagLike("%"+QuestionVO.getTag()+"%");
        }

        List<Question> questions = questionMapper.selectByExample(example);

        PageInfo<Question> pageInfo = new PageInfo<>(questions);
        return new PageVO<>(pageInfo.getTotal(),pageInfo.getList());
    }
    @Override
    public void delete(Integer id) {
        questionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateTop(QuestionVO questionVO) {
        questionExtMapper.updateTop(questionVO.getId(),questionVO.getTop());
    }

}
