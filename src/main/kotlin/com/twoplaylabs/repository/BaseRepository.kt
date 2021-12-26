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
import com.twoplaylabs.data.FeedbackMessage
import com.twoplaylabs.data.Ticket
import com.twoplaylabs.data.User
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.Constants.BETTING_TIPS_COLLECTION
import com.twoplaylabs.util.Constants.DB_CONNECTION_URL
import com.twoplaylabs.util.Constants.FEEDBACKS_COLLECTION
import com.twoplaylabs.util.Constants.TICKETS_COLLECTION
import com.twoplaylabs.util.Constants.USERS_COLLECTION
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

/*
    Author: Damjan Miloshevski 
    Created on 20/06/2021
    Project: betting-doctor
*/
abstract class BaseRepository {
    private val client = KMongo.createClient(System.getenv(DB_CONNECTION_URL))
    private val database = client.getDatabase(Constants.DB_NAME)
    protected val bettingTipsCollection = database.getCollection<BettingTip>(BETTING_TIPS_COLLECTION)
    protected val usersCollection = database.getCollection<User>(USERS_COLLECTION)
    protected val feedbacksCollection = database.getCollection<FeedbackMessage>(FEEDBACKS_COLLECTION)
    protected val ticketsCollection = database.getCollection<Ticket>(TICKETS_COLLECTION)
}