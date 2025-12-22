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


import java.net.URI;
import java.util.List;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORPairsBuilder;
import com.authlete.cbor.CBORString;


/**
 * The {@code StatusListInfo} structure.
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * StatusListInfo = {
 *     "idx" : unsigned integer,
 *     "uri" : tstr
 * }
 * </pre>
 *
 * @see <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">
 *      Token Status List (TSL)</a>
 *
 * @since 1.21
 */
public class StatusListInfo extends CBORPairList
{
    private static final CBORString LABEL_IDX = new CBORString("idx");
    private static final CBORString LABEL_URI = new CBORString("uri");


    /**
     * A constructor with {@code idx} and {@code uri}.
     *
     * @param idx
     *         The {@code idx} claim that represents the index to check
     *         for status information in the Status List for the current
     *         Referenced Token.
     *
     * @param uri
     *         The {@code uri} claim that identifies the Status List Token
     *         containing the status information for the Referenced Token.
     */
    public StatusListInfo(int idx, String uri)
    {
        super(createList(idx, uri));

        validateIdx(idx);
        validateUri(uri);
    }


    private static List<CBORPair> createList(int idx, String uri)
    {
        return new CBORPairsBuilder()
                .add(LABEL_IDX, idx)
                .add(LABEL_URI, uri)
                .build();
    }


    private static void validateIdx(int idx)
    {
        // Token Status List (TSL)
        // 6.3. Referenced Token in COSE
        //
        //   idx:
        //     REQUIRED. Unsigned integer (Major Type 0) The idx (index) claim
        //     MUST specify a non-negative Integer that represents the index
        //     to check for status information in the Status List for the
        //     current Referenced Token.
        //

        if (idx < 0)
        {
            throw new IllegalArgumentException(
                    "'idx' for StatusListInfo must not be negative.");
        }
    }


    private static void validateUri(String uri)
    {
        // Token Status List (TSL)
        // 6.3. Referenced Token in COSE
        //
        //   uri:
        //     REQUIRED. Text string (Major Type 3). The uri (URI) claim MUST
        //     specify a String value that identifies the Status List Token
        //     containing the status information for the Referenced Token.
        //     The value of uri MUST be a URI conforming to [RFC3986].
        //

        if (uri == null)
        {
            throw new IllegalArgumentException(
                    "'uri' for StatusInfoList must not be null.");
        }

        try
        {
            new URI(uri);
        }
        catch (Exception cause)
        {
            throw new IllegalArgumentException(
                    "'uri' for StatusInfoList must be a valid URI.");
        }
    }
}
