/*
 * Copyright (C) 2025 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.authlete.cbor.tsl;


import static com.authlete.cbor.tsl.StatusTypeValue.INVALID;
import static com.authlete.cbor.tsl.StatusTypeValue.SUSPENDED;
import static com.authlete.cbor.tsl.StatusTypeValue.VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;
import org.junit.jupiter.api.Test;

public class StatusListBuilderTest
{
    @Test
    public void test_tsl_c1() throws IOException
    {
        // Token Status List (TSL)

        // Appendix C. Test vectors for Status List encoding
        //
        //   All values that are not mentioned for the examples below can be
        //   assumed to be 0 (VALID). All examples are initialized with a
        //   size of 2^20 entries.

        // C.1. 1 bit Status List
        //
        // status[0] = 0b1
        // status[1993] = 0b1
        // status[25460] = 0b1
        // status[159495] = 0b1
        // status[495669] = 0b1
        // status[554353] = 0b1
        // status[645645] = 0b1
        // status[723232] = 0b1
        // status[854545] = 0b1
        // status[934534] = 0b1
        // status[1000345] = 0b1

        int bits = 1;

        StatusListBuilder builder = new StatusListBuilder(bits).capacity(1048576);
        builder.valueAt(INVALID,       0);
        builder.valueAt(INVALID,    1993);
        builder.valueAt(INVALID,   25460);
        builder.valueAt(INVALID,  159495);
        builder.valueAt(INVALID,  495669);
        builder.valueAt(INVALID,  554353);
        builder.valueAt(INVALID,  645645);
        builder.valueAt(INVALID,  723232);
        builder.valueAt(INVALID,  854545);
        builder.valueAt(INVALID,  934534);
        builder.valueAt(INVALID, 1000345);

        StatusList statusList = builder.build();

        // Uncompressed the value of "lst".
        byte[] uncompressedLst = uncompress(statusList.getLst());

        assertEquals(uncompressedLst.length, (1048576 / (8 / bits)));

        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 0));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 1));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 1992));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 1993));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 1994));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 25459));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 25460));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 25461));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 159494));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 159495));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 159496));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 495668));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 495669));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 495670));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 554352));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 554353));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 554354));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 645644));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 645645));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 645646));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 723231));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 723232));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 723233));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 854544));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 854545));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 854546));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 934533));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 934534));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 934535));

        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 1000344));
        assertEquals(INVALID.getValue(), getValueAt(bits, uncompressedLst, 1000345));
        assertEquals(VALID.getValue(),   getValueAt(bits, uncompressedLst, 1000346));
    }


    @Test
    public void test_tsl_c2() throws IOException
    {
        // Token Status List (TSL)

        // Appendix C. Test vectors for Status List encoding
        //
        //   All values that are not mentioned for the examples below can be
        //   assumed to be 0 (VALID). All examples are initialized with a
        //   size of 2^20 entries.

        // C.2. 2 bit Status List
        //
        // status[0] = 0b01
        // status[1993] = 0b10
        // status[25460]= 0b01
        // status[159495] = 0b11
        // status[495669] = 0b01
        // status[554353] = 0b01
        // status[645645] = 0b10
        // status[723232] = 0b01
        // status[854545] = 0b01
        // status[934534] = 0b10
        // status[1000345] = 0b11

        int bits = 2;

        StatusListBuilder builder = new StatusListBuilder(bits).capacity(1048576);
        builder.valueAt(INVALID,         0);
        builder.valueAt(SUSPENDED,    1993);
        builder.valueAt(INVALID,     25460);
        builder.valueAt(0x3,        159495);
        builder.valueAt(INVALID,    495669);
        builder.valueAt(INVALID,    554353);
        builder.valueAt(SUSPENDED,  645645);
        builder.valueAt(INVALID,    723232);
        builder.valueAt(INVALID,    854545);
        builder.valueAt(SUSPENDED,  934534);
        builder.valueAt(0x3,       1000345);

        StatusList statusList = builder.build();

        // Uncompressed the value of "lst".
        byte[] uncompressedLst = uncompress(statusList.getLst());

        assertEquals(uncompressedLst.length, (1048576 / (8 / bits)));

        assertEquals(INVALID.getValue(),   getValueAt(bits, uncompressedLst, 0));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 1));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 1992));
        assertEquals(SUSPENDED.getValue(), getValueAt(bits, uncompressedLst, 1993));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 1994));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 25459));
        assertEquals(INVALID.getValue(),   getValueAt(bits, uncompressedLst, 25460));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 25461));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 159494));
        assertEquals(0x3,                  getValueAt(bits, uncompressedLst, 159495));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 159496));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 495668));
        assertEquals(INVALID.getValue(),   getValueAt(bits, uncompressedLst, 495669));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 495670));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 554352));
        assertEquals(INVALID.getValue(),   getValueAt(bits, uncompressedLst, 554353));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 554354));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 645644));
        assertEquals(SUSPENDED.getValue(), getValueAt(bits, uncompressedLst, 645645));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 645646));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 723231));
        assertEquals(INVALID.getValue(),   getValueAt(bits, uncompressedLst, 723232));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 723233));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 854544));
        assertEquals(INVALID.getValue(),   getValueAt(bits, uncompressedLst, 854545));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 854546));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 934533));
        assertEquals(SUSPENDED.getValue(), getValueAt(bits, uncompressedLst, 934534));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 934535));

        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 1000344));
        assertEquals(0x3,                  getValueAt(bits, uncompressedLst, 1000345));
        assertEquals(VALID.getValue(),     getValueAt(bits, uncompressedLst, 1000346));
    }


    @Test
    public void test_tsl_c3() throws IOException
    {
        // Token Status List (TSL)

        // Appendix C. Test vectors for Status List encoding
        //
        //   All values that are not mentioned for the examples below can be
        //   assumed to be 0 (VALID). All examples are initialized with a
        //   size of 2^20 entries.

        // C.3. 4 bit Status List
        //
        // status[0] = 0b0001
        // status[1993] = 0b0010
        // status[35460] = 0b0011
        // status[459495] = 0b0100
        // status[595669] = 0b0101
        // status[754353] = 0b0110
        // status[845645] = 0b0111
        // status[923232] = 0b1000
        // status[924445] = 0b1001
        // status[934534] = 0b1010
        // status[1004534] = 0b1011
        // status[1000345] = 0b1100
        // status[1030203] = 0b1101
        // status[1030204] = 0b1110
        // status[1030205] = 0b1111

        int bits = 4;

        StatusListBuilder builder = new StatusListBuilder(bits).capacity(1048576);
        builder.valueAt(0x1,       0);
        builder.valueAt(0x2,    1993);
        builder.valueAt(0x3,   35460);
        builder.valueAt(0x4,  459495);
        builder.valueAt(0x5,  595669);
        builder.valueAt(0x6,  754353);
        builder.valueAt(0x7,  845645);
        builder.valueAt(0x8,  923232);
        builder.valueAt(0x9,  924445);
        builder.valueAt(0xA,  934534);
        builder.valueAt(0xB, 1004534);
        builder.valueAt(0xC, 1000345);
        builder.valueAt(0xD, 1030203);
        builder.valueAt(0xE, 1030204);
        builder.valueAt(0xF, 1030205);

        StatusList statusList = builder.build();

        // Uncompressed the value of "lst".
        byte[] uncompressedLst = uncompress(statusList.getLst());

        assertEquals(uncompressedLst.length, (1048576 / (8 / bits)));

        assertEquals(0x1, getValueAt(bits, uncompressedLst,       0));
        assertEquals(0x2, getValueAt(bits, uncompressedLst,    1993));
        assertEquals(0x3, getValueAt(bits, uncompressedLst,   35460));
        assertEquals(0x4, getValueAt(bits, uncompressedLst,  459495));
        assertEquals(0x5, getValueAt(bits, uncompressedLst,  595669));
        assertEquals(0x6, getValueAt(bits, uncompressedLst,  754353));
        assertEquals(0x7, getValueAt(bits, uncompressedLst,  845645));
        assertEquals(0x8, getValueAt(bits, uncompressedLst,  923232));
        assertEquals(0x9, getValueAt(bits, uncompressedLst,  924445));
        assertEquals(0xA, getValueAt(bits, uncompressedLst,  934534));
        assertEquals(0xB, getValueAt(bits, uncompressedLst, 1004534));
        assertEquals(0xC, getValueAt(bits, uncompressedLst, 1000345));
        assertEquals(0xD, getValueAt(bits, uncompressedLst, 1030203));
        assertEquals(0xE, getValueAt(bits, uncompressedLst, 1030204));
        assertEquals(0xF, getValueAt(bits, uncompressedLst, 1030205));
    }


    @Test
    public void test_tsl_c4() throws IOException
    {
        // Token Status List (TSL)

        // Appendix C. Test vectors for Status List encoding
        //
        //   All values that are not mentioned for the examples below can be
        //   assumed to be 0 (VALID). All examples are initialized with a
        //   size of 2^20 entries.

        // C.4. 8 bit Status List
        //
        // status[52451]  = 0b00000001
        // status[576778] = 0b00000010
        // ...
        // status[468106] = 0b00000100
        // ...
        // status[884834] = 0b00001000
        // ...
        // status[879796] = 0b00010000
        // ...
        // status[752834] = 0b00100000
        // ...
        // status[795775] = 0b01000000
        // ...
        // status[502167] = 0b10000000
        // ...

        int bits = 8;

        StatusListBuilder builder = new StatusListBuilder(bits).capacity(1048576);
        builder.valueAt(0x01,  52451);
        builder.valueAt(0x02, 576778);
        builder.valueAt(0x04, 468106);
        builder.valueAt(0x08, 884834);
        builder.valueAt(0x10, 879796);
        builder.valueAt(0x20, 752834);
        builder.valueAt(0x40, 795775);
        builder.valueAt(0x80, 502167);

        StatusList statusList = builder.build();

        // Uncompressed the value of "lst".
        byte[] uncompressedLst = uncompress(statusList.getLst());

        assertEquals(uncompressedLst.length, (1048576 / (8 / bits)));

        assertEquals(0x01, getValueAt(bits, uncompressedLst,  52451));
        assertEquals(0x02, getValueAt(bits, uncompressedLst, 576778));
        assertEquals(0x04, getValueAt(bits, uncompressedLst, 468106));
        assertEquals(0x08, getValueAt(bits, uncompressedLst, 884834));
        assertEquals(0x10, getValueAt(bits, uncompressedLst, 879796));
        assertEquals(0x20, getValueAt(bits, uncompressedLst, 752834));
        assertEquals(0x40, getValueAt(bits, uncompressedLst, 795775));
        assertEquals(0x80, getValueAt(bits, uncompressedLst, 502167));
    }


    private static int getValueAt(int bits, byte[] values, int index)
    {
        int valueIndex = (index / (8 / bits));
        int shift      = (index % (8 / bits)) * bits;
        int mask       = computeMask(bits);

        return ((values[valueIndex] >> shift) & mask);
    }


    private static int computeMask(int bits)
    {
        switch (bits)
        {
            case 1:
                return 0x1;

            case 2:
                return 0x3;

            case 4:
                return 0xF;

            case 8:
                return 0xFF;

            default:
                return 0;
        }
    }


    private static byte[] uncompress(byte[] compressed) throws IOException
    {
        try (InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressed));
             ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            byte[] buffer = new byte[1024];

            int len;

            while ((len = iis.read(buffer)) != -1)
            {
                baos.write(buffer, 0, len);
            }

            return baos.toByteArray();
        }
    }
}
