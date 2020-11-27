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
            val rating = Api.getInstance().getPictureRating(pic.id)

            Picture(
                title = pic.title,
                id = pic.id,
                categories = pic.categories.toMutableList(),
                description = pic.description ?: "",
                date = pic.date,
                rating = rating.average,
                ratingCount = rating.rating_count
            )
        }

        return images
    }

    suspend fun upload(picture: Picture, bitmap: Bitmap) {
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
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
            val rating = Api.getInstance().getPictureRating(pic.id)

            return Picture(
                title = pic.title,
                id = pic.id,
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
     * Returns whether the picture can be rated.
     */
    suspend fun rate(id: String, rating: Int): Boolean {
        try {
            Api.getInstance().ratePicture(id, ApiRatePictureRequestBody(rating = rating))
        } catch (e: HttpException) {
            if (e.code() == 403) {
                return false
            }
            throw e
        }

        return true
    }

    suspend fun bitmap(pic: Picture): Bitmap {
        return BitmapFactory.decodeStream(loadPicture(pic))
    }

    /**
     * Retrieves the image either from local file cache or
     * from the API.
     */
    suspend fun loadPicture(pic: Picture): InputStream {
        val localImage = cacheDir?.resolve(pic.id!!)
        if (localImage != null) {
            try {
                return localImage.inputStream()
            } catch (e: Exception) {
                // Not found locally, can be ignored.
            }
        }

        var imageStream = Api.getInstance().downloadPicture(pic.id!!).byteStream()

        // Save it, and serve it from file.
        if (localImage != null) {
            imageStream.copyTo(localImage.outputStream())
            imageStream = localImage.inputStream()
        }

        return imageStream
    }
}