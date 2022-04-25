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

import com.twoplaylabs.util.Constants.CHANGE_PWD_ROUTE
import com.twoplaylabs.util.Constants.FEEDBACK_ROUTE
import com.twoplaylabs.util.Constants.ID_ROUTE
import com.twoplaylabs.util.Constants.PUSH_NOTIFICATIONS_ROUTE
import com.twoplaylabs.util.Constants.REFRESH_TOKEN_ROUTE
import com.twoplaylabs.util.Constants.REGISTER_ROUTE
import com.twoplaylabs.util.Constants.SIGN_IN_ROUTE
import com.twoplaylabs.util.Constants.SIGN_OUT_ROUTE
import com.twoplaylabs.util.Constants.USERS_ROUTE
import com.twoplaylabs.util.Constants.VERIFY_ROUTE
import io.ktor.resources.*
import kotlinx.serialization.Serializable

/*
    Author: Damjan Miloshevski 
    Created on 21/04/2022
    Project: betting-doctor
*/
@Serializable
@Resource(USERS_ROUTE)
class Users(val email:String? = null){
    @Serializable
    @Resource(SIGN_IN_ROUTE)
    class SignIn(val parent: Users)

    @Serializable
    @Resource(REGISTER_ROUTE)
    class SignUp(val parent: Users)

    @Serializable
    @Resource(REFRESH_TOKEN_ROUTE)
    class RefreshToken(val parent: Users)

    @Serializable
    @Resource(VERIFY_ROUTE)
    class VerifyAccount(val parent: Users) {
        @Serializable
        @Resource(ID_ROUTE)
        class Id(val parent: VerifyAccount,val id: String)
    }

    @Serializable
    @Resource(SIGN_OUT_ROUTE)
    class SignOut(val parent: Users)

    @Serializable
    @Resource(FEEDBACK_ROUTE)
    class Feedback(val parent: Users)

    @Serializable
    @Resource(ID_ROUTE)
    class Id(val parent: Users,val id: String) {
        @Serializable
        @Resource(CHANGE_PWD_ROUTE)
        class ChangePassword(val parent:Id)
    }

    @Serializable
    @Resource(PUSH_NOTIFICATIONS_ROUTE)
    class Notifications(val parent: Users)
}