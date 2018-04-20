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
public class Float32Decoder extends DataDecoder {

    public Float32Decoder() {
        setType("Float32");
    }

    @Override
    public void decode(byte[] data, ByteOrder byteOrder) throws IOException {
        DataInputStream in =
                new DataInputStream(
                new BufferedInputStream(
                new ByteArrayInputStream(data)));

        int arraySize = data.length / 4;

        setArray(new float[arraySize]);

        for (int i = 0; i < arraySize; i++) {
            int bytes = in.readInt();

            if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
                bytes = Integer.reverseBytes(bytes);
            }
            ((float[]) getArray())[i] = Float.intBitsToFloat(bytes);
        }
    }
}
