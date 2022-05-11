package com.example.communityapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import com.example.communityapp.databinding.FragmentBoardModifyBinding
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.concurrent.thread

class BoardModifyFragment : Fragment() {

    lateinit var boardModifyFragmentBinding : FragmentBoardModifyBinding

    val spinnerData = arrayOf("게시판1", "게시판2", "게시판3", "게시판4")

    lateinit var contentUri: Uri
    var uploadImage : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val act = activity as BoardMainActivity

        boardModifyFragmentBinding = FragmentBoardModifyBinding.inflate(inflater)

        boardModifyFragmentBinding.boardModifyToolbar.title = "글 수정"
        boardModifyFragmentBinding.boardModifyToolbar.inflateMenu(R.menu.board_modify_menu)
        boardModifyFragmentBinding.boardModifyToolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.board_modify_menu_camera -> {
                    val filePath = requireContext().getExternalFilesDir(null).toString()

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    // 촬영한 사진이 저장될 파일 이름
                    val fileName = "/temp_${System.currentTimeMillis()}.jpg"
                    val picPath = "$filePath/$fileName"

                    val file = File(picPath)

                    contentUri = FileProvider.getUriForFile(requireContext(),
                        "kr.co.communityapp.camera.file_provider", file)

                    if(contentUri != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                        startActivityForResult(cameraIntent, 1)
                    }
                    true
                }
                R.id.board_modify_menu_gallery -> {
                    val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    albumIntent.type = "image/*"

                    val mimeType = arrayOf("image/*")
                    albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                    startActivityForResult(albumIntent, 2)

                    true
                }
                R.id.board_modify_menu_upload -> {

                    val boardModifySubject = boardModifyFragmentBinding.boardModifySubject.text.toString()
                    val boardModifyText = boardModifyFragmentBinding.boardModifyText.text.toString()
                    val boardModifyType = act.boardIndexList[boardModifyFragmentBinding.boardModifyType.selectedItemPosition + 1]

                    if (boardModifySubject == null || boardModifySubject.length == 0){
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle("제목 입력 오류")
                        dialogBuilder.setMessage("제목을 입력해주세요")
                        dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                            boardModifyFragmentBinding.boardModifySubject.requestFocus()
                        }
                        dialogBuilder.show()
                        return@setOnMenuItemClickListener true
                    }

                    if (boardModifyText == null || boardModifyText.length == 0){
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle("내용 입력 오류")
                        dialogBuilder.setMessage("내용을 입력해주세요")
                        dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                            boardModifyFragmentBinding.boardModifyText.requestFocus()
                        }
                        dialogBuilder.show()
                        return@setOnMenuItemClickListener true
                    }

                    thread {
                        val client = OkHttpClient()

                        val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/modify_content.jsp"

                        val builder1 = MultipartBody.Builder()
                        builder1.setType(MultipartBody.FORM)
                        builder1.addFormDataPart("content_idx", "${act.readContentIdx}")
                        builder1.addFormDataPart("content_subject", boardModifySubject)
                        builder1.addFormDataPart("content_text", boardModifyText)
                        builder1.addFormDataPart("content_board_idx", "$boardModifyType")

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
                            activity?.runOnUiThread{
                                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputMethodManager.hideSoftInputFromWindow(boardModifyFragmentBinding.boardModifySubject.windowToken, 0)
                                inputMethodManager.hideSoftInputFromWindow(boardModifyFragmentBinding.boardModifyText.windowToken, 0)

                                val dialogBuilder = AlertDialog.Builder(requireContext())
                                dialogBuilder.setTitle("수정완료")
                                dialogBuilder.setMessage("수정이 완료되었습니다")
                                dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                                    act.fragmentRemoveBackStack("board_modify")
                                }
                                dialogBuilder.show()
                            }
                        } else {
                            activity?.runOnUiThread {
                                val dialogBuilder = AlertDialog.Builder(requireContext())
                                dialogBuilder.setTitle("수정오류")
                                dialogBuilder.setMessage("수정 오류가 발생하였습니다")
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

        thread {
            val client = OkHttpClient()

            val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/get_content.jsp"

            val builder1 = FormBody.Builder()
            builder1.add("read_content_idx", "${act.readContentIdx}")
            val formBody = builder1.build()


            val request = Request.Builder().url(site).post(formBody).build()
            val response = client.newCall(request).execute()

            if(response.isSuccessful == true){
                val resultText = response.body?.string()!!.trim()
                val obj = JSONObject(resultText)

                act.runOnUiThread{
                    boardModifyFragmentBinding.boardModifySubject.setText(obj.getString("content_subject"))
                    boardModifyFragmentBinding.boardModifyText.setText(obj.getString("content_text"))
                    val contentImage = obj.getString("content_image")

                    if(contentImage == "null"){
                        boardModifyFragmentBinding.boardModifyImage.visibility = View.GONE
                    } else {
                        thread {
                            val imageUrl =URL("http://${ServerInfo.SERVER_IP}:8080/CommunityServer/upload/$contentImage")
                            val bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                            activity?.runOnUiThread {
                                boardModifyFragmentBinding.boardModifyImage.setImageBitmap(bitmap)
                            }
                        }
                    }

                    val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, act.boardNameList.drop(1))
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    boardModifyFragmentBinding.boardModifyType.adapter = spinnerAdapter
                    boardModifyFragmentBinding.boardModifyType.setSelection(obj.getInt("content_board_idx") - 1)
                }
            }
        }

        return boardModifyFragmentBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            1 -> {
                if(resultCode == Activity.RESULT_OK){
                    uploadImage = BitmapFactory.decodeFile(contentUri.path)
                    boardModifyFragmentBinding.boardModifyImage.setImageBitmap(uploadImage)
                    boardModifyFragmentBinding.boardModifyImage.visibility = View.VISIBLE

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
                            boardModifyFragmentBinding.boardModifyImage.setImageBitmap(uploadImage)
                            boardModifyFragmentBinding.boardModifyImage.visibility = View.VISIBLE
                        } else {
                            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
                            if(cursor != null){
                                cursor.moveToNext()
                                // 이미지 경로를 가져온다.
                                val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                val source = cursor.getString(index)
                                uploadImage = BitmapFactory.decodeFile(source)
                                boardModifyFragmentBinding.boardModifyImage.setImageBitmap(uploadImage)
                                boardModifyFragmentBinding.boardModifyImage.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }
}