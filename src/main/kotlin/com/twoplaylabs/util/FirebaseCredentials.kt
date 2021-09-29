/*
 * MIT License
 *
 * Copyright (c) 2021 2Play Technologies Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.twoplaylabs.util

import com.google.gson.annotations.SerializedName
import java.io.InputStream

/*
    Author: Damjan Miloshevski 
    Created on 16/09/2021
    Project: betting-doctor
*/
data class FirebaseCredentials(
    @SerializedName("type")
    val type: String,
    @SerializedName("project_id")
    val project_id: String,
    @SerializedName("private_key_id")
    val private_key_id: String,
    @SerializedName("private_key")
    val private_key: String,
    @SerializedName("client_email")
    val client_email: String,
    @SerializedName("client_id")
    val client_id: String,
    @SerializedName("auth_uri")
    val auth_uri: String,
    @SerializedName("token_uri")
    val token_uri: String,
    @SerializedName("auth_provider_x509_cert_url")
    val auth_provider_x509_cert_url: String,
    @SerializedName("client_x509_cert_url")
    val client_x509_cert_url: String
)
fun FirebaseCredentials.toInputStream():InputStream{
    val json = GsonUtil.serialize(FirebaseCredentials::class.java,this)
    return json.byteInputStream()
}