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

package com.twoplaylabs.controllers

import com.twoplaylabs.data.FeedbackMessage
import com.twoplaylabs.data.User
import com.twoplaylabs.repository.UsersRepository
import org.koin.java.KoinJavaComponent.inject

/*
    Author: Damjan Miloshevski 
    Created on 05/04/2022
    Project: betting-doctor
*/
class UserControllerImpl(private val repository: UsersRepository):UserController {
    override suspend fun insertUser(user: User) = repository.insertUser(user)

    override suspend fun insertFeedback(feedbackMessage: FeedbackMessage) = repository.insertFeedback(feedbackMessage)

    override suspend fun updateUser(user: User): Long = repository.updateUser(user)
    override suspend fun updateUserPassword(id: String, hashedPassword: String): Long = repository.updateUserPassword(id,hashedPassword)

    override suspend fun verifyUserAccount(id: String): Long = repository.verifyUserAccount(id)

    override suspend fun findUserByEmail(email: String): User?  = repository.findUserByEmail(email)

    override suspend fun findUserById(id: String): User? = repository.findUserById(id)

    override suspend fun findAllUsers(): List<User>  = repository.findAllUsers()

    override suspend fun deleteUserById(id: String): Long = repository.deleteUserById(id)

    override suspend fun deleteAllUsers(): Long  = repository.deleteAllUsers()
}