package com.example.demo;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.python.core.Py.Exception;

/**
 * Created by pengwan on 2017/8/3.
 */
public class CalculateByPythonTest {
    private CalculateByPython calculateByPython = new CalculateByPython();
    @Test
    public void calculate() throws Exception {
        String pythonFilePath = "/Users/pengwan/IdeaProjects/EnvelopeDemo/src/main/resources/data/specture_ansys.py";
        String dataFilePath = "/Users/pengwan/IdeaProjects/EnvelopeDemo/src/main/resources/data/hd_038_fdj_qdd_jx_25600_jsd_1100.451RPM_20170715165953.txt";
        calculateByPython.calculate(pythonFilePath, dataFilePath, 1500,3000,25600,2000);
    }

}