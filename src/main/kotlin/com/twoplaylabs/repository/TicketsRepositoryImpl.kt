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

package com.twoplaylabs.repository

import com.twoplaylabs.data.Ticket
import org.litote.kmongo.*
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 25/12/2021
    Project: betting-doctor
*/
class TicketsRepositoryImpl:BaseRepository(),TicketsRepository {
    override suspend fun insertTicket(ticket: Ticket) {
        ticketsCollection.insertOne(ticket)
    }

    override suspend fun findAllTickets(): List<Ticket> {
       val tickets = ticketsCollection.find()
        return tickets.toList()
    }

    override suspend fun findTicketById(id: String): Ticket? {
        return ticketsCollection.findOneById(id)
    }

    override suspend fun findTicketByDate(date: Date): Ticket? {
        return ticketsCollection.findOne(Ticket::date eq date)
    }

    override suspend fun updateTicket(ticket: Ticket): Long {
        val request = ticketsCollection.updateOne(Ticket::_id eq ticket._id, set(
            Ticket::tips setTo ticket.tips,
            Ticket::date setTo ticket.date
        )
        )
        return request.modifiedCount
    }

    override suspend fun deleteAllTickets(): Long {
        val request = ticketsCollection.deleteMany()
        return request.deletedCount
    }

    override suspend fun deleteTicket(id: String): Long {
        val result =  ticketsCollection.deleteOne(Ticket::_id eq id)
        return result.deletedCount
    }

    override suspend fun deleteTicket(date: Date): Long {
        val result =  ticketsCollection.deleteOne(Ticket::date eq date)
        return result.deletedCount
    }
}