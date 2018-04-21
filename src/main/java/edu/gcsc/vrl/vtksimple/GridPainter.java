package edu.gcsc.vrl.vtksimple;

import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.visual.ImageUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name = "VTU-Viewer (2D, Deprecated)", category = "VTK")
public class GridPainter implements Serializable{

    private static final long serialVersionUID = 1L;

    private Color colorOne = Color.blue;
    private Color colorTwo = Color.red;

    private Point2D offset = new Point2D.Float();

    private boolean flipY;
    private boolean flipX;

    public void paint(Graphics2D g2, UnstructuredGrid grid, String dataArrayName) {

        int width = g2.getDeviceConfiguration().getBounds().width;
        int height = g2.getDeviceConfiguration().getBounds().height;

        DataArray pointArray = null;
        DataArray colorArray = null;

        for (DataArray a : grid.getArrays()) {
            System.out.println("Array: " + a.getName() + ", "
                    + a.getType() + ", #Comp: " + a.getNumberOfComponents());
            if (a.getNumberOfComponents()==3) {
                pointArray = a;
            }

            if (a.getName().equals(dataArrayName)) {
                colorArray = a;
            }

            if (a.getName().equals("offsets")) {

                int[] offsets = (int[]) a.getDataDecoder().getArray();

                int counter = 0;
                for(int i : offsets) {
                    counter++;
                    System.out.println("O: " + i);
                    if (counter>10) break;
                }
            }

            if (a.getName().equals("connectivity")) {

                int[] offsets = (int[]) a.getDataDecoder().getArray();

                int counter = 0;
                for(int i : offsets) {
                    counter++;
                    System.out.println("C: " + i);
                    if (counter>10) break;
                }
            }
        }


        float[] data = (float[]) pointArray.getDataDecoder().getArray();

        int numberOfComponents = pointArray.getNumberOfComponents();
        int numberOfPoints = data.length/numberOfComponents;

        float[][] points =
                new float[numberOfPoints][numberOfComponents];

        for (int i = 0; i < data.length; i++) {
            points[i/numberOfComponents][i%numberOfComponents] = data[i];
        }

        float[] colors = (float[]) colorArray.getDataDecoder().getArray();


        float xMin = Float.MAX_VALUE;
        float yMin = Float.MAX_VALUE;

        float xMax = Float.MIN_VALUE;
        float yMax = Float.MIN_VALUE;

        for (float[] p : points){
            xMin = Math.min(xMin, p[0]);
            yMin = Math.min(yMin, p[1]);

            xMax = Math.max(xMax, p[0]);
            yMax = Math.max(yMax, p[1]);
        }

        float xLength = Math.abs(xMax-xMin);
        float yLength = Math.abs(yMax-yMin);

        float scaleX = width/xLength;
        float scaleY = height/yLength;

        float offsetX = (float) (0 + xMin + getOffset().getX());
        float offsetY = (float) (0 + yMin + getOffset().getY());

        System.out.println("X_MIN: " + xMin);
        System.out.println("X_MAX: " + xMax);

        System.out.println("Y_MIN: " + yMin);
        System.out.println("Y_MAX: " + yMax);

        System.out.println("X_OFFSET: " + offsetX);
        System.out.println("Y_OFFSET: " + offsetY);

        float cMin = Float.MAX_VALUE;
        float cMax = Float.MIN_VALUE;

        for (float c : colors){
            cMin = Math.min(cMin, c);
            cMax = Math.max(cMax, c);
        }

        double cLength = Math.abs(cMax-cMin);

        double scaleColor = 1.d/cLength;

        System.out.println("C_MIN: " + cMin);
        System.out.println("C_MAX: " + cMax);
        System.out.println("C_SCALE: " + scaleColor);

        g2.setColor(colorOne);
        g2.fillRect(0, 0, width, height);

        LinearInterpolation red = new LinearInterpolation(colorOne.getRed(), colorTwo.getRed());
        LinearInterpolation green = new LinearInterpolation(colorOne.getGreen(), colorTwo.getGreen());
        LinearInterpolation blue = new LinearInterpolation(colorOne.getBlue(), colorTwo.getBlue());


        for (int i = 0; i < numberOfPoints;i++) {
            float xOrig = points[i][0];
            float yOrig = points[i][1];
            float zOrig = points[i][2];

            float cOrig = colors[i];

            int x = (int) (xOrig * scaleX+offsetX);
            int y = (int) (yOrig * scaleY+offsetY);

            double v = (double) (cOrig * scaleColor);

            //System.out.println("c: " + v);

            red.step(v);
            green.step(v);
            blue.step(v);

            int r = (int) red.getValue();
            int g = (int) green.getValue();
            int b = (int) blue.getValue();

            //System.out.println("r: " + r + ", g: " + g + ", b: " + b);


            g2.setColor(new Color(r,g,b));


            if (isFlipX()) {
                x = width-x;
            }

            if (isFlipY()) {
                y = height-y;
            }

            g2.fillRect(x, y, 36,36);
        }
    }

    public BufferedImage paint(int w, int h, UnstructuredGrid grid, String dataArrayName) {
        BufferedImage result = ImageUtils.createCompatibleImage(w, h);

        Graphics2D g2 = result.createGraphics();

        paint(g2, grid, dataArrayName);

        g2.dispose();

        return result;
    }

    public void paint (BufferedImage img, UnstructuredGrid grid, String dataArrayName) {
        Graphics2D g2 = img.createGraphics();

        paint(g2, grid,dataArrayName);

        g2.dispose();
    }


    /**
     * @return the colorOne
     */
    public Color getColorOne() {
        return colorOne;
    }

    /**
     * @param colorOne the colorOne to set
     */
    public void setColorOne(Color colorOne) {
        this.colorOne = colorOne;
    }

    /**
     * @return the colorTwo
     */
    public Color getColorTwo() {
        return colorTwo;
    }

    /**
     * @param colorTwo the colorTwo to set
     */
    public void setColorTwo(Color colorTwo) {
        this.colorTwo = colorTwo;
    }

    /**
     * @return the offset
     */
    public Point2D getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(Point2D offset) {
        this.offset = offset;
    }

    /**
     * @return the flipY
     */
    public boolean isFlipY() {
        return flipY;
    }

    /**
     * @param flipY the flipY to set
     */
    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    /**
     * @return the flipX
     */
    public boolean isFlipX() {
        return flipX;
    }

    /**
     * @param flipX the flipX to set
     */
    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }
}
