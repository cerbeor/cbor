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


/**
 * Status Type values for Referenced Tokens.
 *
 * @see <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">
 *      Token Status List (TSL)</a>
 *
 * @see StatusTypeValuesBuilder
 *
 * @since 1.21
 */
public enum StatusTypeValue
{
    /**
     * VALID (0x00) &mdash; The status of the Referenced Token is valid,
     * correct or legal.
     */
    VALID(0),


    /**
     * INVALID (0x01) &mdash; The status of the Referenced Token is revoked,
     * annulled, taken back, recalled or cancelled.
     */
    INVALID(1),


    /**
     * SUSPENDED (0x02) &mdash; The status of the Referenced Token is
     * temporarily invalid, hanging, debarred from privilege. This state
     * is usually temporary.
     */
    SUSPENDED(2),
    ;


    private final int value;


    private StatusTypeValue(int value)
    {
        this.value = value;
    }


    /**
     * Get the numeric value of this status type.
     *
     * @return
     *         The numeric value of this status type.
     */
    public int getValue()
    {
        return value;
    }
}
