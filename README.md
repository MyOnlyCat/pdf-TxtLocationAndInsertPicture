# PDF文字定位插入图片
# 核心库
```xml
<!--    PDF解析核心    -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.22</version>
</dependency>
```
# 效果示例
所有演示基于`AllPdfSearch`类
## 普通文档PDF
```java
// 传入需要查找的文字和图片插入时需要定位的文字下标
PrintTextLocations stripper = new PrintTextLocations("审核单位盖章", 5);
```
![](https://blog-oss-voidday.oss-cn-chengdu.aliyuncs.com/other/202202141509314.png)  
```java
PrintTextLocations stripper = new PrintTextLocations("授权签署人签字", 6);
```
![](https://blog-oss-voidday.oss-cn-chengdu.aliyuncs.com/other/202202141516633.png)   
可以看到图片定位到下标5的"章"字,以及下标6的"字"字
## 特殊类PDF
![](https://blog-oss-voidday.oss-cn-chengdu.aliyuncs.com/other/202202141521385.png)   
```java
PrintTextLocations stripper = new PrintTextLocations("北京/广州办事处", 7);
```
![](https://blog-oss-voidday.oss-cn-chengdu.aliyuncs.com/other/202202141521643.png)   
# 核心代码
- 重写 `writeString`获取文字坐标相关信息
```java
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
```
- Y轴坐标空行计算
# 提示
- 获取的文字Y轴是没有计算空行的,解析时忽略的空行
- 目前定位文字跨行了就无法定位因为PDFbox的原因,本人暂时无法解决
