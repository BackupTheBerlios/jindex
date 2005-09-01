/*
 * Created on Sep 1, 2005
 */
package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
public class ImageUtil
{
    public static BufferedImage getScaledImage(
        BufferedImage bi,
        int width,
        int height)
    {
        BufferedImage new_bi = new BufferedImage(width, width, bi.getType());
        Graphics g = new_bi.getGraphics();
        g.drawImage(bi, 0, 0, width, height, null);
        return new_bi;
    }
    public static BufferedImage getScaledImage(BufferedImage bi, float scale)
    {
        int width = (int) (bi.getWidth() * scale);
        int height = (int) (bi.getHeight() * scale);
        BufferedImage new_bi = new BufferedImage(width, width, bi.getType());
        Graphics g = new_bi.getGraphics();
        g.drawImage(bi, 0, 0, width, height, null);
        return new_bi;
    }
    public static BufferedImage scaleImageToFit(
        BufferedImage bi,
        int i,
        int j,
        ImageObserver imgObserver)
    {
        Dimension dim =
            new Dimension(bi.getWidth(imgObserver), bi.getHeight(imgObserver));
        Dimension dim1 = new Dimension(i, j);
        double d = scaleToFit(dim, dim1);
        double d1 = (double) bi.getWidth(imgObserver) * d;
        double d2 = (double) bi.getHeight(imgObserver) * d;
        BufferedImage new_bi =
            new BufferedImage((int) d1, (int) d2, bi.getType());
        Graphics2D g2d = new_bi.createGraphics();
        g2d.drawImage(bi, 0, 0, (int) d1, (int) d2, imgObserver);
        g2d.dispose();
        return new_bi;
    }
    public static double scaleToFit(Dimension dimension, Dimension dimension1)
    {
        double w1 = dimension.getWidth();
        double h1 = dimension.getHeight();
        double w2 = dimension1.getWidth();
        double h2 = dimension1.getHeight();
        double scale = 1.0D;
        if (w1 > h1)
        {
            if (w1 > w2)
                scale = w2 / w1;
            h1 *= scale;
            if (h1 > h2)
                scale *= h2 / h1;
        }
        else
        {
            if (h1 > h2)
                scale = h2 / h1;
            w1 *= scale;
            if (w1 > w2)
                scale *= w2 / w1;
        }
        return scale;
    }
    public static boolean isFileImage(File file)
    {
        String s = getImageFormatName(file);
        String[] formats = ImageIO.getReaderFormatNames();
        for (int i = 0; i < formats.length; i++)
        {
            if (s.equals(formats[i]))
                return true;
        }
        return false;
    }
    public static String getImageFormatName(File file)
    {
        String s = null;
        if (file.isFile())
        {
            String s1 = file.getName();
            int i = s1.lastIndexOf(".");
            if (i > 0)
                s = s1.substring(i + 1).toLowerCase();
        }
        return s;
    }
    public static BufferedImage iconToBufferedImage(Icon icon)
    {
        int i = icon.getIconWidth();
        int j = icon.getIconHeight();
        BufferedImage bufferedimage = new BufferedImage(i, j, 2);
        Graphics2D graphics2d = bufferedimage.createGraphics();
        icon.paintIcon(new JLabel(), graphics2d, 0, 0);
        graphics2d.dispose();
        return bufferedimage;
    }
    public static BufferedImage rotateImageToRight(BufferedImage bi)
    {
        BufferedImage new_bi =
            new BufferedImage(bi.getHeight(), bi.getWidth(), bi.getType());
        Graphics2D g2d = new_bi.createGraphics();
        AffineTransform at = g2d.getTransform();
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(Math.PI / 2, bi.getWidth() / 2, bi.getHeight() / 2);
        g2d.transform(rotation);
        g2d.drawImage(
            bi,
            null,
            bi.getWidth() / 2 - new_bi.getWidth() / 2,
            (bi.getHeight() / 2 - new_bi.getHeight() / 2) * -1);
        g2d.setTransform(at);
        return new_bi;
    }
    public static BufferedImage rotateImageToLeft(BufferedImage bi)
    {
        BufferedImage new_bi =
            new BufferedImage(bi.getHeight(), bi.getWidth(), bi.getType());
        Graphics2D g2d = new_bi.createGraphics();
        AffineTransform at = g2d.getTransform();
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(
            (-1) * Math.PI / 2,
            bi.getWidth() / 2,
            bi.getHeight() / 2);
        g2d.transform(rotation);
        g2d.drawImage(
            bi,
            null,
            (bi.getWidth() / 2 - new_bi.getWidth() / 2) * -1,
            (bi.getHeight() / 2 - new_bi.getHeight() / 2));
        g2d.setTransform(at);
        return new_bi;
    }
    public static BufferedImage rotateImage(BufferedImage bi, double angle)
    {
        if (angle == 0d)
        {
            return bi;
        }
        int w = 0, h = 0, x = 0, y = 0;
        if (angle % Math.PI == 0d)
        {
            w = bi.getWidth();
            h = bi.getHeight();
        }
        else
        {
            w = bi.getHeight();
            h = bi.getWidth();
        }
        BufferedImage new_bi = new BufferedImage(w, h, bi.getType());
        if (angle > 0)
        {
            x = bi.getWidth() / 2 - new_bi.getWidth() / 2;
            y = (bi.getHeight() / 2 - new_bi.getHeight() / 2) * -1;
        }
        else
        {
            x = (bi.getWidth() / 2 - new_bi.getWidth() / 2) * -1;
            y = (bi.getHeight() / 2 - new_bi.getHeight() / 2);
        }
        Graphics2D g2d = new_bi.createGraphics();
        AffineTransform at = g2d.getTransform();
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(angle, bi.getWidth() / 2, bi.getHeight() / 2);
        g2d.transform(rotation);
        g2d.drawImage(bi, null, x, y);
        g2d.setTransform(at);
        return new_bi;
    }
    public static BufferedImage flipImageHorizontal(BufferedImage bi)
    {
        int w = bi.getWidth(), h = bi.getHeight();
        BufferedImage bi2 = new BufferedImage(w, h, bi.getType());
        bi2.getGraphics().drawImage(
            bi,
            w - 1,
            0,
            0,
            h - 1,
            0,
            0,
            w - 1,
            h - 1,
            null);
        return bi2;
    }
    public static BufferedImage flipImageVertical(BufferedImage bi)
    {
        int w = bi.getWidth(), h = bi.getHeight();
        BufferedImage bi2 = new BufferedImage(w, h, bi.getType());
        bi2.getGraphics().drawImage(
            bi,
            0,
            h - 1,
            w - 1,
            0,
            0,
            0,
            w - 1,
            h - 1,
            null);
        return bi2;
    }
}