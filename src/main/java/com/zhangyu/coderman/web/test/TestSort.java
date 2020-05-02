package com.zhangyu.coderman.web.test;

import org.junit.Test;

public class TestSort {

    @Test
    public void testsortarr(){
        int[] array=new int[]{2,4,2,1,1,6,8,3};
        for (int i = 0; i < array.length-1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if(array[j]<array[j+1]){
                    int temp=array[j];
                    array[j]=array[j+1];
                    array[j+1]=temp;
                }
            }
        }

        for (int i : array) {
            System.out.print(i);
        }
    }
    @Test
    public void testrandom(){
        int a=(int)(Math.random()*10+1);
        System.out.println(a);
    }
}
