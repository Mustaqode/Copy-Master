package dev.mustaq.clipboard.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dev.mustaq.clipboard.R
import kotlinx.android.synthetic.main.fragment_clips_dialog.*

/**
 * Created by Mustaq Sameer on 05/04/20
 */
class ClipsDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_clips_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setupUI()
    }

    private fun setupUI() {
        uiTvClip.text = arguments?.getString(KEY_CLIP)
    }

    companion object {
        private const val KEY_CLIP = "key.clip"
        fun newInstance(clip: String) = ClipsDialogFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_CLIP, clip)
            }
        }
    }

}