package com.example.shsfirstapp.ui.mp3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shsfirstapp.data.AlarmDbHelper
import com.example.shsfirstapp.data.Mp3ViewModel
import com.example.shsfirstapp.data.Mp3ViewModelFactory
import com.example.shsfirstapp.databinding.FragmentMp3Binding
import java.io.File
import java.io.FileOutputStream

class Mp3Fragment : Fragment() {

    companion object {
        const val REQUEST_CODE_PICK_MP3 = 1001
        const val MP3_DIRECTORY = "alarm_sounds"
    }

    private var _binding: FragmentMp3Binding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: Mp3ViewModel
    private lateinit var adapter: Mp3Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMp3Binding.inflate(inflater, container, false)
        val dbHelper = AlarmDbHelper.getInstance(requireContext())
        val factory = Mp3ViewModelFactory(dbHelper)
        viewModel = ViewModelProvider(this, factory).get(Mp3ViewModel::class.java)

        setupRecyclerView()
        setupUploadButton()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = Mp3Adapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // ViewModel의 mp3s LiveData를 관찰
        viewModel.mp3s.observe(viewLifecycleOwner) { mp3s ->
            adapter.submitList(mp3s)
        }
    }

    private fun setupUploadButton() {
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "audio/*"
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_MP3)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_MP3 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileName(uri)
                val destFile = saveToInternalStorage(uri, fileName)
                if (destFile != null) {
                    viewModel.addMp3(fileName, destFile.absolutePath)
                    Toast.makeText(requireContext(), "MP3가 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "MP3 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    private fun saveToInternalStorage(uri: Uri, fileName: String): File? {
        return try {
            val storageDir = File(requireContext().getExternalFilesDir(null), MP3_DIRECTORY)
            if (!storageDir.exists()) storageDir.mkdirs()
            val destFile = File(storageDir, fileName)
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
