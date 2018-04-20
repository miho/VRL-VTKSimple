package edu.gcsc.vrl.vtksimple;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package javaapplication9;
//
//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
//import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.w3c.dom.Node;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class FloatArray extends DataArray {
//
//    private Object dataArray = null;
//
//    public FloatArray(Node n) {
//        super(n);
//        try {
//            init();
//        } catch (IOException ex) {
//            Logger.getLogger(FloatArray.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void init() throws IOException {
//        if (getType().equals("Float32")) {
//            DataInputStream in =
//                    new DataInputStream(
//                    new BufferedInputStream(
//                    new ByteArrayInputStream(getData())));
//
//            for (int i = 0; i < 100; i ++) {
//
//            System.out.println("Number: " + in.readFloat());
//            }
//
//        } else {
//        }
//    }
//}
