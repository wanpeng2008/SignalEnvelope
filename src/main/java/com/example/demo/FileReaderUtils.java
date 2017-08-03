package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rex on 2017/7/27.
 */
public class FileReaderUtils {
    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     * 返回一个包含40000个值的list
     */
    public static List<Double> readTxtFile(String filePath) {
        List<Double> list = new ArrayList<>(20000);
        try {
            String encoding = "gb2312";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null && list.size() < 40000) {
                    //System.out.println(lineTxt);
                    Double txt = Double.valueOf(lineTxt);
                    list.add(txt);
                }
                read.close();
                return list;
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
    public static List<Map<String,Object>> readTxtFileForMap(String filePath) {
        List<Map<String,Object>> list = new ArrayList<>(20000);
        try {
            String encoding = "gb2312";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null && list.size() < 40000) {
                    //System.out.println(lineTxt);
                    Double txt = Double.valueOf(lineTxt);
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("\'info\'",txt);
                    map.put("\'time\'",Double.valueOf(0));
                    list.add(map);
                }
                read.close();
                return list;
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return list;

    }

    public static void main(String[] args) {
        String filePath = "/var/alldata/华电内蒙乌宁巴图风电场_024_齿轮箱_三级驱动_径向_25600_加速度_998.8969RPM_20170727084623.txt";
        String s = readTxtFileForMap(filePath).toString();
        System.out.println(s);

    }
}
