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

import com.twoplaylabs.data.FeedbackMessage
import com.twoplaylabs.data.User
import org.litote.kmongo.*

/*
    Author: Damjan Miloshevski 
    Created on 20/06/2021
    Project: betting-doctor
*/
class UsersRepositoryImpl :UsersRepository,BaseRepository() {
    override suspend fun insertUser(user: User) {
        usersCollection.insertOne(user)
    }

    override suspend fun findUserByEmail(email: String): User? {
        return usersCollection.findOne(User::email eq email)
    }

    override suspend fun findUserById(id: String): User? {
        return usersCollection.findOne(User::_id eq id)
    }

    override suspend fun findAllUsers(): List<User> {
        return usersCollection.find().toList()
    }

    override suspend fun updateUser(user: User): Long {
        val request = usersCollection.updateOne(
            User::email eq user.email,
            set(
                User::name setTo user.name,
                User::surname setTo user.surname,
                User::avatarUrl setTo user.avatarUrl
            )
        )
        return request.modifiedCount
    }

    override suspend fun updateUserPassword(id: String, hashedPassword: String): Long {
        val request = usersCollection.updateOne(User::_id eq id, setValue(User::hashedPassword, hashedPassword))
        return request.modifiedCount
    }

    override suspend fun verifyUserAccount(id: String): Long {
        val request = usersCollection.updateOne(User::_id eq id, setValue(User::isAccountVerified, true))
        return request.modifiedCount
    }

    override suspend fun deleteUserById(id: String): Long {
        val request = usersCollection.deleteOne(User::_id eq id)
        return request.deletedCount
    }

    override suspend fun deleteAllUsers(): Long {
        val request = usersCollection.deleteMany("{}")
        return request.deletedCount
    }

    override suspend fun insertFeedback(feedbackMessage: FeedbackMessage) {
        feedbacksCollection.insertOne(feedbackMessage)
    }
}