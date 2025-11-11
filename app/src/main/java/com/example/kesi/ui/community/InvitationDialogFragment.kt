package com.example.kesi.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kesi.databinding.DialogInvitationBinding

class InvitationDialogFragment(private val inviter: String, private val groupName: String) : DialogFragment() {

//    private var _binding: DialogInvitationBinding? = null
//    private val binding get()= _binding!!
//

    private lateinit var binding: DialogInvitationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogInvitationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvInvite.text = "${inviter}님이 초대함 :"
        binding.tvGruopName.text = groupName

        binding.btnAcceptance.setOnClickListener {

        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}
