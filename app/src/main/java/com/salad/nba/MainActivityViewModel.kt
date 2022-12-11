package com.salad.nba

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salad.nba.Objects.Player
import com.salad.nba.api.NBAApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

class MainActivityViewModel : ViewModel() {

    var players = mutableListOf<Player>()
    var mutablePlayers : MutableStateFlow<List<Player>> = MutableStateFlow(mutableListOf())
    var immutablePlayers = mutablePlayers.asStateFlow()
    var retrofit = Retrofit.Builder().baseUrl("https://www.balldontlie.io/api/v1/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    init {
        viewModelScope.launch {
            fetchPlayers()
        }
    }

    suspend fun fetchPlayers(){
        var jsonResponse = JSONObject(retrofit.create(NBAApi::class.java).getPlayers())
        var jsonArray = jsonResponse.getJSONArray("data")
        players.clear()
        for (i in 0..jsonArray.length()-1){
            var playerObj = JSONObject(jsonArray.get(i).toString())
            var player = Player(playerObj.getString("first_name")+" "+playerObj.getString("last_name"),playerObj.getJSONObject("team").getString("full_name"))
            players.add(player)


        }
        if(players.size > 0){
            //We have players
            mutablePlayers.value = players
        }
         else {
             //We dont have players
             Log.d("MainActivityViewModel","No players found")
        }

    }

    suspend fun fetchWithQuery(query :String){
        viewModelScope.launch {
            Log.d("MainActivityViewModel","Query: "+query)
            var jsonResponse = JSONObject(retrofit.create(NBAApi::class.java).searchPlayer(query))
            var jsonArray = jsonResponse.getJSONArray("data")
        players.clear()
            for(i in 0..jsonArray.length()-1){
                var playerObject = JSONObject(jsonArray.getJSONObject(i).toString())
                var name = playerObject.getString("first_name") + " , " + playerObject.getString("last_name")
                var team = playerObject.getJSONObject("team").getString("full_name")
                players.add(Player(name,team))
            }

            printPlayers()
            Log.d("MainActivityViewModel","Players found: "+players.size.toString())
//            mutablePlayers.value = players

        }
    }

    suspend fun printPlayers(){
        for (player in players){
            Log.d("MainActivityViewModel", player.name)

        }
    }
}