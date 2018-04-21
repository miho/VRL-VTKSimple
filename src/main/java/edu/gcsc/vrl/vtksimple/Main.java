package edu.gcsc.vrl.vtksimple;

import java.awt.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Base64;

public class Main {

    public static void main(String[] args) {
        GridPainter3D p = new GridPainter3D();
        p.paint(Color.RED, Color.GREEN, new File(
                "/Users/miho/Dropbox/G-CSC/Sepp&Miho/summerschool-2018/tmp/vtk-out/out.vtu"),
                "c", false);
    }
}
