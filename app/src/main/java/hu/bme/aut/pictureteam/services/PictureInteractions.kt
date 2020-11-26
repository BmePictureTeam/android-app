package hu.bme.aut.pictureteam.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import hu.bme.aut.pictureteam.models.Picture
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class UploadException(msg: String) : Exception(msg)

object PictureInteractions {
    suspend fun search(): List<Picture> {
        val images: List<Picture>

        val api: Api = Api.getInstance()

        images = api.searchPictures().images.map { pic ->
            val resBody = Api.getInstance().getPicture(pic.id)
            val resBytes = resBody.byteStream()
            val bitmap = BitmapFactory.decodeStream(resBytes)

            val rating = Api.getInstance().getPictureRating(pic.id)

            Picture(
                title = pic.title,
                id = pic.id,
                image = bitmap,
                categories = pic.categories.toMutableList(),
                description = pic.description ?: "",
                date = pic.date,
                rating = rating.average,
                ratingCount = rating.rating_count
            )
        }

        return images
    }

    suspend fun upload(picture: Picture) {
        val res: retrofit2.Response<Unit>

        Categories.updateCategories()

        val id = Api
            .getInstance()
            .createPicture(
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

        res = Api.getInstance().uploadPicture(id, part)

        if (res.code() != 204) {
            throw UploadException(res.errorBody().toString())
        }
    }
}