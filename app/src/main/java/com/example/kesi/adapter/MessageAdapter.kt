package com.example.kesi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kesi.R
import com.example.kesi.data.Message
import com.example.kesi.model.GroupDto
import com.example.kesi.model.GroupMemberDto
import com.example.kesi.setting.RetrofitSetting
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>, private val groupDto: GroupDto) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    //메시지에 따라 어떤 뷰 홀더를 사용할 지를 정하기 위해 아래 변수를 만듦.
    private val receive = 1//받는 타입
    private val send = 2//보내는 타입
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == receive){//받는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            ReceiveViewHolder(view)
        } else {//보내는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
            SendViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //현재 메시지
        val currentMessage = messageList[position]

        //보내는 데이터
        if(holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = currentMessage.message
        } else { //받는 데이터
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message

            if (position == 0) {
                return
            }
            //이전 메시지
            val previousMessage = messageList[position-1]

            if(currentMessage.sendUserEmail == previousMessage.sendUserEmail) {
                viewHolder.sender.visibility = View.GONE
            } else {
                viewHolder.sender.visibility = View.VISIBLE

                val target = groupDto.members.find {
                    currentMessage.sendUserEmail == it.email
                }
                Glide.with(viewHolder.sender)
                    .load(RetrofitSetting.IMAGE_URL + target?.email + target?.imgPath)
                    .fallback(R.drawable.ic_user) //URL에 있는 값이 null 인 경우
                    .error(R.drawable.ic_user) //URL load하는데 문제가 발생한 경우
                    .placeholder(R.drawable.ic_user) //이미지를 가져오기 전까지 보여줄 이미지
                    .into(viewHolder.ivProfile)

                viewHolder.tvName.text = target?.email
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        //메시지 값
        val currentMessage = messageList[position]

        return if(FirebaseAuth.getInstance().currentUser?.email.equals(currentMessage.sendUserEmail)) {
            send
        } else {
            receive
        }
    }

    //보낸 쪽
    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.tvSendMessage)
    }
    //받는 쪽
    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.tvReceiveMessage)
        val sender: LinearLayout = itemView.findViewById(R.id.sender)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}