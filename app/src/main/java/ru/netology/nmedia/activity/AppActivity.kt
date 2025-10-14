package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: AppAuth

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        requestNotificationsPermission()

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent?.let {
            if (it.action == Intent.ACTION_SEND) {
                val text = it.getStringExtra(Intent.EXTRA_TEXT)
                if (!text.isNullOrBlank()) {
                    intent.removeExtra(Intent.EXTRA_TEXT)
                    findNavController(R.id.nav_host_fragment).navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply { textArg = text }
                    )
                }
            }
        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        checkGoogleApiAvailability()

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                menu.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                menu.setGroupVisible(R.id.authenticated, viewModel.authenticated)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signin -> {
                        findNavController(R.id.nav_host_fragment)
                            .navigate(R.id.action_feedFragment_to_signInFragment)
                        true
                    }

                    R.id.signup -> {
                        auth.setAuth(5, "x-token")
                        true
                    }

                    R.id.signout -> {
                        auth.removeAuth()
                        true
                    }

                    else -> false
                }
        })
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 1)
            }
        }
    }

    private fun checkGoogleApiAvailability() {
        val code = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (code != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(code)) {
                googleApiAvailability.getErrorDialog(this, code, 9000)?.show()
            } else {
                Toast.makeText(this, R.string.google_play_unavailable, Toast.LENGTH_LONG).show()
            }
        }
    }
}
