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

package com.twoplaylabs.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.twoplaylabs.util.Constants
/*
    Author: Damjan Miloshevski 
    Created on 06/04/2022
    Project: betting-doctor
*/
class FirebaseManager {

    private fun retrieveFirebaseCredentials(): FirebaseCredentials {
        val serviceAccount = System.getenv(Constants.FIREBASE_TYPE)
        val projectId = System.getenv(Constants.FIREBASE_PROJECT_ID)
        val privateKeyId = System.getenv(Constants.FIREBASE_PRIVATE_KEY_ID)
        val privateKey = System.getenv(Constants.FIREBASE_PRIVATE_KEY).replace("\\n", "\n")
        val email = System.getenv(Constants.FIREBASE_CLIENT_EMAIL)
        val clientId = System.getenv(Constants.FIREBASE_CLIENT_ID)
        val authUri = System.getenv(Constants.FIREBASE_AUTH_URI)
        val tokenUri = System.getenv(Constants.FIREBASE_TOKEN_URI)
        val authProviderx509CertUrl = System.getenv(Constants.FIREBASE_AUTH_PROVIDER_x509_CERT_URL)
        val clientX509CertUrl = System.getenv(Constants.FIREBASE_CLIENT_x509_CERT_URL)

        return FirebaseCredentials(
            serviceAccount,
            projectId,
            privateKeyId,
            privateKey,
            email,
            clientId,
            authUri,
            tokenUri,
            authProviderx509CertUrl,
            clientX509CertUrl
        )
    }

    fun configureFirebase() {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(retrieveFirebaseCredentials().toInputStream()))
            .setStorageBucket(System.getenv(Constants.FIREBASE_STORAGE_BUCKET_URL))
            .build()
        FirebaseApp.initializeApp(options)
    }
}