package com.example.communityapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.hardware.input.InputManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import com.example.communityapp.databinding.FragmentBoardWriteBinding
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class BoardWriteFragment : Fragment() {

    lateinit var boardWriteFragmentBinding : FragmentBoardWriteBinding

    val spinner_data = arrayOf("게시판1", "게시판2", "게시판3", "게시판4")

    lateinit var contentUri : Uri
    var uploadImage : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val act = activity as BoardMainActivity

        // Inflate the layout for this fragment
        boardWriteFragmentBinding = FragmentBoardWriteBinding.inflate(inflater)
        boardWriteFragmentBinding.boardWriteToolbar.title = "게시글 작성"
        boardWriteFragmentBinding.boardWriteToolbar.inflateMenu(R.menu.board_write_menu)
        boardWriteFragmentBinding.boardWriteToolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.board_write_menu_camera -> {
                    val filePath = requireContext().getExternalFilesDir(null).toString()

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    // 촬영한 사진이 저장될 파일 이름
                    val fileName = "/temp_${System.currentTimeMillis()}.jpg"
                    val picPath = "$filePath/$fileName"

                    val file = File(picPath)

                    contentUri = FileProvider.getUriForFile(requireContext(),
                        "kr.co.communityapp.camera.file_provider", file)

                    if(contentUri != null){
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                        startActivityForResult(cameraIntent, 1)
                    }

                    true
                }
                R.id.board_write_menu_gallery -> {

                    val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    albumIntent.type = "image/*"

                    val mimeType = arrayOf("image/*")
                    albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                    startActivityForResult(albumIntent, 2)

                    true
                }
                R.id.board_write_menu_upload -> {
                    val act = activity as BoardMainActivity

                    val boardWriteSubject = boardWriteFragmentBinding.boardWriteSubject.text.toString()
                    val boardWriteText = boardWriteFragmentBinding.boardWriteText.text.toString()
                    val boardWriteType = act.boardIndexList[boardWriteFragmentBinding.boardWriteType.selectedItemPosition + 1]

                    val pref = requireContext().getSharedPreferences("login_data", Context.MODE_PRIVATE)
                    val boardWriterIdx = pref.getInt("login_user_idx", 0)

                    if(boardWriteSubject == null || boardWriteSubject.length == 0){
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle("제목 입력 오류")
                        dialogBuilder.setMessage("제목을 입력해 주세요")
                        dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                            boardWriteFragmentBinding.boardWriteSubject.requestFocus()
                        }
                        dialogBuilder.show()
                        return@setOnMenuItemClickListener true
                    }

                    if(boardWriteText == null || boardWriteText.length == 0){
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle("내용 입력 오류")
                        dialogBuilder.setMessage("내용을 입력해 주세요")
                        dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                            boardWriteFragmentBinding.boardWriteText.requestFocus()
                        }
                        dialogBuilder.show()
                        return@setOnMenuItemClickListener true
                    }

                    thread {
                        val client = OkHttpClient()

                        val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/add_content.jsp"

                        val builder1 = MultipartBody.Builder()
                        builder1.setType(MultipartBody.FORM)
                        builder1.addFormDataPart("content_board_idx", "$boardWriteType")
                        builder1.addFormDataPart("content_writer_idx", "$boardWriterIdx")
                        builder1.addFormDataPart("content_subject", boardWriteSubject)
                        builder1.addFormDataPart("content_text", boardWriteText)

                        var file : File? = null

                        if(uploadImage != null){
                            val filePath = requireContext().getExternalFilesDir(null).toString()
                            val fileName = "/temp_${System.currentTimeMillis()}.jpg"
                            val picPath = "$filePath/$fileName"
                            file = File(picPath)
                            val fos = FileOutputStream(file)
                            uploadImage?.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                            builder1.addFormDataPart("content_image", file.name, file.asRequestBody(MultipartBody.FORM))
                        }

                        val formBody = builder1.build()

                        val request = Request.Builder().url(site).post(formBody).build()
                        val response = client.newCall(request).execute()

                        if(response.isSuccessful == true){

                            val resultText = response.body?.string()!!.trim()
                            act.readContentIdx = Integer.parseInt(resultText)
                            // Log.d("test", "${act.readContentIdx}")

                            activity?.runOnUiThread{
                                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputMethodManager.hideSoftInputFromWindow(boardWriteFragmentBinding.boardWriteSubject.windowToken, 0)
                                inputMethodManager.hideSoftInputFromWindow(boardWriteFragmentBinding.boardWriteText.windowToken, 0)

                                val dialogBuilder = AlertDialog.Builder(requireContext())
                                dialogBuilder.setTitle("작성 완료")
                                dialogBuilder.setMessage("작성이 완료되었습니다")
                                dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                                    act.fragmentRemoveBackStack("board_write")
                                    act.fragmentController("board_read",true,true)
                                }
                                dialogBuilder.show()
                            }
                        } else {
                            activity?.runOnUiThread{
                                val dialogBuilder = AlertDialog.Builder(requireContext())
                                dialogBuilder.setTitle("작성 오류")
                                dialogBuilder.setMessage("작성 오류가 발생하였습니다")
                                dialogBuilder.setPositiveButton("확인", null)
                                dialogBuilder.show()
                            }
                        }
                    }

                    true
                }
                else -> false
            }
        }

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, act.boardNameList.drop(1))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        boardWriteFragmentBinding.boardWriteType.adapter = spinnerAdapter

        if(act.selectedBoardType == 0){
            boardWriteFragmentBinding.boardWriteType.setSelection(0)
        }else{
            boardWriteFragmentBinding.boardWriteType.setSelection(act.selectedBoardType - 1)
        }

        return boardWriteFragmentBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            1 -> {
                if(resultCode == Activity.RESULT_OK){
                    uploadImage = BitmapFactory.decodeFile(contentUri.path)
                    boardWriteFragmentBinding.boardWriteImage.setImageBitmap(uploadImage)

                    val file = File(contentUri.path)
                    file.delete()
                }
            }
            2 -> {
                if(resultCode == Activity.RESULT_OK) {
                    // 선택한 이미지에 접근할 수 있는 uri
                    val uri = data?.data

                    if(uri != null){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            val source = ImageDecoder.createSource(activity?.contentResolver!!, uri)
                            uploadImage = ImageDecoder.decodeBitmap(source)
                            boardWriteFragmentBinding.boardWriteImage.setImageBitmap(uploadImage)
                        } else {
                            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
                            if(cursor != null){
                                cursor.moveToNext()
                                // 이미지 경로를 가져온다.
                                val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                val source = cursor.getString(index)
                                uploadImage = BitmapFactory.decodeFile(source)
                                boardWriteFragmentBinding.boardWriteImage.setImageBitmap(uploadImage)
                            }
                        }
                    }
                }
            }
        }
    }
}