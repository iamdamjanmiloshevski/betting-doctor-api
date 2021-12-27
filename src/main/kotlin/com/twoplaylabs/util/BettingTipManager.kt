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

import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.Team
import com.twoplaylabs.data.sports.PlayerData
import com.twoplaylabs.data.sports.SportsApiData
import com.twoplaylabs.data.sports.SportsData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

/*
    Author: Damjan Miloshevski 
    Created on 27/12/2021
    Project: betting-doctor
*/
object BettingTipManager {
    suspend fun fetchTeamLogosAndUpdateBettingTip(bettingTip: BettingTip, callback: (BettingTip) -> Unit) {
        bettingTip.teamHome.logo = getTeamLogo(bettingTip.sport,bettingTip.teamHome)
        bettingTip.teamAway.logo = getTeamLogo(bettingTip.sport,bettingTip.teamAway)
        callback.invoke(bettingTip)
    }

    private suspend fun getTeamLogo(sport: String, team: Team): String {
        val client = HttpClient()
        val teamData: SportsApiData?
        return try {
            teamData = when (sport) {
                "Tennis", "tennis" -> {
                    client.fetchTeamData<PlayerData>(sport, team)
                }
                else -> {
                    client.fetchTeamData<SportsData>(sport, team)
                }
            }
            client.close()
            TeamImageProvider.getTeamImageUrl(team, sport, teamData)
        } catch (e: Exception) {
            println(e)
            client.close()
            ""

        }
    }

    suspend inline fun <reified T : SportsApiData> HttpClient.fetchTeamData(
        sport: String,
        team: Team
    ): T {
        val teamResponse: HttpResponse =
            this.get(team.name.createURLForTeamData(sport))
        val teamJson: String = teamResponse.receive()
        return GsonUtil.deserialize(T::class.java, teamJson)
    }
    fun String.createURLForTeamData(sport: String): String {
        return when (sport) {
            "Tennis", "tennis" -> "https://www.thesportsdb.com/api/v1/json/".plus(System.getenv(Constants.SPORTSDB_API_KEY)).plus("/searchplayers.php?p=${this}")
            else -> "https://www.thesportsdb.com/api/v1/json/".plus(System.getenv(Constants.SPORTSDB_API_KEY)).plus("/searchteams.php?t=${this}")
        }
    }
}
