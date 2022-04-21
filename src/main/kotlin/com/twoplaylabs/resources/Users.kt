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

import io.ktor.resources.*
import kotlinx.serialization.Serializable

/*
    Author: Damjan Miloshevski 
    Created on 21/04/2022
    Project: betting-doctor
*/
@Serializable
@Resource("/api/v1//users")
class Users(val email:String? = null){
    @Serializable
    @Resource("signin")
    class SignIn(val parent: Users)

    @Serializable
    @Resource("register")
    class SignUp(val parent: Users)

    @Serializable
    @Resource("token")
    class RefreshToken(val parent: Users)

    @Serializable
    @Resource("verify")
    class VerifyAccount(val parent: Users) {
        @Serializable
        @Resource("{id}")
        class Id(val id: String)
    }

    @Serializable
    @Resource("signout")
    class SignOut(val parent: Users)

    @Serializable
    @Resource("feedback")
    class Feedback(val parent: Users)

    @Serializable
    @Resource("{id}")
    class Id(val parent: Users,val id: String) {
        @Serializable
        @Resource("change-password")
        class ChangePassword(val parent:Id)
    }

    @Serializable
    @Resource("notifications")
    class Notifications(val parent: Users)
}