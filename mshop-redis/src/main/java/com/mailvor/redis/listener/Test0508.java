package com.mailvor.redis.listener;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.Scanner;
/**
 * 输入中文或者英文字符
 * 输出字符阵列
 * @author jiangshuai
 *
 */
public class Test0508 {
    public static void main(String[] args) {
        try
        {
            System.out.println("请输入要显示的文字:");
            Scanner s=new Scanner(System.in);
            String old=s.nextLine();
            s.close();
            Font font = new Font("黑体", Font.ROMAN_BASELINE, 18);
//		  Font font = new Font("黑体", Font.ROMAN_BASELINE, 40);
            AffineTransform at = new AffineTransform();
            FontRenderContext frc = new FontRenderContext(at, true, true);
            GlyphVector gv = font.createGlyphVector(frc, old); //要显示的文字(文字的字形)
            Shape shape = gv.getOutline(2, 15);
            int weith = 1000;
            int height = 40;
            boolean[][] view = new boolean[weith][height];
            for (int i = 0; i < weith; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    if (shape.contains(i, j))
                    {
                        view[i][j] = true;
                    } else
                    {
                        view[i][j] = false;
                    }
                }
            }
            System.out.println("转换后的字符文字:");
            for (int j = 0; j < height; j++)
            {
                for (int i = 0; i < weith; i++)
                {
                    if (view[i][j])
                    {
                        System.out.print("*");//替换成你喜欢的图案
                    } else
                    {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
