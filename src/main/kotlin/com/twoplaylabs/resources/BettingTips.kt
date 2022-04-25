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

package com.twoplaylabs.resources


import com.twoplaylabs.util.Constants.API_PREFIX
import com.twoplaylabs.util.Constants.BETTING_TIPS_ROUTE
import com.twoplaylabs.util.Constants.ID_ROUTE
import com.twoplaylabs.util.Constants.OLDER_TIPS_BY_SPORT_ROUTE
import com.twoplaylabs.util.Constants.SPORT_ROUTE
import com.twoplaylabs.util.Constants.UPCOMING_TIPS_BY_SPORT_ROUTE
import io.ktor.resources.*
import kotlinx.serialization.Serializable

/*
    Author: Damjan Miloshevski 
    Created on 20/04/2022
    Project: betting-doctor
*/
@Serializable
@Resource(BETTING_TIPS_ROUTE)
class BettingTips{
    @Serializable
    @Resource(ID_ROUTE)
    class Id(val parent:BettingTips = BettingTips(), val id:String)
    @Serializable
    @Resource(SPORT_ROUTE)
    class Sport(val parent:BettingTips = BettingTips(),val sport:String){
        @Serializable
        @Resource(UPCOMING_TIPS_BY_SPORT_ROUTE)
        class Upcoming(val parent:Sport)
        @Serializable
        @Resource(OLDER_TIPS_BY_SPORT_ROUTE)
        class Older(val parent:Sport)
    }
}