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

package com.twoplaylabs

import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.Status
import com.twoplaylabs.data.Team
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 25/04/2022
    Project: betting-doctor
*/
val dummyResponse = listOf<BettingTip>(
    BettingTip(
        leagueName = "World Cup",
        teamHome = Team(
            name = "Iceland",
            logo = "https://storage.googleapis.com/betting-tips-2-odds.appspot.com/basketball/Iceland.jpeg?GoogleAccessId=firebase-adminsdk-erzcm@betting-tips-2-odds.iam.gserviceaccount.com&Expires=2262514768&Signature=INa6%2F69vXRJOw8oILR7X59NJNmYOc9tVEaDnEgkhqx25Fx9o5pDoDlTdz5Hdw%2BQOPY5MGFA0XG%2BvKjhazdOIG9iBwWtuqnwnNzjY4s9qUYUh8I3fGzblbDuoz9PQ0DNDkJeBRWQZDm%2BNKT229sAF9bMVJYOuP7nr2RXl65bYLk0eLeoJOh4DL2g3t46AdJSb3yAgaRjGGncNRZwh7z2QbQ%2FKZL3ESpGFoy6I5qYLMEgqubXLtwRTBLIakeNODj0AUVV8hj7gH6A%2F6%2BAbNWuJWP9cJagqjnwlUHaTToOLkCQ8mCQVruNN6xRHah8%2BsBe2Cp67DK%2BvNShcWENf2q9LBA%3D%3D"
        ),
        teamAway = Team(
            name = "Montenegro",
            logo = "https://storage.googleapis.com/betting-tips-2-odds.appspot.com/basketball/Montenegro.jpeg?GoogleAccessId=firebase-adminsdk-erzcm@betting-tips-2-odds.iam.gserviceaccount.com&Expires=2262514769&Signature=HJ9j0pPo5rE1oxqmRZYrzXOXs106VkGSMZwy5zCPJzfZ57JRrpfEVyUiCfttCjmnOFc%2F4hmBkHSJJhxBiPOcMDHGDLvSJgxGVMBLL2RYRX8L%2BU3okowrwfjs4aVUs25cQ%2Fgoxl9g%2FR2JVjxMAu1Hxpp9QqF3UJssgX5Td9OobD4c9jaSBnkdEJTjLVebICjByN2KszstCXJkdrUCsiNfsl7tPih%2F5uylpQWXD7YYxqXPOjGmIxcXqzGDD18abXSs7e3Mp3YUA%2FMLLCfJYY61MKsPpfRNr05CbM9G%2BVBQ0Z%2BDOqwsqe3JpVjuHgWDMWv2Lg%2BS97Ij0eXAWc5wVGDhgw%3D%3D"
        ),
        gameTime = Date(),
        bettingType = "2",
        status = Status.WON,
        sport = "Basketball",
        coefficient = null,
        ticketId = null,
        result = "80-82",
        _id = "61433651daaf273759166074"
    ),
    BettingTip(
        leagueName = "Italy - Ligue A - play offs",
        teamHome = Team(
            name = "Venezia",
            logo = "https://storage.googleapis.com/betting-tips-2-odds.appspot.com/basketball/Venezia.jpeg?GoogleAccessId=firebase-adminsdk-erzcm@betting-tips-2-odds.iam.gserviceaccount.com&Expires=2262514904&Signature=TfNZIN9dxXJyTCyLtGZc03vAIyZoUDIrkrfu9sbcOGkO7aAjMdK603YXvF9qb8snVuyYmEIHFDCWoOUur5h9dA%2B7MpXJ11Q%2B6qIqHBnxtaxFfqrlWMTuU1nDzIRTfsukAYKU8%2BfVEG0tBBvCwBm5sMy0v61XmWBZD%2BcGOrCInwWjfjZd%2FW3xMEmK%2FQV4ODyFDocX8emlz8HhQrEdCZrdTLB1XtsX5dx5CCkfb5azeUstEawFom50iCqmMQOhm4DtKRA0D8Wm7QUyEMLnuVCVb17MjW1QH9GmvMVBJQr4AN4aDo%2BB41DDydrjFThoG%2B0iapXgNYIkY7MdkUZlAposmg%3D%3D"
        ),
        teamAway = Team(
            name = "Olimpia Milano",
            logo = "https://storage.googleapis.com/betting-tips-2-odds.appspot.com/basketball/OlimpiaMilano.jpeg?GoogleAccessId=firebase-adminsdk-erzcm@betting-tips-2-odds.iam.gserviceaccount.com&Expires=2262514905&Signature=oxxP7UNSITrmp20g%2BW0lWGIGhIgkRlzfUqU34eUM4iqiLTrYG8zZQIKxGaOQu3GAdBZUvdhNPhW2hVuBKCkkrvZTJOvmYvNPpdiylgxDeLpqAzPKKET9tKK%2FkcgJ%2BcdP%2F8z1pOK1MvoAUoqfyYTsTAOXHhJ9fhXTSdlXE%2FjRhQQHa04hMI4dTAkQBRAVnetgU7Ey5zoY4641eVVppYGIa%2FsJOXqR3WMz74AoaLIj85WSnjMOks2uYTmW9EDVKlOADUjDh9yNeg4Y6EcYCm7TGXrdESMuKTsobcLkft1y0WJcBAfxW7o%2FvnqMaDTa7jyC9xhzxKsAS1TO%2BUUkmwPYEQ%3D%3D"
        ),
        gameTime = Date(),
        bettingType = "2",
        status = Status.WON,
        sport = "Basketball",
        coefficient = null,
        ticketId = null,
        result = "83-93",
        _id = "614336d9daaf273759166075"
    )

)