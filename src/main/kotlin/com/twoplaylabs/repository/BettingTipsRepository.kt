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

package com.twoplaylabs.repository

import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.Ticket
import com.twoplaylabs.data.User
import org.bson.BsonObjectId
import org.bson.BsonValue
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 18/06/2021

    Project: betting-doctor
*/
interface BettingTipsRepository {
    suspend fun insertBettingTip(bettingTip: BettingTip)

    suspend fun findAllBettingTips(): List<BettingTip>
    suspend fun findBettingTipById(id: String): BettingTip?
    suspend fun findBettingTipsBySport(sport: String, upcoming: Boolean): List<BettingTip>

    suspend fun updateBettingTip(bettingTip: BettingTip): Long

    suspend fun deleteAllBettingTips(): Long
    suspend fun deleteBettingTip(id: String): Long


}