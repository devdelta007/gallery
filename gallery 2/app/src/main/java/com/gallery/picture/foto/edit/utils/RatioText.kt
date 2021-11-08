package com.gallery.picture.foto.edit.utils

import androidx.annotation.StringRes
import com.gallery.picture.foto.R

enum class RatioText constructor(@StringRes val ratioTextId: Int, val aspectRatio: AspectRatio) {
    FREE(R.string.iamutkarshtiwari_github_io_ananas_free_size, AspectRatio()),
//    FIT_IMAGE(R.string.iamutkarshtiwari_github_io_ananas_fit_image, AspectRatio(-1f, -1f)),

    //    SQUARE(R.string.iamutkarshtiwari_github_io_ananas_square, AspectRatio(1, 1)),
//    RATIO_3_4(R.string.iamutkarshtiwari_github_io_ananas_ratio3_4, AspectRatio(3, 4)),
//    RATIO_4_3(R.string.iamutkarshtiwari_github_io_ananas_ratio4_3, AspectRatio(4, 3)),
//    RATIO_9_16(R.string.iamutkarshtiwari_github_io_ananas_ratio9_16, AspectRatio(9, 16)),
//    RATIO_16_9(R.string.iamutkarshtiwari_github_io_ananas_ratio16_9, AspectRatio(16, 9)),
//    RATIO_5_9(R.string.iamutkarshtiwari_github_io_ananas_ratio5_9, AspectRatio()),
//    ASPECT_FREE(R.string.iamutkarshtiwari_github_io_ananas_ratio5_9, AspectRatio()),
//    ASPECT_INS_1_1(R.string.aspect_ins_1_1, AspectRatio(1f, 1f)),
    ASPECT_INS_4_5(R.string.aspect_ins_4_5, AspectRatio(4f, 5f)),
    ASPECT_INS_STORY(R.string.aspect_ins_story, AspectRatio(9f, 16f)),
    ASPECT_5_4(R.string.aspect_5_4, AspectRatio(5f, 4f)),
    ASPECT_3_4(R.string.aspect_3_4, AspectRatio(3f, 4f)),
    ASPECT_4_3(R.string.aspect_4_3, AspectRatio(4f, 3f)),
    ASPECT_FACE_POST(R.string.aspect_face_post, AspectRatio(1.91f, 1f)),
    ASPECT_FACE_COVER(R.string.aspect_face_cover, AspectRatio(2.62f, 1f)),
    ASPECT_PIN_POST(R.string.aspect_pin_post, AspectRatio(2f, 3f)),
    ASPECT_3_2(R.string.aspect_3_2, AspectRatio(3f, 2f)),
    ASPECT_9_16(R.string.aspect_9_16, AspectRatio(9f, 16f)),
    ASPECT_16_9(R.string.aspect_16_9, AspectRatio(16f, 9f)),
    ASPECT_1_2(R.string.aspect_1_2, AspectRatio(1f, 2f)),
    ASPECT_YOU_COVER(R.string.aspect_you_cover, AspectRatio(1.77f, 1f)),
    ASPECT_TWIT_POST(R.string.aspect_twit_post, AspectRatio(1.91f, 1f)),
    ASPECT_TWIT_HEADER(R.string.aspect_twit_header, AspectRatio(3f, 1f))
//    ASPECT_A_4(R.string.aspect_a_4, AspectRatio(0.7f, 1f)),
//    ASPECT_A_5(R.string.aspect_a_5, AspectRatio(0.7f, 1f))
}

data class AspectRatio(
        val aspectX: Float = 0f,
        val aspectY: Float = 0f
)
