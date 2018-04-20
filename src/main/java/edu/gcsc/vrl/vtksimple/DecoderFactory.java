package edu.gcsc.vrl.vtksimple;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.HashMap;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DecoderFactory {
    private HashMap<String,DataDecoder> decoders =
            new HashMap<String, DataDecoder>();

    public DecoderFactory() {
        addDecoder(new DataDecoder());
        addDecoder(new Float32Decoder());
        addDecoder(new Int32Decoder());
        addDecoder(new Int8Decoder());
    }

    public void addDecoder(DataDecoder decoder) {
        decoders.put(decoder.getType(), decoder);
    }

    public DataDecoder removeDecoder(String type) {
        return decoders.remove(type);
    }

    public DataDecoder getDecoder(String type) {
        DataDecoder result = decoders.get(type);
        if (result == null) {
            result = new DataDecoder();
        }
        return result.instance();
    }
}
