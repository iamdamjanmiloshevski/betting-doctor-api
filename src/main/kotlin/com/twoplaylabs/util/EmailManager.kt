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

import org.apache.commons.mail.HtmlEmail

/*
    Author: Damjan Miloshevski 
    Created on 15/09/2021
    Project: betting-doctor
*/
object EmailManager {
     fun sendNoReplyEmail(emailParameters: Map<String, String?>) {
        val message: String? = emailParameters["message"]
        val htmlMessage: String? = emailParameters["htmlMessage"]
        val subject: String? = emailParameters["subject"]
        val to: String? = emailParameters["to"]
        val username = System.getenv(Constants.EMAIL_USERNAME)
        val password = System.getenv(Constants.EMAIL_PASSWORD)
        val host = System.getenv(Constants.MAIL_SERVER)
        val smtpPort = System.getenv(Constants.SMTP_PORT).toInt()
        val email = HtmlEmail()
        email.hostName = host
        email.setSmtpPort(smtpPort)
        email.setAuthentication(username, password)
        email.isSSLOnConnect = true
        email.setFrom(username)
        email.subject = subject
        try {
            email.setHtmlMsg(htmlMessage)
        } catch (e: Exception) {
            email.setMsg(message)
        }
        email.addTo(to)
        email.send()
    }
}