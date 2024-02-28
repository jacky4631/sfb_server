package com.mailvor.modules.tools.utils;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 按几率产生随机数
 * 例如，产生0.1-100的随机数，0.1-1的几率是90%，1-10的几率是9%，10-100的几率是1%
 */
public class RateRandomNumber {
//    private final static List<Double> separates = Arrays.asList(2.2, 3.5);
    private final static List<Integer> percents = Arrays.asList(6, 20, 39, 30, 2, 2, 1);

    public static double random(double min,double max){
        List<Double> separates = new ArrayList<>();
        separates.add(max/6);
        separates.add(max/4.3);
        separates.add(max/4);
        separates.add(max/3);
        separates.add(max/1.5);
        separates.add(max-0.5);
        return produceRateRandomNumber(min, max, separates, percents);
    }
    /**
     * 产生随机数
     * @param min 最小值
     * @param max 最大值
     * @return 随机结果
     */
    public static double produceRandomNumber(double min,double max){
        return RandomUtils.nextDouble(min,max); //[min,max]
    }

    /**
     * 按比率产生随机数
     * @param min 最小值
     * @param max 最大值
     * @param separates 分割值（中间插入数）
     * @param percents 每段数值的占比（几率）
     * @return 按比率随机结果
     */
    public static double produceRateRandomNumber(double min, double max, List<Double> separates, List<Integer> percents){
        if(min > max){
            throw new IllegalArgumentException("min值必须小于max值");
        }
        if(separates == null || percents==null || separates.size()==0){
            return produceRandomNumber(min,max);
        }
        if(separates.size() +1 != percents.size()){
            throw new IllegalArgumentException("分割数字的个数加1必须等于百分比个数");
        }
        int totalPercent = 0;
        for(Integer p:percents){
            if(p<0 || p>100){
                throw  new IllegalArgumentException("百分比必须在[0,100]之间");
            }
            totalPercent += p;
        }
        if(totalPercent != 100){
            throw new IllegalArgumentException("百分比之和必须为100");
        }
        for(double s:separates){
            if(s <= min || s >= max){
                throw new IllegalArgumentException("分割数值必须在(min,max)之间");
            }
        }
        int rangeCount = separates.size()+1; //例如：3个插值，可以将一个数值范围分割成4段
        //构造分割的n段范围
        List<Range> ranges = new ArrayList<Range>();
        int scopeMax = 0;
        for(int i=0;i<rangeCount;i++){
            Range range = new Range();
            range.min = (i==0 ? min:separates.get(i-1));
            range.max = (i== rangeCount-1 ?max:separates.get(i));
            range.percent = percents.get(i);

            //片段占比，转换为[1,100]区间的数字
            range.percentScopeMin = scopeMax +1;
            range.percentScopeMax = range.percentScopeMin + (range.percent-1);
            scopeMax = range.percentScopeMax;

            ranges.add(range);
        }
        //结果赋初值
        double r = min;
        int randomInt = RandomUtils.nextInt(1,101); //[1,100]
        for(int i=0;i<ranges.size();i++){
            Range range = ranges.get(i);
            //判断使用哪个range产生最终的随机数
            if(range.percentScopeMin <= randomInt && randomInt <= range.percentScopeMax){
                r = produceRandomNumber(range.min,range.max);
                break;
            }
        }
        return r;
    }

    public static class Range{
        public double min;
        public double max;
        public int percent; //百分比

        public int percentScopeMin; //百分比转换为[1,100]的数字的最小值
        public int percentScopeMax; //百分比转换为[1,100]的数字的最大值
    }

    public static void main(String[] args) {
        List<Double> separates = new ArrayList<>();
        //倍数
        double max = 1.8;
        separates.add(max/7);
        separates.add(max/4);
        separates.add(max/2.5);
        separates.add(max/1.8);
        separates.add(max-0.5);
        List<Integer> percents = new ArrayList<>();
        //几率
        percents.add(70);
        percents.add(12);
        percents.add(8);
        percents.add(6);
        percents.add(3);
        percents.add(1);
        for(int i=0;i<100;i++) {
            double number = produceRateRandomNumber(0.25, max, separates, percents);
            System.out.println(String.format("%.2f",number));
        }
    }
}
