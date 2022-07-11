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


import com.twoplaylabs.util.convertIfSoccer
import junit.framework.Assert.assertEquals
import org.junit.Test


/*
    Author: Damjan Miloshevski 
    Created on 19/06/2021
    Project: betting-doctor
    © 2Play Technologies  2021. All rights reserved
*/
class RoutingUtilsTest {
    @Test
    fun convertIfSoccer_sportSoccerNotCapitalized_returnsFootball(){
        val sport = "soccer"
        assertEquals("football",sport.convertIfSoccer())
    }

    @Test
    fun convertIfSoccer_sportSoccerCapitalized_returnsFootball(){
        val sport = "Soccer"
        assertEquals("Football",sport.convertIfSoccer())
    }

    @Test
    fun convertIfSoccer_sportElse_returnsElse(){
        val sport = "basketball"
        assertEquals("basketball",sport.convertIfSoccer())
    }
}