package com.example.demo;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by pengwan on 2017/8/3.
 */

public class CalculateByPython {
    public void init() {
        PySystemState sys = new PySystemState();
        System.out.println(sys.path.toString());    // previous
        sys = Py.getSystemState();
        sys.path.add("/Library/Frameworks/Python.framework/Versions/2.7/lib");
        System.out.println(sys.path.toString());   // later
    }
    public void calculate(String pythonFilePath, String inputDataFilePath, int minFreq, int maxFreq, int samplingFreq, int steps) {
        //this.init();
        PyArray pyArrayFromFile = null;
        try {
            //pyArrayFromFile = this.getPyArrayFromFile(inputDataFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(pythonFilePath);
        /*PyFunction func = interpreter.get("spe_env", PyFunction.class);
        PyObject[] args = {new PyInteger(minFreq), new PyInteger(maxFreq), new PyInteger(samplingFreq), new PyInteger(steps), pyArrayFromFile};*/
        PyFunction func = interpreter.get("test", PyFunction.class);
        PyObject[] args = {new PyInteger(minFreq), new PyInteger(maxFreq), new PyInteger(samplingFreq), new PyInteger(steps), new PyString(inputDataFilePath) {
            @Override
            protected PyObject pyget(int i) {
                return null;
            }

            @Override
            protected PyObject getslice(int i, int i1, int i2) {
                return null;
            }

            @Override
            protected PyObject repeat(int i) {
                return null;
            }
        }};
        PyObject pyobj = func.__call__(args);
        System.out.println("answer = " + pyobj.toString());

    }

    private PyArray getPyArrayFromFile(String filePath) throws Exception {
        PyArray pyArray = new PyArray(Double.class, 10000);
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    System.out.println(lineTxt);
                    pyArray.append(new PyFloat(Double.parseDouble(lineTxt)));
                }
                read.close();
            } else {
                throw new Exception("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("读取文件内容出错");
        }
        return pyArray;
    }
}