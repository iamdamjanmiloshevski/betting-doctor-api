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

import com.google.cloud.storage.*
import com.google.firebase.cloud.StorageClient
import com.twoplaylabs.data.Team
import com.twoplaylabs.data.sports.SportsApiData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.bits.*
import java.io.ByteArrayInputStream
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

/*
    Author: Damjan Miloshevski 
    Created on 14/09/2021
    Project: betting-doctor
*/
object TeamImageProvider {
    private val bucket = StorageClient.getInstance().bucket()
    val IMAGE_LINK_VALIDATION_DAYS = ceil((20 * 365).toDouble()).toLong()

    suspend fun getTeamImageUrl(team: Team, sport: String, sportsApiData: SportsApiData): String {
        val imageName = generateImageName(team, sport)
        val blobInfo = generateImageBlobInfo(imageName)
        val blob = bucket.get(blobInfo.name, Storage.BlobGetOption.shouldReturnRawInputStream(false))
        return if (blob != null) {
            val url = bucket.get(imageName).signUrl(IMAGE_LINK_VALIDATION_DAYS, TimeUnit.DAYS).toString()
            println("Image exists. Returning existing url $url")
            url
        } else {
            println("Image doesn't exist. Downloading and saving to Firebase")
            val logoByteArray = downloadImage(sportsApiData)
            saveImageToFirebaseAndReturnUrl(imageName, logoByteArray)
        }
    }


    fun generateImageName(team: Team, sport: String): String {
        val name = team.name.replace("\\s+".toRegex(), "")
        return when (sport) {
            "Soccer", "soccer" -> "football/".plus(name).plus(".jpeg")
            else -> sport.lowercase(Locale.getDefault()).plus("/").plus(name).plus(".jpeg")
        }
    }

    private fun saveImageToFirebaseAndReturnUrl(teamName: String, byteArray: ByteArray): String {
        val bais = ByteArrayInputStream(byteArray)
        val blobInfo = generateImageBlobInfo(teamName)
        bucket.create(blobInfo.name, bais, Bucket.BlobWriteOption.doesNotExist())
        val fileUrl = bucket.get(blobInfo.name).signUrl(IMAGE_LINK_VALIDATION_DAYS, TimeUnit.DAYS)
        println("Uploaded to ${fileUrl}")
        return fileUrl.toString()
    }

    private suspend fun downloadImage(team: SportsApiData): ByteArray {
        val httpClient = HttpClient()
        val statement = httpClient.request<HttpStatement>(team.getLogo())
        return statement.execute {
            val contentLength = it.contentLength()?.lowInt ?: 0
            val byteArray = ByteArray(contentLength)
            var offset = 0
            do {
                val currentRead = it.content.readAvailable(byteArray, offset, byteArray.size)
                offset += currentRead
                println("Download in progress, offset: ${offset}, current read ${currentRead} / ${contentLength}")
            } while (offset < contentLength)
            println("Download done")
            return@execute byteArray
        }
    }

    private fun generateImageBlobInfo(name: String): BlobInfo =
        BlobInfo.newBuilder(BlobId.of(bucket.name, name)).setContentType("image/jpeg").build()
}