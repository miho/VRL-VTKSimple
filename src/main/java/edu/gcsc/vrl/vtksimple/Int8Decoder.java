package edu.gcsc.vrl.vtksimple;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Int8Decoder extends DataDecoder {

    public Int8Decoder() {
        setType("Int8");
    }

    @Override
    public void decode(byte[] data, ByteOrder byteOrder) throws IOException {
        
            DataInputStream in =
                    new DataInputStream(
                    new BufferedInputStream(
                    new ByteArrayInputStream(data)));

            int arraySize = data.length;

            setArray(new byte[arraySize]);

            for (int i = 0; i < arraySize; i++) {

//                byte value = in.readByte();
//
//                if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
//                    value = Byte.reverseBytes(value);
//                }

                ((byte[]) getArray())[i] = in.readByte();
            }
    }
}
