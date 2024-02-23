package com.zh.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.coobird.thumbnailator.Thumbnails;

@Component
public class PictureUtils {
    private static int targetSizeKB;
    private static double quality;

    @Value("${custom.pictureCompress.targetSizeKB}")
    public void setTargetSizeKB(int targetSizeKB) {
        PictureUtils.targetSizeKB = targetSizeKB;
    }

    @Value("${custom.pictureCompress.quality}")
    public void setQuality(double quality) {
        PictureUtils.quality = quality;
    }
    public static byte[] compressImage(byte[] originalImage) throws IOException {
    // 设置压缩参数，具体可以根据需求调整
    
    // 使用 Thumbnails 进行压缩
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(new ByteArrayInputStream(originalImage))
            .outputFormat("jpg") // 输出格式为JPEG
            .scale(1.0) // 按比例缩放，1.0表示不缩放
            .outputQuality(quality) // 设置压缩质量
            .toOutputStream(outputStream);

    // 循环进行压缩，直到目标大小
    double qualityTemp = quality;
    while (outputStream.size() > targetSizeKB * 1024 && qualityTemp>0.1) {
        outputStream.reset();
        qualityTemp -= 0.1;
        System.out.println(qualityTemp);
        Thumbnails.of(new ByteArrayInputStream(originalImage))
                .outputFormat("jpg")
                .scale(1.0)
                .outputQuality(qualityTemp)
                .toOutputStream(outputStream);
    }

    return outputStream.toByteArray();
}
    
}
