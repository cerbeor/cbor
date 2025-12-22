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
 * The {@code Status} structure.
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * Status = {
 *     "status_list" : {@link StatusList}
 * }
 * </pre>
 *
 * <p>
 * NOTE: The <a href=
 * "https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">Token
 * Status List</a> specification defines the {@code Status} CBOR structure
 * as a generic map that can hold any status mechanism. However, at present,
 * no status mechanisms other than {@code StatusList} exist.
 * </p>
 *
 * @see <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">
 *      Token Status List (TSL)</a>
 *
 * @since 1.21
 */
public class Status extends CBORPairList
{
    private static final CBORString LABEL_STATUS_LIST = new CBORString("status_list");


    public Status(StatusList statusList)
    {
        super(createList(statusList));

        validateStatusList(statusList);
    }


    private static List<CBORPair> createList(StatusList statusList)
    {
        return new CBORPairsBuilder()
                .add(LABEL_STATUS_LIST, statusList)
                .build();
    }


    private static void validateStatusList(StatusList statusList)
    {
        // Token Status List (TSL)
        // 6.3. Referenced Token in COSE
        //
        //   status_list (status list):
        //     REQUIRED when the status mechanism defined in this specification
        //     is used. It has the same definition as the status_list claim in
        //     Section 6.2 but MUST be encoded as a StatusListInfo CBOR
        //     structure with the following fields:
        //
        //     idx:
        //       REQUIRED. Unsigned integer (Major Type 0) The idx (index)
        //       claim MUST specify a non-negative Integer that represents the
        //       index to check for status information in the Status List for
        //       the current Referenced Token.
        //
        //     uri:
        //       REQUIRED. Text string (Major Type 3). The uri (URI) claim MUST
        //       specify a String value that identifies the Status List Token
        //       containing the status information for the Referenced Token.
        //       The value of uri MUST be a URI conforming to [RFC3986].
        //

        if (statusList == null)
        {
            throw new IllegalArgumentException(
                    "'status_list' for Status must not be null.");
        }
    }
}
