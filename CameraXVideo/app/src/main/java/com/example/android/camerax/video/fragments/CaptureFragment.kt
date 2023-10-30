/**
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Simple app to demonstrate CameraX Video capturing with Recorder ( to local files ), with the
 * following simple control follow:
 *   - user starts capture.
 *   - this app disables all UI selections.
 *   - this app enables capture run-time UI (pause/resume/stop).
 *   - user controls recording with run-time UI, eventually tap "stop" to end.
 *   - this app informs CameraX recording to stop with recording.stop() (or recording.close()).
 *   - CameraX notify this app that the recording is indeed stopped, with the Finalize event.
 *   - this app starts VideoViewer fragment to view the captured result.
 */

package com.example.android.camerax.video.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.res.Configuration
import java.text.SimpleDateFormat
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.android.camerax.video.R
import com.example.android.camerax.video.databinding.FragmentCaptureBinding
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.video.*
import androidx.concurrent.futures.await
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.camera.utils.GenericListAdapter
import com.example.android.camerax.video.extensions.getAspectRatio
import com.example.android.camerax.video.extensions.getAspectRatioString
import com.example.android.camerax.video.extensions.getNameString
import kotlinx.coroutines.*
import java.util.*

class CaptureFragment : Fragment() {

    // UI with ViewBinding
    private var _captureViewBinding: FragmentCaptureBinding? = null
    private val captureViewBinding get() = _captureViewBinding!!
    private val captureLiveStatus = MutableLiveData<String>()

    /** Host's navigation controller */
    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.fragment_container)
    }

    private val cameraCapabilities = mutableListOf<CameraCapability>()

    private lateinit var videoCapture: VideoCapture<Recorder>
    private var currentRecording: Recording? = null
    private lateinit var recordingState: VideoRecordEvent

    // Camera UI  states and inputs
    enum class UiState {
        IDLE,       // Not recording, all UI controls are active.
        RECORDING,  // Camera is recording, only display Pause/Resume & Stop button.
        FINALIZED,  // Recording just completes, disable all RECORDING UI controls.
        RECOVERY    // For future use.
    }

    private var cameraIndex = 0
    private var qualityIndex = DEFAULT_QUALITY_IDX
    private var audioEnabled = false

    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private var enumerationDeferred: Deferred<Unit>? = null

    // main cameraX capture functions
    /**
     *   Always bind preview + video capture use case combinations in this sample
     *   (VideoCapture can work on its own). The function should always execute on
     *   the main thread.d
     */
    private suspend fun bindCaptureUsecase() {
        val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()

        val cameraSelector = getCameraSelector(cameraIndex)

        // create the user required QualitySelector (video resolution): we know this is
        // supported, a valid qualitySelector will be created.
        val quality = cameraCapabilities[cameraIndex].qualities[qualityIndex]
        val qualitySelector = QualitySelector.from(quality)//通过qualityselector.from设置视频分辨率
        //默认的视频分辨率取决于设备的摄像头特性和默认设置。CameraX 通常会选择最适合当前设备的默认设置，这可能因设备而异。一般来说，它会选择一个平衡了质量和性能的设置。
        //如果没有显式设置视频质量，CameraX 通常会选择一个标准的预设分辨率，这可能是设备支持的最常见的分辨率之一，
        //  通常是全高清（1080p）或更低分辨率，具体取决于设备和摄像头规格。这个默认分辨率通常是在 CameraX 相机功能的设备支持列表中找到的。
        //如果没有明确设置视频质量，CameraX 会使用设备支持的一个默认分辨率，
        // 这通常是一个常见的标准分辨率，比如全高清（1080p）或更低分辨率
        //CameraX Recorder 支持以下预定义的视频分辨率 Qualities：
        //
        //Quality.UHD，适用于 4K 超高清视频大小 (2160p)
        //Quality.FHD，适用于全高清视频大小 (1080p)
        //Quality.HD，适用于高清视频大小 (720p)
        //Quality.SD，适用于标清视频大小 (480p)


        //qualitySelector = QualitySelector.fromOrderedList(
        //         listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD),
        //         FallbackStrategy.lowerQualityOrHigherThan(Quality.SD))
        //设置首选分辨率和备选分辨率，首选分辨率全部不支持时备选分辨率启用
        //
        //val cameraInfo = cameraProvider.availableCameraInfos.filter {//得到摄像头形象
        //    Camera2CameraInfo
        //    .from(it)
        //    .getCameraCharacteristic(CameraCharacteristics.LENS\_FACING) == CameraMetadata.LENS_FACING_BACK
        //}
        //val supportedQualities = QualitySelector.getSupportedQualities(cameraInfo[0])
        //val filteredQualities = arrayListOf (Quality.UHD, Quality.FHD, Quality.HD, Quality.SD)
        //                       .filter { supportedQualities.contains(it) }
        //val qualitySelector = QualitySelector.from(filteredQualities[position])  //position为选择项
                //获取设备支持的分辨率列表，然后选择其一创建QualitySelector



        //getSupportedDynamicRanges() 获取相机和视频输出支持的所有动态范围。

        //getSupportedQualities(@NonNull DynamicRange dynamicRange) 获取输入动态范围支持的所有质量。
        //isStabilizationSupported()检测设备是否支持视频稳定功能
        //public interface VideoCapabilities 查询设备上的视频录制能力。
        //getResolution() 从输入质量获取相应的分辨率
        //abstract boolean isQualitySupported(
        //    @NonNull Quality quality,
        //    @NonNull DynamicRange dynamicRange
        //) 检查质量是否受支持。
        //getSupportedQualities()   取设备上所有受支持的质量,返回的列表按质量大小从大到小排序、


        captureViewBinding.previewView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val orientation = this@CaptureFragment.resources.configuration.orientation
            dimensionRatio = quality.getAspectRatioString(
                quality,
                (orientation == Configuration.ORIENTATION_PORTRAIT)
            )
        }

        val preview = Preview.Builder()
            .setTargetAspectRatio(quality.getAspectRatio(quality))
            .build().apply {
                setSurfaceProvider(captureViewBinding.previewView.surfaceProvider)
            }

        // build a recorder, which can:
        //   - record video/audio to MediaStore(only shown here), File, ParcelFileDescriptor
        //   - be used create recording(s) (the recording performs recording)
        val recorder = Recorder.Builder()//获取录音机
            .setQualitySelector(qualitySelector)//获取此记录器的质量选择器。
            .build()
        //getAspectRatio() 获取此录制器的纵横比。
        // getTargetVideoEncodingBitRate() 获取此录制器的目标视频编码比特率。
        //getVideoCapabilities（）返回与输入摄像机信息相关的记录器
        //prepareRecording(
        //    Context context,
        //     FileOutputOptions fileOutputOptions
        //)准备将记录保存到 File
        //提供的指定要使用的文件。FileOutputOptions
        //Recorder 支持以下类型的 OutputOptions：
        //FileDescriptorOutputOptions()，用于捕获到 FileDescriptor 中。
        //FileOutputOptions()，用于捕获到 File 中。
        //MediaStoreOutputOptions()，用于捕获到 MediaStore 中。
        //无论使用哪种 OutputOptions 类型，都能通过 setFileSizeLimit() 来设置文件大小上限
        //prepareRecording() 会返回 PendingRecording 对象，该对象是一个中间对象，用于创建相应的 Recording 对象


        //针对 Recording 使用 pause()/resume()/stop() 来控制录制操作。
        //无论录制处于暂停状态还是活跃状态，都可以调用 stop() 来终止 Recording。
        // Recorder 一次支持一个 Recording 对象。对前面的 Recording 对象调用 Recording.stop() 或 Recording.close() 后，您便可以开始新的录制。
        //withAudioEnabled（）是否启用音频，必须先调用此方法，然后才能在录制中启用音频

        videoCapture = VideoCapture.withOutput(recorder)//绑定recorder
        ///图片拍摄和图片分析用例的默认宽高比为 4:3。
        //您无法针对同一个用例设置目标宽高比和目标分辨率。如果这样做，则会在构建配置对象时抛出 IllegalArgumentException。

        //使用 VideoRecordEvent 监听器调用 start() 以开始录制。
        //public final class VideoRecordEvent.Finalize extends VideoRecordEvent   录制的完成。
        // ...录制的暂停，恢复，开始类似
        // final class VideoRecordEvent.Status extends VideoRecordEvent  行的录制的状态报告
        //getOutputOptions() 获取与此事件关联的内容
        //getRecordingStats()  获取当前事件的记录统计信息。
        //在事件监听器内响应 VideoRecordEvents。


        //public final class CameraXConfig 用于向 CameraX 添加实现和用户特定行为的配置
        //.setAvailableCamerasLimiter() 过滤掉了某个摄像头，则 CameraX 在运行时会假定该摄像头不存在，如果过滤掉前置，那么前置摄像头就无法使用
        //getAvailableCamerasLimiter(@Nullable CameraSelector valueIfMissing) 返回用于确定可用摄像机
        //getCameraOpenRetryMaxTimeoutInMillisWhileResuming() 返回处于活动恢复模式时相机打开重试的最大超时（以毫秒为单位）  如果未设置此值，则默认返回 -1L。
        //getOptionPriority(@NonNull Config.Option<Object> opt) 返回指定选项的值的当前优先级
        // 如果指定选项有多个不同优先级的值，则将返回最高优先级。如果该选项不存在，则将抛出一个。IllegalArgumentException
        ///public @NonNull Set<Config.OptionPriority> getPriorities(@NonNull Config.Option<Object> option) 返回为指定选项设置的所有优先级的
        //public static boolean hasConflict(
        //    @NonNull Config.OptionPriority priority1,
        //    @NonNull Config.OptionPriority priority2
        //)返回这些值是否冲突。OptionPriority  目前，不允许同一选项具有不同的优先级值。
        //mergeConfigs(@Nullable Config extendedConfig, @Nullable Config baseConfig)合并两个配置。

        //viewPort   CameraX 可保证一个组中的所有用例的剪裁矩形都指向摄像头传感器中的同一个区域。
        //ViewPort 用于指定最终用户可看到的缓冲区矩形。CameraX 会根据视口的属性及附加的用例计算出可能的最大剪裁矩形。
        //作用：
        // 实时预览显示： Viewport 允许应用程序实时显示相机捕获的内容。用户能够在应用界面上看到实时的相机预览，这对于拍摄照片、录制视频或进行实时交互非常重要。
        //灵活的布局控制： 通过 Viewport，你可以定义相机预览的显示位置和大小。这使得你可以在应用的界面中根据需求自由地安排和调整相机预览的布局，以适应不同屏幕尺寸和设备方向。
        //支持不同的分辨率和纵横比： CameraX 中的 Viewport 允许相机预览适应不同的设备分辨率和纵横比。
        // 无论设备的屏幕大小和比例如何，ViewPort 可以调整预览以适应最佳的预览体验。
        //交互性： 通过 Viewport，你可以添加相机控制功能，比如对焦、变焦等，以及其他与预览相关的交互操作，以提供更好的用户体验。
        //getAspectRatio() 获取 的纵横比
        //getRotation()
        // getScaleType()
        //getLayoutDirection()获取 的布局方向



        //用 CameraSelector.Builder.addCameraFilter() 按 CameraCharacteristics 过滤可用设备列表。
        //注意：摄像头设备必须经过系统识别，并显示在 CameraManager.getCameraIdList() 中，然后才可供使用。
        //此外，每个原始设备制造商 (OEM) 都必须自行选择是否支持外接摄像头设备。因此，在尝试使用任何外接摄像头之前，请务必检查 PackageManager.FEATURE_CAMERA_EXTERNAL 是否已启用



        //public interface CameraControl 摄像头控制
        //cancelFocusAndMetering() 取消当前并清除自动对焦/自动/AWB 区域
        //startFocusAndMetering() 可根据指定的 FocusMeteringAction 设置 AF/AE/AWB 测光区域
        // ，以触发自动对焦和曝光测光。有许多摄像头应用通过这种方式实现“点按对焦”功能。
        //MeteringPoint
        //首先，使用 MeteringPointFactory.createPoint(float x, float y, float size) 创建 MeteringPoint。 MeteringPoint 表示摄像头 Surface 上的单个点
        // 它以标准化形式存储，所以能轻松转换为传感器坐标，从而用于指定 AF/AE/AWB 区域。
        //MeteringPoint 的大小介于 0 到 1 之间，默认大小为 0.15f。调用 MeteringPointFactory.createPoint(float x, float y, float size) 时
        // ，CameraX 会为提供的 size 创建以 (x, y) 为中心的矩形区域。Z
        //abstract @NonNull ListenableFuture<Integer> setExposureCompensationIndex(int value)
        //置相机的曝光补偿值。
        //只允许一个同时运行。如果连续执行多个设置，则相机中将仅保留最新的一个设置。其他操作将被取消，
        // ListenableFuture 将失败，并显示 .取消之前的所有操作后，相机设备将根据最新设置调整亮


        //enableTorch(boolean torch) 启用手电筒或禁用手电筒。
        //hasFlashUnit()//返回是否有闪光灯组件  如果照相机没有闪光灯组件，则手电筒状态将为 hasFlashUnitOFF
        //启用手电筒后，无论闪光灯模式设置如何，手电筒在拍照和拍视频时都会保持开启状态。仅当手电筒被停用时，ImageCapture 中的 flashMode 才会起作用。
        //setExposureCompensationIndex(int value) 设置相机的曝光补偿值。
        //setLinearZoom(@FloatRange(from = 0.0, to = 1.0) float linearZoom) 按 0f 到 1.0f 之间的线性缩放值设置当前缩放。
        //setZoomRatio(float ratio) 用于按变焦比例设置变焦。
        //该比率必须在 CameraInfo.getZoomState().getValue().getMinZoomRatio() 到 CameraInfo.getZoomState().getValue().getMaxZoomRatio() 的范围内。
        // 否则，该函数会返回失败的 ListenableFuture。
        //setLinearZoom() 使用 0 到 1.0 之间的线性变焦值设置当前变焦操作。
        //线性变焦的优势在于，它可以使视野范围 (FOV) 随变焦的变化而缩放。因此，线性变焦非常适合与 Slider 视图搭配使用。
        //CameraInfo.getZoomState() 会返回当前变焦状态的 LiveData


        //曝光补偿
        //当应用需要对自动曝光 (AE) 输出结果以外的曝光值 (EV) 进行微调时，曝光补偿很有用。CameraX 将按以下方式组合曝光补偿值，以确定当前图片条件下所需的曝光：
        //Exposure = ExposureCompensationIndex * ExposureCompensationStep
        //CameraX 提供 Camera.CameraControl.setExposureCompensationIndex() 函数，用于将曝光补偿设置为索引值。
        //当索引值为正值时，会调亮图片；当索引值为负值时，会调暗图片。应用可以按下一部分中所述的 CameraInfo.ExposureState.exposureCompensationRange()
        // 查询支持的范围。如果相应的值受支持，则当在拍摄请求中成功启用该值时，返回的 ListenableFuture 便会完成；
        // 如果指定的索引超出支持范围，则 setExposureCompensationIndex() 会导致返回的 ListenableFuture 立即完成，并显示失败的结果。
        //CameraX 仅保留最新的未完成 setExposureCompensationIndex() 请求。如果在上一个请求尚未执行时便多次调用该函数，会导致请求被取消。


        //Camera.CameraInfo.getExposureState() 可检索当前的 ExposureState，其中包括：
        //对曝光补偿控制的可支持性。
        //当前的曝光补偿指数。
        //曝光补偿索引范围。
        //用于计算曝光补偿值的曝光补偿步骤。


        //public interface CameraInfo 相机信息
        //abstract @NonNull LiveData<CameraState> getCameraState() 返回相机的状态
        //abstract @NonNull ExposureState getExposureState()  获取曝光状态
        //default @FloatRange(from = 0, fromInclusive = false) float getIntrinsicZoomRatio()  返回此相机的固有缩放比。

        //abstract int getSensorRotationDegrees()  返回相对于默认设备方向的传感器旋转（以度为单位）。
        //abstract int getSensorRotationDegrees(int relativeRotation)  返回相对于给定旋转值的传感器旋转（以度为单位）。

        //图片拍摄：
        //setCaptureMode() 可用于配置拍摄照片时所采用的拍摄模式：
        //CAPTURE_MODE_MINIMIZE_LATENCY：缩短图片拍摄的延迟时间。
        //CAPTURE_MODE_MAXIMIZE_QUALITY：提高图片拍摄的图片质量。
        //拍摄模式默认为 CAPTURE_MODE_MINIMIZE_LATENCY。

        //零快门延迟 (CAPTURE_MODE_ZERO_SHOT_LAG) 以拍摄模式的形式提供。
        // 与默认拍摄模式 CAPTURE_MODE_MINIMIZE_LATENCY 相比，启用零快门延迟后，延迟时间会明显缩短
        //default boolean isZslSupported()  启用零快门延迟之前， 确定相关设备是否符合要求
        //零快门延迟仅适用于图片拍摄用例。您无法为视频拍摄用例或相机扩展程序启用该功能。
        // 由于使用闪光灯会增加延迟时间，因此当闪光灯开启或处于自动模式时，零快门延迟将不起作用。
        //setFlashMode() 设置闪光灯模式
        //默认闪光灯模式为 FLASH_MODE_OFF
        //FLASH_MODE_ON：闪光灯始终处于开启状态。
        //FLASH_MODE_AUTO：在弱光环境下拍摄时，自动开启闪光灯。

        //图片分析   ImageAnalysis：
        //当应用的分析流水线无法满足 CameraX 的帧速率要求时，您可以将 CameraX 配置为通过以下其中一种方式丢帧
        // 非阻塞（默认）：在该模式下，执行器始终会将最新的图像缓存到图像缓冲区（与深度为 1 的队列相似），与此同时，应用会分析上一个图像。
        // 如果 CameraX 在应用完成处理之前收到新图像，则新图像会保存到同一缓冲区，并覆盖上一个图像。
        // 这种情况下，ImageAnalysis.Builder.setImageQueueDepth() 不起任何作用，缓冲区内容始终会被覆盖
        // 可以通过使用 STRATEGY_KEEP_ONLY_LATEST 调用 setBackpressureStrategy() 来启用该非阻塞模式。
        //阻塞：在该模式下，内部执行器可以向内部图像队列添加多个图像，并仅在队列已满时才开始丢帧
        // 系统会在整个相机设备上进行屏蔽：如果相机设备具有多个绑定用例，那么在 CameraX 处理这些图像时，系统会屏蔽所有这些用例。
        //可以通过将 STRATEGY_BLOCK_PRODUCER 传递到 setBackpressureStrategy() 来启用阻塞模式。
        // 还可以通过使用 ImageAnalysis.Builder.setImageQueueDepth() 来配置图像队列深度。
        //可通过 setOutputImageFormat(int) 支持 YUV_420_888 和 RGBA_8888。默认格式为 YUV_420_888。
        //不能同时设置Resolution 和 AspectRatio

        //clearAnalyzer()删除以前设置的分析器
        //getBackgroundExecutor()返回将用于后台任务的执行程序。
        // getBackpressureStrategy()返回获取图像的模式
        ///getImageQueueDepth()返回可用于相机管道的图像数
        //getOutputImageFormat()获取输出图像格式
        //getResolutionInfo()  获取分辨率相关信息
        // getResolutionSelector()返回分辨率选择器设置
        // getTargetRotation()返回图像的预期目标的旋转。
        //setAnalyzer(设置分析器以接收和分析图像。
        //    @NonNull Executor executor,
        //    @NonNull ImageAnalysis.Analyzer analyzer
        //)
        //在每个分析器中，应用都会收到一个 ImageProxy，它是 Media.Image 的封装容器。可以使用 ImageProxy.getFormat() 来查询图像格式

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                videoCapture,
                preview
                // 如需以正确的屏幕方向显示预览数据，您可以使用 Preview.PreviewOutput() 的元数据输出创建转换。
            )

        } catch (exc: Exception) {
            // we are on main thread, let's reset the controls on the UI.
            Log.e(TAG, "Use case binding failed", exc)
            resetUIandState("bindToLifecycle failed: $exc")
        }
        enableUI(true)
    }

    /**
     * Kick start the video recording
     *   - config Recorder to capture to MediaStoreOutput
     *   - register RecordEvent Listener
     *   - apply audio request from user
     *   - start recording!
     * After this function, user could start/pause/resume/stop recording and application listens
     * to VideoRecordEvent for the current recording status.
     */
    @SuppressLint("MissingPermission")
    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val name = "CameraX-recording-" +
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        // configure Recorder and Start recording to the mediaStoreOutput.
        currentRecording = videoCapture.output
            .prepareRecording(requireActivity(), mediaStoreOutput)
            .apply { if (audioEnabled) withAudioEnabled() }
            .start(mainThreadExecutor, captureListener)

        Log.i(TAG, "Recording started")
    }

    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status)
            recordingState = event

        updateUI(event)

        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
            lifecycleScope.launch {
                navController.navigate(
                    CaptureFragmentDirections.actionCaptureToVideoViewer(
                        event.outputResults.outputUri
                    )
                )
            }
        }
    }

    /**
     * Retrieve the asked camera's type(lens facing type). In this sample, only 2 types:
     *   idx is even number:  CameraSelector.LENS_FACING_BACK
     *          odd number:   CameraSelector.LENS_FACING_FRONT
     */
    private fun getCameraSelector(idx: Int): CameraSelector {
        if (cameraCapabilities.size == 0) {
            Log.i(TAG, "Error: This device does not have any camera, bailing out")
            requireActivity().finish()
        }
        return (cameraCapabilities[idx % cameraCapabilities.size].camSelector)
    }

    data class CameraCapability(val camSelector: CameraSelector, val qualities: List<Quality>)

    /**
     * Query and cache this platform's camera capabilities, run only once.
     */
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider = ProcessCameraProvider.getInstance(requireContext()).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(requireActivity(), camSelector)
                            QualitySelector
                                .getSupportedQualities(camera.cameraInfo)
                                .filter { quality ->
                                    listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD)
                                        .contains(quality)
                                }.also {
                                    cameraCapabilities.add(CameraCapability(camSelector, it))
                                }
                        }
                    } catch (exc: java.lang.Exception) {
                        Log.e(TAG, "Camera Face $camSelector is not supported")
                    }
                }
            }
        }
    }

    /**
     * One time initialize for CameraFragment (as a part of fragment layout's creation process).
     * This function performs the following:
     *   - initialize but disable all UI controls except the Quality selection.
     *   - set up the Quality selection recycler view.
     *   - bind use cases to a lifecycle camera, enable UI controls.
     */
    private fun initCameraFragment() {
        initializeUI()
        viewLifecycleOwner.lifecycleScope.launch {
            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }
            initializeQualitySectionsUI()

            bindCaptureUsecase()
        }
    }

    /**
     * Initialize UI. Preview and Capture actions are configured in this function.
     * Note that preview and capture are both initialized either by UI or CameraX callbacks
     * (except the very 1st time upon entering to this fragment in onCreateView()
     */
    @SuppressLint("ClickableViewAccessibility", "MissingPermission")
    private fun initializeUI() {
        captureViewBinding.cameraButton.apply {
            setOnClickListener {
                cameraIndex = (cameraIndex + 1) % cameraCapabilities.size
                // camera device change is in effect instantly:
                //   - reset quality selection
                //   - restart preview
                qualityIndex = DEFAULT_QUALITY_IDX
                initializeQualitySectionsUI()
                enableUI(false)
                viewLifecycleOwner.lifecycleScope.launch {
                    bindCaptureUsecase()
                }
            }
            isEnabled = false
        }

        // audioEnabled by default is disabled.
        captureViewBinding.audioSelection.isChecked = audioEnabled
        captureViewBinding.audioSelection.setOnClickListener {
            audioEnabled = captureViewBinding.audioSelection.isChecked
        }

        // React to user touching the capture button
        captureViewBinding.captureButton.apply {
            setOnClickListener {
                if (!this@CaptureFragment::recordingState.isInitialized ||
                    recordingState is VideoRecordEvent.Finalize
                ) {
                    enableUI(false)  // Our eventListener will turn on the Recording UI.
                    startRecording()
                } else {
                    when (recordingState) {
                        is VideoRecordEvent.Start -> {
                            currentRecording?.pause()
                            captureViewBinding.stopButton.visibility = View.VISIBLE
                        }

                        is VideoRecordEvent.Pause -> currentRecording?.resume()
                        is VideoRecordEvent.Resume -> currentRecording?.pause()
                        else -> throw IllegalStateException("recordingState in unknown state")
                    }
                }
            }
            isEnabled = false
        }

        captureViewBinding.stopButton.apply {
            setOnClickListener {
                // stopping: hide it after getting a click before we go to viewing fragment
                captureViewBinding.stopButton.visibility = View.INVISIBLE
                if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
                    return@setOnClickListener
                }

                val recording = currentRecording
                if (recording != null) {
                    recording.stop()
                    currentRecording = null
                }
                captureViewBinding.captureButton.setImageResource(R.drawable.ic_start)
            }
            // ensure the stop button is initialized disabled & invisible
            visibility = View.INVISIBLE
            isEnabled = false
        }

        captureLiveStatus.observe(viewLifecycleOwner) {
            captureViewBinding.captureStatus.apply {
                post { text = it }
            }
        }
        captureLiveStatus.value = getString(R.string.Idle)
    }

    /**
     * UpdateUI according to CameraX VideoRecordEvent type:
     *   - user starts capture.
     *   - this app disables all UI selections.
     *   - this app enables capture run-time UI (pause/resume/stop).
     *   - user controls recording with run-time UI, eventually tap "stop" to end.
     *   - this app informs CameraX recording to stop with recording.stop() (or recording.close()).
     *   - CameraX notify this app that the recording is indeed stopped, with the Finalize event.
     *   - this app starts VideoViewer fragment to view the captured result.
     */
    private fun updateUI(event: VideoRecordEvent) {
        val state = if (event is VideoRecordEvent.Status) recordingState.getNameString()
        else event.getNameString()
        when (event) {
            is VideoRecordEvent.Status -> {
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
            }

            is VideoRecordEvent.Start -> {
                showUI(UiState.RECORDING, event.getNameString())
            }

            is VideoRecordEvent.Finalize -> {
                showUI(UiState.FINALIZED, event.getNameString())
            }

            is VideoRecordEvent.Pause -> {
                captureViewBinding.captureButton.setImageResource(R.drawable.ic_resume)
            }

            is VideoRecordEvent.Resume -> {
                captureViewBinding.captureButton.setImageResource(R.drawable.ic_pause)
            }
        }

        val stats = event.recordingStats
        val size = stats.numBytesRecorded / 1000
        val time = java.util.concurrent.TimeUnit.NANOSECONDS.toSeconds(stats.recordedDurationNanos)
        var text = "${state}: recorded ${size}KB, in ${time}second"
        if (event is VideoRecordEvent.Finalize)
            text = "${text}\nFile saved to: ${event.outputResults.outputUri}"

        captureLiveStatus.value = text
        Log.i(TAG, "recording event: $text")
    }

    /**
     * Enable/disable UI:
     *    User could select the capture parameters when recording is not in session
     *    Once recording is started, need to disable able UI to avoid conflict.
     */
    private fun enableUI(enable: Boolean) {
        arrayOf(
            captureViewBinding.cameraButton,
            captureViewBinding.captureButton,
            captureViewBinding.stopButton,
            captureViewBinding.audioSelection,
            captureViewBinding.qualitySelection
        ).forEach {
            it.isEnabled = enable
        }
        // disable the camera button if no device to switch
        if (cameraCapabilities.size <= 1) {
            captureViewBinding.cameraButton.isEnabled = false
        }
        // disable the resolution list if no resolution to switch
        if (cameraCapabilities[cameraIndex].qualities.size <= 1) {
            captureViewBinding.qualitySelection.apply { isEnabled = false }
        }
    }

    /**
     * initialize UI for recording:
     *  - at recording: hide audio, qualitySelection,change camera UI; enable stop button
     *  - otherwise: show all except the stop button
     */
    private fun showUI(state: UiState, status: String = "idle") {
        captureViewBinding.let {
            when (state) {
                UiState.IDLE -> {
                    it.captureButton.setImageResource(R.drawable.ic_start)
                    it.stopButton.visibility = View.INVISIBLE

                    it.cameraButton.visibility = View.VISIBLE
                    it.audioSelection.visibility = View.VISIBLE
                    it.qualitySelection.visibility = View.VISIBLE
                }

                UiState.RECORDING -> {
                    it.cameraButton.visibility = View.INVISIBLE
                    it.audioSelection.visibility = View.INVISIBLE
                    it.qualitySelection.visibility = View.INVISIBLE

                    it.captureButton.setImageResource(R.drawable.ic_pause)
                    it.captureButton.isEnabled = true
                    it.stopButton.visibility = View.VISIBLE
                    it.stopButton.isEnabled = true
                }

                UiState.FINALIZED -> {
                    it.captureButton.setImageResource(R.drawable.ic_start)
                    it.stopButton.visibility = View.INVISIBLE
                }

                else -> {
                    val errorMsg = "Error: showUI($state) is not supported"
                    Log.e(TAG, errorMsg)
                    return
                }
            }
            it.captureStatus.text = status
        }
    }

    /**
     * ResetUI (restart):
     *    in case binding failed, let's give it another change for re-try. In future cases
     *    we might fail and user get notified on the status
     */
    private fun resetUIandState(reason: String) {
        enableUI(true)
        showUI(UiState.IDLE, reason)

        cameraIndex = 0
        qualityIndex = DEFAULT_QUALITY_IDX
        audioEnabled = false
        captureViewBinding.audioSelection.isChecked = audioEnabled
        initializeQualitySectionsUI()
    }

    /**
     *  initializeQualitySectionsUI():
     *    Populate a RecyclerView to display camera capabilities:
     *       - one front facing
     *       - one back facing
     *    User selection is saved to qualityIndex, will be used
     *    in the bindCaptureUsecase().
     */
    private fun initializeQualitySectionsUI() {
        val selectorStrings = cameraCapabilities[cameraIndex].qualities.map {
            it.getNameString()
        }
        // create the adapter to Quality selection RecyclerView
        captureViewBinding.qualitySelection.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GenericListAdapter(
                selectorStrings,
                itemLayoutId = R.layout.video_quality_item
            ) { holderView, qcString, position ->

                holderView.apply {
                    findViewById<TextView>(R.id.qualityTextView)?.text = qcString
                    // select the default quality selector
                    isSelected = (position == qualityIndex)
                }

                holderView.setOnClickListener { view ->
                    if (qualityIndex == position) return@setOnClickListener

                    captureViewBinding.qualitySelection.let {
                        // deselect the previous selection on UI.
                        it.findViewHolderForAdapterPosition(qualityIndex)
                            ?.itemView
                            ?.isSelected = false
                    }
                    // turn on the new selection on UI.
                    view.isSelected = true
                    qualityIndex = position

                    // rebind the use cases to put the new QualitySelection in action.
                    enableUI(false)
                    viewLifecycleOwner.lifecycleScope.launch {
                        bindCaptureUsecase()
                    }
                }
            }
            isEnabled = false
        }
    }

    // System function implementations
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _captureViewBinding = FragmentCaptureBinding.inflate(inflater, container, false)
        return captureViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCameraFragment()
    }

    override fun onDestroyView() {
        _captureViewBinding = null
        super.onDestroyView()
    }

    companion object {
        // default Quality selection if no input from UI
        const val DEFAULT_QUALITY_IDX = 0
        val TAG: String = CaptureFragment::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}