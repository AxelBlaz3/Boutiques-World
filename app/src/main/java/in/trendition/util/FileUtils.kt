package `in`.trendition.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Object for handling File related operations
 */
object FileUtils {

    /**
     * Copy InputStream into a file.
     * @param inputStream: InputStream that should be copied.
     * @param imageFile: Target file.
     */
    fun copyInputStreamToFile(inputStream: InputStream, imageFile: File?) {
        try {
            inputStream.use { input ->
                FileOutputStream(imageFile).use { output ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get the extension for the given content uri.
     * @param context: Context.
     * @param imageUri: Content uri of selected file.
     * @return String: File extension (jpg, png, etc...).
     */
    fun getExtension(context: Context, imageUri: Uri): String {
        try {
            context.contentResolver.query(imageUri, null, null, null, null)?.let { cursor ->
                cursor.moveToFirst()
                val fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                return fileName.substring(fileName.lastIndexOf('.') + 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Return default extension
        return "png"
    }
}