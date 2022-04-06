package com.twoplaylabs.plugins


import com.twoplaylabs.util.convertIfSoccer
import junit.framework.Assert.assertEquals
import org.junit.Test


/*
    Author: Damjan Miloshevski 
    Created on 19/06/2021
    Project: betting-doctor
    © 2Play Technologies  2021. All rights reserved
*/
class RoutingTest {
    @Test
    fun convertIfSoccer_sportSoccerNotCapitalized_returnsFootball(){
        val sport = "soccer"
        assertEquals("football",sport.convertIfSoccer())
    }

    @Test
    fun convertIfSoccer_sportSoccerCapitalized_returnsFootball(){
        val sport = "Soccer"
        assertEquals("Football",sport.convertIfSoccer())
    }

    @Test
    fun convertIfSoccer_sportElse_returnsElse(){
        val sport = "basketball"
        assertEquals("basketball",sport.convertIfSoccer())
    }
}