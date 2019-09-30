package com.judopay.arch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebSettings;

import com.judopay.BuildConfig;
import com.judopay.devicedna.DeviceDNA;
import com.judopay.devicedna.PermissionUtil;
import com.judopay.model.Browser;
import com.judopay.model.ClientDetails;
import com.judopay.model.ConsumerDevice;
import com.judopay.model.EnhancedPaymentDetail;
import com.judopay.model.GeoLocation;
import com.judopay.model.SDKInfo;
import com.judopay.model.ThreeDSecure;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PayLoadUtil {

    public static EnhancedPaymentDetail getEnhancedPaymentDetail(final Context context) {

        final SDKInfo sdkInfo = getSdkInfo();
        final ConsumerDevice consumerDevice = getConsumerDevice(context);

        return new EnhancedPaymentDetail(sdkInfo, consumerDevice);
    }

    private static SDKInfo getSdkInfo() {
        return new SDKInfo(BuildConfig.VERSION_NAME, "Judo-Android");
    }


    private static ConsumerDevice getConsumerDevice(final Context context) {
        final ClientDetails clientDetails = getClientDetails(context);
        final GeoLocation geolocation = getGeolocation(context);
        final ThreeDSecure threeDSecure = getThreeDSecureInfo(context);
        final String ipAddress = getIPAddress();
        return new ConsumerDevice(ipAddress, clientDetails, geolocation, threeDSecure);
    }

    private static ClientDetails getClientDetails(final Context context) {
        final DeviceDNA deviceDNA = new DeviceDNA(context);
        final Map<String, String> mapDeviceDna = deviceDNA.getDeviceDNA();

        return new ClientDetails(mapDeviceDna.get("key"), mapDeviceDna.get("value"));
    }

    private static ThreeDSecure getThreeDSecureInfo(final Context context) {
        final Browser browser = getBrowserInfo(context);
        return new ThreeDSecure(browser);
    }

    private static Browser getBrowserInfo(final Context context) {

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        final TimeZone defaultTimeZone = TimeZone.getDefault();

        final String deviceLanguage = Locale.getDefault().getLanguage();
        final String screenHeight = String.valueOf(metrics.heightPixels);
        final String screenWidth = String.valueOf(metrics.widthPixels);
        final String timeZone = defaultTimeZone.getDisplayName(false, TimeZone.SHORT);
        final String userAgent = WebSettings.getDefaultUserAgent(context);

        return new Browser(deviceLanguage, screenHeight, screenWidth, timeZone, userAgent);
    }

    private static GeoLocation getGeolocation(final Context context) {
        final Location lastKnowLocation = getLastKnownLocation(context);

        final double latitude = lastKnowLocation != null ? lastKnowLocation.getLatitude() : 0d;
        final double longitude = lastKnowLocation != null ? lastKnowLocation.getLongitude() : 0d;
        return new GeoLocation(latitude, longitude);
    }

    @SuppressLint("MissingPermission")
    private static Location getLastKnownLocation(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = manager.getProviders(true);
        Location lastKnownLocation = null;
        for (String provider : providers) {

            final boolean accessFineLocation = PermissionUtil.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION);
            final boolean accessCoarseLocation = PermissionUtil.isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (accessCoarseLocation || accessFineLocation) {
                if (lastKnownLocation == null) {
                    lastKnownLocation = manager.getLastKnownLocation(provider);
                } else {
                    Location location = manager.getLastKnownLocation(provider);
                    if (location != null && location.getTime() > lastKnownLocation.getTime()) { {
                        lastKnownLocation = location;
                    }
                }
            }
        }
        return lastKnownLocation;
    }

    private static String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
