package hu.bme.aut.pictureteam.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import hu.bme.aut.pictureteam.models.Picture
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class UploadException(msg: String): Exception(msg)

object PictureInteractions {
    suspend fun search(): List<Picture> {
        val images: List<Picture>

        val api: Api = Api.getInstance()

        images = api.searchPictures().images.map {
            val resBody = Api.getInstance().getPicture(it.id)
            val resBytes = resBody.byteStream()
            val bitmap = BitmapFactory.decodeStream(resBytes)

            Picture(
                bitmap,
                it.title,
                it.categories.map {
                    Categories.categoryIdToTitle[it]!!
                }.toMutableList(),
                it.description ?: "",
                ""
            )
        }

        return images
    }

    suspend fun upload(picture: Picture) {
        val res: retrofit2.Response<Unit>

        Categories.updateCategories()

        val id = Api
            .getInstance()
            .createImage(
                ApiCreateImageRequestBody(
                    picture.categories,
                    picture.description,
                    picture.title!!
                )
            ).id

        val stream = ByteArrayOutputStream()
        picture.image!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.toByteArray()
        val byteArray = stream.toByteArray()
        picture.image.recycle()

        val part = MultipartBody.Part.createFormData(
            "image",
            "image.png",
            RequestBody.create(MediaType.parse("image/*"), byteArray)
        )

        res = Api.getInstance().uploadImage(id, part)

        if (res.code() != 204) {
            throw UploadException(res.errorBody().toString())
        }
    }
}