package com.gritja.vaderhurts.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gritja.vaderhurts.databinding.FragmentHomeBinding
import fuel.Fuel
import fuel.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

 @Serializable
data class CurrentUnitsJson(@SerialName("temperature_2m") val temperature: String)
@Serializable
data class CurrentDataJson(@SerialName("temperature_2m") val temperature: Float)

@Serializable
data class CurrentWeatherResponse(
    val current: CurrentDataJson, @SerialName("current_units") val currentUnits: CurrentUnitsJson
)


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.textHome.setText("Weather data cannot be provided without your permission.")
        val root: View = binding.root
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    val lm: LocationManager =
                        context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager;

                    val providers: List<String> = lm.getProviders(true)
                    var bestLocation2: Location? = null
                    for (provider in providers) {
                        val l: Location = lm.getLastKnownLocation(provider) ?: continue
                        if (bestLocation2 == null || l.getAccuracy() < bestLocation2.getAccuracy()) {
                            // Found best last known location: %s", l);
                            bestLocation2 = l
                        }
                    }

                    Log.w("best_location", bestLocation2?.toString() ?: "this is null");
                    Log.w("loll", "All is well");
                    val theUser = "TY for permission your location is lat:${bestLocation2?.latitude.toString()} long:${bestLocation2?.longitude.toString()}"
                    binding.textHome.setText(theUser)

                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            -> {
                val lm: LocationManager =
                    context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager;

                val providers: List<String> = lm.getProviders(true)
                var bestLocation: Location? = null
                for (provider in providers) {
                    val l: Location = lm.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l
                    }
                }

                Log.w("best_location", bestLocation?.toString() ?: "this is null");
                Log.w("loll", "All is well");
                // DOUBLE CHECK if bestLocation is not null
                val theUser = "Your location is lat:${bestLocation?.latitude.toString()} long:${bestLocation?.longitude.toString()}"

                runBlocking {
                    val withUnknownKeys = Json { ignoreUnknownKeys = true }
                    val result: String = Fuel.get("https://api.open-meteo.com/v1/forecast?longitude=${bestLocation?.longitude.toString()}&latitude=${bestLocation?.latitude.toString()}&current=temperature_2m").body.string()
                    val currentWeatherResponse = withUnknownKeys.decodeFromString<CurrentWeatherResponse>(result)
                    val uiText = "Your current weather is ${currentWeatherResponse.current.temperature} ${currentWeatherResponse.currentUnits.temperature}"
                    binding.textHome.setText(uiText)
                }
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }

        return binding.root
    }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}