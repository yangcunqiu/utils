package com.ycq.utils.qrcode;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *  二维码相关工具类
 * @author 杨存秋
 * @version 1.0
 * date: 2020/1/10 19:19
 * @since JDK 1.8
 */
public class QRCodeUtil {

    private QRCodeUtil(){}

    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    // 生成二维码相关参数
    private static int QRCODE_WIDTH = 500;
    private static int QRCODE_HEIGHT = 500;
    private static String QRCODE_FORMATNAME = "png";

    // 二维码内嵌logo相关参数
    private static final int LOGO_WIDTH = 50;
    private static final int LOGO_HEIGHT = 50;
    private static final int LOGO_HALF_WIDTH = LOGO_WIDTH / 2;
    private static final int LOGO_FRAME_WIDTH = 2;

    // 二维码写码器
    private static MultiFormatWriter multiWriter = new MultiFormatWriter();
    // 二维码读码器
    private static MultiFormatReader formatReader = new MultiFormatReader();

    /**
     * Description: 生成二维码保存在本地
     * @param content: 二维码内容, width: 二维码长度, height: 二维码宽度,
     *                 logoUrl: logo图片的路径, QRCodePath: 生成的二维码存放路径,
     *                 formatName: 生成二维码后缀
     * @author 杨存秋
     * date 2020/1/7 15:19
     */
    public static void createQRCode(String content, String logoUrl, String QRCodePath, String formatName) {

        // 将画好的二维码写到指定文件
        try {
            ImageIO.write(
                    genBarcode(content, logoUrl),  // 获取二维码缓存图片
                    formatName == null ? QRCODE_FORMATNAME : formatName,
                    new File(QRCodePath)
            );
        } catch (Exception e) {
            logger.error("生成带logo二维码到本地失败: content: {}; logoUrl: {}, QRCodePath: {}, formatName: {}", content, logoUrl, QRCodePath, formatName);

            e.printStackTrace();
        }

    }

    // createQRCode重载 无logo
    public static void createQRCode(String content, String QRCodePath, String formatName) {

        try {
            BitMatrix bitMatrix = getBitMatrix(content);
            Path file = new File(QRCodePath).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, formatName == null ? QRCODE_FORMATNAME : formatName, file);
        } catch (Exception e) {
            logger.error("生成无logo二维码到本地失败: content: {}; QRCodePath: {}, formatName: {}", content, QRCodePath, formatName);
            e.printStackTrace();
        }
    }

    // 生成二维码
    private static BitMatrix getBitMatrix(String content) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //L级：约可纠错7%的数据码字,M级：约可纠错15%的数据码字,Q级：约可纠错25%的数据码字,H级：约可纠错30%的数据码字
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);

        return multiWriter.encode(content, BarcodeFormat.QR_CODE, QRCODE_WIDTH, QRCODE_HEIGHT, hints);
    }


    /**
     * Description: 生成二维码, 返回BufferImage对象
     * @param content: 二维码内容, logoUrl: logo
     * @return BufferedImage
     * @author 杨存秋
     * date 2020/1/7 17:34
     */
    public static BufferedImage getBufferImage(String content, String logoUrl) {
        try {
            return genBarcode(content, logoUrl);
        } catch (Exception e) {
            logger.error("获取带logo BufferImage对象失败: content: {}, logoUrl: {}", content, logoUrl);
            e.printStackTrace();
        }
        return null;
    }

    // getBufferImage重载 无logo
    public static BufferedImage getBufferImage(String content) {
        try {
            int[] pixels = new int[QRCODE_WIDTH * QRCODE_HEIGHT];
            BitMatrix bitMatrix = getBitMatrix(content);
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    pixels[y * QRCODE_WIDTH + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xfffffff;
                }
            }
            BufferedImage image = new BufferedImage(QRCODE_WIDTH, QRCODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            image.getRaster().setDataElements(0, 0, QRCODE_WIDTH, QRCODE_HEIGHT, pixels);
            return image;
        } catch (Exception e){
            logger.error("获取无logo BufferImage对象失败: content: {}", content);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Description: 生成二维码, 返回InputStream对象
     * @param content: 二维码内容, logoUrl: logo, formatName: 生成二维码的图片后缀
     * @return InputStream
     * @author 杨存秋
     * date 2020/1/7 17:37
     */
    public static InputStream getInputStream(String content, String logoUrl, String formatName) {
        try {
            BufferedImage bufferedImage = genBarcode(content, logoUrl);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, formatName == null ? QRCODE_FORMATNAME : formatName, byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e){
            logger.error("获取带logo InputStream对象失败: content: {}; logoUrl: {}; formatName: {}", content, logoUrl, formatName);
            e.printStackTrace();
        }
        return null;
    }

    // getInputStream重载 无logo
    public static InputStream getInputStream(String content, String formatName) {
        try {
            BufferedImage bufferedImage = getBufferImage(content);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, formatName == null ? QRCODE_FORMATNAME : formatName, byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e){
            logger.error("获取无logo InputStream对象失败: content: {}; formatName: {}", content, formatName);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Description: 在内存生成带logo的二维码缓存图片
     *
     * @param content: 二维码内容, logoUrl: logo路径
     * @return BufferedImage
     * @author 杨存秋
     * date 2020/1/7 15:40
     */
    private static BufferedImage genBarcode(String content, String logoUrl) throws WriterException, IOException {

        // 读取logo图像
        BufferedImage scaleImage = addLogo(logoUrl);

        // 创建二维数组
        int[][] srcPixels = new int[LOGO_WIDTH][LOGO_HEIGHT];
        for (int i = 0; i < scaleImage.getWidth(); i++) {
            for (int j = 0; j < scaleImage.getHeight(); j++) {
                srcPixels[i][j] = scaleImage.getRGB(i, j);
            }
        }

        // 生成二维码
        BitMatrix bitMatrix = getBitMatrix(content);
        // 二维矩阵转为一维像素数组
        int halfW = bitMatrix.getWidth() / 2;
        int halfH = bitMatrix.getHeight() / 2;
        int[] pixels = new int[QRCODE_WIDTH * QRCODE_HEIGHT];

        // 画logo
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                // 读取图片
                if (x > halfW - LOGO_HALF_WIDTH && x < halfW + LOGO_HALF_WIDTH && y > halfH - LOGO_HALF_WIDTH
                        && y < halfH + LOGO_HALF_WIDTH) {
                    pixels[y * QRCODE_WIDTH + x] = srcPixels[x - halfW + LOGO_HALF_WIDTH][y - halfH + LOGO_HALF_WIDTH];
                }
                // 在图片四周形成边框
                else if ((x > halfW - LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH && x < halfW - LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH
                        && y > halfH - LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH && y < halfH + LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH)
                        || (x > halfW + LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH && x < halfW + LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH
                        && y > halfH - LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH
                        && y < halfH + LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH)
                        || (x > halfW - LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH && x < halfW + LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH
                        && y > halfH - LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH
                        && y < halfH - LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH)
                        || (x > halfW - LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH && x < halfW + LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH
                        && y > halfH + LOGO_HALF_WIDTH - LOGO_FRAME_WIDTH
                        && y < halfH + LOGO_HALF_WIDTH + LOGO_FRAME_WIDTH)) {
                    pixels[y * QRCODE_WIDTH + x] = 0xfffffff;
                } else {
                    // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * QRCODE_WIDTH + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xfffffff;
                }
            }
        }
        BufferedImage image = new BufferedImage(QRCODE_WIDTH, QRCODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, QRCODE_WIDTH, QRCODE_HEIGHT, pixels);
        return image;
    }

    /**
     * Description: 读取logo并按比例缩放
     *
     * @param logoUrl: logo路径
     * @return BufferedImage
     * @author 杨存秋
     * date 2020/1/7 15:37
     */
    private static BufferedImage addLogo(String logoUrl) throws IOException {
        BufferedImage srcImage;
        // 判断logoUrl是否是本地图片
        File logoFile = new File(logoUrl);
        if (logoFile.exists()){
            srcImage = ImageIO.read(logoFile);
        } else {
            // 网络图片
            URL url = new URL(logoUrl);
            srcImage = ImageIO.read(url);
        }
        Image destImage = srcImage.getScaledInstance(LOGO_WIDTH, LOGO_HEIGHT, BufferedImage.SCALE_SMOOTH);
        // 计算缩放比例
        double ratio;
        if ((srcImage.getHeight() > LOGO_HEIGHT) || (srcImage.getWidth() > LOGO_WIDTH)) {
            if (srcImage.getHeight() > srcImage.getWidth()) {
                ratio = (new Integer(LOGO_HEIGHT)).doubleValue() / srcImage.getHeight();
            } else {
                ratio = (new Integer(LOGO_WIDTH)).doubleValue() / srcImage.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            destImage = op.filter(srcImage, null);
        }
        // 空余补白
        BufferedImage image = new BufferedImage(LOGO_WIDTH, LOGO_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = image.createGraphics();
        graphic.setColor(Color.white);
        graphic.fillRect(0, 0, LOGO_WIDTH, LOGO_HEIGHT);
        if (LOGO_WIDTH == destImage.getWidth(null)) {
            graphic.drawImage(destImage, 0, (LOGO_HEIGHT - destImage.getHeight(null)) / 2, destImage.getWidth(null),
                    destImage.getHeight(null), Color.white, null);
        } else {
            graphic.drawImage(destImage, (LOGO_WIDTH - destImage.getWidth(null)) / 2, 0, destImage.getWidth(null),
                    destImage.getHeight(null), Color.white, null);
        }
        graphic.dispose();
        destImage = image;
        return (BufferedImage) destImage;
    }





    /**
     * Description: 解析二维码
     * @param BufferedImage对象
     * @return Result
     * @author 杨存秋
     * date 2020/1/8 16:18
     */
    private static Result decodeQRCode(BufferedImage image) throws NotFoundException {
        // 二维码格式化读取器
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

        //定义解析二维码参数
        Map<DecodeHintType, String> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        return formatReader.decode(binaryBitmap,hints);
    }

    /**
     * Description: 解析本地二维码
     * @param filePath: 文件路径
     * @return 二维码内容
     * @author 杨存秋
     * date 2020/1/8 16:18
     */
    public static String readQRCode(String filePath){
        try {
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(file);
            Result result = decodeQRCode(image);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 重载 解析BufferImage对象
    public static String readQRCode(BufferedImage image){
        try{
            Result result = decodeQRCode(image);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 重载 解析InputStream对象
    public static String readQRCode(InputStream inputStream){
        try{
            BufferedImage image = ImageIO.read(inputStream);
            Result result = decodeQRCode(image);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
