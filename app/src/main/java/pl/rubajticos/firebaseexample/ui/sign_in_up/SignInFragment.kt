package pl.rubajticos.firebaseexample.ui.sign_in_up

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentSignInBinding
import pl.rubajticos.firebaseexample.di.SignInRequest
import pl.rubajticos.firebaseexample.di.SignUpRequest
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.util.EventObserver
import java.io.FileOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(
    R.layout.fragment_sign_in,
    FragmentSignInBinding::inflate
) {
    @Inject
    lateinit var googleSignInClient: SignInClient

    @Inject
    @SignInRequest
    lateinit var googleSignInRequest: BeginSignInRequest

    @Inject
    @SignUpRequest
    lateinit var googleSignUpRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2
    private val CREATE_FILE = 1
    private val viewModel: SignInViewModel by viewModels()

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }

    }

    override fun setupView() {
        binding.signInEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onEvent(SignInFormEvent.EmailChanged(p0.toString()))
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.signInPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onEvent(SignInFormEvent.PasswordChanged(p0.toString()))
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.mode.setOnCheckedChangeListener { _, checked ->
            viewModel.onEvent(SignInFormEvent.ModeChanged(checked))
        }

        binding.signIn.setOnClickListener {
            writeStorageAccessFrameworkFile(requireContext())
//            updateOrRequestPermissions()

//            viewModel.onEvent(SignInFormEvent.Submit)
//            val file = File(
//                Environment.getExternalStorageDirectory(),
//                "MRTest.txt"
//            )
//            val imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//
//            val contentValue = ContentValues().apply {
//                put(MediaStore.Images.Media.DISPLAY_NAME, "test.jpg")
//                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//            }
//
//            try {
//                requireActivity().contentResolver.insert(imageCollection, contentValue)
//            } catch (e: IOException) {
//                Log.d("MRMR", e.localizedMessage ?: "Error")
//            }
//
//            val file = Environment.getExternalStorageDirectory()
////            file.mkdirs()
//            if (file.exists()) {
//                Log.d("MRMR", file.absolutePath)
//            } else {
//                Log.d("MRMR", "File not exists")
//            }
        }

        binding.googleSignInBtn.setOnClickListener {
            viewModel.onGoogleButtonClick()
        }
    }

    private fun writeStorageAccessFrameworkFile(context: Context) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TITLE, "MRMR")
        startActivityForResult(intent, CREATE_FILE)
    }

    private fun beginSignInWithGoogle(request: BeginSignInRequest) {
        googleSignInClient.beginSignIn(request)
            .addOnSuccessListener {
                try {
                    startIntentSenderForResult(
                        it.pendingIntent.intentSender,
                        REQ_ONE_TAP,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("MRMR", "starting intent failure ${e.localizedMessage}")
                }
            }
            .addOnFailureListener {
                Log.d("MRMR", "beginSignIn failure ${it.localizedMessage}")
            }
    }

    override fun observeEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is SignInEvent.Loading -> showToast(getString(R.string.loading))
                is SignInEvent.Error -> showToast(it.text.asString(requireContext()))
                is SignInEvent.Success -> showToast(it.text.asString(requireContext()))
                is SignInEvent.State -> {
                    binding.signInEmailTextInput.error =
                        it.state.emailError?.asString(requireContext())
                    binding.signInPasswordTextInput.error =
                        it.state.passwordError?.asString(requireContext())
                    binding.mode.isChecked = it.state.registerMode
                    if (it.state.registerMode) {
                        binding.signIn.text = getString(R.string.sign_up)
                        binding.googleSignInBtn.text = getString(R.string.sign_up_google)
                    } else {
                        binding.signIn.text = getString(R.string.sign_in)
                        binding.googleSignInBtn.text = getString(R.string.sign_in_google)
                    }
                }
                SignInEvent.SignInWithGoogle -> beginSignInWithGoogle(googleSignInRequest)
                SignInEvent.SignUpWithGoogle -> beginSignInWithGoogle(googleSignUpRequest)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = googleSignInClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    Log.d("MRMR", "token=$idToken")
                    if (idToken != null) {
                        viewModel.onEvent(SignInFormEvent.SubmitWithGoogle(idToken))
                    } else {
                        Log.d("MRMR", " Google ID Token is null")
                    }
                } catch (e: ApiException) {
                    Log.d("MRMR", "result ${e.localizedMessage}")
                }
            }
            CREATE_FILE -> {
                Log.d("MRMR", "File created")
                try {
                    data?.data?.let {
                        val pfd: ParcelFileDescriptor? =
                            requireActivity().contentResolver.openFileDescriptor(it, "w")
                        val fileOutputStream = FileOutputStream(pfd?.fileDescriptor)
                        fileOutputStream.write(
                            """Overwritten at ${System.currentTimeMillis()}
    """.toByteArray()
                        )
                        fileOutputStream.close()
                        pfd?.close()
                        fileOutputStream.close()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MRMR", "Document not written")
                }
            }

        }
    }
}