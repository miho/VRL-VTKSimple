package edu.gcsc.vrl.vtksimple;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DataDecoder {
    private Object array;
    private String type = UNSUPPORTED;
    private static final String UNSUPPORTED = "Unsupported";

    public DataDecoder() {
    }

    public DataDecoder instance() {
        DataDecoder result = null;
        try {
            result =  getClass().newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(DataDecoder.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DataDecoder.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public void decode(byte[] data, ByteOrder byteOrder) throws IOException, UnsupportedArrayTypeException{
        throw new UnsupportedOperationException(">> Binary format unsupported!");
    }

    /**
     * @return the array
     */
    public Object getArray() {
        return array;
    }

    /**
     * @param array the array to set
     */
    protected void setArray(Object array) {
        this.array = array;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    protected void setType(String type) {
        this.type = type;
    }

}
