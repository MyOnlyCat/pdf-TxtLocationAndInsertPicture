package com.lq.pdfdemo;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * PrintTextLocations
 *
 * @author cat
 * @description TODO
 * @date 2022/2/11
 */
@Slf4j
public class PrintTextLocations extends PDFTextStripper {

    public Float xTarget = 0F;

    public Float yTarget = 0F;

    public Float lastY = 0F;

    public String targetString;

    public int targetIndex;

    public Float fontSize = 0F;


    public PrintTextLocations(String targetString, int targetIndex) throws IOException {
        if (targetString.length() == 0) {
            throw new RuntimeException("定位长度不能为0");
        }else {
            this.targetString = targetString;
            this.targetIndex = targetIndex;
        }
    }

    /**
     * 重写方法,获取字坐标
     *
     * @param text
     * @param textPositions
     * @throws IOException
     */
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        for (int i = 0; i < textPositions.size(); i++) {
            TextPosition textInfo = textPositions.get(i);
            String pdfStringTxt = textInfo.getUnicode();
            List<String> targetStringList = List.of(this.targetString.split(""));
            int targetSize = targetStringList.size();
            String firstString = targetStringList.get(0);
            boolean findTarget = false;
            // 打印文字
//            log.info("{}, X: {}, Y: {}", pdfStringTxt, textInfo.getX(), textInfo.getY());
            if (firstString.equals(pdfStringTxt)) {
                for (int j = 1; j <targetSize; j++) {
                    String nextString = targetStringList.get(j);
                    if (i + targetSize <= textPositions.size()
                            && nextString.equals(textPositions.get(i + j).getUnicode())) {
                        findTarget = true;
                    } else {
                        findTarget = false;
                        break;
                    }
                }
                if (findTarget) {
                    // 定位,只定位第一次出现
                    // X轴定时时需要加上文字的宽度,让添加的图片定位准确
                    fontSize = textPositions.get(i + targetIndex).getFontSizeInPt();
                    xTarget = textPositions.get(i + targetIndex).getX();
                    yTarget = textPositions.get(i + targetIndex).getY();
                    break;
                }
            }
        }
    }
}

