package com.lq.pdfdemo;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.*;

/**
 * AllPdfSearch
 *
 * @author cat
 * @description TODO
 * @date 2022/2/14
 */
@Slf4j
public class AllPdfSearch {
    public static void main(String[] args) {
        File pdfFile = new File("D://test5.pdf");
        try {
            // 加载PDF
            PDDocument pdfDocument = PDDocument.load(pdfFile);
            // 获取总页数,从1开始计算
            int page = pdfDocument.getPages().getCount();
            PrintTextLocations stripper = new PrintTextLocations("北京/广州办事处", 7);
            // 全页遍历
            for (int i = 1; i <= page; i++) {
                log.info("当前处理第{}页", i);
                // 文本位置排序
                stripper.setSortByPosition(true);
                // 起始页
                stripper.setStartPage(i);
                // 结束页
                stripper.setEndPage(i);
                Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
                stripper.writeText(pdfDocument, dummy);
                // X Y 轴出现说明找到目标
                if (stripper.xTarget != 0F && stripper.yTarget != 0F) {
                    // 找到目标
                    log.info("目标在第{}页", i);
                    log.info("目标X轴(会计算左右空行): {}, 目标Y轴(不包含换行符占用行): {}", stripper.xTarget, stripper.yTarget);
                    // 获取页高
                    float upperRightY = pdfDocument.getPage(i-1).getBBox().getUpperRightY();
                    log.info("页高: {}", upperRightY);
                    // 传入图片路径,以及PDF文档对象
                    PDImageXObject imageObject = PDImageXObject.createFromFile("D://廖琦测试_长方形.png", pdfDocument);
                    PDPage pdfDocumentPage = pdfDocument.getPage(i-1);
                    PDPageContentStream pdPageContentStream = new PDPageContentStream(pdfDocument, pdfDocumentPage, PDPageContentStream.AppendMode.APPEND , true, true);
                    float imageWidth = 70F;
                    float imageHeight = 70F;
                    float x = stripper.xTarget + stripper.fontSize;
                    float y = upperRightY - stripper.yTarget - imageWidth;
                    pdPageContentStream.drawImage(imageObject, x, y, imageWidth, imageHeight);
                    log.info("定位坐标X:{} Y:{}", x, y);
                    pdPageContentStream.close();
                    // PDF导出
                    pdfDocument.save(new File("D://2.pdf"));
                    pdfDocument.close();
                    // 只要定位到就停止
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
