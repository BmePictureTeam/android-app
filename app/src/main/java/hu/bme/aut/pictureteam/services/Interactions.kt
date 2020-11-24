package hu.bme.aut.pictureteam.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.ui.main.PictureAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

object Interactions {
    suspend fun search(adapter: PictureAdapter): List<Picture> {
        val images: List<Picture>

        withContext(Dispatchers.IO) {
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
        }

        return images
    }

    suspend fun upload(picture: Picture): retrofit2.Response<Unit> {
        val res: retrofit2.Response<Unit>

        withContext(Dispatchers.IO) {
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
        }

        return res
    }
}