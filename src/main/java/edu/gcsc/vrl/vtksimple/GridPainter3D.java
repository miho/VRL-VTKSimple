/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.vtksimple;

import eu.mihosoft.vrl.animation.AnimationInterpolation;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.Node;
import eu.mihosoft.vrl.v3d.Triangle;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.vecmath.Color3f;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name = "VTU-Viewer (Deprecated)", category = "VTK/Deprecated")
public class GridPainter3D implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @return
     */
    @MethodInfo(hide = true)
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            UnstructuredGrid grid, String colorArrayName) {
        return paint(colorOne, colorTwo, grid, 25f, colorArrayName);
    }

    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @return
     */
    @MethodInfo()
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            @ParamInfo(style = "load-dialog") File f,
            @ParamInfo(name = "color array:") String colorArrayName,
            @ParamInfo(name = "show hight:") Boolean useColorAsZ) {
        return paint(colorOne, colorTwo, new UnstructuredGrid(f),
                25f, colorArrayName, null, null, useColorAsZ, null);
    }

    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @return
     */
    @MethodInfo()
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            @ParamInfo(style = "load-dialog") File f,
            @ParamInfo(name = "color array:") String colorArrayName,
            @ParamInfo(name = "min value (optional):", nullIsValid = true) Float min,
            @ParamInfo(name = "max value (optional):", nullIsValid = true) Float max,
            @ParamInfo(name = "show height:") Boolean useColorAsZ,
            @ParamInfo(name = "scale height (optional):", nullIsValid = true) Float scaleZ) {
        return paint(colorOne, colorTwo, new UnstructuredGrid(f),
                25f, colorArrayName, min, max, useColorAsZ, scaleZ);
    }
    
    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @return
     */
    @MethodInfo()
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            @ParamInfo(style = "load-dialog") File f,
            @ParamInfo(name = "color array:") String colorArrayName,
            @ParamInfo(name = "min value (optional):", nullIsValid = true) Float min,
            @ParamInfo(name = "max value (optional):", nullIsValid = true) Float max,
            @ParamInfo(name = "show volume:") Boolean showVolume,
            @ParamInfo(name = "show height:") Boolean useColorAsZ,
            @ParamInfo(name = "scale height (optional):", nullIsValid = true) Float scaleZ) {
        return paint(colorOne, colorTwo, new UnstructuredGrid(f),
                25f, colorArrayName, min, max, useColorAsZ, scaleZ, showVolume);
    }

    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @return
     */
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            UnstructuredGrid grid) {
        return paint(colorOne, colorTwo, grid, 25f, "ndata000");
    }

    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @param maxLength
     * @return
     */
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            UnstructuredGrid grid, Float maxLength, String colorArrayName) {
        return paint(colorOne, colorTwo, grid,
                maxLength, colorArrayName, null, null, false, null);

    }
    
    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @param maxLength
     * @return
     */
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            UnstructuredGrid grid, Float maxLength, String colorArrayName,
            Float rangeMin, Float rangeMax, Boolean useColorAsZ, Float scaleZ) {
        return paint(colorOne, colorTwo, grid, maxLength, colorArrayName,
                rangeMin, rangeMax, useColorAsZ, scaleZ, true);
    }

    /**
     *
     * @param colorOne
     * @param colorTwo
     * @param grid
     * @param maxLength
     * @return
     */
    public VGeometry3D paint(
            Color colorOne, Color colorTwo,
            UnstructuredGrid grid, Float maxLength, String colorArrayName,
            Float rangeMin, Float rangeMax, Boolean useColorAsZ, Float scaleZ,
            boolean showVolume) {

        // evaluate data arrays and convert array data
        float[] pointData = null;
        float[] colors = null;
        int[] offsets = null;
        int[] connectivity = null;
        byte[] types = null;

        for (DataArray a : grid.getArrays()) {
//            System.out.println("Array: " + a.getName() + ", "
//                    + a.getType() + ", #Comp: " + a.getNumberOfComponents());
            if (a.getNumberOfComponents() == 3) {
                pointData = (float[]) a.getDataDecoder().getArray();
                log("Points: " + pointData.length);
            }

            if (a.getName().equals("connectivity")) {
                connectivity = (int[]) a.getDataDecoder().getArray();
                log("connectivity: " + connectivity.length);
            }

            if (a.getName().equals("offsets")) {
                offsets = (int[]) a.getDataDecoder().getArray();
                log("offsets: " + offsets.length);
            }

            if (a.getName().equals("types")) {
                types = (byte[]) a.getDataDecoder().getArray();
                log("types: " + types.length);
            }

            if (a.getName().equals(colorArrayName)) {
                colors = (float[]) a.getDataDecoder().getArray();
                log("colors: " + colors.length);
            }
        }


        log("Used Cell Types: ");
        
        Set<Byte> shownTypes = new HashSet<Byte>();
        
        for (byte b : types) {
            if (!shownTypes.contains(b)) {
                log(b + " ");
                shownTypes.add(b);
            }
        }

        log("");

        // convert point data from one dimensional array to three dimensional
        // point array

        int numberOfComponents = 3;
        int numberOfPoints = pointData.length / numberOfComponents;

        float[][] points =
                new float[numberOfPoints][numberOfComponents];

        for (int i = 0; i < numberOfPoints*3; i++) {

            int pIndex = i / numberOfComponents;
            int compIndex = i % numberOfComponents;

            points[pIndex][compIndex] = pointData[i];
        }

        // find min max coordinate values etc.

        float xMin = Float.MAX_VALUE;
        float yMin = Float.MAX_VALUE;
        float zMin = Float.MAX_VALUE;

        float xMax = Float.MIN_VALUE;
        float yMax = Float.MIN_VALUE;
        float zMax = Float.MIN_VALUE;

        for (float[] p : points) {

            for (int i = 0; i < 3; i++) {
                if (p[i] == Float.floatToIntBits(Float.NaN)) {
                    p[i] = 0;
                }
            }

            xMin = Math.min(xMin, p[0]);
            yMin = Math.min(yMin, p[1]);
            zMin = Math.min(zMin, p[2]);

            xMax = Math.max(xMax, p[0]);
            yMax = Math.max(yMax, p[1]);
            zMax = Math.max(zMax, p[2]);

//            System.out.println("P[0]: " + p[0] + " P[1]: " + p[1] + " P[2]: " + p[2]);
        }

        float xLength = Math.abs(xMax - xMin);
        float yLength = Math.abs(yMax - yMin);
        float zLength = Math.abs(zMax - zMin);

        float offsetX = (float) xMin;
        float offsetY = (float) yMin;
        float offsetZ = (float) zMin;

//        System.out.println("X_MIN: " + xMin);
//        System.out.println("X_MAX: " + xMax);
//
//        System.out.println("Y_MIN: " + yMin);
//        System.out.println("Y_MAX: " + yMax);
//
//        System.out.println("Z_MIN: " + zMin);
//        System.out.println("Z_MAX: " + zMax);
//
//        System.out.println("X_OFFSET: " + offsetX);
//        System.out.println("Y_OFFSET: " + offsetY);


        // compute color scale

        float cMin = Float.MAX_VALUE;
        float cMax = Float.MIN_VALUE;

        if (rangeMin != null && rangeMax != null) {

            // custom min and max values
            cMin = rangeMin;
            cMax = rangeMax;

            // trim values to range
            for (int cInd = 0; cInd < colors.length; cInd++) {
                colors[cInd] = Math.max(cMin, colors[cInd]);
                colors[cInd] = Math.min(cMax, colors[cInd]);
            }

        } else {

            // search min and max values
            for (float c : colors) {
                cMin = Math.min(cMin, c);
                cMax = Math.max(cMax, c);
            }
        }

        double cLength = Math.abs(cMax - cMin);
        double scaleColor = 1.d;

        if (cLength > 0) {
            scaleColor = 1.d / cLength;
        }

//        System.out.println("C_MIN: " + cMin);
//        System.out.println("C_MAX: " + cMax);
//        System.out.println("C_SCALE: " + scaleColor);


        // define linear color interpolators

        LinearInterpolation red =
                new LinearInterpolation(colorOne.getRed(), colorTwo.getRed());
        LinearInterpolation green =
                new LinearInterpolation(colorOne.getGreen(), colorTwo.getGreen());
        LinearInterpolation blue =
                new LinearInterpolation(colorOne.getBlue(), colorTwo.getBlue());

        // geometry

        VTriangleArray triangleArray = new VTriangleArray();

        // the previous offset from the offsets data array
        // which is used to compute the current element size
        // (number of points per element)
        int previousOffset = 0;

        // offset for the connectivity array
        int connectivityOffset = 0;

        // compute geometry scale
        float scale = maxLength / Math.max(Math.max(xLength, yLength), zLength);
        
        // determines whether volume elements are defined in this grid
        boolean hasVolumeElements = false;
        
        for (int i = 0; i < offsets.length; i++) {

            int type = types[i];

            // element size defines the number of points of an element
            int elementSize = offsets[i] - previousOffset;

            previousOffset = offsets[i];

            // stores the points of the current element
            Node[] nodes = new Node[elementSize];

            for (int j = 0; j < elementSize; j++) {

//                System.out.println("ElementSize: " + elementSize);

                // the connectivity array contains the point indices of this
                // element
                int pointIndex = connectivity[connectivityOffset + j];

//                cMax = 30;

                // compute color (linear color scale)
                float color = colors[pointIndex] - cMin;
                float colorScale = 1.f / Math.abs(cMax - cMin);

                red.step(color * colorScale);
                blue.step(color * colorScale);
                green.step(color * colorScale);

                // translate values (center is (0,0,0))
                float x = points[pointIndex][0] - offsetX - xLength / 2.f;
                float y = points[pointIndex][1] - offsetY - yLength / 2.f;
                float z = points[pointIndex][2] - offsetZ - zLength / 2.f;

                // scale values
                x *= scale;
                y *= scale;
                z *= scale;

                // we overwrite z coordinate with color value
                if (useColorAsZ) {
                    z = colors[pointIndex] - offsetZ - zLength / 2.f;

                    if (scaleZ != null) {
                        z *= colorScale * scaleZ;
                    } else {
                        z *= colorScale * maxLength;
                    }
                }

                int r = (int) red.getValue();
                int g = (int) green.getValue();
                int b = (int) blue.getValue();

                try {
                    // create node
                    nodes[j] =
                            new Node(x, y, z,
                            new Color3f(new Color(r, g, b)));
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
//                    System.out.println(
//                            ">> color values: c="
//                            + color * colorScale
//                            + ", r=" + r + ", g=" + g + ", b=" + b);
                }
            }

            connectivityOffset += elementSize;

            // we only support triangles (type 5), quads (type 9)
            //and tetrahedrons (type 10)
            // (quads are represented by two triangles)
            // (tetrahedrons are represented by four triangles)
            // otherwise we do nothing (the current element will then be ignored)
            if (type == 5 || type == 9 || type == 10) {

                // triangle
                if (type == 5 && elementSize == 3) {
                    triangleArray.addTriangle(
                            new Triangle(nodes[0], nodes[1], nodes[2]));
                }

                // quad
                if (type == 9 && elementSize == 4) {
                    triangleArray.addTriangle(
                            new Triangle(nodes[0], nodes[1], nodes[2]));
                    triangleArray.addTriangle(
                            new Triangle(nodes[0], nodes[2], nodes[3]));
                }

                // tetra
                if (type == 10 && elementSize == 4 && showVolume) {
                    triangleArray.addTriangle(
                            new Triangle(nodes[0], nodes[1], nodes[2]));
                    triangleArray.addTriangle(
                            new Triangle(nodes[0], nodes[1], nodes[3]));
                    triangleArray.addTriangle(
                            new Triangle(nodes[0], nodes[2], nodes[3]));
                    triangleArray.addTriangle(
                            new Triangle(nodes[1], nodes[2], nodes[3]));
                }
                
                // tetra currently is the only supported volume element
                hasVolumeElements = hasVolumeElements || type == 10;
            }

        }

        // create the final geometry (with vertex coloring)
        VGeometry3D result = new VGeometry3D(
                triangleArray, Color.black, Color.white, 1.f, true, true, 
                showVolume && hasVolumeElements);

        return result;
    }

    private void log(String msg) {
        if(isDebug()) {
            System.out.println("VTU-Plotter: "+msg);
        }
    }

    private static boolean debug;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean state) {
        GridPainter3D.debug = state;
    }
}
