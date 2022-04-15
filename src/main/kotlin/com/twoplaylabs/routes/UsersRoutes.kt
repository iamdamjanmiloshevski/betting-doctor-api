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

package com.twoplaylabs.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.messaging.FirebaseMessaging
import com.twoplaylabs.controllers.*
import com.twoplaylabs.data.*
import com.twoplaylabs.data.common.Message
import com.twoplaylabs.util.AuthUtil
import com.twoplaylabs.util.AuthUtil.generateWelcomeUrl
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.EmailManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.bson.types.ObjectId
import org.koin.java.KoinJavaComponent.inject
import java.lang.Exception
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 05/04/2022
    Project: betting-doctor
*/
fun Route.userController(controller: UserController, tokenController: TokenController) {
    val emailManager by inject<EmailManager>(EmailManager::class.java)
    route(Constants.USERS_ROUTE) {
        authenticate(System.getenv(Constants.AUTH_CONFIG_ALL)) {
            getAllUsers(controller)
            getUserById(controller)
            getUserByEmail(controller)
            updateUser(controller)
            changePassword(controller)
            deleteUser(controller)
            sendPushNotifications()
        }
        signIn(controller, tokenController)
        signUp(controller, emailManager)
        refreshToken(controller, tokenController)
        verifyAccount(controller)
        signOut(controller)
        sendFeedback(controller,emailManager)
    }
}

private fun Route.sendFeedback(controller: UserController,emailManager: EmailManager) {
    post(Constants.FEEDBACK_ROUTE) {
        val feedbackMessage = call.receive<FeedbackMessage>()
        try {
            val id = ObjectId()
            val feedbackMsgUpdated = feedbackMessage.copy(_id = id.toString(), createdAt = Date())
            controller.insertFeedback(feedbackMsgUpdated)
            val emailParameters = mapOf<String, String?>(
                "message" to String.format(Constants.FEEDBACK_SUCCESS_MESSAGE1, feedbackMsgUpdated.name),
                "htmlMessage" to String.format(Constants.FEEDBACK_HTML_MESSAGE, feedbackMsgUpdated.name),
                "subject" to "Thank you",
                "to" to feedbackMsgUpdated.email
            )
            emailManager.sendNoReplyEmail(emailParameters)
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

private fun Route.signOut(controller: UserController) {
    post(Constants.SIGN_OUT_ROUTE) {
        val userInput = call.receive<UserInput>()
        try {
            val user = controller.findUserByEmail(userInput.email)
            user?.let {
                call.respond(HttpStatusCode.OK, Message(Constants.SUCCESS, HttpStatusCode.OK.value))
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

private fun Route.verifyAccount(controller: UserController) {
    get(Constants.VERIFY_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val user = controller.findUserById(id)
            user?.let {
                val modifiedCount = controller.verifyUserAccount(id)
                if (modifiedCount > 0) {
                    call.respondHtml {
                        head {
                            title {
                                +Constants.WELCOME
                            }
                        }
                        body {
                            h3 {
                                +String.format(Constants.HELLO_TEMPLATE, it.name)
                            }
                            p {
                                +Constants.ACCOUNT_VERIFIED_MSG
                            }
                            p {
                                +Constants.SIGN_IN_VERIFICATION_MSG
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

private fun Route.refreshToken(
    controller: UserController,
    tokenController: TokenController
) {
    val authUtil by inject<AuthUtil>(AuthUtil::class.java)
    post(Constants.REFRESH_TOKEN) {
        val refreshToken = call.receive<RefreshToken>()
        try {
            val userEmail = refreshToken.userEmail
            val token = refreshToken.token
            val user = controller.findUserByEmail(userEmail)
            user?.let { bettingDoctorUser ->
                if (bettingDoctorUser.isAccountVerified) {
                    val userToken = tokenController.findTokensByEmail(userEmail)
                    userToken?.let {
                        if (it.tokens.contains(token)) {
                            val accessToken = authUtil.generateAccessToken(bettingDoctorUser)
                            it.tokens.add(accessToken.refreshToken)
                            val updatedCount = tokenController.updateToken(it)
                            println("Updated count $updatedCount")
                            if (updatedCount > 0) call.respond(HttpStatusCode.OK, accessToken) else call.respond(
                                HttpStatusCode.NotFound
                            )
                        } else call.respond(HttpStatusCode.Unauthorized)
                    } ?: call.respond(HttpStatusCode.NoContent)
                } else call.respond(
                    HttpStatusCode.Forbidden,
                    Message(Constants.VERIFY_ACCOUNT_MSG, HttpStatusCode.Forbidden.value)
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

private fun Route.signUp(
    controller: UserController,
    emailManager: EmailManager
) {
    post(Constants.REGISTER_ROUTE) {
        val userInput = call.receive<UserInput>()
        try {
            val userInDb = controller.findUserByEmail(userInput.email)
            if (userInDb != null) {
                call.respond(HttpStatusCode.Forbidden, String.format(Constants.USER_EXISTS, userInDb.email))
            } else {
                val hashedPassword =
                    BCrypt.withDefaults()
                        .hashToString(Constants.PASSWORD_HASH_COST, userInput.password.toCharArray()).toString()
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
                controller.insertUser(user)
                val verifierUrl = user._id.generateWelcomeUrl()
                val emailParameters = mapOf<String, String?>(
                    "message" to String.format(Constants.REGISTER_HTML_MESSAGE2, user.name, verifierUrl),
                    "htmlMessage" to String.format(Constants.REGISTER_HTML_MESSAGE, user.name, verifierUrl),
                    "subject" to Constants.WELCOME,
                    "to" to user.email
                )
                emailManager.sendNoReplyEmail(emailParameters)
                call.respond(
                    HttpStatusCode.Created,
                    Message(
                        String.format(Constants.REGISTER_SUCCESS_MESSAGE, user.email),
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

private fun Route.signIn(
    controller: UserController,
    tokenController: TokenController
) {
    val authUtil by inject<AuthUtil>(AuthUtil::class.java)
    post(Constants.SIGN_IN_ROUTE) {
        val userInput = call.receive<UserInput>()
        try {
            val user = controller.findUserByEmail(userInput.email)
            user?.let { bettingDoctorUser ->
                if (bettingDoctorUser.isAccountVerified) {
                    val hashedPassword = bettingDoctorUser.hashedPassword
                    val result = BCrypt.verifyer().verify(userInput.password.toCharArray(), hashedPassword)
                    if (result.verified) {
                        val accessToken = authUtil.generateAccessToken(bettingDoctorUser)
                        tokenController.insertToken(bettingDoctorUser.email, accessToken.refreshToken)
                        call.respond(HttpStatusCode.OK, accessToken)
                    } else call.respond(
                        HttpStatusCode.Forbidden,
                        Message(Constants.PWD_INCORRECT, HttpStatusCode.Forbidden.value)
                    )
                } else call.respond(
                    HttpStatusCode.Forbidden,
                    Message(Constants.VERIFY_ACCOUNT_MSG, HttpStatusCode.Forbidden.value)
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

private fun Route.sendPushNotifications() {
    post(Constants.PUSH_NOTIFICATIONS) {
        val notification = call.receive<Notification>()
        try {
            var notificationMessage: String? = ""
            var channelId: String? = ""
            var channel: String? = ""
            when (notification.topic) {
                NotificationTopic.Tips -> {
                    notificationMessage = Constants.NOTIFICATION_TIPS_MSG
                    channelId = Constants.NOTIFICATION_CHANNEL_TIPS
                    channel = Constants.NOTIFICATION_CHANNEL_TIPS
                }
                NotificationTopic.Ticket -> {
                    notificationMessage = Constants.NOTIFICATION_TICKET_MSG
                    channelId = Constants.NOTIFICATION_CHANNEL_TICKET
                    channel = Constants.NOTIFICATION_CHANNEL_TICKET
                }
            }
            val topic = notification.topic.value
            val message = com.google.firebase.messaging.Message.builder()
                .putAllData(
                    mapOf(
                        Constants.TOPIC to topic,
                        Constants.MESSAGE to notificationMessage,
                        Constants.CHANNEL to channel,
                        Constants.CHANNEL_ID to channelId
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

private fun Route.deleteUser(controller: UserController) {
    delete(Constants.ID_ROUTE) {
        val parameters = call.parameters
        val id = parameters[Constants.PARAM_ID] ?: return@delete call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val deletedCount = controller.deleteUserById(id)
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

private fun Route.changePassword(controller: UserController) {
    put(Constants.CHANGE_PWD_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@put call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        val userInput = call.receive<UserInput>()
        try {
            val user = controller.findUserById(id)
            user?.let {
                val result = BCrypt.verifyer().verify(userInput.password.toCharArray(), it.hashedPassword)
                if (result.verified) {
                    userInput.newPassword?.let { newPwd ->
                        val newPassword =
                            BCrypt.withDefaults()
                                .hashToString(Constants.PASSWORD_HASH_COST, newPwd.toCharArray()).toString()
                        val modifiedCount = controller.updateUserPassword(id, newPassword)
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

private fun Route.updateUser(controller: UserController) {
    put(Constants.ID_ROUTE) {
        val parameters = call.parameters
        val userToUpdate = call.receive<User>()
        val id = parameters[Constants.PARAM_ID] ?: return@put call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val existingUser = controller.findUserById(id)
            existingUser?.let {
                val modifiedCount = controller.updateUser(userToUpdate)
                if (modifiedCount > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        Message(
                            String.format(Constants.USER_UPDATED_SUCCESSFULLY, InputType.email),
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

private fun Route.getUserByEmail(controller: UserController) {
    get(Constants.SEARCH_ROUTE) {
        val email = call.parameters[Constants.PARAM_EMAIL] ?: return@get call.respond(
            HttpStatusCode.BadRequest, Message(Constants.MISSING_EMAIL, HttpStatusCode.BadRequest.value)
        )
        try {
            val user = controller.findUserByEmail(email)
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

private fun Route.getUserById(controller: UserController) {
    get(Constants.ID_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val user = controller.findUserById(id)
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

private fun Route.getAllUsers(controller: UserController) {
    get {
        try {
            val users = controller.findAllUsers()
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