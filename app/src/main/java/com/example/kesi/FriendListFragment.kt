package com.example.kesi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.databinding.FragmentFriendListBinding
import com.example.kesi.databinding.FragmentListBinding
import com.example.kesi.databinding.ItemFriendListBinding

class FriendListFragment : Fragment() {
    lateinit var binding:FragmentFriendListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileList = ArrayList<Profile>()
        //TODO 친구목록 삽입
        profileList.add(Profile(name="김재원"))
        profileList.add(Profile(name="김태수"))
        profileList.add(Profile(name="김시현"))
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = FriendListAdapter(profileList)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(),LinearLayoutManager.VERTICAL))
    }
}

//뷰 홀더 클래스
class FriendListViewHolder(val binding:ItemFriendListBinding): RecyclerView.ViewHolder(binding.root)

//어댑터 클래스
class FriendListAdapter(val profile: ArrayList<Profile>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //뷰 홀더를 준비하려고 자동으로 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val binding = ItemFriendListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FriendListViewHolder(binding)
    }

    //항목의 개수를 판단하려고 자동으로 호출되는 함수
    override fun getItemCount(): Int = profile.size

    //뷰 홀더의 뷰에 데이터를 출력하려고 자동으로 호출되는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as FriendListViewHolder).binding
        binding.imageView.setImageResource(profile[position].image)
        binding.tvName.text = profile[position].name
        binding.itemRoot.setOnClickListener{
            //TODO 각각의 항목을 클릭했을 때 이벤트 처리
        }
    }

}

//프로필 데이터 클래스
data class Profile(val image: Int = R.drawable.ic_user, val name:String)