package com.example.domain;

import com.example.models.ElementPercentageFitStore;
import com.example.models.TypeAlloyMatch;
import com.example.utils.Calculator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class BlobDecoder {
    public static List<TypeAlloyMatch> decodeMatches(byte[] blob) {
        ByteBuffer buf = ByteBuffer.wrap(blob).order(ByteOrder.LITTLE_ENDIAN);
        List<TypeAlloyMatch> out = new ArrayList<>();
        while (buf.remaining() >= Integer.BYTES*3 + Float.BYTES + Integer.BYTES) {
            int id          = buf.getInt();
            int nameId      = buf.getInt();
            int typeId      = buf.getInt();
            float fit       = Calculator.twoSigFigsMantissa(buf.getFloat());
            int elementsNum = buf.getInt();
            out.add(new TypeAlloyMatch(id, nameId, typeId, fit, elementsNum));
        }
        return out;
    }

    public static List<ElementPercentageFitStore> decodeElements(byte[] blob) {
        ByteBuffer buf = ByteBuffer.wrap(blob).order(ByteOrder.LITTLE_ENDIAN);
        List<ElementPercentageFitStore> out = new ArrayList<>();
        while (buf.remaining() >= Integer.BYTES + Float.BYTES*3 + Integer.BYTES) {
            int   ind       = buf.getInt() + 1; // +1 ?
            float min       = buf.getFloat();
            float max       = buf.getFloat();
            float fit       = Calculator.twoSigFigsMantissa(buf.getFloat());
            int   commentId = buf.getInt();
            out.add(new ElementPercentageFitStore(ind, min, max, fit, commentId));
        }
        return out;
    }
}
