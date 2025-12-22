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
 * A utility class to construct a {@link StatusList} instance.
 *
 * @see <a href="https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/">
 *      Token Status List (TSL)</a>
 *
 * @see StatusList
 *
 * @since 1.21
 */
public class StatusListBuilder
{
    private final StatusTypeValuesBuilder valuesBuilder;
    private String aggregationUri;


    /**
     * A constructor with {@code bits}.
     *
     * @param bits
     *         The number of bits per Referenced Token. The allowed values
     *         for {@code bits} are 1, 2, 4 and 8.
     */
    public StatusListBuilder(int bits)
    {
        this.valuesBuilder = new StatusTypeValuesBuilder(bits);
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
    public StatusListBuilder valueAt(StatusTypeValue value, int index)
    {
        valuesBuilder.valueAt(value, index);

        return this;
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
    public StatusListBuilder valueAt(int value, int index)
    {
        valuesBuilder.valueAt(value, index);

        return this;
    }


    /**
     * Set the aggregation URL.
     *
     * @param url
     *         The URI to retrieve the Status List Aggregation. This parameter
     *         is optional.
     *
     * @return
     *         {@code this} object.
     */
    public StatusListBuilder aggregationUri(String uri)
    {
        this.aggregationUri = uri;

        return this;
    }


    /**
     * Set the capacity of the list. This method sets {@code 0} at the index of
     * {@code capacity - 1}.
     *
     * <p>
     * You can call the {@code valueAt} methods with a larger index than the
     * specified by this method. In that case, the capacity increases automatically.
     * </p>
     *
     * @param capacity
     *         The capacity of the list.
     *
     * @return
     *         {@code this} object.
     */
    public StatusListBuilder capacity(int capacity)
    {
        int index = capacity - 1;

        if (0 <= index)
        {
            valueAt(0, index);
        }

        return this;
    }


    /**
     * Construct a {@link StatusList} instance.
     *
     * @return
     *         The constructed {@link StatusList} instance.
     */
    public StatusList build()
    {
        int    bits = valuesBuilder.getBits();
        byte[] lst  = valuesBuilder.build();

        return new StatusList(bits, lst, aggregationUri);
    }
}
