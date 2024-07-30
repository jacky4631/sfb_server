package com.mailvor.modules.tools.service.impl;

import com.mailvor.modules.tools.config.AliOssConfig;
import com.mailvor.modules.tools.service.AliOssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AliOssServiceImpl implements AliOssService {

    @Override
    public String uploadFileAvatar(MultipartFile file) {
        try {
            //创建ossClinet实例
            OSS ossClient = new OSSClientBuilder().build(AliOssConfig.END_POINT,
                    AliOssConfig.ACCESS_KEY_ID, AliOssConfig.ACCESS_KEY_SECRET);
            //上传文件流
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            String filename = file.getOriginalFilename();
            //调用oss方法实现上传
            //1.第一个参数为bucket名称  第二个为文件路径和文件名称  第三个为上传文件的输入流
            ossClient.putObject(AliOssConfig.BUCKET_NAME, AliOssConfig.AVATAR_PATH + filename,inputStream);
            ossClient.shutdown();
            //把上传文件的路径进行返回 https://renyi-1997.oss-cn-beijing.aliyuncs.com/888.jpg?versionId=null
            return AliOssConfig.DOMAIN + AliOssConfig.AVATAR_PATH + filename;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String uploadCard(InputStream inputStream, String filename, String path) {
        //创建ossClinet实例
        OSS ossClient = new OSSClientBuilder().build(AliOssConfig.END_POINT,
                AliOssConfig.ACCESS_KEY_ID, AliOssConfig.ACCESS_KEY_SECRET);
        //上传文件流
        //获取文件名称
        //调用oss方法实现上传
        //1.第一个参数为bucket名称  第二个为文件路径和文件名称  第三个为上传文件的输入流
        ossClient.putObject(AliOssConfig.BUCKET_NAME, AliOssConfig.CARD_PATH + path + filename,inputStream);
        ossClient.shutdown();
        //把上传文件的路径进行返回 https://renyi-1997.oss-cn-beijing.aliyuncs.com/888.jpg?versionId=null
        return AliOssConfig.DOMAIN + AliOssConfig.CARD_PATH  + path + filename;
    }



    public String uploadImage(MultipartFile file) {
        try {
            //创建ossClinet实例
            OSS ossClient = new OSSClientBuilder().build(AliOssConfig.END_POINT,
                    AliOssConfig.ACCESS_KEY_ID, AliOssConfig.ACCESS_KEY_SECRET);
            //上传文件流
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            String filename = file.getOriginalFilename();
            //调用oss方法实现上传
            //1.第一个参数为bucket名称  第二个为文件路径和文件名称  第三个为上传文件的输入流
            ossClient.putObject(AliOssConfig.BUCKET_NAME, AliOssConfig.IMAGE_PATH + filename,inputStream);
            ossClient.shutdown();
            //把上传文件的路径进行返回 https://renyi-1997.oss-cn-beijing.aliyuncs.com/888.jpg?versionId=null
            return AliOssConfig.DOMAIN + AliOssConfig.IMAGE_PATH + filename;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<String> uploadImages(MultipartFile[] files) {
        List<String> paths = new ArrayList<>(files.length);
        for(MultipartFile file : files) {
            String path = uploadImage(file);
            paths.add(path);
        }
        return paths;
    }
}
