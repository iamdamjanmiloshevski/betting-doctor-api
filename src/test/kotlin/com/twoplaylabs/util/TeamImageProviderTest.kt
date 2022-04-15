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

import com.twoplaylabs.data.Team
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import junit.framework.Assert.assertEquals
import org.junit.Test

/*
    Author: Damjan Miloshevski 
    Created on 14/09/2021
    Project: betting-doctor
*/
internal class teamImageProviderTest{
    private val teamImageProvider = TeamImageProvider(HttpClient(Apache))
    @Test
    fun generateImageName_sportTennisLowerCase_returnsCorrectName() {
        val expected = "tennis/NovakDjokovic.jpeg"
        val teamMock = Team("Novak Djokovic","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"tennis"))
    }
    @Test
    fun generateImageName_sportTennisUpperCase_returnsCorrectName() {
        val expected = "tennis/NovakDjokovic.jpeg"
        val teamMock = Team("Novak Djokovic","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"Tennis"))
    }
    @Test
    fun generateImageName_sportFootballUpperCase_returnsCorrectName() {
        val expected = "football/ManchesterUnited.jpeg"
        val teamMock = Team("Manchester United","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"Football"))
    }
    @Test
    fun generateImageName_sportFootballLowerCase_returnsCorrectName() {
        val expected = "football/ManchesterUnited.jpeg"
        val teamMock = Team("Manchester United","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"football"))
    }
    @Test
    fun generateImageName_sportSoccerUpperCase_returnsCorrectName() {
        val expected = "football/ManchesterUnited.jpeg"
        val teamMock = Team("Manchester United","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"Soccer"))
    }
    @Test
    fun generateImageName_sportSoccerLowerCase_returnsCorrectName() {
        val expected = "football/ManchesterUnited.jpeg"
        val teamMock = Team("Manchester United","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"soccer"))
    }
    @Test
    fun generateImageName_sportOtherUpperCase_returnsCorrectName() {
        val expected = "basketball/NewYorkKnicks.jpeg"
        val teamMock = Team("New York Knicks","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"Basketball"))
    }
    @Test
    fun generateImageName_sportOtherLowerCase_returnsCorrectName() {
        val expected = "basketball/NewYorkKnicks.jpeg"
        val teamMock = Team("New York Knicks","")
        assertEquals(expected,teamImageProvider.generateImageName(team = teamMock,"basketball"))
    }
}