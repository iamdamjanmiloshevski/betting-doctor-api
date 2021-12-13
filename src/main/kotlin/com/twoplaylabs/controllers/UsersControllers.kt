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

import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.messaging.FirebaseMessaging
import com.twoplaylabs.auth.JWTService
import com.twoplaylabs.common.Message
import com.twoplaylabs.data.FeedbackMessage
import com.twoplaylabs.data.User
import com.twoplaylabs.data.UserInput
import com.twoplaylabs.repository.UsersRepository
import com.twoplaylabs.util.AuthUtil.generateAccessToken
import com.twoplaylabs.util.AuthUtil.generateWelcomeUrl
import com.twoplaylabs.util.AuthUtil.isRefreshTokenValid
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.Constants.ACCOUNT_VERIFIED_MSG
import com.twoplaylabs.util.Constants.AUTH_CONFIG_ALL
import com.twoplaylabs.util.Constants.CHANGE_PWD_ROUTE
import com.twoplaylabs.util.Constants.FEEDBACK_HTML_MESSAGE
import com.twoplaylabs.util.Constants.FEEDBACK_ROUTE
import com.twoplaylabs.util.Constants.FEEDBACK_SUCCESS_MESSAGE1
import com.twoplaylabs.util.Constants.HELLO_TEMPLATE
import com.twoplaylabs.util.Constants.ID_ROUTE
import com.twoplaylabs.util.Constants.JWT_AUDIENCE
import com.twoplaylabs.util.Constants.JWT_ID
import com.twoplaylabs.util.Constants.MISSING_REFRESH_TOKEN
import com.twoplaylabs.util.Constants.PARAM_REFRESH_TOKEN
import com.twoplaylabs.util.Constants.PASSWORD_HASH_COST
import com.twoplaylabs.util.Constants.PUSH_NOTIFICATIONS
import com.twoplaylabs.util.Constants.REFRESH_TOKEN
import com.twoplaylabs.util.Constants.REGISTER_HTML_MESSAGE
import com.twoplaylabs.util.Constants.REGISTER_HTML_MESSAGE2
import com.twoplaylabs.util.Constants.REGISTER_ROUTE
import com.twoplaylabs.util.Constants.REGISTER_SUCCESS_MESSAGE1
import com.twoplaylabs.util.Constants.SIGN_IN_VERIFICATION_MSG
import com.twoplaylabs.util.Constants.TOPIC
import com.twoplaylabs.util.Constants.UNABLE_TO_VERIFY_REFRESH_TOKEN
import com.twoplaylabs.util.Constants.USERS_ROUTE
import com.twoplaylabs.util.Constants.VERIFY_ACCOUNT_MSG
import com.twoplaylabs.util.Constants.VERIFY_ROUTE
import com.twoplaylabs.util.Constants.WELCOME
import com.twoplaylabs.util.EmailManager.sendNoReplyEmail
import com.twoplaylabs.util.JWTDecoder
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.bson.types.ObjectId
import java.util.*
import kotlin.text.toCharArray

/*
    Author: Damjan Miloshevski 
    Created on 25/06/2021
    Project: betting-doctor
*/
fun Route.usersController(repository: UsersRepository, jwtService: JWTService) {
    route(USERS_ROUTE) {
        authenticate(System.getenv(AUTH_CONFIG_ALL)) {
            getAllUsersController(repository)
            getUserByIdController(repository)
            updateUserController(repository)
            changePasswordController(repository)
            deleteUserController(repository)
            sendNotificationController()
        }
        signInController(repository, jwtService)
        registerController(repository)
        refreshTokenController(repository, jwtService)
        verifyAccountController(repository)
        signOutController(repository)
        feedbackController(repository)
    }
}

fun Route.sendNotificationController() {
    this.post(PUSH_NOTIFICATIONS) {
        val topic = call.parameters[TOPIC] ?: return@post call.respond(
            HttpStatusCode.BadRequest,
            Message("Please provide notifications topic", HttpStatusCode.BadRequest.value)
        )
        try {
            val notificationMessage = when (topic) {
                "new-tips" -> "New betting tips available!"
                else -> "Check out what's new in Betting Doctor!"
            }
            val message = com.google.firebase.messaging.Message.builder()
                .putData("message", notificationMessage)
                .setTopic(topic)
                .build()
            val response = FirebaseMessaging.getInstance().send(message)
            call.respond(HttpStatusCode.OK)
            println("Successfully send message $response")
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}


private fun Route.deleteUserController(repository: UsersRepository) {
    this@deleteUserController.delete(ID_ROUTE) {
        val parameters = call.parameters
        val id = parameters[Constants.PARAM_ID] ?: return@delete call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val deletedCount = repository.deleteUserById(id)
            if (deletedCount > 0) {
                call.respond(HttpStatusCode.OK, Message(Constants.SUCCESS, HttpStatusCode.OK.value))
            } else call.respond(
                HttpStatusCode.NoContent,
                Message(Constants.NO_USER_FOUND, HttpStatusCode.NoContent.value)
            )
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.changePasswordController(repository: UsersRepository) {
    put(CHANGE_PWD_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@put call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        val userInput = call.receive<UserInput>()
        try {
            val user = repository.findUserById(id)
            user?.let {
                val result = BCrypt.verifyer().verify(userInput.password.toCharArray(), it.hashedPassword)
                if (result.verified) {
                    userInput.newPassword?.let { newPwd ->
                        val newPassword =
                            BCrypt.withDefaults().hashToString(PASSWORD_HASH_COST, newPwd.toCharArray()).toString()
                        val modifiedCount = repository.updateUserPassword(id, newPassword)
                        if (modifiedCount > 0) {
                            call.respond(
                                HttpStatusCode.OK,
                                Message(
                                    Constants.PWD_CHANGED_SUCCESS,
                                    HttpStatusCode.OK.value
                                )
                            )
                        } else call.respond(
                            HttpStatusCode.Conflict,
                            Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.Conflict.value)
                        )
                    }
                } else call.respond(
                    HttpStatusCode.Forbidden,
                    Message(Constants.PWD_NO_MATCH, HttpStatusCode.Forbidden.value)
                )
            } ?: call.respond(
                HttpStatusCode.NotFound,
                Message(Constants.NO_USER_FOUND, HttpStatusCode.NotFound.value)
            )
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.updateUserController(repository: UsersRepository) {
    put(ID_ROUTE) {
        val parameters = call.parameters
        val userToUpdate = call.receive<User>()
        val id = parameters[Constants.PARAM_ID] ?: return@put call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val existingUser = repository.findUserById(id)
            existingUser?.let {
                val modifiedCount = repository.updateUser(userToUpdate)
                if (modifiedCount > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        Message(
                            String.format(Constants.USER_UPDATED_SUCCESSFULLY, it.email),
                            HttpStatusCode.OK.value
                        )
                    )
                } else call.respond(
                    HttpStatusCode.NoContent,
                    Message(Constants.NO_SUCH_USER_TO_UPDATE, HttpStatusCode.NoContent.value)
                )
            } ?: call.respond(
                HttpStatusCode.NotFound,
                Message(Constants.NO_USER_FOUND, HttpStatusCode.NotFound.value)
            )
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.getUserByIdController(repository: UsersRepository) {
    get(ID_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val user = repository.findUserById(id)
            user?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(
                HttpStatusCode.NotFound,
                Message(Constants.NO_USER_FOUND, HttpStatusCode.NotFound.value)
            )
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.getAllUsersController(repository: UsersRepository) {
    get {
        try {
            val users = repository.findAllUsers()
            call.respond(users)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.signOutController(repository: UsersRepository) {
    post(Constants.SIGN_OUT_ROUTE) {
        val userInput = call.receive<UserInput>()
        try {
            val user = repository.findUserByEmail(userInput.email)
            user?.let {
                call.respond(HttpStatusCode.OK, Message(Constants.SUCCESS, HttpStatusCode.OK.value))
            } ?: call.respond(HttpStatusCode.NotFound, Message(Constants.NO_USER_FOUND, HttpStatusCode.NotFound.value))
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.registerController(repository: UsersRepository) {
    post(REGISTER_ROUTE) {
        val userInput = call.receive<UserInput>()
        try {
            val userInDb = repository.findUserByEmail(userInput.email)
            if (userInDb != null) {
                call.respond(HttpStatusCode.Forbidden, String.format(Constants.USER_EXISTS, userInDb.email))
            } else {
                val hashedPassword =
                    BCrypt.withDefaults().hashToString(PASSWORD_HASH_COST, userInput.password.toCharArray()).toString()
                val id = ObjectId()
                val user = User(
                    _id = id.toString(),
                    name = userInput.name,
                    surname = userInput.surname,
                    email = userInput.email,
                    avatarUrl = userInput.avatarUrl,
                    hashedPassword = hashedPassword,
                    role = userInput.role
                )
                repository.insertUser(user)
                val verifierUrl = user._id.generateWelcomeUrl()
                val emailParameters = mapOf<String, String?>(
                    "message" to String.format(REGISTER_HTML_MESSAGE2, user.name, verifierUrl),
                    "htmlMessage" to String.format(REGISTER_HTML_MESSAGE, user.name, verifierUrl),
                    "subject" to WELCOME,
                    "to" to user.email
                )
                sendNoReplyEmail(emailParameters)
                call.respond(
                    HttpStatusCode.Created,
                    Message(
                        String.format(REGISTER_SUCCESS_MESSAGE1, user.email),
                        HttpStatusCode.Created.value
                    )
                )
            }
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}


private fun Route.refreshTokenController(repository: UsersRepository, jwtService: JWTService) {
    get(REFRESH_TOKEN) {
        val parameters = call.parameters
        val refreshToken = parameters[PARAM_REFRESH_TOKEN] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            Message(MISSING_REFRESH_TOKEN, HttpStatusCode.BadRequest.value)
        )
        try {
            val decodedJWT = jwtService.decodeJWT(refreshToken)
            val jwtArgs = JWTDecoder.decodeJWT(decodedJWT)
            val payload = jwtArgs.second
            val jti = System.getenv(JWT_ID)
            val audience = System.getenv(JWT_AUDIENCE)
            if (payload.jti != jti) {
                application.log.error("Invalid JTI")
                call.respond(
                    HttpStatusCode.Forbidden,
                    Message(UNABLE_TO_VERIFY_REFRESH_TOKEN, HttpStatusCode.Forbidden.value)
                )
            } else if (payload.aud != audience) {
                application.log.error("Invalid AUDIENCE")
                call.respond(
                    HttpStatusCode.Forbidden,
                    Message(UNABLE_TO_VERIFY_REFRESH_TOKEN, HttpStatusCode.Forbidden.value)
                )
            } else {
                val user = payload.user
                if (user.isAccountVerified) {
                    val isTokenValid = isRefreshTokenValid(payload.exp)
                    if (isTokenValid) {
                        val authenticatedUser = repository.findUserById(payload.user.id)
                        authenticatedUser?.let { authUser ->
                            if (authUser.isAccountVerified) {
                                val accessToken = generateAccessToken(authUser, jwtService)
                                val userRefreshTokens = authUser.refreshTokens
                                if (userRefreshTokens.size >= 5) {
                                    cleanupTokens(jwtService, userRefreshTokens)
                                }
                                userRefreshTokens.add(accessToken.refreshToken)
                                (0..userRefreshTokens.indexOf(accessToken.refreshToken)).forEach {
                                    Collections.swap(userRefreshTokens, 0, it)
                                }
                                authUser.refreshTokens = userRefreshTokens
                                val modifiedCount = repository.updateUser(authUser)
                                if (modifiedCount > 0) {
                                    call.respond(HttpStatusCode.OK, accessToken)
                                }
                            } else {
                                println("User not verified")
                                call.respond(
                                    HttpStatusCode.Forbidden,
                                    Message(UNABLE_TO_VERIFY_REFRESH_TOKEN, HttpStatusCode.Forbidden.value)
                                )
                            }
                        } ?: call.respond(
                            HttpStatusCode.NotFound,
                            Message(Constants.NO_USER_FOUND, HttpStatusCode.NotFound.value)
                        )
                    } else {
                        println("token expired")
                        call.respond(
                            HttpStatusCode.Forbidden,
                            Message(UNABLE_TO_VERIFY_REFRESH_TOKEN, HttpStatusCode.Forbidden.value)
                        )
                    }
                } else call.respond(
                    HttpStatusCode.Forbidden,
                    Message(UNABLE_TO_VERIFY_REFRESH_TOKEN, HttpStatusCode.Forbidden.value)
                )
            }
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun cleanupTokens(jwtService: JWTService, userRefreshTokens: MutableList<String>) {
    for (token in userRefreshTokens) {
        val decoded = jwtService.decodeJWT(token)
        val jwtArgs = JWTDecoder.decodeJWT(decoded)
        val tokenPayload = jwtArgs.second
        if (!isRefreshTokenValid(tokenPayload.exp)) {
            userRefreshTokens.remove(token)
        }
    }
}

private fun Route.verifyAccountController(repository: UsersRepository) {
    get(VERIFY_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val user = repository.findUserById(id)
            user?.let {
                val modifiedCount = repository.verifyUserAccount(id)
                if (modifiedCount > 0) {
                    call.respondHtml {
                        head {
                            title {
                                +WELCOME
                            }
                        }
                        body {
                            h3 {
                                +String.format(HELLO_TEMPLATE, it.name)
                            }
                            p {
                                +ACCOUNT_VERIFIED_MSG
                            }
                            p {
                                +SIGN_IN_VERIFICATION_MSG
                            }

                        }
                    }
                } else call.respond(
                    HttpStatusCode.Conflict,
                    Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.Conflict.value)
                )
            } ?: call.respond(
                HttpStatusCode.NotFound,
                Message(Constants.NO_USER_FOUND, HttpStatusCode.NotFound.value)
            )
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.signInController(repository: UsersRepository, jwtService: JWTService) {
    post(Constants.SIGN_IN_ROUTE) {
        val userInput = call.receive<UserInput>()
        try {
            val user = repository.findUserByEmail(userInput.email)
            user?.let { bettingDoctorUser ->
                if (bettingDoctorUser.isAccountVerified) {
                    val hashedPassword = bettingDoctorUser.hashedPassword
                    val result = BCrypt.verifyer().verify(userInput.password.toCharArray(), hashedPassword)
                    if (result.verified) {
                        val accessToken = generateAccessToken(bettingDoctorUser, jwtService)
                        bettingDoctorUser.refreshTokens.add(accessToken.refreshToken)
                        val modifiedCount = repository.updateUser(bettingDoctorUser)
                        if (modifiedCount > 0) call.respond(HttpStatusCode.OK, accessToken)
                    } else call.respond(
                        HttpStatusCode.Forbidden,
                        Message(Constants.PWD_INCORRECT, HttpStatusCode.Forbidden.value)
                    )
                } else call.respond(
                    HttpStatusCode.Forbidden,
                    Message(VERIFY_ACCOUNT_MSG, HttpStatusCode.Forbidden.value)
                )
            } ?: call.respond(
                HttpStatusCode.NotFound,
                Message(String.format(Constants.NO_USER_WITH_EMAIL, userInput.email), HttpStatusCode.NotFound.value)
            )
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.feedbackController(repository: UsersRepository) {
    post(FEEDBACK_ROUTE) {
        val feedbackMessage = call.receive<FeedbackMessage>()
        try {
            val id = ObjectId()
            feedbackMessage._id = id.toString()
            feedbackMessage.createdAt = Date()
            repository.insertFeedback(feedbackMessage)
            val emailParameters = mapOf<String, String?>(
                "message" to String.format(FEEDBACK_SUCCESS_MESSAGE1, feedbackMessage.name),
                "htmlMessage" to String.format(FEEDBACK_HTML_MESSAGE, feedbackMessage.name),
                "subject" to "Thank you",
                "to" to feedbackMessage.email
            )
            sendNoReplyEmail(emailParameters)
            call.respond(HttpStatusCode.Created, feedbackMessage)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}
