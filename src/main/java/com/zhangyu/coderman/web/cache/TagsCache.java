package com.zhangyu.coderman.web.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsCache {

    private String tagTitle;

    private List<String> tags=new ArrayList<>();

    public static List<TagsCache> getTagsCache(){

        List<TagsCache> tagsCaches = new ArrayList<>();

        TagsCache t1 = new TagsCache();
        t1.setTagTitle("编程语言");
        t1.setTags(Arrays.asList("Java","CSS","Html","JavaScript","shell","C#","R语言",
                "PHP","go","html5","C++","sql","vb","c语言","Go语言","Swift","Python","OC","汇编"));

        TagsCache t2 = new TagsCache();
        t2.setTagTitle("框架");
        t2.setTags(Arrays.asList("Spring","Spring Boot","Struts2","JPA","Spring MVC","Hibernate","Bootstrap","VUE"));

        TagsCache t3 = new TagsCache();
        t3.setTagTitle("开发工具");
        t3.setTags(Arrays.asList("MySql","Tomcat","Oracle","IDEA","HBuilder","Postman",
                " GitHub","MyEclipse","Eclipse","NetBeans","android studio","XScope","Xcode","APICloud"));

        TagsCache t5 = new TagsCache();
        t5.setTagTitle("数据库");
        t5.setTags(Arrays.asList("MySql","Redis","Oracle","Post greSQL","Access","FileMaker","Clipper","foshub"
                ,"Sybase","FoxPro","SQL Server"));

        TagsCache t4 = new TagsCache();
        t4.setTagTitle("操作系统");
        t4.setTags(Arrays.asList("center os","windows","ubuntu","鸿蒙","windows 7","Windows 10","Mac OS","Android"));

        tagsCaches.add(t1);
        tagsCaches.add(t2);
        tagsCaches.add(t3);
        tagsCaches.add(t4);
        tagsCaches.add(t5);
        return tagsCaches;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
