package hu.bme.aut.pictureteam.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import hu.bme.aut.pictureteam.models.Picture
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class UploadException(msg: String) : Exception(msg)

object PictureInteractions {
    var cacheDir: File? = null

    suspend fun search(offset: Int? = 0, text: String? = null): List<Picture> {
        val images: List<Picture>

        val api: Api = Api.getInstance()

        images = api.searchPictures(offset ?: 0, 10, search = text).images.map { pic ->
            val bitmap = BitmapFactory.decodeStream(getImage(pic.id))

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
                    picture.title
                )
            ).id

        val stream = ByteArrayOutputStream()
        picture.image!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.toByteArray()
        val byteArray = stream.toByteArray()

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

    suspend fun getById(id: String): Picture? {
        try {
            val pic = Api.getInstance().getPicture(id).image
            val resBody = Api.getInstance().downloadPicture(pic.id)
            val resBytes = resBody.byteStream()
            val bitmap = BitmapFactory.decodeStream(resBytes)

            val rating = Api.getInstance().getPictureRating(pic.id)

            return Picture(
                title = pic.title,
                id = pic.id,
                image = bitmap,
                categories = pic.categories.toMutableList(),
                description = pic.description ?: "",
                date = pic.date,
                rating = rating.average,
                ratingCount = rating.rating_count
            )
        } catch (e: HttpException) {
            if (e.code() == 404) {
                return null
            }

            throw e
        }
    }

    /**
     * Retrieves the image either from local file cache or
     * from the API.
     */
    private suspend fun getImage(id: String): InputStream {
        val localImage = cacheDir?.resolve(id)
        if (localImage != null) {
            try {
                return localImage.inputStream()
            } catch(e: Exception) {
                // Not found locally, can be ignored.
            }
        }

        var imageStream = Api.getInstance().downloadPicture(id).byteStream()

        // Save it, and serve it from file.
        if (localImage != null) {
            imageStream.copyTo(localImage.outputStream())
            imageStream = localImage.inputStream()
        }

        return imageStream
    }
}