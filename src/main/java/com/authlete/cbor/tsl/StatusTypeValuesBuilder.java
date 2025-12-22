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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;


/**
 * A utility class to construct the compressed byte array of a token status list.
 *
 * @see <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">
 *      Token Status List (TSL)</a>
 *
 * @see StatusTypeValue
 *
 * @since 1.21
 */
public class StatusTypeValuesBuilder
{
    private final int bits;
    private final int maxValue;
    private final ValuesHolder holder;
    private int maxIndex;


    /**
     * A constructor with {@code bits}.
     *
     * @param bits
     *         The number of bits per Referenced Token. The allowed values
     *         for {@code bits} are 1, 2, 4 and 8.
     */
    public StatusTypeValuesBuilder(int bits)
    {
        this.bits     = validateBits(bits);
        this.maxValue = computeMaxValue(bits);
        this.holder   = new ValuesHolder();
        this.maxIndex = -1;
    }


    private static int validateBits(int bits)
    {
        switch (bits)
        {
            case 1:
            case 2:
            case 4:
            case 8:
                // OK
                return bits;
        }

        throw new IllegalArgumentException(
                "'bits' must be 1, 2, 4 or 8.");
    }


    private static int computeMaxValue(int bits)
    {
        switch (bits)
        {
            case 1:
                // 2^1 - 1
                return 1;

            case 2:
                // 2^2 - 1
                return 3;

            case 4:
                // 2^4 - 1
                return 15;

            case 8:
                // 2^8 - 1
                return 255;

            default:
                // This won't happen.
                return 0;
        }
    }


    /**
     * Get the number of bits per Referenced Token.
     *
     * @return
     *         The number of bits per Referenced Token.
     */
    public int getBits()
    {
        return bits;
    }


    private int getMaxIndex()
    {
        return maxIndex;
    }


    private void updateMaxIndex(int index)
    {
        maxIndex = Math.max(maxIndex, index);
    }


    /**
     * Set the value at the index.
     *
     * @param value
     *         The value to set.
     *
     * @param index
     *         The index to set the value at.
     *
     * @return
     *         {@code this} object.
     */
    public StatusTypeValuesBuilder valueAt(StatusTypeValue value, int index)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(
                    "'value' must not be null.");
        }

        return valueAt(value.getValue(), index);
    }


    /**
     * Set the value at the index.
     *
     * @param value
     *         The value to set.
     *
     * @param index
     *         The index to set the value at.
     *
     * @return
     *         {@code this} object.
     */
    public StatusTypeValuesBuilder valueAt(int value, int index)
    {
        // Ensure that the 'value' and 'index' fall within the permitted ranges.
        checkValueAtParameters(value, index);

        // Update the maximum index.
        updateMaxIndex(index);

        // Set the value at the index.
        holder.setValueAt(value, index);

        return this;
    }


    private void checkValueAtParameters(int value, int index)
    {
        if (value < 0)
        {
            throw new IllegalArgumentException(
                    "'value' must not be negative.");
        }

        if (maxValue < value)
        {
            throw new IllegalArgumentException(String.format(
                    "The 'value' exceeds the permitted maximum value (%d).",
                    maxValue));
        }

        if (index < 0)
        {
            throw new IllegalArgumentException(
                    "'index' must not be negative.");
        }

        if (Integer.MAX_VALUE < index)
        {
            throw new IllegalArgumentException(String.format(
                    "The 'index' exceeds the permitted maximum value (%d).",
                    Integer.MAX_VALUE));
        }
    }


    /**
     * Construct the status values.
     *
     * <p>
     * Note that the returned value is compressed using DEFLATE with the zlib
     * data format, as required by the specification.
     * </p>
     *
     * @return
     *         The status values.
     */
    public byte[] build()
    {
        return build(true);
    }


    /**
     * Construct the status values.
     *
     * @param compressed
     *         {@code true} to compress the byte array representing the status values.
     *         Note that the Token Status List specification requires compression.
     *         The reason for allowing {@code false} is only for debugging purposes.
     *
     * @return
     *         The status values.
     */
    public byte[] build(boolean compressed)
    {
        byte[] bytes = holder.build();

        if (compressed)
        {
            // Compress the status values using DEFLATE with the zlib data format,
            // as required by the specification.
            bytes = compress(bytes);
        }

        return bytes;
    }


    // NOTE for code readers:
    //
    // ValuesHolder is a kind of sparse array. The large index range is
    // divided into three layers, which are represented by HighMap, MidMap,
    // and LowValues, respectively.
    //
    //   highIndex = (index >> 20) & 0b111_1111_1111;  // 0 .. 2047
    //   midIndex  = (index >> 10) & 0b011_1111_1111;  // 0 .. 1023
    //   lowIndex  = (index >>  0) & 0b011_1111_1111;  // 0 .. 1023
    //
    //   HighMap [ highIndex ] -> MidMap
    //   MidMap  [ midIndex  ] -> LowValues
    //   LowValues             -> values (byte[1024 * bits])
    //


    private class ValuesHolder
    {
        private final HighMap highMap = new HighMap();

        public void setValueAt(int value, int index)
        {
            highMap.setValueAt(value, index);
        }

        public byte[] build()
        {
            if (getMaxIndex() < 0)
            {
                return new byte[0];
            }

            // Prepare a byte array that can hold all the status values
            // before compression.
            int nBytes   = (getMaxIndex() / (8 / bits)) + 1;
            byte[] bytes = new byte[nBytes];

            highMap.fill(bytes);

            return bytes;
        }
    }


    private class HighMap extends TreeMap<Integer, MidMap>
    {
        private static final long serialVersionUID = 1L;

        public void setValueAt(int value, int index)
        {
            // Compute the key for this HighMap instance.
            int highIndex = (index >> 20) & 0b111_1111_1111;  // 0 .. 2047 (2^11-1)
            Integer key   = Integer.valueOf(highIndex);

            // Obtain the MidMap instance.
            MidMap midmap = get(key);

            if (midmap == null)
            {
                midmap = new MidMap();
                put(key, midmap);
            }

            midmap.setValueAt(value, index);
        }

        public void fill(byte[] bytes)
        {
            for (Map.Entry<Integer, MidMap> entry : entrySet())
            {
                int highIndex = entry.getKey();
                MidMap midmap = entry.getValue();

                midmap.fill(bytes, highIndex);
            }
        }
    }


    private class MidMap extends TreeMap<Integer, LowValues>
    {
        private static final long serialVersionUID = 1L;

        public void setValueAt(int value, int index)
        {
            // Compute the key for this MidMap instance.
            int midIndex = (index >> 10) & 0b11_1111_1111;  // 0 .. 1023 (2^10-1)
            Integer key  = Integer.valueOf(midIndex);

            // Obtain the LowValues instance.
            LowValues values = get(key);

            if (values == null)
            {
                values = new LowValues();
                put(key, values);
            }

            values.setValueAt(value, index);
        }

        public void fill(byte[] bytes, int highIndex)
        {
            for (Map.Entry<Integer, LowValues> entry : entrySet())
            {
                int       midIndex  = entry.getKey();
                LowValues lowValues = entry.getValue();

                lowValues.fill(bytes, highIndex, midIndex);
            }
        }
    }


    private class LowValues
    {
        private final byte[] values = new byte[1024 * bits];

        public void setValueAt(int value, int index)
        {
            int lowIndex   = (index >>  0) & 0b11_1111_1111;  // 0 .. 1023 (2^10-1)
            int valueIndex = (lowIndex / (8 / bits));
            int shift      = (lowIndex % (8 / bits)) * bits;
            int mask       = maxValue << shift;

            values[valueIndex] &= (byte)~mask;             // Clear the current bits.
            values[valueIndex] |= (byte)(value << shift);  // Set the new bits.
        }

        /**
         * Copy the values held by this {@code LowValues} instance to the
         * appropriate location in the provided byte array.
         */
        public void fill(byte[] bytes, int highIndex, int midIndex)
        {
            // The start point of the destination to copy the values to.
            int destStart = ((highIndex << 20) | (midIndex << 10)) / (8 / bits);

            // The end point (exclusive) of the destination to copy the values to.
            int destEnd = Math.min(destStart + values.length, bytes.length);

            // The number of bytes to copy.
            int length = destEnd - destStart;

            System.arraycopy(values, 0, bytes, destStart, length);
        }
    }


    /**
     * Compress the input data using DEFLATE with the zlib data format.
     */
    private static byte[] compress(byte[] input)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

        try (DeflaterOutputStream dos = new DeflaterOutputStream(baos, deflater))
        {
            dos.write(input);
        }
        catch (IOException cause)
        {
            // No compression
            return input;
        }

        return baos.toByteArray();
    }
}
