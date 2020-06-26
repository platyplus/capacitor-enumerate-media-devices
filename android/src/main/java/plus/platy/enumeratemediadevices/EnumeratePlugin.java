package plus.platy.enumeratemediadevices;

import com.getcapacitor.JSObject;
import com.getcapacitor.JSArray;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.CAMERA_SERVICE;

@NativePlugin()
public class EnumeratePlugin extends Plugin {
    static final String FRONT_CAM = "Front Camera";
    static final String BACK_CAM = "Back Camera";
    static final String EXTERNAL_CAM = "External Camera";
    static final String UNKNOWN_CAM = "Unknown Camera";

    static final String BUILTIN_MIC = "Built-in Microphone";
    static final String BLUETOOTH_MIC = "Bluetooth";
    static final String WIRED_MIC = "Wired Microphone";
    static final String USB_MIC = "USB Microphone";
    static final String UNKNOWN_MIC = "Unknown Microphone";

    private JSArray devices;

    @PluginMethod()
    public void echo(PluginCall call) {
        String value = call.getString("value");
JSArray arr = new JSArray();

        JSObject ret = new JSObject();
        ret.put("value", value);

        call.success(ret);
    }

    private String getVideoType(CameraCharacteristics input) {
        String deviceType = "";
        String num = "";

        try {
            for (int i = 0; i < this.devices.length(); ++i) {
                JSONObject obj = this.devices.getJSONObject(i);
                String id = obj.getString("label");
                if (id.contains(EXTERNAL_CAM)) {
                    num = Integer.toString(Integer.parseInt(num) + 1);
                }
            }
        } catch (JSONException e) {
            System.out.println("ERROR JSONException " + e.toString());
        }

        switch (input.get(CameraCharacteristics.LENS_FACING)) {
            case CameraCharacteristics.LENS_FACING_FRONT:
                deviceType = FRONT_CAM;
                break;
            case CameraCharacteristics.LENS_FACING_BACK:
                deviceType = BACK_CAM;
                break;
            case CameraCharacteristics.LENS_FACING_EXTERNAL:
                deviceType = EXTERNAL_CAM + " " + num;
                break;
            default:
                deviceType = UNKNOWN_CAM;
        }

        return deviceType;
    }


    private String getAudioType(AudioDeviceInfo input) {
        String deviceType = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            switch (input.getType()) {
                case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    deviceType = input.getProductName().toString() + " " + BLUETOOTH_MIC;
                    break;
                case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                    deviceType = input.getProductName().toString() + " " + BUILTIN_MIC;
                    break;
                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                    deviceType = input.getProductName().toString() + " " + WIRED_MIC;
                    break;
                case AudioDeviceInfo.TYPE_USB_DEVICE:
                    deviceType = input.getProductName().toString() + " " + USB_MIC;
                    break;
                default:
                    deviceType = UNKNOWN_MIC;
                    break;
            }
        } else {
            System.out.println("WARNING WRONG ANDROID SDK VERSION");
        }
        return deviceType;
    }

    @PluginMethod()
    public void enumerateDevices(PluginCall call) {
        this.devices = new JSArray();

        // Video inputs
        CameraManager camera = (CameraManager) this.getContext().getSystemService(CAMERA_SERVICE);

        try {
            String[] cameraId = camera.getCameraIdList();
            CameraCharacteristics characteristics;
            String label = "";

            for (int i = 0; i < cameraId.length; i++) {
                JSONObject device = new JSONObject();
                characteristics = camera.getCameraCharacteristics(cameraId[i]);
                label = this.getVideoType(characteristics);
                device.put("deviceId", cameraId[i]);
                device.put("groupId", "");
                device.put("kind", "videoinput");
                device.put("label", label);
                this.devices.put(device);
            }

        } catch (CameraAccessException e) {
            System.out.println("ERROR IOException " + e.toString());

        } catch (JSONException e) {
            System.out.println("ERROR IOException " + e.toString());
        }

        AudioManager audioManager = (AudioManager) this.getContext().getSystemService(AUDIO_SERVICE);

//        AudioDeviceInfo[] mics = new AudioDeviceInfo[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            System.out.println("PLUGIN DEBUG");

            AudioDeviceInfo[] mics = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            System.out.println(mics.toString());
            for (int i = 0; i < mics.length; i++) {
                String label = "";
                Integer type = mics[i].getType();
                if ((type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO || type == AudioDeviceInfo.TYPE_BUILTIN_MIC
                        || type == AudioDeviceInfo.TYPE_WIRED_HEADSET || type == AudioDeviceInfo.TYPE_USB_DEVICE)
                ) {
                    JSONObject device = new JSONObject();

                    label = this.getAudioType(mics[i]);
                    try {
                        device.put("deviceId", Integer.toString(mics[i].getId()));
                        device.put("groupId", "");
                        device.put("kind", "audioinput");
                        device.put("label", label);
                        this.devices.put(device);
                    } catch (JSONException e) {
                        System.out.println("ERROR JSONException " + e.toString());
                    }
                }
            }
        } else {
            System.out.println("WARNING WRONG ANDROID SDK VERSION");
        }

        JSObject ret = new JSObject();
        ret.put("devices", devices);
        call.success(ret);
    }
}
