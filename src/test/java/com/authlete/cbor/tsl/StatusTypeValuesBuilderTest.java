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
import org.junit.jupiter.api.Test;


public class StatusTypeValuesBuilderTest
{
    @Test
    public void test_tsl_4_1_bits1()
    {
        // Token Status List (TSL)
        // 4.1. Compressed Byte Array

        // status[0] = 0b1
        // status[1] = 0b0
        // status[2] = 0b0
        // status[3] = 0b1
        // status[4] = 0b1
        // status[5] = 0b1
        // status[6] = 0b0
        // status[7] = 0b1
        // status[8] = 0b1
        // status[9] = 0b1
        // status[10] = 0b0
        // status[11] = 0b0
        // status[12] = 0b0
        // status[13] = 0b1
        // status[14] = 0b0
        // status[15] = 0b1

        StatusTypeValuesBuilder builder = new StatusTypeValuesBuilder(1);

        builder.valueAt(INVALID,  0);
        builder.valueAt(VALID,    1);
        builder.valueAt(VALID,    2);
        builder.valueAt(INVALID,  3);
        builder.valueAt(INVALID,  4);
        builder.valueAt(INVALID,  5);
        builder.valueAt(VALID,    6);
        builder.valueAt(INVALID,  7);
        builder.valueAt(INVALID,  8);
        builder.valueAt(INVALID,  9);
        builder.valueAt(VALID,   10);
        builder.valueAt(VALID,   11);
        builder.valueAt(VALID,   12);
        builder.valueAt(INVALID, 13);
        builder.valueAt(VALID,   14);
        builder.valueAt(INVALID, 15);

        byte[] bytes = builder.build(false); // not compressed

        // byte no            0                  1
        // bit no      7 6 5 4 3 2 1 0    7 6 5 4 3 2 1 0
        //            +-+-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+-+
        // values     |1|0|1|1|1|0|0|1|  |1|0|1|0|0|0|1|1|
        //            +-+-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+-+
        // index       7 6 5 4 3 2 1 0   15   ...  10 9 8
        //            \_______________/  \_______________/
        // byte value       0xB9               0xA3

        assertEquals(2, bytes.length);
        assertEquals((byte)0xB9, bytes[0]);
        assertEquals((byte)0xA3, bytes[1]);
    }


    @Test
    public void test_tsl_4_1_bits2()
    {
        // Token Status List (TSL)
        // 4.1. Compressed Byte Array

        // status[0] = 0b01
        // status[1] = 0b10
        // status[2] = 0b00
        // status[3] = 0b11
        // status[4] = 0b0
        // status[5] = 0b01
        // status[6] = 0b00
        // status[7] = 0b01
        // status[8] = 0b01
        // status[9] = 0b10
        // status[10] = 0b11
        // status[11] = 0b11

        StatusTypeValuesBuilder builder = new StatusTypeValuesBuilder(2);

        builder.valueAt(INVALID,    0);
        builder.valueAt(SUSPENDED,  1);
        builder.valueAt(VALID,      2);
        builder.valueAt(0x03,       3);
        builder.valueAt(VALID,      4);
        builder.valueAt(INVALID,    5);
        builder.valueAt(VALID,      6);
        builder.valueAt(INVALID,    7);
        builder.valueAt(INVALID,    8);
        builder.valueAt(SUSPENDED,  9);
        builder.valueAt(0x03,      10);
        builder.valueAt(0x03,      11);

        byte[] bytes = builder.build(false); // not compressed

        // byte no            0                  1                  2
        // bit no      7 6 5 4 3 2 1 0    7 6 5 4 3 2 1 0    7 6 5 4 3 2 1 0
        //            +-+-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+-+
        // values     |1|1|0|0|1|0|0|1|  |0|1|0|0|0|1|0|0|  |1|1|1|1|1|0|0|1|
        //            +-+-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+-+
        //             \ / \ / \ / \ /    \ / \ / \ / \ /    \ / \ / \ / \ /
        // status      11  00  10  01     01  00  01  00     11  11  10  01
        // index        3   2   1   0      7   6   5   4      11  10  9   8
        //              \___________/      \___________/      \___________/
        // byte value       0xC9               0x44               0xF9

        assertEquals(3, bytes.length);
        assertEquals((byte)0xC9, bytes[0]);
        assertEquals((byte)0x44, bytes[1]);
        assertEquals((byte)0xF9, bytes[2]);
    }
}
