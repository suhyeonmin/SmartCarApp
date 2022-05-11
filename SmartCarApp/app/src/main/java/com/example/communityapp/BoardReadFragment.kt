package com.example.communityapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.communityapp.databinding.BoardMainRecyclerItemBinding
import com.example.communityapp.databinding.FragmentBoardMainBinding
import com.example.communityapp.databinding.FragmentBoardReadBinding
import okhttp3.FormBody
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread


class BoardReadFragment : Fragment() {

    lateinit var boardReadFragmentBinding : FragmentBoardReadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        boardReadFragmentBinding = FragmentBoardReadBinding.inflate(inflater)

        boardReadFragmentBinding.boardReadToolbar.title = "게시글읽기"

        val navIcon = requireContext().getDrawable(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        boardReadFragmentBinding.boardReadToolbar.navigationIcon = navIcon

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            boardReadFragmentBinding.boardReadToolbar.navigationIcon?.colorFilter = BlendModeColorFilter(
                Color.parseColor("#FFFFFF"), BlendMode.SRC_ATOP)
        } else {
            boardReadFragmentBinding.boardReadToolbar.navigationIcon?.setColorFilter(
                Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP
            )
        }

        boardReadFragmentBinding.boardReadToolbar.setNavigationOnClickListener{
            val act = activity as BoardMainActivity
            act.fragmentRemoveBackStack("board_read")
        }

        thread{
            val client = OkHttpClient()

            val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/get_content.jsp"

            val act = activity as BoardMainActivity

            val builder1 = FormBody.Builder()
            builder1.add("read_content_idx","${act.readContentIdx}")
            val formBody = builder1.build()

            val request = Request.Builder().url(site).post(formBody).build()
            val response = client.newCall(request).execute()

            if(response.isSuccessful == true){
                val resultText = response.body?.string()!!.trim()
                val obj = JSONObject(resultText)

                val contentWriterIdx = obj.getInt("content_writer_idx")

                activity?.runOnUiThread{
                    boardReadFragmentBinding.boardReadSubject.text = obj.getString("content_subject");
                    boardReadFragmentBinding.boardReadWriter.text = obj.getString("content_nick_name")
                    boardReadFragmentBinding.boardReadWriteDate.text = obj.getString("content_write_date")
                    boardReadFragmentBinding.boardReadText.text = obj.getString("content_text")

                    val contentImage = obj.getString("content_image")
                    if(contentImage == "null"){
                        boardReadFragmentBinding.boardReadImage.visibility = View.GONE
                    } else {
                        thread {
                            val imageUrl = URL("http://${ServerInfo.SERVER_IP}:8080/CommunityServer/upload/$contentImage")
                            val bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                            activity?.runOnUiThread{
                                boardReadFragmentBinding.boardReadImage.setImageBitmap(bitmap)
                            }
                        }
                    }
                    val pref = requireContext().getSharedPreferences("login_data", Context.MODE_PRIVATE)
                    val loginUserIdx = pref.getInt("login_user_idx", -1)

                    if(loginUserIdx == contentWriterIdx){
                        boardReadFragmentBinding.boardReadToolbar.inflateMenu(R.menu.board_read_menu)
                        boardReadFragmentBinding.boardReadToolbar.setOnMenuItemClickListener{
                            when(it.itemId){
                                R.id.board_read_menu_modify -> {
                                    val act = activity as BoardMainActivity
                                    act.fragmentController("board_modify",true,true)
                                    true
                                }
                                R.id.board_read_menu_delete -> {

                                    thread{
                                        val act = activity as BoardMainActivity

                                        val client = OkHttpClient()

                                        val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/delete_content.jsp"

                                        val builder1 = FormBody.Builder()
                                        builder1.add("content_idx", "${act.readContentIdx}")
                                        val formBody = builder1.build()

                                        val request = Request.Builder().url(site).post(formBody).build()
                                        val response = client.newCall(request).execute()

                                        if(response.isSuccessful == true){
                                            activity?.runOnUiThread{
                                                val dialogBuilder = AlertDialog.Builder(requireContext())
                                                dialogBuilder.setTitle("글 삭제")
                                                dialogBuilder.setMessage("글이 삭제되었습니다")
                                                dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                                                    val act = activity as BoardMainActivity
                                                    act.fragmentRemoveBackStack("board_read")
                                                }
                                                dialogBuilder.show()
                                            }
                                        }
                                    }
                                    
                                    true
                                }
                                else -> false
                            }
                        }
                    }
                }
            }
        }

        return boardReadFragmentBinding.root
    }
}