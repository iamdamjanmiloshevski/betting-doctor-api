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
import org.litote.kmongo.*
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 18/06/2021
    Project: betting-doctor
*/
class BettingTipsRepositoryImpl : BettingTipsRepository, BaseRepository() {
    override suspend fun insertBettingTip(bettingTip: BettingTip) {
        bettingTipsCollection.insertOne(bettingTip)
    }

    override suspend fun findAllBettingTips(): List<BettingTip> {
        val items = bettingTipsCollection.find()
        return items.toList()
    }

    override suspend fun findBettingTipById(id: String): BettingTip? {
        return bettingTipsCollection.findOneById(id)
    }

    override suspend fun findBettingTipsBySport(sport: String, upcoming: Boolean): List<BettingTip> {
        val currentDate = Date()
        return if (upcoming) {
            val items =
                bettingTipsCollection.find(and(BettingTip::sport eq sport, BettingTip::gameTime gte currentDate))
                    .sort(ascending(BettingTip::gameTime))
            items.toList()
        } else {
            val items = bettingTipsCollection.find(and(BettingTip::sport eq sport, BettingTip::gameTime lt currentDate))
                .sort(ascending(BettingTip::gameTime))
            items.toList()
        }
    }

    override suspend fun updateBettingTip(bettingTip: BettingTip): Long {
        val request = bettingTipsCollection.updateOne(
            BettingTip::_id eq bettingTip._id, set(
                BettingTip::bettingType setTo bettingTip.bettingType,
                BettingTip::gameTime setTo bettingTip.gameTime,
                BettingTip::result setTo bettingTip.result,
                BettingTip::sport setTo bettingTip.sport,
                BettingTip::status setTo bettingTip.status,
                BettingTip::teamAway setTo bettingTip.teamAway,
                BettingTip::teamHome setTo bettingTip.teamHome,
                BettingTip::leagueName setTo bettingTip.leagueName
            )
        )
        return request.modifiedCount
    }

    override suspend fun deleteBettingTip(id: String): Long {
        val request = bettingTipsCollection.deleteOne(BettingTip::_id eq id)
        return request.deletedCount
    }

    override suspend fun deleteAllBettingTips(): Long {
        val request = bettingTipsCollection.deleteMany("{}")
        return request.deletedCount
    }
}