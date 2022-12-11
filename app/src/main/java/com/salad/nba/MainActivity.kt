package com.salad.nba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salad.nba.Objects.Player
import com.salad.nba.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding :ActivityMainBinding
    lateinit var viewModel :MainActivityViewModel
    lateinit var adapter : PlayerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_main, null)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchPb.isVisible = true
        viewModel = MainActivityViewModel()


        lifecycleScope.launch {
            viewModel.immutablePlayers.collect {
                wireAdapter(it)
            }
        }

        binding.apply {
            searchByPlayer.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    lifecycleScope.launch{

                        binding.searchPb.isVisible = true
                        viewModel.fetchWithQuery(p0.toString())
                        viewModel.immutablePlayers.collect() {
                            wireAdapter(it)
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }
    }

    fun wireAdapter(it :List<Player>){
        Log.d("MainActivity: ","Collector called")
        binding.searchPb.isVisible = false
        adapter = PlayerAdapter(it,this@MainActivity)
        binding.playersList.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.playersList.adapter = adapter
        adapter.notifyDataSetChanged()
    }



    class PlayerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        lateinit var name :TextView
        lateinit var team :TextView

        var view = itemView

        init {
            name = itemView.findViewById(R.id.name)
            team = itemView.findViewById(R.id.team)
            view.setOnClickListener {
                Log.d("MainActivity","Clicked player: "+name.text.toString())
            }
        }






    }

    class PlayerAdapter(var playersList :List<Player>, var context :MainActivity) : RecyclerView.Adapter<PlayerViewHolder>() {

        var players = playersList
        var ctx = context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
            var v = LayoutInflater.from(context).inflate(R.layout.custom_player,parent,false)
            return PlayerViewHolder(v)
        }

        override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
            var player = players.get(position)
            holder.name.setText(player.name)
            holder.team.setText(player.team)


        }



        override fun getItemCount(): Int {
            return players.size
        }
    }


}