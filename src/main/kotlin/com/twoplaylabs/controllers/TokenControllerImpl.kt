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

import com.twoplaylabs.data.Token
import com.twoplaylabs.repository.TokensRepository
import org.koin.java.KoinJavaComponent.inject

/*
    Author: Damjan Miloshevski 
    Created on 06/04/2022
    Project: betting-doctor
*/
class TokenControllerImpl(private val repository: TokensRepository) : TokenController {
    override suspend fun insertToken(userEmail: String, token: String) = repository.insertToken(userEmail, token)

    override suspend fun updateToken(token: Token): Long = repository.updateToken(token)

    override suspend fun findTokensByEmail(email: String): Token? = repository.findTokensByEmail(email)

    override suspend fun findAllTokens(): List<Token> = repository.findAllTokens()

    override suspend fun deleteToken(userEmail: String, token: String): Long = repository.deleteToken(userEmail, token)

    override suspend fun deleteAllTokens(): Long = repository.deleteAllTokens()
}