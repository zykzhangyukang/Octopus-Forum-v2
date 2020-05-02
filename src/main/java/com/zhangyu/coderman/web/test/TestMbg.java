package com.zhangyu.coderman.web.test;

import com.zhangyu.coderman.web.controller.AuthorizeController;
import com.zhangyu.coderman.web.myenums.QuestionSortType;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestMbg {

    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("mbg.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
    @Test
    public void testprint(){
        String s = AuthorizeController.encryptPasswordWithSHA512("14876_1_1481673726_srzpDr45DsmSvbV3xnVfFRIYq9URgPxN");
        System.out.println(s);
    }
    @Test
    public void testenum(){
        System.out.println(QuestionSortType.ALL.name().equals("ALL"));
    }
}
