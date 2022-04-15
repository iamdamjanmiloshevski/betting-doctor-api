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

package com.twoplaylabs.di


import com.twoplaylabs.auth.JWTService
import com.twoplaylabs.client.client
import com.twoplaylabs.controllers.*
import com.twoplaylabs.firebase.FirebaseManager
import com.twoplaylabs.repository.*
import com.twoplaylabs.util.*
import org.koin.dsl.module
import org.litote.kmongo.KMongo

/*
    Author: Damjan Miloshevski 
    Created on 05/04/2022
    Project: betting-doctor
*/
val databaseModule = module {
    single { KMongo.createClient(System.getenv(Constants.DB_CONNECTION_URL)) }
    single { FirebaseManager() }
}
val repoModule = module {
    single<BettingTipsRepository> { BettingTipsRepositoryImpl() }
    single<TicketsRepository> { TicketsRepositoryImpl() }
    single<TokensRepository> { TokensRepositoryImpl() }
    single<UsersRepository> { UsersRepositoryImpl() }
}
val controllersModule = module {
    single<BettingTipsController> { BettingTipsControllerImpl(get()) }
    single<UserController> { UserControllerImpl(get()) }
    single<TokenController> { TokenControllerImpl(get()) }
    single<TicketController> { TicketControllerImpl(get()) }
}
val authenticationModule = module {
    single { JWTService() }
    single { AuthUtil }

}
val clientModule = module { single { client } }

val emailModule = module { single { EmailManager } }

val bettingTipsModule = module {
    single {
        BettingTipManager(
            get(),
            get()
        )
    }
    single { TeamImageProvider(get()) }
}

val gsonModule = module { single { GsonUtil } }