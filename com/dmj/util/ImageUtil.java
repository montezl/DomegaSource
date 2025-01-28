package com.dmj.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.dmj.cs.bean.Rectangle;
import com.dmj.domain.Img;
import com.dmj.domain.Questionimage;
import com.dmj.service.ctb.Ctb_StandardAndFine_img;
import com.dmj.service.examManagement.ExamManageService;
import com.dmj.serviceimpl.ctb.Ctb_StandardAndFine_img_Serviceimpl;
import com.dmj.serviceimpl.examManagement.ExamManageServiceimpl;
import com.dmj.serviceimpl.systemManagement.SystemServiceImpl;
import com.dmj.util.config.Configuration;
import com.dmj.util.excel.ExcelHelper;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.zht.db.ServiceFactory;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.jfree.util.Log;
import sun.misc.BASE64Encoder;

/* loaded from: ImageUtil.class */
public class ImageUtil {
    public static final String Image_Part = "ImagePart";
    public static final String Image_Rate = "ImageRate";
    public static final String Image_Width = "ImageWidth";
    public static final String Image_Height = "ImageHeight";
    private static ExamManageService examManageService = (ExamManageService) ServiceFactory.getObject(new ExamManageServiceimpl());
    private static Ctb_StandardAndFine_img imageService = (Ctb_StandardAndFine_img) ServiceFactory.getObject(new Ctb_StandardAndFine_img_Serviceimpl());
    Logger log = Logger.getLogger(getClass());

    public static byte[] imageToByte(String path) {
        JPEGEncodeParam param1 = new JPEGEncodeParam();
        try {
            FileSeekableStream ss = new FileSeekableStream(path);
            RenderedImage createImageDecoder = ImageCodec.createImageDecoder("tiff", ss, param1);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(createImageDecoder, Const.IMAGE_FORMAT, out);
            return out.toByteArray();
        } catch (Exception e) {
            Log.error("ImageUtil imageToByte(): ", e);
            return null;
        }
    }

    public static byte[] tiffToJpg(byte[] fs, int index) {
        try {
            TIFFEncodeParam param = new TIFFEncodeParam();
            ByteArrayInputStream in = new ByteArrayInputStream(fs);
            ImageDecoder dec = ImageCodec.createImageDecoder("tiff", in, (ImageDecodeParam) null);
            dec.getNumPages();
            param.setCompression(32773);
            param.setLittleEndian(false);
            RenderedImage page = dec.decodeAsRenderedImage(0);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(page, Const.IMAGE_FORMAT, out);
            return out.toByteArray();
        } catch (IOException e) {
            Log.error("TIFF图片转换为 JPG文件错误 ", e);
            return fs;
        } catch (Exception e2) {
            Log.error("TIFF图片转换为 JPG文件错误 ", e2);
            return fs;
        }
    }

    public static byte[] InputStreamToByte(InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        while (true) {
            int ch = iStrm.read();
            if (ch != -1) {
                bytestream.write(ch);
            } else {
                byte[] imgdata = bytestream.toByteArray();
                bytestream.close();
                return imgdata;
            }
        }
    }

    public static String getImageWidthAndHeight(byte[] img) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(img);
            BufferedImage image = ImageIO.read(in);
            String str = image.getWidth() + Const.STRING_SEPERATOR + image.getHeight();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Img getImageWidthAndHeight_bean(byte[] img) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(img);
            BufferedImage image = ImageIO.read(in);
            Img ii = new Img();
            if (image != null) {
                ii.setHeight(String.valueOf(image.getHeight()));
                ii.setWidth(String.valueOf(image.getWidth()));
            }
            return ii;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<byte[]> splitImage(byte[] img, int depart_num, int depart_index) {
        List<byte[]> list = new ArrayList<>();
        try {
            new ByteArrayInputStream(img);
            BufferedImage image = createImageFromBytes(img);
            int chunkWidth = image.getWidth();
            int chunkHeight = image.getHeight() / depart_num;
            int x = 0;
            while (x < depart_num) {
                int topHeight = x > 0 ? 80 : 0;
                BufferedImage new_img = new BufferedImage(chunkWidth, chunkHeight, image.getType());
                Graphics2D gr = new_img.createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, 0, (chunkHeight * x) - topHeight, chunkWidth, (chunkHeight * x) + chunkHeight, (ImageObserver) null);
                gr.dispose();
                byte[] bytes = bufferedImageTobytes(new_img, 0.1f);
                list.add(bytes);
                x++;
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    private static BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage toBufferedImage(byte[] imagedata) {
        BufferedImage createImage = Toolkit.getDefaultToolkit().createImage(imagedata);
        if (createImage instanceof BufferedImage) {
            return createImage;
        }
        Image image = new ImageIcon(createImage).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth((ImageObserver) null), image.getHeight((ImageObserver) null), 1);
        } catch (HeadlessException e) {
        }
        if (bimage == null) {
            bimage = new BufferedImage(image.getWidth((ImageObserver) null), image.getHeight((ImageObserver) null), 1);
        }
        Graphics2D createGraphics = bimage.createGraphics();
        createGraphics.drawImage(image, 0, 0, (ImageObserver) null);
        createGraphics.dispose();
        return bimage;
    }

    public static Map getImagePart(byte[] img, int height, int maxHeight) {
        Map<String, Integer> map = new HashMap<>();
        BufferedImage image = null;
        if (null == img || img.length <= 0) {
            return map;
        }
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(img);
            BufferedImage image2 = ImageIO.read(in);
            int h = image2.getHeight();
            if (h < height) {
                map.put(Image_Part, 1);
                map.put(Image_Rate, 5);
                map.put(Image_Width, Integer.valueOf((int) Math.ceil(image2.getWidth())));
                map.put(Image_Height, Integer.valueOf((int) Math.ceil(image2.getHeight())));
                return map;
            }
            if (h < maxHeight) {
                map.put(Image_Part, 1);
                map.put(Image_Rate, 4);
                map.put(Image_Width, Integer.valueOf((int) Math.ceil(image2.getWidth())));
                map.put(Image_Height, Integer.valueOf((int) Math.ceil(image2.getHeight())));
                return map;
            }
            int part = h % height == 0 ? h / height : (h / height) + 1;
            map.put(Image_Part, Integer.valueOf(part));
            map.put(Image_Rate, 4);
            map.put(Image_Width, Integer.valueOf((int) Math.ceil(image2.getWidth())));
            map.put(Image_Height, Integer.valueOf((int) Math.ceil(image2.getHeight() / part)));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put(Image_Part, 1);
            map.put(Image_Rate, 5);
            map.put(Image_Width, Integer.valueOf((int) Math.ceil(image.getWidth())));
            map.put(Image_Height, Integer.valueOf((int) Math.ceil(image.getHeight())));
            return map;
        }
    }

    public static byte[] commbinSubfieldImage(byte[] image, boolean isSplit, int screenHeight, int showWidth) {
        if (!isSplit) {
            return image;
        }
        if (null == image) {
            return null;
        }
        try {
            if (image.length <= 0) {
                return null;
            }
            ByteArrayInputStream in = new ByteArrayInputStream(image);
            BufferedImage imgs = ImageIO.read(in);
            int width = imgs.getWidth();
            int height = imgs.getHeight();
            float rate = Float.valueOf(showWidth).floatValue() / Float.valueOf(width).floatValue();
            if (height * rate <= screenHeight) {
                return image;
            }
            int finalWidth = width * 2;
            int finalHeight = height / 2;
            int drawHeight = finalHeight + 20;
            BufferedImage outImage = new BufferedImage(finalWidth, drawHeight, 1);
            Graphics2D g2d = outImage.getGraphics();
            g2d.drawImage(imgs, 0, 0, width, drawHeight, 0, 0, width, drawHeight, (ImageObserver) null);
            g2d.drawImage(imgs, width, 0, finalWidth, drawHeight, 0, finalHeight - 20, width, height, (ImageObserver) null);
            byte[] bytes = bufferedImageTobytes(outImage, 0.1f);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] bufferedImageTobytes(BufferedImage image, float quality) {
        if (image == null) {
            return null;
        }
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(2);
        iwp.setCompressionQuality(quality);
        iwp.setProgressiveMode(0);
        ColorModel colorModel = ColorModel.getRGBdefault();
        iwp.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(1, 1)));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IIOImage iIamge = new IIOImage(image, (List) null, (IIOMetadata) null);
        try {
            writer.setOutput(ImageIO.createImageOutputStream(byteArrayOutputStream));
            writer.write((IIOMetadata) null, iIamge, iwp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (Exception e2) {
        }
        return bytes;
    }

    public static String CombineImages(String FileName1, String FileName2) {
        try {
            Image bg_src = GetImage(FileName1);
            Image mask_src = GetImage(FileName2);
            return CombineTwoImages(bg_src, mask_src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String CombineImages(String FileName1, byte[] array) {
        try {
            Image bg_src = GetImage(FileName1);
            Image mask_src = GetImage(array);
            return CombineTwoImages(bg_src, mask_src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String CombineImages(byte[] array1, byte[] array2) {
        try {
            Image bg_src = GetImage(array1);
            Image mask_src = GetImage(array2);
            return CombineTwoImages(bg_src, mask_src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String CombineTwoImages(Image bg_src, Image mask_src) throws IOException {
        byte[] data = CombineTwoImagesToByteArray(bg_src, mask_src);
        BASE64Encoder encoder = new BASE64Encoder();
        String rv = encoder.encode(data);
        return rv;
    }

    public static byte[] CombineTwoImagesToByteArray(Image bg_src, Image mask_src) throws IOException {
        if (null == bg_src) {
            return null;
        }
        if (mask_src == null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write((BufferedImage) bg_src, "png", out);
            return out.toByteArray();
        }
        int bg_width = bg_src.getWidth((ImageObserver) null);
        int bg_height = bg_src.getHeight((ImageObserver) null);
        int mask_width = mask_src.getWidth((ImageObserver) null);
        int mask_height = mask_src.getHeight((ImageObserver) null);
        BufferedImage tag = new BufferedImage(bg_width, bg_height, 1);
        Graphics2D g2d = tag.createGraphics();
        g2d.drawImage(bg_src, 0, 0, bg_width, bg_height, (ImageObserver) null);
        g2d.setComposite(AlphaComposite.getInstance(10, 1.0f));
        g2d.drawImage(mask_src, 0, 0, mask_width, mask_height, (ImageObserver) null);
        g2d.setComposite(AlphaComposite.getInstance(3));
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        ImageIO.write(tag, "png", out2);
        return out2.toByteArray();
    }

    public static InputStream GetInputStream(String url) {
        InputStream inStream = null;
        try {
            URL Url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            inStream = conn.getInputStream();
        } catch (Exception e) {
        }
        return inStream;
    }

    public static String CombineImagesByUrl(String url1, String url2, String projectPath) {
        try {
            Image bg_src = GetImage(url1, projectPath);
            Image mask_src = GetImage(url2, projectPath);
            return CombineTwoImages(bg_src, mask_src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] CombineImagesByUrl2(String url1, String url2, String projectPath) {
        try {
            Image bg_src = GetImage(url1, projectPath);
            Image mask_src = GetImage(url2, projectPath);
            return CombineTwoImagesToByteArray(bg_src, mask_src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String CombineImagesByUrl(String url1, byte[] array, String projectPath) {
        try {
            Image bg_src = GetImage(url1, projectPath);
            Image mask_src = GetImage(array);
            return CombineTwoImages(bg_src, mask_src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Image GetImage(byte[] array) throws IOException {
        if (array == null || array.length == 0) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        BufferedImage read = ImageIO.read(bis);
        bis.close();
        return read;
    }

    public static Image GetImage(String url, String projectPath) throws IOException {
        InputStream inStream = GetInputStream(url);
        if (inStream == null) {
            inStream = GetInputStream(projectPath + (url.startsWith("/") ? "" : "/") + url);
        }
        BufferedImage read = ImageIO.read(inStream);
        inStream.close();
        return read;
    }

    public static Image GetImage(String FileName) throws IOException {
        File bgfile = new File(FileName);
        return ImageIO.read(bgfile);
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int len = inStream.read(buffer);
            if (len != -1) {
                outStream.write(buffer, 0, len);
            } else {
                inStream.close();
                return outStream.toByteArray();
            }
        }
    }

    public static void deleteOneFile(String path) {
        try {
            SystemServiceImpl.deletefile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] addTextToImage_old(byte[] img, List<Questionimage> questionList) throws IOException {
        int flagW;
        Font f;
        ByteArrayInputStream in = new ByteArrayInputStream(img);
        String folderPath = System.getProperty("webapp.root") + "common/image/examInfo";
        BufferedImage bAllRight = imgToByte(folderPath + "/bAllRight.png");
        BufferedImage bAllWrong = imgToByte(folderPath + "/bAllWrong.png");
        BufferedImage bHalfOf = imgToByte(folderPath + "/bHalfOf.png");
        BufferedImage lAllRight = imgToByte(folderPath + "/lAllRight.png");
        BufferedImage lAllWrong = imgToByte(folderPath + "/lAllWrong.png");
        BufferedImage lHalfOf = imgToByte(folderPath + "/lHalfOf.png");
        BufferedImage buffImg = ImageIO.read(in);
        if (null == buffImg) {
            return FileUtil.getFileByteArray(folderPath + "/emptyW.gif");
        }
        int heightDouble = buffImg.getHeight();
        int widthDouble = buffImg.getWidth();
        BufferedImage grbImage = new BufferedImage(widthDouble, heightDouble, 1);
        Graphics g = grbImage.getGraphics();
        g.drawImage(buffImg, 0, 0, (ImageObserver) null);
        Color mycolor = Color.red;
        g.setColor(mycolor);
        Font f60 = new Font("微软雅黑", 0, 60);
        new Font("微软雅黑", 0, 40);
        int b = 100;
        for (int i = 0; i < questionList.size(); i++) {
            Questionimage question = questionList.get(i);
            if (question.getExt3() != null) {
                if (question.getPage() == 1) {
                    int a = "0".equals(question.getExt2()) ? 50 : 100;
                    g.setFont(f60);
                    g.drawString(question.getSubjectName() + "：" + ConvertHelper.replace0(question.getExt3()), a, b);
                    Font f2 = new Font("微软雅黑", 0, 40);
                    g.setFont(f2);
                    int b2 = b + 60;
                    g.drawString(ConvertHelper.replace0(question.getOqts()), a, b2);
                    int b3 = b2 + 50;
                    g.drawString(ConvertHelper.replace0(question.getSqts()), a, b3);
                    b = b3 + 80;
                }
            } else if (null != question.getQuestionType()) {
                if ("1".equals(question.getQuestionType())) {
                    int juli = (int) Float.parseFloat(question.getExt1());
                    if (Integer.parseInt(question.getQuestionW()) < 500) {
                        flagW = ((int) Float.parseFloat(question.getQuestionW())) / 3;
                        f = new Font("Arial", 1, flagW / 2);
                        if (Integer.parseInt(question.getQuestionH()) < 200) {
                            flagW = Integer.parseInt(question.getQuestionH()) / 3;
                            f = new Font("Arial", 1, flagW / 2);
                        }
                    } else {
                        flagW = 40;
                        f = new Font("Arial", 1, 60);
                    }
                    g.setFont(f);
                    if (!"1".equals(question.getType())) {
                        String scoreString = ConvertHelper.replace0(String.valueOf(question.getQuestionScore())) + "/" + ConvertHelper.replace0(String.valueOf(question.getFullScore()));
                        int strW = g.getFontMetrics(f).stringWidth(scoreString);
                        int cha = ((int) Float.parseFloat(question.getScoreW())) - strW;
                        g.drawString(scoreString, juli + cha, ((int) Float.parseFloat(question.getExt2())) + ((50 * flagW) / bAllRight.getWidth()));
                        if (Double.parseDouble(String.valueOf(question.getQuestionScore())) == Double.parseDouble(String.valueOf(question.getFullScore()))) {
                            g.drawImage(bAllRight, Integer.parseInt(question.getQuestionX()), (int) Float.parseFloat(question.getQuestionY()), flagW, (bAllRight.getHeight() * flagW) / bAllRight.getWidth(), (ImageObserver) null);
                        } else if (Double.parseDouble(String.valueOf(question.getQuestionScore())) == 0.0d) {
                            g.drawImage(bAllWrong, Integer.parseInt(question.getQuestionX()), (int) Float.parseFloat(question.getQuestionY()), flagW, (bAllWrong.getHeight() * flagW) / bAllWrong.getWidth(), (ImageObserver) null);
                        } else {
                            g.drawImage(bHalfOf, Integer.parseInt(question.getQuestionX()), (int) Float.parseFloat(question.getQuestionY()), flagW, (bHalfOf.getHeight() * flagW) / bHalfOf.getWidth(), (ImageObserver) null);
                        }
                    }
                    if (!"1".equals(question.getNum()) && question.getImg() != null) {
                        String imgPath = imageService.getImgPath(question.getExamPaperNum().toString(), null) + "/" + question.getImg();
                        Questionimage questionImg = new Questionimage();
                        questionImg.setImg(imgPath);
                        Questionimage fill = questionImg.fill();
                        ByteArrayInputStream biaoZhuin = new ByteArrayInputStream(fill.getImgByte());
                        BufferedImage biaoZhuinImage = ImageIO.read(biaoZhuin);
                        g.drawImage(biaoZhuinImage, Integer.parseInt(question.getQuestionX()), (int) Float.parseFloat(question.getQuestionY()), (int) Float.parseFloat(question.getQuestionW()), (int) Float.parseFloat(question.getQuestionH()), (ImageObserver) null);
                    }
                } else {
                    String answer = question.getAnswer();
                    if (Double.parseDouble(String.valueOf(question.getQuestionScore())) == Double.parseDouble(String.valueOf(question.getFullScore()))) {
                        g.drawImage(lAllRight, ((int) Float.parseFloat(question.getExt1())) + 4, (int) Float.parseFloat(question.getExt2()), (ImageObserver) null);
                    } else if (Double.parseDouble(String.valueOf(question.getQuestionScore())) == 0.0d) {
                        if ("-1".equals(answer)) {
                            g.drawImage(lAllWrong, ((int) Float.parseFloat(question.getExt1())) + 4, (int) Float.parseFloat(question.getExt2()), (ImageObserver) null);
                        } else {
                            Font f3 = new Font("Arial", 1, 30);
                            g.setFont(f3);
                            g.drawString(question.getAnswer(), ((int) Float.parseFloat(question.getExt1())) + 4, ((int) Float.parseFloat(question.getExt2())) + 20);
                        }
                    } else if ("-1".equals(answer)) {
                        g.drawImage(lHalfOf, ((int) Float.parseFloat(question.getExt1())) + 4, (int) Float.parseFloat(question.getExt2()), (ImageObserver) null);
                    } else {
                        Font f4 = new Font("Arial", 1, 30);
                        g.setFont(f4);
                        g.drawString(question.getAnswer(), ((int) Float.parseFloat(question.getExt1())) + 4, ((int) Float.parseFloat(question.getExt2())) + 20);
                    }
                }
            }
        }
        g.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(new BufferedImage[]{grbImage}).scale(1.0d).outputFormat("png").toOutputStream(os);
        byte[] bytes = os.toByteArray();
        try {
            os.close();
        } catch (Exception e) {
        }
        return bytes;
    }

    public static byte[] addTextToImage(byte[] img, List<Questionimage> questionList, String tupianType) throws IOException {
        Image image;
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(true);
        Object logType = session.getAttribute("logType");
        ByteArrayInputStream in = new ByteArrayInputStream(img);
        String folderPath = System.getProperty("webapp.root") + "common/image/examInfo";
        Image imgToByte = imgToByte(folderPath + "/bAllRight.png");
        Image imgToByte2 = imgToByte(folderPath + "/bAllWrong.png");
        Image imgToByte3 = imgToByte(folderPath + "/bHalfOf.png");
        BufferedImage lAllRight = imgToByte(folderPath + "/lAllRight.png");
        BufferedImage lAllWrong = imgToByte(folderPath + "/lAllWrong.png");
        BufferedImage lHalfOf = imgToByte(folderPath + "/lHalfOf.png");
        BufferedImage buffImg = ImageIO.read(in);
        if (null == buffImg) {
            return FileUtil.getFileByteArray(folderPath + "/emptyW.gif");
        }
        int heightDouble = buffImg.getHeight();
        int widthDouble = buffImg.getWidth();
        BufferedImage grbImage = new BufferedImage(widthDouble, heightDouble, 1);
        Graphics g = grbImage.getGraphics();
        g.drawImage(buffImg, 0, 0, (ImageObserver) null);
        String scoreString = "";
        Color mycolor = Color.red;
        g.setColor(mycolor);
        Font f60 = new Font("微软雅黑", 0, 60);
        new Font("微软雅黑", 0, 40);
        int b = 100;
        int oneHbcqW = 0;
        int oneHbcqH = 0;
        String pid = "";
        String studentReportShowItem = "";
        DecimalFormat df2 = new DecimalFormat("#.##");
        String basePath = "";
        if (questionList.size() > 0) {
            basePath = imageService.getImgPath(questionList.get(0).getExamPaperNum().toString(), null);
        }
        for (int i = 0; i < questionList.size(); i++) {
            Questionimage question = questionList.get(i);
            String jisuanType = question.getJisuanType();
            if (i == 0) {
                String studentReportShowItem2 = null == question.getStudentReportShowItem() ? "0" : question.getStudentReportShowItem();
                studentReportShowItem = "T".equals(Convert.toStr(logType, "")) ? "0" : studentReportShowItem2;
            }
            if (question.getExt3() != null) {
                if (question.getPage() == 1) {
                    int a = "0".equals(question.getExt2()) ? 50 : 100;
                    g.setFont(f60);
                    if ("0".equals(jisuanType)) {
                        if ("1".equals(studentReportShowItem)) {
                            Font f2 = new Font("微软雅黑", 0, 40);
                            g.setFont(f2);
                            b += 60;
                            g.drawString("客观题正确率：" + ExcelHelper.formatPercent(question.getOqts(), question.getFullScore(), df2, "--"), a, b);
                        } else {
                            Font f22 = new Font("微软雅黑", 0, 40);
                            g.setFont(f22);
                            b += 60;
                            g.drawString("客观题得分：" + ConvertHelper.replace0(question.getOqts()), a, b);
                        }
                    } else if ("2".equals(studentReportShowItem)) {
                        g.drawString(question.getSubjectName() + "：" + Convert.toStr(question.getDengji(), "--"), a, b);
                    } else if ("1".equals(studentReportShowItem)) {
                        g.drawString(question.getSubjectName() + "：" + ExcelHelper.formatPercent(question.getExt3(), question.getFullScore(), df2, "--"), a, b);
                        Font f23 = new Font("微软雅黑", 0, 40);
                        g.setFont(f23);
                        int b2 = b + 60;
                        g.drawString("客观题正确率：" + ExcelHelper.formatPercent(question.getOqts(), question.getFullScore(), df2, "--"), a, b2);
                        b = b2 + 50;
                        g.drawString("主观题正确率：" + ExcelHelper.formatPercent(question.getSqts(), question.getFullScore(), df2, "--"), a, b);
                    } else {
                        g.drawString(question.getSubjectName() + "：" + ConvertHelper.replace0(question.getExt3()), a, b);
                        Font f24 = new Font("微软雅黑", 0, 40);
                        g.setFont(f24);
                        int b3 = b + 60;
                        g.drawString("客观题得分：" + ConvertHelper.replace0(question.getOqts()), a, b3);
                        b = b3 + 50;
                        g.drawString("主观题得分：" + ConvertHelper.replace0(question.getSqts()), a, b);
                    }
                    b += 80;
                }
            } else if (null != question.getQuestionType()) {
                BigDecimal questionScore = question.getQuestionScore();
                BigDecimal fullScore = question.getFullScore();
                if ("1".equals(question.getQuestionType())) {
                    if (!"0".equals(jisuanType)) {
                        int questionX = Convert.toInt(question.getQuestionX(), 0).intValue();
                        int questionY = Convert.toInt(question.getQuestionY(), 0).intValue();
                        int questionW = Convert.toInt(question.getQuestionW(), 0).intValue();
                        int questionH = Convert.toInt(question.getQuestionH(), 0).intValue();
                        List<Rectangle> qiList = new ArrayList<>();
                        if (StrUtil.isNotEmpty(question.getTag())) {
                            qiList = JSONObject.parseArray(question.getTag(), Rectangle.class);
                        }
                        if ("F".equals(question.getCross_page())) {
                            if (CollUtil.isNotEmpty(qiList)) {
                                Rectangle firstObj = qiList.get(0);
                                if (questionW * 2 < firstObj.getWidth()) {
                                    if ("dtk".equals(tupianType)) {
                                        questionX = firstObj.getX();
                                        questionY = firstObj.getY();
                                    }
                                    questionW = firstObj.getWidth();
                                    questionH = firstObj.getHeight();
                                }
                            }
                            int flagW = questionW < 300 ? (questionW * 55) / 300 : 55;
                            if (questionH < 200) {
                                flagW = (questionH * flagW) / 200;
                            }
                            int flagW2 = flagW < 8 ? 8 : flagW;
                            Font f = new Font("Arial", 1, flagW2);
                            g.setFont(f);
                            if (fullScore.subtract(questionScore).compareTo(new BigDecimal("0.00001")) == -1) {
                                image = imgToByte;
                            } else if (questionScore.compareTo(new BigDecimal("0.00001")) == -1) {
                                image = imgToByte2;
                            } else {
                                image = imgToByte3;
                            }
                            int imgH = (imgToByte.getHeight() * flagW2) / imgToByte.getWidth();
                            if ("hbcq".equals(question.getType())) {
                                int fontW = (flagW2 * 4) / 5;
                                Font f3 = new Font("Arial", 1, fontW);
                                g.setFont(f3);
                                String quesStr = "T" + question.getQuestionName() + ": ";
                                String scoreStr = "";
                                if ("0".equals(studentReportShowItem)) {
                                    scoreStr = " " + ConvertHelper.replace0(String.valueOf(question.getQuestionScore())) + "/" + ConvertHelper.replace0(String.valueOf(question.getFullScore())) + "  ";
                                } else if ("1".equals(studentReportShowItem) || "1".equals(Configuration.getInstance().getShowRateOfStudentPaper())) {
                                    scoreStr = " " + ExcelHelper.formatPercent(question.getQuestionScore(), question.getFullScore(), df2, "--") + "  ";
                                }
                                int quesStrW = g.getFontMetrics(f3).stringWidth(quesStr);
                                int scoreStrW = g.getFontMetrics(f3).stringWidth(scoreStr);
                                if (!pid.equals(question.getQuestionNum())) {
                                    pid = question.getQuestionNum();
                                    oneHbcqW = 20;
                                    oneHbcqH = 20 + imgH;
                                } else if (oneHbcqH + imgH <= questionH && oneHbcqW + quesStrW + flagW2 + scoreStrW > questionW) {
                                    oneHbcqW = 20;
                                    oneHbcqH += imgH;
                                }
                                g.drawString(quesStr, questionX + oneHbcqW, questionY + oneHbcqH);
                                g.drawImage(image, questionX + quesStrW + oneHbcqW, (questionY + oneHbcqH) - imgH, flagW2, imgH, (ImageObserver) null);
                                g.drawString(scoreStr, questionX + quesStrW + flagW2 + oneHbcqW, questionY + oneHbcqH);
                                if (oneHbcqH + imgH <= questionH) {
                                    oneHbcqW += quesStrW + flagW2 + scoreStrW;
                                }
                            } else if ("0".equals(question.getType())) {
                                if ("0".equals(studentReportShowItem)) {
                                    scoreString = ConvertHelper.replace0(String.valueOf(question.getQuestionScore())) + "/" + ConvertHelper.replace0(String.valueOf(question.getFullScore()));
                                } else if ("1".equals(studentReportShowItem) || "1".equals(Configuration.getInstance().getShowRateOfStudentPaper())) {
                                    scoreString = ExcelHelper.formatPercent(question.getQuestionScore(), question.getFullScore(), df2, "--");
                                }
                                int strW = g.getFontMetrics(f).stringWidth(scoreString);
                                int cha = (questionW - strW) - 20;
                                g.drawString(scoreString, questionX + cha, questionY + imgH + 20);
                                g.drawImage(image, questionX + 20, questionY + 20, flagW2, (imgToByte.getHeight() * flagW2) / imgToByte.getWidth(), (ImageObserver) null);
                            }
                        }
                        int questionX2 = Convert.toInt(question.getQuestionX(), 0).intValue();
                        int questionY2 = Convert.toInt(question.getQuestionY(), 0).intValue();
                        int questionW2 = Convert.toInt(question.getQuestionW(), 0).intValue();
                        int questionH2 = Convert.toInt(question.getQuestionH(), 0).intValue();
                        int questionHCurrent = 0;
                        int questionHSum = questionH2;
                        if ("qt".equals(tupianType) || "T".equals(question.getCross_page())) {
                            Map crossPageQuestionMap = examManageService.getFistQuestionCommentImg(question.getId(), question.getStudentId(), question.getScoreId());
                            question.setImg(Convert.toStr(crossPageQuestionMap.get("img"), (String) null));
                            questionHCurrent = ((Integer) crossPageQuestionMap.get("questionHCurrent")).intValue();
                            questionHSum = ((Integer) crossPageQuestionMap.get("questionHSum")).intValue();
                        }
                        if (!"1".equals(question.getNum()) && question.getImg() != null) {
                            String imgPath = basePath + "/" + question.getImg();
                            Questionimage questionImg = new Questionimage();
                            questionImg.setImg(imgPath);
                            Questionimage fill = questionImg.fill();
                            ByteArrayInputStream biaoZhuin = new ByteArrayInputStream(fill.getImgByte());
                            BufferedImage biaoZhuinImage = ImageIO.read(biaoZhuin);
                            biaoZhuin.close();
                            int questionH3 = "qt".equals(tupianType) ? questionHSum : questionH2;
                            g.drawImage(biaoZhuinImage, questionX2, questionY2, questionX2 + questionW2, questionY2 + questionH3, 0, questionHCurrent, questionW2, questionHCurrent + questionH3, (ImageObserver) null);
                            if ("dtk".equals(tupianType) && null != question.getTag()) {
                                for (Rectangle qi : qiList) {
                                    questionHCurrent += questionH3;
                                    int questionX3 = qi.getX();
                                    int questionY3 = qi.getY();
                                    int questionW3 = qi.getWidth();
                                    questionH3 = qi.getHeight();
                                    g.drawImage(biaoZhuinImage, questionX3, questionY3, questionX3 + questionW3, questionY3 + questionH3, 0, questionHCurrent, questionW3, questionHCurrent + questionH3, (ImageObserver) null);
                                }
                            }
                        }
                    }
                } else {
                    String answer = question.getAnswer();
                    if (fullScore.subtract(questionScore).compareTo(new BigDecimal("0.00001")) == -1) {
                        g.drawImage(lAllRight, ((int) Float.parseFloat(question.getExt1())) + 4, (int) Float.parseFloat(question.getExt2()), (ImageObserver) null);
                    } else if (questionScore.compareTo(new BigDecimal("0.00001")) == -1) {
                        if ("-1".equals(answer)) {
                            g.drawImage(lAllWrong, ((int) Float.parseFloat(question.getExt1())) + 4, (int) Float.parseFloat(question.getExt2()), (ImageObserver) null);
                        } else {
                            Font f4 = new Font("Arial", 1, 30);
                            g.setFont(f4);
                            g.drawString(question.getAnswer(), ((int) Float.parseFloat(question.getExt1())) + 4, ((int) Float.parseFloat(question.getExt2())) + 20);
                        }
                    } else if ("-1".equals(answer)) {
                        g.drawImage(lHalfOf, ((int) Float.parseFloat(question.getExt1())) + 4, (int) Float.parseFloat(question.getExt2()), (ImageObserver) null);
                    } else {
                        g.drawImage(lHalfOf, ((int) Float.parseFloat(question.getExt1())) + 4, (int) Float.parseFloat(question.getExt2()), (ImageObserver) null);
                        Font f5 = new Font("Arial", 1, 30);
                        g.setFont(f5);
                        g.drawString(question.getAnswer(), ((int) Float.parseFloat(question.getExt1())) + 4 + lHalfOf.getWidth(), ((int) Float.parseFloat(question.getExt2())) + 20);
                    }
                }
            }
        }
        g.dispose();
        byte[] bytes = ImgUtil.toBytes(grbImage, Const.IMAGE_FORMAT);
        return bytes;
    }

    public static BufferedImage imgToByte(String path) throws IOException {
        InputStream input = null;
        try {
            try {
                input = new FileInputStream(path);
                BufferedImage read = ImageIO.read(input);
                if (input != null) {
                    input.close();
                }
                return read;
            } catch (Exception e) {
                e.printStackTrace();
                if (input != null) {
                    input.close();
                    return null;
                }
                return null;
            }
        } catch (Throwable th) {
            if (input != null) {
                input.close();
            }
            throw th;
        }
    }

    public static String imgToBase64(String path) throws IOException {
        byte[] data = new byte[0];
        try {
            InputStream input = new FileInputStream(path);
            int i = input.available();
            data = new byte[i];
            input.read(data);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        String imgBase64 = encoder.encode(data);
        return imgBase64;
    }

    public static void main(String[] args) throws IOException {
    }

    public static byte[] tiffToPng(String filename) {
        byte[] out = new byte[0];
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            try {
                inputStream = cn.hutool.core.io.FileUtil.getInputStream(filename);
                ImageDecoder dec = ImageCodec.createImageDecoder("tiff", inputStream, (ImageDecodeParam) null);
                RenderedImage op = dec.decodeAsRenderedImage(0);
                ImageEncoder en = ImageCodec.createImageEncoder("png", new ByteArrayOutputStream(), (ImageEncodeParam) null);
                en.encode(op);
                outputStream = (ByteArrayOutputStream) en.getOutputStream();
                out = outputStream.toByteArray();
                outputStream.flush();
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                return out;
            } catch (Exception e3) {
                Log.error("TIFF图片转换为 PNG文件错误 ", e3);
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
                return out;
            }
        } catch (Throwable th) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
            throw th;
        }
    }
}
