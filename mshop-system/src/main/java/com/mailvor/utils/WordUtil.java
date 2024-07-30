package com.mailvor.utils;

import cn.hutool.core.io.FileUtil;
import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.poi.xwpf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WordUtil {
    /**
     *
     * @Title: replaceAndGenerateWord
     * @Description: 替换word中需要替换的特殊字符
     * 优化后是为了分别减少map遍历，增加性能，前提是表格的替换的数据不一样，所以将两者分离处理（加空间，提性能）
     * @param srcPath 需要替换的文档全路径
     * @param exportFile 替换后文档的保存路径
     * @param contentMap {key:将要被替换的内容,value:替换后的内容}
     * @return boolean 返回成功状态
     */
    public static boolean replaceAndGenerateWord(String srcPath, String exportFile, Map<String, String> contentMap) {
        boolean bool = true;
        try {
            URL url = new URL(srcPath);

            InputStream inputStream = url.openStream();
            XWPFDocument document = new XWPFDocument(url.openStream());
            // 替换段落中的指定文字
            Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
            while (itPara.hasNext()) {
                XWPFParagraph paragraph = itPara.next();
                commonCode(paragraph, contentMap);
            }
            // 替换表格中的指定文字
            Iterator<XWPFTable> itTable = document.getTablesIterator();
            while (itTable.hasNext()) {
                XWPFTable table = itTable.next();
                int rcount = table.getNumberOfRows();
                for (int i = 0; i < rcount; i++) {
                    XWPFTableRow row = table.getRow(i);
                    List<XWPFTableCell> cells = row.getTableCells();
                    for (XWPFTableCell cell : cells) {
                        //单元格中有段落，得做段落处理
                        List<XWPFParagraph> paragraphs = cell.getParagraphs();
                        for (XWPFParagraph paragraph : paragraphs) {
                            commonCode(paragraph, contentMap);
                        }
                    }
                }
            }

            FileOutputStream outStream = new FileOutputStream(exportFile);
            document.write(outStream);
            outStream.close();
            inputStream.close();
        }catch (Exception e){
            bool = false;
            e.printStackTrace();
        }
        return bool;
    }

    /**
     *
     * @Title: commonCode
     * @Description: 替换内容
     * @param paragraph 被替换的文本信息
     * @param contentMap {key:将要被替换的内容,value:替换后的内容}
     */
    private static void commonCode(XWPFParagraph paragraph,Map<String, String> contentMap){
        List<XWPFRun> runs = paragraph.getRuns();
        for (XWPFRun run : runs) {
            String oneparaString = run.getText(run.getTextPosition());
            if (StringUtils.isBlank(oneparaString)){
                continue;
            }
            for (Map.Entry<String, String> entry : contentMap.entrySet()) {
                oneparaString = oneparaString.replace(entry.getKey(), StringUtils.isBlank(entry.getValue()) ? "--" : entry.getValue());
            }
            run.setText(oneparaString, 0);
        }
    }

    /**
     *
     * @Title: getLicense
     * @Description:验证license许可凭证
     * @return boolean 返回验证License状态
     */
    private static boolean getLicense(String licenseUrl) {
        boolean result = true;
        try {
            URL url = new URL(licenseUrl);
            InputStream inputStream = url.openStream();
            new License().setLicense(inputStream);
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @Title: wordConverterToPdf
     * @Description: word转pdf(aspose转换)
     * @param wordPath word 全路径，包括文件全称
     * @param pdfPath pdf 保存路径，包括文件全称
     * @return boolean 返回转换状态
     */
    public static boolean wordConverterToPdf(String wordPath, String pdfPath, String licenseUrl) {
        System.out.println("===================aspose开始转换=======================");
        //开始时间
        long start = System.currentTimeMillis();
        boolean bool = false;
        // 验证License,若不验证则转化出的pdf文档会有水印产生
        if (!getLicense(licenseUrl)) return bool;
        try {
            FileOutputStream os = new FileOutputStream(pdfPath);// 新建一个pdf文档输出流
            Document doc = new Document(wordPath);// Address是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            os.close();
            bool = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("(aspose)word转换PDF完成，用时"+(System.currentTimeMillis()-start)/1000.0+"秒");
        }
        return bool;
    }
    public static String generateEContract(String localPath, Long uid, Map<String, String> replaceMap,
                                           String faceUrl, String contractUrl, String licenseUrl, String sealUrl,
                                           String cardFPath,String cardBPath) throws Exception {
        String basePath = localPath + uid + File.separator;
        String pdfPath = convertToPdf(basePath, contractUrl, licenseUrl, replaceMap);
        if(pdfPath == null) {
            return null;
        }
        String targetPdfPath = basePath+ "E" + uid + "T"+System.currentTimeMillis()/1000 + ".pdf";
        itextPDFAddPicture(Files.newInputStream(new File(pdfPath).toPath()),
                targetPdfPath,
                sealUrl,
                faceUrl,cardFPath, cardBPath);

        return targetPdfPath;
    }
    /**
     * 生成电子合同
     * */
    public static String convertToPdf(String basePath, String contractUrl, String licenseUrl,
                                      Map<String, String> replaceMap){
        File dir = new File(basePath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        // 合同保存路径
        Long millis = System.currentTimeMillis();
        String destPath = basePath + "contract"+ millis + ".docx";
        //替换合同里面的参数
        replaceAndGenerateWord(contractUrl,
                destPath, replaceMap);

        StringBuilder pdfFile = new StringBuilder(basePath).append("contract").append(millis).append(".pdf");
        String pdfPath = pdfFile.toString();
        //将word转pdf文件
        Boolean bool = WordUtil.wordConverterToPdf(destPath, pdfPath, licenseUrl);
        if (!bool) {
            System.out.println("合同word转pdf文件失败");
            return null;
        }

        return pdfPath;
    }

    /**
     *
     * @Title: itextPDFAddPicture
     * @Description: 为pdf加图片(电子合同盖公章)
     * @param inputStream 电子合同pdf文件流
     * @param targetPath 保存路径
     * @throws Exception 异常参数
     */
    public static void itextPDFAddPicture(InputStream inputStream, String targetPath,
                                          String sealPath, String facePath,
                                          String cardFPath,String cardBPath) throws Exception{
        // 1.1 读取模板文件
        PdfReader reader = new PdfReader(inputStream);
        // 1.2 创建文件输出流
        FileOutputStream out = new FileOutputStream(targetPath);
        // 2、创建PdfStamper对象
        PdfStamper stamper = new PdfStamper(reader, out);
        int chunkWidth = 120;// 公章大小，x轴
        int chunkHeight = 120;// 公章大小，y轴
        // 4、读取公章
        BufferedImage sealBufferedImage = ImageIO.read(new URL(sealPath));

        BufferedImage cardFBufferedImage = ImageIO.read(new URL(cardFPath));
        BufferedImage cardBBufferedImage = ImageIO.read(new URL(cardBPath));
        //骑缝章
        BufferedImage[] imgs = splitImage(sealBufferedImage, 1, 3);
        BufferedImage leftBufferedImage = imgs[0];// 左边公章图片流
        BufferedImage centerBufferedImage = imgs[1];// 中间公章图片流
        BufferedImage rightBufferedImage = imgs[2];// 右边公章图片流
//
//        // 5、读公章图片
        Image sealImage = Image.getInstance(imageToBytes(sealBufferedImage));
        Image cardFImage = Image.getInstance(imageToBytes(cardFBufferedImage));
        Image cardBImage = Image.getInstance(imageToBytes(cardBBufferedImage));
        Image leftImage = Image.getInstance(imageToBytes(leftBufferedImage));
        Image centerImage = Image.getInstance(imageToBytes(centerBufferedImage));
        Image rightImage = Image.getInstance(imageToBytes(rightBufferedImage));

        // 获取pdf页面的高和宽
        Rectangle pageSize = reader.getPageSize(1);
        float height = pageSize.getHeight();
        float width = pageSize.getWidth();


        // 6、为pdf每页加印章
        // 设置公章的位置
        float xL = width - chunkWidth/3 - 2;
        float yL = height/2-chunkHeight/2-25;

        float xR = width-chunkHeight/3 + chunkHeight/12  -8;
        float yR = yL;
        // 6.1 第一页盖左章
        leftImage.scaleToFit(chunkWidth, chunkHeight);// 公章大小
        leftImage.setAbsolutePosition(xL, yL);// 公章位置

        centerImage.scaleToFit(chunkWidth, chunkHeight);// 公章大小
        centerImage.setAbsolutePosition(xL, yL);// 公章位置

        // 6.2 第二页盖右章
        rightImage.scaleToFit(chunkWidth, chunkHeight);// 公章大小
        rightImage.setAbsolutePosition(xR, yR);// 公章位置
        int pdfPages = reader.getNumberOfPages();// pdf页面页码
        // 遍历为每页盖左章或右章
        for (int i = 1; i <= pdfPages; i++) {
            if (i % 3 == 0) {// 盖右章
                stamper.getOverContent(i).addImage(rightImage);
            } else if(i % 3 == 2){// 盖中章
                stamper.getOverContent(i).addImage(centerImage);
            } else {// 盖左章
                stamper.getOverContent(i).addImage(leftImage);
            }
        }

        //人脸照片
        if(StringUtils.isNotBlank(facePath)) {
            // 读取人脸图片
            BufferedImage signatureBufferedImage = ImageIO.read(new URL(facePath));

            Image signatureImage = Image.getInstance(imageToBytes(signatureBufferedImage));

            signatureImage.scaleToFit(60, 60);
            signatureImage.setAbsolutePosition(300, height-520);
            stamper.getOverContent(pdfPages).addImage(signatureImage);
        }
        // 6.3 第一页盖公章
//        sealImage.scaleToFit(chunkWidth, chunkWidth);
//        sealImage.setAbsolutePosition(100, height-chunkHeight-560);
//        stamper.getOverContent(1).addImage(sealImage);

        //最后一页日期处盖公章
        sealImage.scaleToFit(chunkWidth, chunkWidth);
        sealImage.setAbsolutePosition(140, height-chunkHeight-510);
        stamper.getOverContent(pdfPages).addImage(sealImage);


        //第一页身份证正反面
        cardFImage.scaleToFit(214, 135);
        cardFImage.setAbsolutePosition(100, height-410);
        stamper.getOverContent(1).addImage(cardFImage);

        cardBImage.scaleToFit(214, 135);
        cardBImage.setAbsolutePosition(340, height-410);
        stamper.getOverContent(1).addImage(cardBImage);

        // 7、关闭相关流
        stamper.close();
        out.close();
        reader.close();
        inputStream.close();
    }
    public static byte[] imageToBytes(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image,"png",baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
    /**
     *
     * @Title: splitImage
     * @Description: 分割图片
     * @param image 图片BufferedImage流
     * @param rows 分割行
     * @param cols 分割列
     * @return BufferedImage[] 返回分割后的图片流
     */
    public static BufferedImage[] splitImage(BufferedImage image, int rows, int cols) {
        // 分割成4*4(16)个小图
        int chunks = rows * cols;
        // 计算每个小图的宽度和高度
        int chunkWidth = image.getWidth() / cols + 3;// 向右移动3
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage[] imgs = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //设置小图的大小和类型
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, BufferedImage.TYPE_INT_RGB);
                //写入图像内容
                Graphics2D gr = imgs[count].createGraphics();
                // 增加下面代码使得背景透明
                imgs[count] = gr.getDeviceConfiguration().createCompatibleImage(chunkWidth, chunkHeight, Transparency.TRANSLUCENT);
//                gr.setBackground(Color.WHITE);// 背景为白色
//                 // 加上这句才算真正将背景颜色设置为透明色
//                gr.clearRect(0, 0,chunkWidth,chunkHeight);
                gr.dispose();
                gr = imgs[count].createGraphics();
                gr.drawImage(image, 0, 0,
                        chunkWidth, chunkHeight,
                        chunkWidth * y, chunkHeight * x,
                        chunkWidth * y + chunkWidth,
                        chunkHeight * x + chunkHeight, null);
                gr.dispose();
                count++;
            }
        }
        return imgs;
    }
}

