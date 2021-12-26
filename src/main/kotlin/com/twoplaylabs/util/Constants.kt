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

package com.twoplaylabs.util

/*
    Author: Damjan Miloshevski 
    Created on 18/06/2021
    Project: betting-doctor
*/
object Constants {
    const val DB_NAME = "betting-doctor"
    private const val API_PREFIX = "/api/v1"
    const val BETTING_TIPS_COLLECTION = "betting-tips"
    const val USERS_COLLECTION = "users"
    const val TICKETS_COLLECTION = "tickets"
    const val FEEDBACKS_COLLECTION = "feedbacks"
    const val WELCOME = "Welcome to Betting Doctor!"
    const val PASSWORD_HASH_COST = 16
    const val HELLO_TEMPLATE = "Hello %s"
    const val ACCOUNT_VERIFIED_MSG = "You've successfully verified your account."
    const val SIGN_IN_VERIFICATION_MSG = "You can sign in now"
    const val AUTHENTICATION = "Authentication"
    const val ID = "_id"
    const val ROLE = "role"
    const val USER = "user"
    const val IS_ACCOUNT_VERIFIED = "isAccountVerified"
    const val TOPIC = "topic"

    //region routes
    const val BETTING_TIPS_ROUTE = "${API_PREFIX}/betting-tips"
    const val USERS_ROUTE = "${API_PREFIX}/users"
    const val UPCOMING_TIPS_BY_SPORT_ROUTE = "/{sport}/upcoming"
    const val OLDER_TIPS_BY_SPORT_ROUTE = "/{sport}/older"
    const val ID_ROUTE = "/{id}"
    const val CHANGE_PWD_ROUTE = "/{id}/change-password"
    const val SIGN_IN_ROUTE = "/signin"
    const val REGISTER_ROUTE = "/register"
    const val VERIFY_ROUTE = "/verify/{id}"
    const val SIGN_OUT_ROUTE = "/signout"
    const val FEEDBACK_ROUTE = "/feedback"
    const val REFRESH_TOKEN = "/tokens/refresh-token/{refreshToken}"
    const val PUSH_NOTIFICATIONS = "/notifications/{topic}"
    //endregion

    //region https messages
    const val SOMETHING_WENT_WRONG = "Something went wrong!"
    const val MISSING_SPORT = "Missing or malformed sport"
    const val MISSING_ID = "Missing or malformed id"
    const val NO_USER_FOUND = "No user found!"
    const val USER_UPDATED_SUCCESSFULLY = "User %s has been updated successfully!"
    const val NO_USER_WITH_EMAIL = "No user with email %s found"
    const val NO_SUCH_USER_TO_UPDATE = "No such user found to update!"
    const val USER_EXISTS = "User with email %s already exists!"
    const val PWD_CHANGED_SUCCESS = "Password changed successfully. Please sign in with your new password"
    const val PWD_NO_MATCH = "Passwords do not match!"
    const val PWD_INCORRECT = "Incorrect password!"
    const val SUCCESS = "Success"
    const val NO_BETTING_TIP_ID = "No betting tip with id %s"
    const val VERIFY_ACCOUNT_MSG = "Please verify your account!"
    const val REGISTER_SUCCESS_MESSAGE =
        "Hi %s,\n\n You've successfully registered to Betting Doctor.\n\nPlease verify your account in the link below\n%s"
    const val REGISTER_SUCCESS_MESSAGE1 =
        "You've successfully registered to Betting Doctor.\n Please check your email %s for further instructions."
    const val ACCOUNT_VERIFY_SUCCESS_MESSAGE = "You've successfully verified your account, you can sign in now."
    const val REGISTER_HTML_MESSAGE =
        "<html><body><p>Hi %s,<br/>You've successfully registered to Betting Doctor<br/><br/>Please verify your account by clicking on the following <a href='%s'>link</a>"
    const val REGISTER_HTML_MESSAGE2 =
        "Hi %s,\n You've successfully registered to Betting Doctor\n\nPlease verify your account by clicking on the following link %s"
   const val FEEDBACK_SUCCESS_MESSAGE1 = "Dear %s,\n\n Thank you for your feedback.\n\n We will review it carefully and get back to you as soon as possible.\n\nKind regards, \n\n 2Play Technologies Team"
    const val FEEDBACK_HTML_MESSAGE =
        "<html><body><p>Dear %s,<br/><br/> Thank you for your feedback.<br/><br/>We will review it carefully and get back to you as soon as possible.<br/><br/>Kind regards, <br/></br/> 2Play Technologies team"
    const val INSUFFICIENT_PERMISSIONS = "Insufficient permissions"
    const val MISSING_REFRESH_TOKEN = "Missing refresh token"
    const val UNABLE_TO_VERIFY_REFRESH_TOKEN = "Unable to verify refresh token"
    const val FEEDBACK_SUCCESS = "Your feedback has been submitted successfully"
    //endregion

    //region params
    const val PARAM_SPORT = "sport"
    const val PARAM_ID = "id"
    const val PARAM_REFRESH_TOKEN = "refreshToken"
    //endregion

    //region Environment variables
    const val EMAIL_USERNAME = "EMAIL_USERNAME"
    const val EMAIL_PASSWORD = "EMAIL_PASSWORD"
    const val MAIL_SERVER = "MAIL_SERVER"
    const val API_BASE_URL = "API_BASE_URL"
    const val SMTP_PORT = "SMTP_PORT"
    const val AUTH_CONFIG_ALL = "AUTH_CONFIG_ALL"
    const val AUTH_CONFIG_ADMIN = "AUTH_CONFIG_ADMIN"
    const val PORT = "PORT"
    const val JWT_SECRET = "JWT_SECRET"
    const val JWT_AUDIENCE = "JWT_AUDIENCE"
    const val JWT_ID = "JWT_ID"
    const val DB_CONNECTION_URL = "DB_CONNECTION_URL"
    const val FIREBASE_STORAGE_BUCKET_URL = "FIREBASE_STORAGE_BUCKET_URL"
    const val FIREBASE_TYPE = "FIREBASE_TYPE"
    const val FIREBASE_PROJECT_ID= "FIREBASE_PROJECT_ID"
    const val FIREBASE_PRIVATE_KEY_ID = "FIREBASE_PRIVATE_KEY_ID"
    const val FIREBASE_PRIVATE_KEY ="FIREBASE_PRIVATE_KEY"
    const val FIREBASE_CLIENT_EMAIL= "FIREBASE_CLIENT_EMAIL"
    const val FIREBASE_CLIENT_ID= "FIREBASE_CLIENT_ID"
    const val FIREBASE_AUTH_URI="FIREBASE_AUTH_URI"
    const val FIREBASE_TOKEN_URI= "FIREBASE_TOKEN_URI"
    const val FIREBASE_AUTH_PROVIDER_x509_CERT_URL= "FIREBASE_AUTH_PROVIDER_x509_CERT_URL"
    const val FIREBASE_CLIENT_x509_CERT_URL = "FIREBASE_CLIENT_x509_CERT_URL"
    const val SPORTSDB_API_KEY = "SPORTSDB_API_KEY"
    //endregion
}