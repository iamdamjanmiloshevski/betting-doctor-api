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
import com.twoplaylabs.data.*
import com.twoplaylabs.repository.TokensRepository
import com.twoplaylabs.repository.UsersRepository
import com.twoplaylabs.util.AuthUtil.generateAccessToken
import com.twoplaylabs.util.AuthUtil.generateWelcomeUrl
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.Constants.ACCOUNT_VERIFIED_MSG
import com.twoplaylabs.util.Constants.AUTH_CONFIG_ADMIN
import com.twoplaylabs.util.Constants.AUTH_CONFIG_ALL
import com.twoplaylabs.util.Constants.CHANGE_PWD_ROUTE
import com.twoplaylabs.util.Constants.CHANNEL
import com.twoplaylabs.util.Constants.CHANNEL_ID
import com.twoplaylabs.util.Constants.FEEDBACK_HTML_MESSAGE
import com.twoplaylabs.util.Constants.FEEDBACK_ROUTE
import com.twoplaylabs.util.Constants.FEEDBACK_SUCCESS_MESSAGE1
import com.twoplaylabs.util.Constants.HELLO_TEMPLATE
import com.twoplaylabs.util.Constants.ID_ROUTE
import com.twoplaylabs.util.Constants.MESSAGE
import com.twoplaylabs.util.Constants.NOTIFICATION_CHANNEL_TICKET
import com.twoplaylabs.util.Constants.NOTIFICATION_CHANNEL_TIPS
import com.twoplaylabs.util.Constants.NOTIFICATION_TICKET_MSG
import com.twoplaylabs.util.Constants.NOTIFICATION_TIPS_MSG
import com.twoplaylabs.util.Constants.PARAM_EMAIL
import com.twoplaylabs.util.Constants.PASSWORD_HASH_COST
import com.twoplaylabs.util.Constants.PUSH_NOTIFICATIONS
import com.twoplaylabs.util.Constants.REFRESH_TOKEN
import com.twoplaylabs.util.Constants.REGISTER_HTML_MESSAGE
import com.twoplaylabs.util.Constants.REGISTER_HTML_MESSAGE2
import com.twoplaylabs.util.Constants.REGISTER_ROUTE
import com.twoplaylabs.util.Constants.REGISTER_SUCCESS_MESSAGE
import com.twoplaylabs.util.Constants.SEARCH_ROUTE
import com.twoplaylabs.util.Constants.SIGN_IN_VERIFICATION_MSG
import com.twoplaylabs.util.Constants.TOKENS_ROUTE
import com.twoplaylabs.util.Constants.TOKEN_DISABLED_SUCCESS
import com.twoplaylabs.util.Constants.TOKEN_REJECT_ROUTE
import com.twoplaylabs.util.Constants.TOPIC
import com.twoplaylabs.util.Constants.USERS_ROUTE
import com.twoplaylabs.util.Constants.VERIFY_ACCOUNT_MSG
import com.twoplaylabs.util.Constants.VERIFY_ROUTE
import com.twoplaylabs.util.Constants.WELCOME
import com.twoplaylabs.util.EmailManager.sendNoReplyEmail
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.bson.types.ObjectId
import java.lang.Exception
import java.util.*
import kotlin.text.toCharArray

/*
    Author: Damjan Miloshevski 
    Created on 25/06/2021
    Project: betting-doctor
*/


fun Route.usersRouter(repository: UsersRepository, tokensRepository: TokensRepository, jwtService: JWTService) {
    route(USERS_ROUTE) {
        authenticate(System.getenv(AUTH_CONFIG_ALL)) {
            getAllUsers(repository)
            getUserById(repository)
            getUserByEmail(repository)
            updateUser(repository)
            changePassword(repository)
            deleteUser(repository)
            sendNotification()
        }
        authenticate(System.getenv(AUTH_CONFIG_ADMIN)) {
            rejectToken(tokensRepository)
            getAllTokens(tokensRepository)
        }
        signIn(repository, tokensRepository, jwtService)
        register(repository)
        refreshToken(repository, tokensRepository, jwtService)
        verifyAccount(repository)
        signOut(repository)
        feedback(repository)
    }
}

fun Route.getUserByEmail(repository: UsersRepository) {
    get(SEARCH_ROUTE) {
        val email = call.parameters[PARAM_EMAIL] ?: return@get call.respond(
            HttpStatusCode.BadRequest, Message(Constants.MISSING_EMAIL, HttpStatusCode.BadRequest.value)
        )
        try {
            val user = repository.findUserByEmail(email)
            user?.let {
                call.respond(HttpStatusCode.OK, user)
            } ?: call.respond(
                HttpStatusCode.NotFound,
                Message("User with email $email not found", HttpStatusCode.NotFound.value)
            )
        } catch (e: Exception) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

fun Route.getAllTokens(tokensRepository: TokensRepository) {
    get(TOKENS_ROUTE) {
        try {
            val tokens = tokensRepository.findAllTokens()
            call.respond(tokens)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

fun Route.sendNotification() {
    this.post(PUSH_NOTIFICATIONS) {
        val notification = call.receive<Notification>()
        try {
            var notificationMessage: String? = ""
            var channelId: String? = ""
            var channel: String? = ""
            when (notification.topic) {
                NotificationTopic.Tips -> {
                    notificationMessage = NOTIFICATION_TIPS_MSG
                    channelId = NOTIFICATION_CHANNEL_TIPS
                    channel = NOTIFICATION_CHANNEL_TIPS
                }
                NotificationTopic.Ticket -> {
                    notificationMessage = NOTIFICATION_TICKET_MSG
                    channelId =NOTIFICATION_CHANNEL_TICKET
                    channel = NOTIFICATION_CHANNEL_TICKET
                }
            }
            val topic = notification.topic.value
            val message = com.google.firebase.messaging.Message.builder()
                .putAllData(
                    mapOf(
                        TOPIC to topic,
                        MESSAGE to notificationMessage,
                        CHANNEL to channel,
                        CHANNEL_ID to channelId
                    )
                )
                .setTopic(topic)
                .build()
            FirebaseMessaging.getInstance().send(message)
            call.respond(
                HttpStatusCode.OK, Notification(notification.topic, notificationMessage, channel, channelId)
            )
            println("Successfully sent message to topic $topic")
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}


private fun Route.deleteUser(repository: UsersRepository) {
    this@deleteUser.delete(ID_ROUTE) {
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

private fun Route.changePassword(repository: UsersRepository) {
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

private fun Route.updateUser(repository: UsersRepository) {
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

private fun Route.getUserById(repository: UsersRepository) {
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

private fun Route.getAllUsers(repository: UsersRepository) {
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

private fun Route.signOut(repository: UsersRepository) {
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

private fun Route.register(repository: UsersRepository) {
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
                        String.format(REGISTER_SUCCESS_MESSAGE, user.email),
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

private fun Route.rejectToken(tokensRepository: TokensRepository) {
    post(TOKEN_REJECT_ROUTE) {
        val refreshToken = call.receive<RefreshToken>()
        try {
            val userEmail = refreshToken.userEmail
            val token = refreshToken.token
            val modifiedCount = tokensRepository.deleteToken(userEmail, token)
            if (modifiedCount > 0) {
                call.respond(
                    HttpStatusCode.OK,
                    Message(
                        TOKEN_DISABLED_SUCCESS,
                        HttpStatusCode.OK.value
                    )
                )
            } else call.respond(HttpStatusCode.Unauthorized)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.refreshToken(
    repository: UsersRepository,
    tokensRepository: TokensRepository,
    jwtService: JWTService
) {
    post(REFRESH_TOKEN) {
        val refreshToken = call.receive<RefreshToken>()
        try {
            val userEmail = refreshToken.userEmail
            val token = refreshToken.token
            val user = repository.findUserByEmail(userEmail)
            user?.let { bettingDoctorUser ->
                if (bettingDoctorUser.isAccountVerified) {
                    val userToken = tokensRepository.findTokensByEmail(userEmail)
                    userToken?.let {
                        if (it.tokens.contains(token)) {
                            val accessToken = generateAccessToken(bettingDoctorUser, jwtService)
                            it.tokens.add(accessToken.refreshToken)
                            val updatedCount = tokensRepository.updateToken(it)
                            println("Updated count $updatedCount")
                            if (updatedCount > 0) call.respond(HttpStatusCode.OK, accessToken) else call.respond(
                                HttpStatusCode.NotFound
                            )
                        } else call.respond(HttpStatusCode.Unauthorized)
                    } ?: call.respond(HttpStatusCode.NoContent)
                } else call.respond(
                    HttpStatusCode.Forbidden,
                    Message(VERIFY_ACCOUNT_MSG, HttpStatusCode.Forbidden.value)
                )
            } ?: call.respond(
                HttpStatusCode.NotFound,
                Message(
                    String.format(Constants.NO_USER_WITH_EMAIL, userEmail),
                    HttpStatusCode.NotFound.value
                )
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

private fun Route.verifyAccount(repository: UsersRepository) {
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

private fun Route.signIn(
    repository: UsersRepository,
    tokensRepository: TokensRepository,
    jwtService: JWTService
) {
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
                        tokensRepository.insertToken(bettingDoctorUser.email, accessToken.refreshToken)
                        call.respond(HttpStatusCode.OK, accessToken)
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

private fun Route.feedback(repository: UsersRepository) {
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
