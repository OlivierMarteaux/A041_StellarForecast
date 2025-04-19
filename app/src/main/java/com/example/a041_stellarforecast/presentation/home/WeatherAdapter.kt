package com.example.a041_stellarforecast.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a041_stellarforecast.databinding.ItemWeatherBinding
import com.example.a041_stellarforecast.domain.model.WeatherReportModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * L'Adapter a pour rôle de lier les données à la vue. Il agit comme un pont entre les données,
 * généralement stockées dans une liste, et la vue qui les affiche. L'adapter crée également
 * les ViewHolders nécessaires pour chaque élément de données, ce qui permet un recyclage
 * efficace des vues et une meilleure performance.
 */
class WeatherAdapter() : ListAdapter<WeatherReportModel, WeatherAdapter.WeatherViewHolder>(DiffCallback) {

    /**
     * Classe interne représentant chaque élément de la liste, responsable de la liaison des données
     * avec les vues.
     */
    class WeatherViewHolder(private val binding: ItemWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault())

        /**
         * Lie les propriétés des données de prévisions météorologiques aux éléments de la vue,
         * tels que la température, les températures maximale et minimale, ainsi que les
         * précipitations. C’est la méthode que vous modifierez le plus afin d’afficher
         * les données souhaitées.
         */
        fun bind(weather: WeatherReportModel) {
            val formattedDate: String = dateFormatter.format(weather.date.time)
            binding.textViewDateTime.text = formattedDate
            binding.textViewStargazing.text = if (weather.isGoodForStargazing) "⭐️" else "☁️"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val itemView = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = getItem(position)
        holder.bind(weather)
    }

    /**
     * Aide à déterminer quelles données ont changé entre les anciennes et les nouvelles listes
     * d'éléments. Cela aide à optimiser l'affichage en mettant à jour uniquement les parties
     * nécessaires de l'interface utilisateur lorsqu'il y a des changements. Dans notre cas,
     * nous vérifions seulement si la date change.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<WeatherReportModel>() {
        override fun areItemsTheSame(oldItem: WeatherReportModel, newItem: WeatherReportModel): Boolean {
            return oldItem.date == newItem.date
        }
        override fun areContentsTheSame(oldItem: WeatherReportModel, newItem: WeatherReportModel): Boolean {
            return oldItem == newItem
        }
    }
}