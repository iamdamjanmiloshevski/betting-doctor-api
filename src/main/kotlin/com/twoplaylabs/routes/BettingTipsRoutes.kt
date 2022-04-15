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

import com.twoplaylabs.controllers.BettingTipsController
import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.User
import com.twoplaylabs.data.UserRole
import com.twoplaylabs.data.common.Message
import com.twoplaylabs.util.BettingTipManager
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.convertIfSoccer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
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
    route(Constants.BETTING_TIPS_ROUTE){
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
}

private fun Route.getBettingTipById(controller: BettingTipsController) {
    get(Constants.ID_ROUTE) {
        //get betting tip by id
        val id = call.parameters[Constants.PARAM_ID] ?: return@get call.respondText(
            Constants.MISSING_ID,
            status = HttpStatusCode.BadRequest
        )
        val bettingTip = controller.findBettingTipById(id)
        bettingTip?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(
            HttpStatusCode.NotFound,
            Message(String.format(Constants.NO_BETTING_TIP_ID, id), HttpStatusCode.NotFound.value)
        )
    }
}

private fun Route.getPastBettingTipsBySport(controller: BettingTipsController) {
    get(Constants.OLDER_TIPS_BY_SPORT_ROUTE) {
        val sport = call.parameters[Constants.PARAM_SPORT] ?: return@get call.respond(
            HttpStatusCode.BadRequest, Message(Constants.MISSING_SPORT, HttpStatusCode.BadRequest.value)
        )
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
    get(Constants.UPCOMING_TIPS_BY_SPORT_ROUTE) {
        val sport = call.parameters[Constants.PARAM_SPORT] ?: return@get call.respond(
            HttpStatusCode.BadRequest, Message(Constants.MISSING_SPORT, HttpStatusCode.BadRequest.value)
        )
        try {
            val tips = controller.findBettingTipsBySport(
                sport.convertIfSoccer()
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
    get {
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
    delete(Constants.ID_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@delete call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
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
    delete {
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
    put {
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
    post {
        //post betting tip
        val principal = call.principal<User>()
        if (principal?.role != UserRole.ADMIN) {
            call.respond(
                HttpStatusCode.Unauthorized,
                Message(Constants.INSUFFICIENT_PERMISSIONS, HttpStatusCode.Unauthorized.value)
            )
        }
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



    