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


import java.util.List;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORPairsBuilder;
import com.authlete.cbor.CBORString;


/**
 * The {@code StatusList} structure.
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * StatusList = {
 *     "bits" : 1 / 2 / 4 / 8,     ; The number of bits used per Referenced Token
 *     "lst"  : bstr,              ; Byte string that contains the Status List
 *     ? "aggregation_uri" : tstr  ; link to the Status List Aggregation
 * }
 * </pre>
 *
 * @see <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">
 *      Token Status List (TSL)</a>
 *
 * @see StatusListBuilder
 *
 * @since 1.21
 */
public class StatusList extends CBORPairList
{
    private static final CBORString LABEL_BITS            = new CBORString("bits");
    private static final CBORString LABEL_LST             = new CBORString("lst");
    private static final CBORString LABEL_AGGREGATION_URI = new CBORString("aggregation_uri");


    private final int bits;
    private final byte[] lst;
    private final String aggregationUri;


    /**
     * A constructor with {@code bits}, {@code lst}, and optional
     * {@code aggregation_uri}.
     *
     * @param bits
     *         The number of bits per Referenced Token in the compressed byte
     *         array ({@code lst}). The allowed values for {@code bits} are
     *         1, 2, 4 and 8.
     *
     * @param lst
     *         The status values for all the Referenced Tokens it conveys
     *         statuses for.
     *
     * @param aggregationUri
     *         The URI to retrieve the Status List Aggregation. This parameter
     *         is optional.
     */
    public StatusList(int bits, byte[] lst, String aggregationUri)
    {
        super(createList(bits, lst, aggregationUri));

        validateBits(bits);
        validateLst(lst);

        this.bits           = bits;
        this.lst            = lst;
        this.aggregationUri = aggregationUri;
    }


    private static List<CBORPair> createList(
            int bits, byte[] lst, String aggregationUri)
    {
        return new CBORPairsBuilder()
                .add(          LABEL_BITS,            bits)
                .add(          LABEL_LST,             lst)
                .addUnlessNull(LABEL_AGGREGATION_URI, aggregationUri)
                .build();
    }


    private static void validateBits(int bits)
    {
        // Token Status List (TSL)
        // 4.3. Status List in CBOR Format
        //
        //   bits:
        //     REQUIRED. CBOR Unsigned integer (Major Type 0) that contains
        //     the number of bits per Referenced Token in the compressed byte
        //     array (lst). The allowed values for bits are 1, 2, 4 and 8.
        //

        switch (bits)
        {
            case 1:
            case 2:
            case 4:
            case 8:
                // OK
                return;
        }

        throw new IllegalArgumentException(
                "'bits' for StatusList must be 1, 2, 4 or 8.");
    }


    private static void validateLst(byte[] lst)
    {
        // Token Status List (TSL)
        // 4.3. Status List in CBOR Format
        //
        //   lst:
        //     REQUIRED. CBOR Byte string (Major Type 2) that contains the
        //     status values for all the Referenced Tokens it conveys statuses
        //     for. The value MUST be the compressed byte array as specified in
        //     Section 4.1.
        //

        if (lst == null)
        {
            throw new IllegalArgumentException(
                    "'lst' for StatusList must not be null.");
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


    /**
     * Get the status values.
     *
     * <p>
     * Note that the returned value is compressed using DEFLATE with the zlib
     * data format, as required by the specification.
     * </p>
     *
     * @return
     *         The status values.
     */
    public byte[] getLst()
    {
        return lst;
    }


    /**
     * Get the URI of the status list aggregation.
     *
     * @return
     *         The URI of the status list aggregation.
     */
    public String getAggregationUri()
    {
        return aggregationUri;
    }
}
