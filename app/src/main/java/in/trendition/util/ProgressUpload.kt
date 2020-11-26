package `in`.trendition.util

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProgressUpload {
    private var totalBytesToUpload = 0L
    private var totalBytesUploaded = 0L
    private var listener: UploadListener? = null

    interface UploadListener {
        fun onProgressUpdate(percentage: Int)
        fun onError(e: Exception)
    }

    companion object {
        const val DEFAULT_BUFFER_SIZE = 2048
    }

    inner class ProgressRequestBody(private val mediaType: MediaType?, private val content: Any) :
        RequestBody() {

        override fun contentType(): MediaType? = mediaType

        override fun contentLength(): Long =
            if (content is File)
                content.length()
            else
                (content as String).toByteArray().size.toLong()

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            try {
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                if (content is File) {
                    val inputStream = FileInputStream(content)
                    var bytesRead = inputStream.read(buffer)
                    while (bytesRead != -1) {
                        // Write to buffer.
                        sink.write(buffer, 0, bytesRead)

                        // Update totalBytesUploaded and notify about the progress percentage.
                        totalBytesUploaded += bytesRead
                        val progress = ((totalBytesUploaded * 100) / totalBytesToUpload).toInt()
                        listener?.onProgressUpdate(progress)

                        // Read from buffer.
                        bytesRead = inputStream.read(buffer)
                    }
                } else {
                    val inputStream = (content as String).byteInputStream()
                    var bytesRead = inputStream.read(buffer)
                    while (bytesRead != -1) {
                        // Write to buffer.
                        sink.write(buffer, 0, bytesRead)

                        // Update totalBytesUploaded and notify about the progress percentage.
                        totalBytesUploaded += bytesRead
                        listener?.onProgressUpdate(((totalBytesUploaded * 100) / totalBytesToUpload).toInt())

                        // Read from buffer.
                        bytesRead = inputStream.read(buffer)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onError(e)
            }
        }
    }

    fun prepareToUpload(
        contentMap: HashMap<String, ProgressRequestBody>,
        files: List<MultipartBody.Part>
    ) {
        // Add text content length.
        for (requestBody in contentMap.values)
            totalBytesToUpload += requestBody.contentLength()
        // Add files content length.
        for (file in files)
            totalBytesToUpload += file.body().contentLength()
    }

    fun setUploadListener(listener: UploadListener) {
        this.listener = listener
    }
}