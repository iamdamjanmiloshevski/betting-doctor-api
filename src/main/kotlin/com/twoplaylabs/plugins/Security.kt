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

package com.twoplaylabs.plugins


import com.twoplaylabs.auth.JWTService
import com.twoplaylabs.data.UserRole
import com.twoplaylabs.repository.UsersRepositoryImpl
import com.twoplaylabs.util.Constants.AUTH_CONFIG_ADMIN
import com.twoplaylabs.util.Constants.AUTH_CONFIG_ALL
import com.twoplaylabs.util.Constants.ID
import com.twoplaylabs.util.Constants.ROLE
import com.twoplaylabs.util.toUserRole
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*


/*
    Author: Damjan Miloshevski 
    Created on 23/06/2021
    Project: betting-doctor
*/
fun Application.configureSecurity(jwtService: JWTService, usersRepository: UsersRepositoryImpl) {
    install(Authentication) {
        jwt(System.getenv(AUTH_CONFIG_ALL)) {
            verifier(jwtService.verifier())
            realm = jwtService.realm()
            validate { credential ->
                if (credential.audience.contains(jwtService.audience())) {
                    val payload = credential.payload
                    val id = payload.getClaim(ID)
                    return@validate usersRepository.findUserById(id.asString())
                } else null
            }
        }
        jwt(System.getenv(AUTH_CONFIG_ADMIN)) {
            verifier(jwtService.verifier())
            realm = jwtService.realm()
            validate { credential ->
                if (credential.audience.contains(jwtService.audience())) {
                    val payload = credential.payload
                    val id = payload.getClaim(ID)
                    val roleId = payload.getClaim(ROLE)
                    if (roleId.asString().toUserRole() == UserRole.ADMIN) {
                        return@validate usersRepository.findUserById(id.asString())
                    } else null
                } else null
            }
        }
    }
}


