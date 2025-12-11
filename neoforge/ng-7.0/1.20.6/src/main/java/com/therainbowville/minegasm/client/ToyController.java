package com.therainbowville.minegasm.client;

import com.therainbowville.minegasm.config.MinegasmConfig;
import io.github.blackspherefollower.buttplug4j.client.ButtplugClientDevice;
import io.github.blackspherefollower.buttplug4j.connectors.jetty.websocket.client.ButtplugClientWSClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class ToyController {
    private static final Logger LOGGER = LogManager.getLogger();
    public static String lastErrorMessage = "";
    public static boolean isConnected = false;
    public static double currentVibrationLevel = 0;
    private static ButtplugClientWSClient client = new ButtplugClientWSClient("Minegasm");
    private static ButtplugClientDevice device = null;
    private static boolean shutDownHookAdded = false;

    public static boolean connectDevice() {
        try {
            device = null;
            client.disconnect();

            LOGGER.info("URL: " + MinegasmConfig.serverUrl);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future future = executor.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    client.connect(new URI(MinegasmConfig.serverUrl));
                    return null;
                }
            });

            try {
                future.get(3, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                client = new ButtplugClientWSClient("Minegasm");
                throw new TimeoutException("Could not find WebSocket");
            } finally {
                executor.shutdownNow();
            }

            client.startScanning();

            Thread.sleep(5000);
            client.requestDeviceList();

            LOGGER.info("Enumerating devices...");

            List<ButtplugClientDevice> devices = client.getDevices();

            int nDevices = devices.size();
            LOGGER.info(nDevices);

            if (nDevices < 1) {
                lastErrorMessage = "No device found";
            }

            for (ButtplugClientDevice dev : devices) {
                if (dev.getScalarVibrateCount() != 0) {
                    LOGGER.info(dev.getDisplayName());
                    device = dev;
                    try {
                        device.sendScalarVibrateCmd(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            if (Objects.nonNull(device) && !shutDownHookAdded) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        LOGGER.info("Disconnecting devices...");
                        client.stopAllDevices();
                        client.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));

                shutDownHookAdded = true;
            }

            isConnected = true;
        } catch (Exception e) {
            lastErrorMessage = e.getMessage();
            LOGGER.error(e.getMessage(), e);
        }

        return Objects.nonNull(device);
    }

    public static void setVibrationLevel(double level) {
        if (Objects.isNull(device)) return;

        if (MinegasmConfig.vibrate) {
            try {
                device.sendScalarVibrateCmd(level);
                currentVibrationLevel = level;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (currentVibrationLevel > 0) {
                try {
                    level = 0;
                    device.sendScalarVibrateCmd(level);
                    currentVibrationLevel = level;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getDeviceName() {
        return (Objects.nonNull(device)) ? device.getDisplayName() : "<none>";
    }

    public static long getDeviceId() {
        return (Objects.nonNull(device)) ? device.getDeviceIndex() : -1;
    }

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }
}
