package `in`.trendition.ui

import `in`.trendition.BuildConfig
import `in`.trendition.network.BoutiqueService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivityViewModel @Inject constructor(private val boutiqueService: BoutiqueService) :
    ViewModel() {
    private val isUpdateAvailable: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val downloadProgress: MutableLiveData<Int> = MutableLiveData(0)
    private val isUpdateDownloaded: MutableLiveData<Boolean> = MutableLiveData(false)
    var downloadUrl = ""

    // Track upload progress.
    private val uploadProgress: MutableLiveData<Int> = MutableLiveData(0)

    fun getIsUpdateAvailable(): LiveData<Boolean?> = isUpdateAvailable
    fun getDownloadProgress(): LiveData<Int> = downloadProgress
    fun getIsUpdateDownloaded(): LiveData<Boolean> = isUpdateDownloaded
    fun setIsUpdateAvailable(isUpdateAvailable: Boolean) {
        this.isUpdateAvailable.postValue(isUpdateAvailable)
    }

    fun getUploadProgress(): LiveData<Int> = uploadProgress
    fun setUploadProgress(progress: Int) {
        uploadProgress.postValue(progress)
    }

    fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = boutiqueService.checkUpdate().execute()
                response.body()?.let {
                    (it as LinkedTreeMap<*, *>).run {
                        val dateFormat = SimpleDateFormat("yyyy.MM.dd.HHmmss")
                        val currentVersionDate = dateFormat.parse(BuildConfig.VERSION_NAME)
                        val latestVersionDate = dateFormat.parse(this["version"] as String)
                        if (currentVersionDate != null && latestVersionDate != null && currentVersionDate.before(
                                latestVersionDate
                            )
                        ) {
                            // New update
                            downloadUrl = this["url"] as String
                            setIsUpdateAvailable(isUpdateAvailable = true)
                            return@launch
                            // }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setIsUpdateAvailable(isUpdateAvailable = false)
        }
    }

    fun downloadUpdatePackage(externalFilesDir: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val connection = URL(downloadUrl).openConnection() as HttpURLConnection
                val localFile =
                    File(externalFilesDir, downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1))
                var localFileSize: Long
                connection.run {
                    if (!localFile.exists())
                        localFile.createNewFile()
                    localFileSize = localFile.length()
                    setRequestProperty("Range", "bytes=$localFileSize-")
                    setRequestProperty("Accept-Encoding", "identity")
                    connect()
                }

                val contentLength = connection.contentLengthLong
                if (contentLength <= 0) {
                    connection.disconnect()
                    isUpdateDownloaded.postValue(true)
                    return@launch
                }
                val totalLength = contentLength + localFileSize.toInt()

                val downloadStream = connection.inputStream
                val outputFile = if (localFileSize > 0)
                    FileOutputStream(localFile, true)
                else
                    FileOutputStream(localFile)

                val data = ByteArray(1024)
                var bytesDownloaded = localFileSize
                while (true) {
                    val nextByte = downloadStream.read(data)
                    if (nextByte == -1)
                        break
                    outputFile.write(data, 0, nextByte)
                    bytesDownloaded += nextByte

                    // Update progress here
                    downloadProgress.postValue(((bytesDownloaded * 100) / totalLength).toInt())
                }
                isUpdateDownloaded.postValue(true)
                return@launch
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isUpdateDownloaded.postValue(false)
        }
    }
}