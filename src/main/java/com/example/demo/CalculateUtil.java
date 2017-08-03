package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CalculateUtil {

	/**
	 * @Fields value_key_default : value_key默认
	 */
	static String value_key_default = "info";

	/**
	 * @Fields vs : 微妙级别
	 */
	static Double vs = 1000000.0;

	/**
	 * @Title getPinPuData
	 * @Date 2015-8-26 下午1:58:38
	 * @author WANGSHANGWEI
	 * @Description 获取频谱数据
	 * @param inputData
	 *            类似于 [{'info':-0.2474162,'time':0},
	 *            {'info':-0.16007847,'time':0}, {'info':-0.14737481,'time':0},
	 *            {'info':-0.0060465336,'time':0}]的字符串。该字符串代表[信号],在这里就是波形数据。
	 *            建议：开发人员把波形数据传入，并减少对oge的访问。
	 * @param sampleRate
	 *            采样间隔时间，单位微妙
	 * @param value_key
	 *            指定 @param inputData 对应的数据里面 数据double值对应的key值。例："info"。
	 *            另外，开发人员可以不传入该字段，默认"info"。
	 * @return 类似于[{hengIndex:0.02154,
	 *         zongValue:0.6598745896},{hengIndex:0.1102154,
	 *         zongValue:0.659874581196},...]
	 */
	public static String getPinPuData(String inputData, Long sampleRate,
			String value_key) {
		String result = "[{}]";
		if (UtilValidator.isEmpty(inputData)
				|| UtilValidator.isEmpty(sampleRate)) {
			return result;
		}
		// 赋予默认值
		if (UtilValidator.isEmpty(value_key)) {
			value_key = "info";
		}
		List<Double> orginDouble = new ArrayList<Double>();
		initData(inputData, orginDouble, value_key);
		if (!UtilValidator.isEmpty(orginDouble)) {
			result = getPinPuDataNormal(orginDouble, sampleRate);
		}
		return result;
	}

	/**
	 * @Title getPinPuDataNormal
	 * @Date 2015-8-26 下午3:37:53
	 * @author WANGSHANGWEI
	 * @Description 获取频谱数据正式
	 * @param orginDouble
	 *            原始list
	 * @param sampleRate
	 *            周期
	 * @return String
	 */
	private static String getPinPuDataNormal(List<Double> orginDouble,
			Long sampleRate) {
		/* 采样频率 */
		Double cypl = vs / sampleRate.doubleValue();
		// 总共数据大小
		int size = orginDouble.size();
		// 数据大小的一半
		int sizeNormal = size / 2;
		StringBuilder resultS = new StringBuilder();
		// 纵坐标list
		List<Double> zongList = new ArrayList<Double>();
		// 初始化复数
		double re[] = new double[size];
		double im[] = new double[size];
		getOriginComplex(orginDouble,re,im);

		// fft算法之后的数据
		//FFT.transform(re,im);
		//GeneralFFT.transform(re,im);

		//调用库进行fft运算（之前的fft算法不能对非2的幂次计算）
		double[] total = new double[re.length*2];
		for (int i = 0; i < re.length; i++) {
			total[2*i] = re[i];
			total[2*i+1] = im[i];
		}

		//double[] b = new double[]{1,0,2,0,3,0,4,0,5,0};
		DoubleFFT_1D doubleFFT_1D = new DoubleFFT_1D(re.length);
		doubleFFT_1D.complexForward(total);
		for (int j = 0; j < total.length; j++) {
			if (j % 2 == 0) {
				re[j/2] = total[j];
			} else {
				im[j/2] = total[j];
			}
		}

		zongList = getZongZuoBiao(re,im, sizeNormal, size);
		// 组装数据
		compositeDatas(resultS, zongList, cypl);
		return resultS.toString();
	}

	/**
	 * @Title compositeDatas
	 * @Date 2015-8-26 下午5:43:33
	 * @author WANGSHANGWEI
	 * @Description 组装数据
	 * @param resultS
	 * @param
	 * @param zongList
	 * @param cypl
	 *            采样频率
	 */
	private static void compositeDatas(StringBuilder resultS,
			List<Double> zongList, Double cypl) {
		// 纵坐标数目
		int size = zongList.size();
		// 间隔
		double jg = cypl / size / 2.0;
		resultS.append("[");
		if (!UtilValidator.isEmpty(zongList)) {
			for (int i = 0; i < zongList.size(); i++) {
				double hengk = i * 1.0 * jg;
				long heng = Math.round(hengk);
				if (i == zongList.size() - 1) {
					resultS.append("{");
					if (UtilValidator.isEmpty(zongList.get(i))) {
						resultS.append("\"hengIndex\":").append(heng).append("");
					} else {
						resultS.append("\"hengIndex\":").append(heng).append(",");
						resultS.append("\"zongValue\":").append(zongList.get(i))
								.append("");
					}
					resultS.append("}");
				} else {
					resultS.append("{");
					if (UtilValidator.isEmpty(zongList.get(i))) {
						resultS.append("\"hengIndex\":").append(heng).append("");
					} else {
						resultS.append("\"hengIndex\":").append(heng).append(",");
						resultS.append("\"zongValue\":").append(zongList.get(i))
								.append("");
					}
					resultS.append("},");
				}
			}
		}
		resultS.append("]");
	}

	/**
	 * @Title getZongZuoBiao
	 * @Date 2015-8-26 下午5:38:35
	 * @author WANGSHANGWEI
	 * @Description 获取纵坐标list
	 * @param sizeNormal
	 * @param size
	 * @return
	 */
	private static List<Double> getZongZuoBiao(double re[],double im[],
			int sizeNormal, int size) {
		List<Double> zongs = new ArrayList<Double>();
		for (int i = 1; i <= sizeNormal; i++) {
			int index = i - 1;
			if (!UtilValidator.isEmpty(re[index])&&!UtilValidator.isEmpty(im[index])) {
				Double val = Math.hypot(re[index],im[index]) * 2.0 / size;
				zongs.add(val);
			}
		}
		return zongs;
	}

	/**
	 * @Title getOriginComplex
	 * @Date 2015-8-26 下午5:11:20
	 * @author WANGSHANGWEI
	 * @Description 初始化复数
	 * @param orginDouble
	 * @return
	 */
	private static void getOriginComplex(List<Double> orginDouble,double re[],double im[]) {
		int len = orginDouble.size();
		for (int i = 0; i < len; i++) {
			Double val = orginDouble.get(i);
			if (!UtilValidator.isEmpty(val)) {
				re[i] = val;
				im[i] = 0.0;
			}
		}
	}


	/**
	 * @Title getBaoLuoPuData
	 * @Date 2015-8-27 上午10:43:10
	 * @author WANGSHANGWEI
	 * @Description 获取包络谱分析数据
	 * @param inputData
	 *            类似于 [{'info':-0.2474162,'time':0},
	 *            {'info':-0.16007847,'time':0}, {'info':-0.14737481,'time':0},
	 *            {'info':-0.0060465336,'time':0}]的字符串。该字符串代表[信号],在这里就是波形数据。
	 *            建议：开发人员把波形数据传入，并减少对oge的访问。
	 * @param sampleRate
	 *            采样间隔时间，单位微妙
	 * @param value_key
	 *            指定 @param inputData 对应的数据里面 数据double值对应的key值。例："info"。
	 *            另外，开发人员可以不传入该字段，默认"info"。
	 * @param lvBoLow
	 *            滤波器低频截止频率
	 * @param lvBoHigh
	 *            滤波器高频截止频率
	 * @param lvBoSteps
	 *            滤波器阶数，可变，滤波器系数
	 * @return
	 */
	public static String getBaoLuoPuData(String inputData, Long sampleRate,
			String value_key, Double lvBoLow, Double lvBoHigh, Long lvBoSteps) {
		String result = "[{}]";
		if (UtilValidator.isEmpty(inputData)
				|| UtilValidator.isEmpty(sampleRate)
				|| UtilValidator.isEmpty(lvBoSteps)) {
			return result;
		}
		if (UtilValidator.isEmpty(value_key)) {
			value_key = "info";
		}
		List<Double> orginDouble = new ArrayList<Double>();
		initData(inputData, orginDouble, value_key);
		if (!UtilValidator.isEmpty(orginDouble)) {
			result = getBaoLuoPuDataNoraml(orginDouble, sampleRate, lvBoLow,
					lvBoHigh, lvBoSteps);
			if (UtilValidator.isEmpty(result)) {
				result = "[{}]";
			}
		}
		return result;
	}

	/**
	 * @Title getBaoLuoPuDataNoraml
	 * @Date 2015-8-27 下午2:19:39
	 * @author WANGSHANGWEI
	 * @Description 获取包络谱数据
	 * @param orginDouble
	 *            波形数据
	 * @param sampleRate
	 *            采样间隔时间，单位微妙
	 * @param lvBoLow
	 *            滤波低频
	 * @param lvBoHigh
	 *            滤波高频
	 * @param lvBoSteps
	 *            阶
	 * @return String
	 */
	private static String getBaoLuoPuDataNoraml(List<Double> orginDouble,
			Long sampleRate, Double lvBoLow, Double lvBoHigh, Long lvBoSteps) {
		/* 采样频率 */
		Double cypl = vs / sampleRate.doubleValue();
		if (sampleRate == 39L) {
			cypl = 25600.0;
		}
		if (UtilValidator.isEmpty(lvBoLow) || UtilValidator.isEmpty(lvBoHigh)) {
			return null;
		}
		// 求脉冲序列 N个数
		Double[] impulseResponse = getImpulseResponse(lvBoSteps, lvBoLow,
				lvBoHigh, cypl);
		// 包络算法fft之前的数据
		List<Double> beforeFFT = getBaoLuoBeforeFFT(impulseResponse,
				orginDouble, cypl, lvBoSteps);
		// 包络数据
		return getPinPuDataNormal(beforeFFT, sampleRate);
	}

	/**
	 * @Title getBaoLuoBeforeFFT
	 * @Date 2015-8-27 下午5:26:57
	 * @author WANGSHANGWEI
	 * @Description 获取包络数据在频谱分析之前
	 * @param impulseResponse
	 * @param orginDouble
	 * @param cypl
	 * @param lvBoSteps
	 * @return
	 */
	private static List<Double> getBaoLuoBeforeFFT(Double[] impulseResponse,
			List<Double> orginDouble, Double cypl, Long lvBoSteps) {
		if (UtilValidator.isEmpty(impulseResponse)) {
			return null;
		}
		List<Double> results = new ArrayList<Double>();
		// 索引
		int count = 0;
		// 单个输出数据
		double outputData = 0;
		// 原始波形数目
		int orignDataLen = orginDouble.size();
		// 处理以后的数据
		Double[] orginDoubleDeal = new Double[lvBoSteps.intValue()];
		while (true) {
			if (count == orignDataLen)
				break;
			Double nowData = orginDouble.get(count);
			if (!UtilValidator.isEmpty(nowData)) {
				// 输入数据向左平移，为卷积做准备
				orginDoubleDeal = dealInputDateToLeft(nowData, lvBoSteps,
						orginDoubleDeal);
				// 滤波器处理，主要是卷积运算·
				outputData = realTimeFIRFilter(impulseResponse, lvBoSteps,
						orginDoubleDeal);
				results.add(outputData);
			}
			count++;
		}
		return results;
	}

	/**
	 * @Title realTimeFIRFilter
	 * @Date 2015-8-27 下午6:54:34
	 * @author WANGSHANGWEI
	 * @Description 滤波器处理，主要是卷积运算·
	 * @param impulseResponse
	 * @param lvBoSteps
	 * @param orginDouble
	 * @return
	 */
	private static double realTimeFIRFilter(Double[] impulseResponse,
			Long lvBoSteps, Double[] orginDouble) {
		// 数目
		int count;
		// 输出数据
		double outputData = 0.0;
		// 倒数第一个
		int k = lvBoSteps.intValue() - 1;
		// 逐个循环
		for (count = 0; count < lvBoSteps; count++) {
			Double bxD = orginDouble[k - count];
			if (!UtilValidator.isEmpty(bxD)) {
				if (!UtilValidator.isEmpty(impulseResponse[count])) {
					outputData += (impulseResponse[count]) * bxD;
				}
			}
		}
		return outputData;
	}

	/**
	 * @Title dealInputDateToLeft
	 * @Date 2015-8-27 下午5:49:33
	 * @author WANGSHANGWEI
	 * @Description 输入数据向左平移，为卷积做准备
	 * @param nowData
	 *            现有数据
	 * @param lvBoSteps
	 *            阶数
	 * @param orginDoubleDeal
	 *            波形数据列表
	 * @return
	 */
	private static Double[] dealInputDateToLeft(Double nowData, Long lvBoSteps,
			Double[] orginDoubleDeal) {
		int count;
		Double[] orginDoubleResult = new Double[lvBoSteps.intValue()];
		for (count = 0; count < lvBoSteps - 1; count++) {
			Double re = orginDoubleDeal[count + 1];
			if (!UtilValidator.isEmpty(re)) {
				orginDoubleResult[count] = re;
			}
		}
		orginDoubleResult[lvBoSteps.intValue() - 1] = nowData;
		return orginDoubleResult;
	}

	/**
	 * @Title getImpulseResponse
	 * @Date 2015-8-27 下午4:17:38
	 * @author WANGSHANGWEI
	 * @Description 获取脉冲序列 N个数
	 * @param lvBoSteps
	 * @param lvBoLow
	 * @param lvBoHigh
	 * @param cypl
	 * @return
	 */
	private static Double[] getImpulseResponse(Long lvBoSteps, Double lvBoLow,
			Double lvBoHigh, Double cypl) {
		// 改造滤波低频，滤波高频
		lvBoLow = 2.0 * Math.PI * lvBoLow * (1.0 / cypl);
		lvBoHigh = 2.0 * Math.PI * lvBoHigh * (1.0 / cypl);
		// 阶数
		int N = lvBoSteps.intValue();
		// 结果
		Double[] outputData = new Double[N];
		// 高低中间值
		Double lvBoMid = (lvBoLow + lvBoHigh) / 2.0;
		int count = 0;
		for (count = -(N - 1) / 2; count < (N - 1) / 2; count++) {
			outputData[count + (N - 1) / 2] = (lvBoMid / Math.PI)
					* sinc((lvBoMid / Math.PI) * (double) (count));
		}
		for (count = -(N - 1) / 2; count <= (N - 1) / 2; count++) {
			int index = count + ((N - 1) / 2);
			if (!UtilValidator.isEmpty(outputData[index])) {
				outputData[index] *= (0.54 - 0.46 * Math
						.cos((2 * Math.PI * count) / (N - 1)));
			}
		}
		return outputData;
	}

	/**
	 * @Title initData
	 * @Date 2015-8-29 上午10:42:28
	 * @author WANGSHANGWEI
	 * @Description 初始化波形数据
	 * @param inputData
	 *            字符串
	 * @param list
	 *            double列表
	 * @param value_key
	 *            指定key
	 */
	private static void initData(String inputData, List<Double> list,
			String value_key) {
		// 预处理
		inputData = getInputData(inputData);
		ObjectMapper objectMapper = new ObjectMapper();
		Object pojoValO = null;
		Double pojoVal = null;
		try {
			List<Map<String, Object>> listOfMap = objectMapper.readValue(
					inputData, List.class);
			for (Map<String, Object> map : listOfMap) {
				pojoValO = map.get(value_key);
				if (!UtilValidator.isEmpty(pojoValO)
						&& pojoValO instanceof Double) {
					pojoVal = (Double) pojoValO;
					list.add(pojoVal);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title getInputData
	 * @Date 2015-8-29 下午12:07:58
	 * @author WANGSHANGWEI
	 * @Description 处理json字符串
	 * @param inputData
	 * @return
	 */
	public static String getInputData(String inputData) {
		StringBuilder resultJson = new StringBuilder();
		if (",".equals(inputData.substring(inputData.length() - 2,
				inputData.length() - 1))) { // 处理非法字符
			inputData = resultJson
					.append(inputData.substring(0, inputData.length() - 2))
					.append("]").toString();
		}
		if (inputData.contains("\'")) {
			inputData = inputData.replaceAll("\'", "\"");
		}
		return inputData;
	}

	/**
	 * @Title sinc
	 * @Date 2015-8-27 下午4:38:57
	 * @author WANGSHANGWEI
	 * @Description 求sin值
	 * @param n
	 * @return
	 */
	private static double sinc(double n) {
		// 代码
		if (n == 0) {
			return (double) 1;
		} else {
			return (double) (Math.sin(Math.PI * n) / (Math.PI * n));
		}
	}

	/**
	 * @Title getBoPropData
	 * @Date 2015-8-30 下午4:02:43
	 * @author WANGSHANGWEI
	 * @Description 求波形有效值，峰峰值，峭度
	 * @param inputData
	 *            类似于 [{'info':-0.2474162,'time':0},
	 *            {'info':-0.16007847,'time':0}, {'info':-0.14737481,'time':0},
	 *            {'info':-0.0060465336,'time':0}]的字符串。该字符串代表[信号],在这里就是波形数据。
	 *            建议：开发人员把波形数据传入，并减少对oge的访问。
	 * @param value_key
	 *            指定 @param inputData 对应的数据里面 数据double值对应的key值。例："info"。
	 *            另外，开发人员可以不传入该字段，默认"info"。
	 * @return {'validValue' : 23, 'fengfengValue' : 56, 'qiaodu' : 20}
	 */
	public static String getBoPropData(String inputData, String value_key) {
		String result = "[{}]";
		if (UtilValidator.isEmpty(inputData)) {
			return result;
		}
		if (UtilValidator.isEmpty(value_key)) {
			value_key = "info";
		}
		List<Double> orginDouble = new ArrayList<Double>();
		initData(inputData, orginDouble, value_key);
		if (!UtilValidator.isEmpty(orginDouble)) {
			result = getBoPropDataNormal(orginDouble);
			if (UtilValidator.isEmpty(result)) {
				result = "[{}]";
			}
		}
		return result;
	}

	/**
	 * @Title getBoPropDataNormal
	 * @Date 2015-8-30 下午4:13:38
	 * @author WANGSHANGWEI
	 * @Description 获取波形属性数据，包括有效值，峰峰值，峭度
	 * @param orginDouble
	 *            原始波形数据
	 * @return
	 */
	private static String getBoPropDataNormal(List<Double> orginDouble) {
		Double validValue = getValidDataNormal(orginDouble);
		Double ffValue = getFengFengDataNormal(orginDouble);
		Double qiaoduValue = getQiaoDuDataNormal(orginDouble);
		StringBuilder result = new StringBuilder();
		result.append("{").append("validValue:").append(validValue);
		result.append(",").append("ffValue:").append(ffValue);
		result.append(",").append("qiaodu:").append(qiaoduValue);
		result.append("}");
		return result.toString();
	}

	
	/**
	 * @Title getQiaoDuDataNormal
	 * @Date 2016-1-28 下午2:21:18
	 * @author ZHANGCHUANYUN
	 * @Description 得到峭度指标
	 * @param orginDouble
	 * @return
	 */
	private static Double getQiaoDuDataNormal(List<Double> orginDouble) {
		// 逐个循环，求出平均值
		Double avg=getAvgNotmal(orginDouble);
		//得到标准方差
		Double fc=getFcNotmal(orginDouble,avg);
		//得到峭度
		Double qiaodu = getQiaoDuNormal(orginDouble,avg);
		//得到峭度指标=峭度/标准方程的4次方
		if(fc>0){
			return qiaodu/(Math.pow(fc, 4.0));
		}
		return 0.0;
	}
	
	/**
	 * @Title getQiaoDuDataNormal
	 * @Date 2016-1-28 下午2:21:18
	 * @author ZHANGCHUANYUN
	 * @Description 得到峭度
	 * @param orginDouble
	 * @return
	 */
	private static Double getQiaoDuNormal(List<Double> orginDouble, Double avg) {
		int size = orginDouble.size();
		Double sum=0.0;
		for (Double val : orginDouble) {
			if (!UtilValidator.isEmpty(val)) {
				sum +=Math.pow((val-avg),4.0);
			}
		}
		sum = sum / size;
		return sum;
	}

	/**
	 * @Title getFcNotmal
	 * @Date 2016-1-28 下午2:21:18
	 * @author ZHANGCHUANYUN
	 * @Description 得到方差
	 * @param orginDouble
	 * @param avg
	 * @return
	 */
	private static Double getFcNotmal(List<Double> orginDouble,Double avg) {
		int size = orginDouble.size();
		Double sum=0.0;
		for (Double val : orginDouble) {
			if (!UtilValidator.isEmpty(val)) {
				sum +=Math.pow((val-avg),2.0);
			}
		}
		sum = sum / size;
		return Math.sqrt(sum);
	}

	/**
	 * @Title getAvgNotmal
	 * @Date 2016-1-28 下午2:21:18
	 * @author ZHANGCHUANYUN
	 * @Description 得到算数平均值
	 * @param orginDouble
	 * @return
	 */
	private static Double getAvgNotmal(List<Double> orginDouble) {
		int size = orginDouble.size();
		Double sum=0.0;
		for (Double val : orginDouble) {
			if (!UtilValidator.isEmpty(val)) {
				sum +=val;
			}
		}
		sum = sum / size;
		return sum;
	}

	/**
	 * @Title getFengFengDataNormal
	 * @Date 2015-8-31 下午2:21:18
	 * @author WANGSHANGWEI
	 * @Description 找到波形峰峰值
	 * @param orginDouble
	 * @return
	 */
	private static Double getFengFengDataNormal(List<Double> orginDouble) {
		Double maxValue = 0.0;
		Double minValue = 20.0;
		/* 找到最大最小值 */
		for (Double val : orginDouble) {
			if (val > maxValue) {
				maxValue = val;
			}
			if (val < minValue) {
				minValue = val;
			}
		}
		return maxValue - minValue;
	}

	/**
	 * @Title getValidDataNormal
	 * @Date 2015-8-31 上午9:39:48
	 * @author WANGSHANGWEI
	 * @Description 求波形有效数
	 * @param orginDouble
	 * @return
	 */
	private static Double getValidDataNormal(List<Double> orginDouble) {
		// 逐个循环
		Double sum = 0.0;
		int size = orginDouble.size();
		for (Double val : orginDouble) {
			if (!UtilValidator.isEmpty(val)) {
				sum += Math.pow(val, 2.0);
			}
		}
		sum = sum / size;
		return Math.sqrt(sum);
	}
/*	public static void main(String args[]){
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



	}*/
}
