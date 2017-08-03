package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.python.core.PyInteger;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by pengwan on 2017/8/3.
 */
public class CalculateUtilTest {
    @Test
    public void test(){
        String filePath = "/Users/pengwan/IdeaProjects/EnvelopeDemo/src/main/resources/data/hd_038_fdj_qdd_jx_25600_jsd_1100.451RPM_20170715165953.txt";

        //String filePathb = "/var/alldata/a.txt";

        //拿到采样频率
        String[] pathArray = filePath.split("_");
        Long cypl = Long.valueOf(pathArray[5]);
        Long step = 1*1000000/cypl;

        //数据预处理
        String txt = FileReaderUtils.readTxtFileForMap(filePath).toString();
        List<Double> pythonList = FileReaderUtils.readTxtFile(filePath);
        String finaltxt = txt.replace("=",":").replace(" ","");

        //获得频谱数据
        //String s1 = CalculateUtil.getPinPuData(finaltxt,1000L,"info");
        //System.out.println(s1);

        //获得包络
        String s2 = CalculateUtil.getBaoLuoPuData(finaltxt,step,"info",2500.0,3000.0,200L);
        System.out.println(s2);

        //转化数据
        JsonParser jp = new JsonParser();
        Gson gs = new Gson();
    }


}