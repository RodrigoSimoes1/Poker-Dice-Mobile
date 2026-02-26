package com.example.myapplication.match.domain

import com.example.myapplication.match.model.output.MatchEvent
import kotlinx.coroutines.flow.Flow

interface MatchSseService {
    suspend fun subscribeToMatch(matchId: Int): Flow<MatchEvent>
}
