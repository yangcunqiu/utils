package com.ycq.utils.qrcode;

import com.google.zxing.WriterException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description:
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/1/10 19:27
 * @since JDK 1.8
 */
public class QRCodeUtilTest {

    //
    @Test
    public void test() {

        // 二维码内容
        String content = "https://www.baidu.com";
        // logo图片路径
        String logoUrl = "https://m.baidu.com/static/index/plus/plus_logo_web.png";
        // 生成的二维码保存路径
        String QRCodePath = "e:/QRCode.png";
        QRCodeUtil.createQRCode(content, logoUrl, QRCodePath, null);
    }


    @Test
    public void download() throws IOException {
        //new一个URL对象
        URL url = new URL("https://img2018.cnblogs.com/blog/1696344/201906/1696344-20190606114827017-1408927750.png");
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        OutputStream outputStream = new FileOutputStream(new File("e:/test.png"));
        IOUtils.copy(inStream, outputStream);

    }

    @Test
    public void urlTest() throws IOException {
        //new一个URL对象
        URL url = new URL("https://img2018.cnblogs.com/blog/1696344/201906/1696344-20190606114827017-1408927750.png");
        InputStream inputStream = url.openStream();
        OutputStream outputStream = new FileOutputStream(new File("e:/test.png"));
        IOUtils.copy(inputStream, outputStream);
    }

    @Test
    public void imageTest() {
        // 二维码内容
        String content = "https://www.baidu.com";
        // logo图片路径
        String logoUrl = "https://m.baidu.com/static/index/plus/plus_logo_web.png";
        BufferedImage bufferedImage = QRCodeUtil.getBufferImage(content, logoUrl);
        System.out.println(bufferedImage);
    }

    @Test
    public void inputTest() throws IOException {
        // 二维码内容
        String content = "https://txqa.ziyun-cloud.com/service/park/visitorReport/export/hierarchy?employeeToken=2A76A774BB01662A6F0D4C5BDEA1D71D344EFA196E7DBD3F1708229EE5B2E0CA48E6DEAA57BFD3640011A09BAB2BB1812FFE849896FD3A0AF8211B7D846B0C4C0A8DD055A926F3CC990BE4E7EBA769374E582052E7C97F23D6AEB0EB0C7E0A0B00C8DA0DB49CA81992290767C0378AC44E6A58B457953C23DAEFB243836DE7894C6DB58B96EF69DF89B71F42915F5728B6B7362772BA77653B691EF65822CA5E5DDE0AE4459DA7749E406CB623AE05847F236479DA8BD9138619F3E2572F73920CDBE0E2B12AFEE03D339611766B293DB2559FB6CBE3B2B4B69DFFDF8E203DECE5E5FF07FB2E445CCD0DB4907CCB255F2DB1818F35555A224EA09BD735D44DB18C99A047718047ED2C0D6640C1A225DEB742B4863EE7B5DED54BB78CE73CAA98DB51076B63E8B7B12B13F363BD66B9225E84450E6BB950E6BEF4B4A07E230F0BA53DD512060325424E682F9DB98358413C521A064C37BAC9297F51A123A0BA38B4AE45D627B06F8F0BFE031B1D737218042145A21B7CBFE8FEE47FEB343E93F3";
        // logo图片路径
        String logoUrl = "https://m.baidu.com/static/index/plus/plus_logo_web.png";
        InputStream inputStream = QRCodeUtil.getInputStream(content, logoUrl, null);
        IOUtils.copy(inputStream, new FileOutputStream(new File("e://logoInput.png")));
    }

    @Test
    public void noLogo() {
        String content = "https://baidu.com";
        String QRCodePath = "e:/noLogo.png";
        QRCodeUtil.createQRCode(content, QRCodePath, null);
    }

    @Test
    public void noLogoImage() throws IOException {
        String content = "https://baidu.com";
        BufferedImage bufferImage = QRCodeUtil.getBufferImage(content);
        ImageIO.write(bufferImage, "png", new File("e://noLogoImage.png"));
    }

    @Test
    public void noLogoInput() throws IOException, WriterException {
        String content = "https://baidu.com";
        InputStream inputStream = QRCodeUtil.getInputStream(content, null);
        IOUtils.copy(inputStream, new FileOutputStream(new File("e://noLogoInput.png")));
    }

    @Test
    public void readFile(){
        String filePath = "e://logoInput.png";
        String QRCodeText = QRCodeUtil.readQRCode(filePath);
        System.out.println(QRCodeText);
    }

    @Test
    public void readBufferImage() throws IOException {
        String filePath = "e://logoInput.png";
        File file = new File(filePath);
        BufferedImage image = ImageIO.read(file);
        String QRCodeText = QRCodeUtil.readQRCode(image);
        System.out.println(QRCodeText);
    }

    @Test
    public void readInputStream() throws IOException {
        String filePath = "e://logoInput.png";
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        String QRCodeText = QRCodeUtil.readQRCode(inputStream);
        System.out.println(QRCodeText);
    }

    @Test
    public void readURl() throws IOException {
        BufferedImage image = ImageIO.read(new URL("https://www.liantu.com/images/2013/case/4.jpg"));
        String QRCodeText = QRCodeUtil.readQRCode(image);
        System.out.println(QRCodeText);
    }
}
