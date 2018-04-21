package edu.gcsc.vrl.vtksimple;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//import eu.mihosoft.vrl.io.Base64;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DataArray {

    private byte[] data;
    private HashMap<String, String> attributes = new HashMap<String, String>();
    private int numberOfComponents = 1;
    private String type = "?";
    private String name = "?";
    private DataDecoder dataDecoder = new DataDecoder();

    public DataArray() {
    }

    public DataArray(Node n, DecoderFactory decoderFactory, ByteOrder byteOrder) {


        for (int j = 0; j < n.getAttributes().getLength(); j++) {
            Node a = n.getAttributes().item(j);
//            log("Attribute " + j + " : " + a);

            attributes.put(a.getNodeName(), a.getNodeValue());
        }
        
        
        if (attributes.get("NumberOfComponents") != null) {
            numberOfComponents =
                    new Integer(attributes.get("NumberOfComponents"));
        }

        type = attributes.get("type").trim();

        if (attributes.get("Name") != null) {
            name = attributes.get("Name").trim();
        }

        log("DataArray: " + name);
        log(">> type: " + type);

        if (attributes.get("format").trim().equals("binary")) {
            log(">> format: binary");

            // each Base64 entry consists of two entries
            // we only use the second one to get rid of the data length
            // int32 header
            int dataStartPos = 0;//n.getTextContent().indexOf("==")+2;
            String dataString = n.getTextContent().substring(dataStartPos).trim();

            //data = Base64.decode(dataString);

            byte[] origData = Base64.getDecoder().decode(dataString);

            // remove header
            data = new byte[origData.length-4];

            System.arraycopy(origData,4,data,0,data.length);

            log(">> BASE64 String length: " + dataString.length());
            log(">> array size: " + data.length);

        } else {
            // don't know what to do...
        }


        dataDecoder = decoderFactory.getDecoder(type);

        try {
            dataDecoder.decode(data, byteOrder);
        } catch (IOException ex) {
            Logger.getLogger(DataArray.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (UnsupportedArrayTypeException ex) {
            Logger.getLogger(DataArray.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return the attributes
     */
    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the numberOfComponents
     */
    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the dataDecoder
     */
    public DataDecoder getDataDecoder() {
        return dataDecoder;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    private static void log(String msg) {
        if(isDebug()) {
            System.out.println("DataArray: " + msg);
        }
    }

    public static void setDebug(boolean debug) {
        DataArray.debug = debug;
    }

    private static boolean isDebug() {
        return debug;
    }

    private static boolean debug;
}
