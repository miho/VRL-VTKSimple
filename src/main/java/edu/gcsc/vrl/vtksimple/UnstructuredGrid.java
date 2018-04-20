package edu.gcsc.vrl.vtksimple;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class UnstructuredGrid implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<DataArray> arrays = new ArrayList<DataArray>();
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    public UnstructuredGrid() {
    }

    public UnstructuredGrid(File file) {
        DecoderFactory decoderFactory = new DecoderFactory();

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("Root element "
                    + doc.getDocumentElement().getNodeName());

            String byteOrderString = doc.getDocumentElement().getAttribute("byte_order");

            if (byteOrderString.trim().equals("LittleEndian")) {
                byteOrder = ByteOrder.LITTLE_ENDIAN;
            }


            NodeList nodeLst = doc.getElementsByTagName("UnstructuredGrid");

            for (int s = 0; s < nodeLst.getLength(); s++) {

                Node fstNode = nodeLst.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element fstElmnt = (Element) fstNode;
                    NodeList fstNmElmntLst =
                            fstElmnt.getElementsByTagName("DataArray");

                    for (int i = 0; i < fstNmElmntLst.getLength(); i++) {
                        Node n = fstNmElmntLst.item(i);
//                        System.out.println("Node " + i + " : " + n);

                        DataArray array = new DataArray(n, decoderFactory, byteOrder);

                        arrays.add(array);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * @return the arrays
     */
    public ArrayList<DataArray> getArrays() {
        return arrays;
    }
}
