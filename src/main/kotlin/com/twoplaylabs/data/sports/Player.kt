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

package com.twoplaylabs.data.sports


import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("dateBorn")
    val dateBorn: String,
    @SerializedName("dateSigned")
    val dateSigned: Any,
    @SerializedName("idAPIfootball")
    val idAPIfootball: Any,
    @SerializedName("idPlayer")
    val idPlayer: String,
    @SerializedName("idPlayerManager")
    val idPlayerManager: Any,
    @SerializedName("idSoccerXML")
    val idSoccerXML: Any,
    @SerializedName("idTeam")
    val idTeam: String,
    @SerializedName("idTeam2")
    val idTeam2: Any,
    @SerializedName("idTeamNational")
    val idTeamNational: Any,
    @SerializedName("intLoved")
    val intLoved: String,
    @SerializedName("intSoccerXMLTeamID")
    val intSoccerXMLTeamID: Any,
    @SerializedName("strAgent")
    val strAgent: Any,
    @SerializedName("strBanner")
    val strBanner: Any,
    @SerializedName("strBirthLocation")
    val strBirthLocation: String,
    @SerializedName("strCollege")
    val strCollege: Any,
    @SerializedName("strCreativeCommons")
    val strCreativeCommons: String,
    @SerializedName("strCutout")
    val strCutout: String,
    @SerializedName("strDescriptionCN")
    val strDescriptionCN: Any,
    @SerializedName("strDescriptionDE")
    val strDescriptionDE: Any,
    @SerializedName("strDescriptionEN")
    val strDescriptionEN: String,
    @SerializedName("strDescriptionES")
    val strDescriptionES: Any,
    @SerializedName("strDescriptionFR")
    val strDescriptionFR: Any,
    @SerializedName("strDescriptionHU")
    val strDescriptionHU: Any,
    @SerializedName("strDescriptionIL")
    val strDescriptionIL: Any,
    @SerializedName("strDescriptionIT")
    val strDescriptionIT: Any,
    @SerializedName("strDescriptionJP")
    val strDescriptionJP: Any,
    @SerializedName("strDescriptionNL")
    val strDescriptionNL: Any,
    @SerializedName("strDescriptionNO")
    val strDescriptionNO: Any,
    @SerializedName("strDescriptionPL")
    val strDescriptionPL: Any,
    @SerializedName("strDescriptionPT")
    val strDescriptionPT: Any,
    @SerializedName("strDescriptionRU")
    val strDescriptionRU: Any,
    @SerializedName("strDescriptionSE")
    val strDescriptionSE: Any,
    @SerializedName("strFacebook")
    val strFacebook: String,
    @SerializedName("strFanart1")
    val strFanart1: Any,
    @SerializedName("strFanart2")
    val strFanart2: Any,
    @SerializedName("strFanart3")
    val strFanart3: Any,
    @SerializedName("strFanart4")
    val strFanart4: Any,
    @SerializedName("strGender")
    val strGender: String,
    @SerializedName("strHeight")
    val strHeight: String,
    @SerializedName("strInstagram")
    val strInstagram: String,
    @SerializedName("strKit")
    val strKit: Any,
    @SerializedName("strLocked")
    val strLocked: String,
    @SerializedName("strNationality")
    val strNationality: String,
    @SerializedName("strNumber")
    val strNumber: Any,
    @SerializedName("strOutfitter")
    val strOutfitter: Any,
    @SerializedName("strPlayer")
    val strPlayer: String,
    @SerializedName("strPosition")
    val strPosition: String,
    @SerializedName("strRender")
    val strRender: String,
    @SerializedName("strSide")
    val strSide: Any,
    @SerializedName("strSigning")
    val strSigning: String,
    @SerializedName("strSport")
    val strSport: String,
    @SerializedName("strTeam")
    val strTeam: String,
    @SerializedName("strTeam2")
    val strTeam2: Any,
    @SerializedName("strThumb")
    val strThumb: String,
    @SerializedName("strTwitter")
    val strTwitter: String,
    @SerializedName("strWage")
    val strWage: String,
    @SerializedName("strWebsite")
    val strWebsite: String,
    @SerializedName("strWeight")
    val strWeight: String,
    @SerializedName("strYoutube")
    val strYoutube: String
)