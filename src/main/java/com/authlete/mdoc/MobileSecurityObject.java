/*
 * Copyright (C) 2023-2025 Authlete, Inc.
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
package com.authlete.mdoc;


import java.util.List;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORPairsBuilder;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.tsl.Status;


/**
 * The {@code MobileSecurityObject} structure.
 *
 * <p>
 * For details, see ISO/IEC 18013-5:2021, 9.1.2.4 Signing method and structure for MSO.
 * </p>
 *
 * <h3>Definition</h3>
 *
 * <pre>
 * MobileSecurityObject = {
 *     "version" : tstr,                ; Version of the MobileSecurityObject
 *     "digestAlgorithm" : tstr,        ; Message digest algorithm used
 *     "valueDigests" : {@link ValueDigests},   ; Digests of all data elements per namespace
 *     "deviceKeyInfo" : {@link DeviceKeyInfo},
 *     "docType" : tstr,                ; docType as used in Documents
 *     "validityInfo" : {@link ValidityInfo}
 * }
 * </pre>
 *
 * <h3>Additional Parameters</h3>
 *
 * <p>
 * The <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">Token
 * Status List</a> specification introduces a new {@code status} parameter to be used
 * with the Status List mechanism.
 * </p>
 *
 * <blockquote>
 * <p><i>
 * ISO mdoc [ISO.mdoc] may utilize the Status List mechanism by introducing the
 * {@code status} parameter in the Mobile Security Object (MSO) as specified in
 * Section 9.1.2 of [ISO.mdoc]. The {@code status} parameter contains the
 * {@code Status} CBOR structure as described in Section 6.3.
 * </i></p>
 * </blockquote>
 *
 * @since 1.5
 *
 * @see <a href="https://www.iso.org/standard/69084.html">ISO/IEC 18013-5:2021</a>
 * @see <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">Token Status List</a>
 *
 * @see MobileSecurityObjectBytes
 */
public class MobileSecurityObject extends CBORPairList
{
    private static final String DEFAULT_VERSION          = "1.0";
    private static final String DEFAULT_DIGEST_ALGORITHM = "SHA-256";

    private static final CBORString LABEL_VERSION          = new CBORString("version");
    private static final CBORString LABEL_DIGEST_ALGORITHM = new CBORString("digestAlgorithm");
    private static final CBORString LABEL_VALUE_DIGESTS    = new CBORString("valueDigests");
    private static final CBORString LABEL_DEVICE_KEY_INFO  = new CBORString("deviceKeyInfo");
    private static final CBORString LABEL_DOC_TYPE         = new CBORString("docType");
    private static final CBORString LABEL_VALIDITY_INFO    = new CBORString("validityInfo");
    private static final CBORString LABEL_STATUS           = new CBORString("status");


    /**
     * A constructor with the default version and the default digest algorithm.
     *
     * <p>
     * This constructor is an alias of {@link #MobileSecurityObject(String,
     * String, ValueDigests, DeviceKeyInfo, String, ValidityInfo)
     * MobileSecurityObject}{@code ("1.0", "SHA-256", valueDigests,
     * deviceKeyInfo, docType, validityInfo)}.
     * </p>
     */
    public MobileSecurityObject(
            ValueDigests valueDigests, DeviceKeyInfo deviceKeyInfo,
            String docType, ValidityInfo validityInfo)
    {
        this(DEFAULT_VERSION, DEFAULT_DIGEST_ALGORITHM,
                valueDigests, deviceKeyInfo, docType, validityInfo);
    }


    /**
     * A constructor with the default version and the default digest algorithm.
     *
     * <p>
     * This constructor is an alias of {@link #MobileSecurityObject(String,
     * String, ValueDigests, DeviceKeyInfo, String, ValidityInfo, Status)
     * MobileSecurityObject}{@code ("1.0", "SHA-256", valueDigests,
     * deviceKeyInfo, docType, validityInfo, status)}.
     * </p>
     */
    public MobileSecurityObject(
            ValueDigests valueDigests, DeviceKeyInfo deviceKeyInfo,
            String docType, ValidityInfo validityInfo, Status status)
    {
        this(DEFAULT_VERSION, DEFAULT_DIGEST_ALGORITHM,
                valueDigests, deviceKeyInfo, docType, validityInfo, status);
    }


    public MobileSecurityObject(
            String version, String digestAlgorithm,
            ValueDigests valueDigests, DeviceKeyInfo deviceKeyInfo,
            String docType, ValidityInfo validityInfo)
    {
        this(version, digestAlgorithm, valueDigests,
                deviceKeyInfo, docType, validityInfo, null);
    }


    public MobileSecurityObject(
            String version, String digestAlgorithm,
            ValueDigests valueDigests, DeviceKeyInfo deviceKeyInfo,
            String docType, ValidityInfo validityInfo, Status status)
    {
        super(createList(
                version, digestAlgorithm, valueDigests,
                deviceKeyInfo, docType, validityInfo, status));
    }


    private static List<CBORPair> createList(
            String version, String digestAlgorithm,
            ValueDigests valueDigests, DeviceKeyInfo deviceKeyInfo,
            String docType, ValidityInfo validityInfo, Status status)
    {
        return new CBORPairsBuilder()
                .addUnlessNull(LABEL_VERSION,          version)
                .addUnlessNull(LABEL_DIGEST_ALGORITHM, digestAlgorithm)
                .addUnlessNull(LABEL_VALUE_DIGESTS,    valueDigests)
                .addUnlessNull(LABEL_DEVICE_KEY_INFO,  deviceKeyInfo)
                .addUnlessNull(LABEL_DOC_TYPE,         docType)
                .addUnlessNull(LABEL_VALIDITY_INFO,    validityInfo)
                .addUnlessNull(LABEL_STATUS,           status)
                .build();
    }
}
