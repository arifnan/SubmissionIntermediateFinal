package com.example.submissionintermediate.ui.newstory

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.submissionintermediate.R
import com.example.submissionintermediate.auth.Result
import com.example.submissionintermediate.auth.ViewModelFactory
import com.example.submissionintermediate.databinding.ActivityNewStoryBinding
import com.example.submissionintermediate.ui.main.MainViewModel
import com.example.submissionintermediate.utils.getImageUri
import com.example.submissionintermediate.utils.reduceFileImage
import com.example.submissionintermediate.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: String? = null
    private var lon: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.upload_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLastLocation()
            } else {
                lat = ""
                lon = ""
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private var launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivItemImage.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()
            val lat = this.lat
            val lon = this.lon

            showLoading(true)

            viewModel.getSession().observe(this) { user ->
                viewModel.addNewStory(user.token, description, imageFile, lat, lon)
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                    binding.buttonAdd.isEnabled = false
                                    binding.btnGallery.isEnabled = false
                                }

                                is Result.Success -> {
                                    showLoading(false)
                                    binding.buttonAdd.isEnabled = true
                                    binding.btnGallery.isEnabled = true
                                    showToast(getString(R.string.add_new_story_success))
                                    finish()
                                }

                                is Result.Error -> {
                                    showLoading(false)
                                    binding.buttonAdd.isEnabled = true
                                    binding.btnGallery.isEnabled = true
                                    showToast(getString(R.string.add_new_story_failed))
                                }
                            }
                        }
                    }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun getMyLastLocation() {
        if (checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toString()
                    lon = location.longitude.toString()
                } else {
                    Toast.makeText(
                        this@NewStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    binding.cbLocation.isChecked = false
                }
            }
        }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}