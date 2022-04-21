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

import com.twoplaylabs.common.authorize
import com.twoplaylabs.controllers.BettingTipsController
import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.User
import com.twoplaylabs.data.UserRole
import com.twoplaylabs.data.common.Message
import com.twoplaylabs.resources.BettingTips
import com.twoplaylabs.util.BettingTipManager
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.convertIfSoccer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 05/04/2022
    Project: betting-doctor
*/

fun Route.bettingTipsController(controller:BettingTipsController){
    val bettingTipsManager by inject<BettingTipManager>(BettingTipManager::class.java)
        authenticate(System.getenv(Constants.AUTH_CONFIG_ADMIN)) {
            createBettingTip(bettingTipsManager, controller)
            updateBettingTip(bettingTipsManager, controller)
            deleteAllBettingTips(controller)
            deleteBettingTipById(controller)
        }
        getAllBettingTips(controller)
        getUpcomingBettingTipsBySport(controller)
        getPastBettingTipsBySport(controller)
        getBettingTipById(controller)
}

private fun Route.getBettingTipById(controller: BettingTipsController) {
    get<BettingTips.Id> {bettingTip ->
        val id = bettingTip.id
        val bTip = controller.findBettingTipById(id)
        bTip?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(
            HttpStatusCode.NotFound,
            Message(String.format(Constants.NO_BETTING_TIP_ID, id), HttpStatusCode.NotFound.value)
        )
    }
}

private fun Route.getPastBettingTipsBySport(controller: BettingTipsController) {
    get<BettingTips.Sport.Older> {bettingTip ->
        val sport = bettingTip.parent.sport
        try {
            val tips = controller.findBettingTipsBySport(
                sport.convertIfSoccer()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                false
            )
            call.respond(HttpStatusCode.OK, tips)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.getUpcomingBettingTipsBySport(controller: BettingTipsController) {
    get<BettingTips.Sport.Upcoming> { sport ->
        try {
            val tips = controller.findBettingTipsBySport(
                sport.parent.sport.convertIfSoccer()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                true
            )
            call.respond(HttpStatusCode.OK, tips)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.getAllBettingTips(controller: BettingTipsController) {
    get<BettingTips> {
    //get all betting tips
        try {
            val items = controller.findAllBettingTips()
            call.respond(items)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.deleteBettingTipById(controller: BettingTipsController) {
    delete<BettingTips.Id> {bettingTip->
        call.authorize()
        val id = bettingTip.id
        try {
            val deletedCount = controller.deleteBettingTip(id)
            application.log.debug("Deleted items $deletedCount")
            if (deletedCount > 0) {
                call.respond(HttpStatusCode.OK, Message(Constants.SUCCESS, HttpStatusCode.OK.value))
            } else call.respond(HttpStatusCode.NotFound, Message("Content not found", HttpStatusCode.NotFound.value))
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.deleteAllBettingTips(controller: BettingTipsController) {
    delete<BettingTips> {
        call.authorize()
        try {
            val deletedCount = controller.deleteAllBettingTips()
            application.log.debug("Deleted items $deletedCount")
            if (deletedCount > 0) {
                call.respond(HttpStatusCode.OK)
            } else call.respond(HttpStatusCode.NoContent)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.updateBettingTip(
    bettingTipsManager: BettingTipManager,
    controller: BettingTipsController
) {
    put<BettingTips> {
        call.authorize()
        val bettingTip = call.receive<BettingTip>()
        try {
            bettingTipsManager.fetchTeamLogosAndUpdateBettingTip(bettingTip, callback = { updatedBettingTip ->
                runBlocking {
                    val updatedCount = controller.updateBettingTip(updatedBettingTip)
                    application.log.debug("Updated documents $updatedCount")
                    if (updatedCount > 0) {
                        call.respond(HttpStatusCode.Accepted, bettingTip)
                    } else call.respond(HttpStatusCode.NoContent)
                }
            })

        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.createBettingTip(
    bettingTipsManager: BettingTipManager,
    controller: BettingTipsController
) {
    post<BettingTips> {
        call.authorize()
        val bettingTip = call.receive<BettingTip>()
        try {
            bettingTipsManager.fetchTeamLogosAndUpdateBettingTip(bettingTip, callback = { updatedBettingTip ->
                runBlocking {
                    controller.insertBettingTip(updatedBettingTip)
                    call.respond(HttpStatusCode.Created, updatedBettingTip)
                }
            })
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}



    