package it.upo.reti2;

import de.fh_zwickau.informatik.sensor.IZWayApi;
import de.fh_zwickau.informatik.sensor.ZWayApiHttp;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample usage of the Z-Way API. It looks for all sensors and power outlets.
 * It reports the temperature and power consumption of available sensors and, then, turn power outlets on for 10 seconds.
 * <p>
 * It uses the Z-Way library for Java included in the lib folder of the project.
 *
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (24/05/2017)
 * @see <a href="https://github.com/pathec/ZWay-library-for-Java">Z-Way Library on GitHub</a> for documentation about the used library
 */
public class ZWayExample {

    public static void main(String[] args) {
        // init logger
        Logger logger = LoggerFactory.getLogger(ZWayExample.class);

        // sample RaZberry IP address
        String ipAddress = "192.168.1.2";

        // sample username and password
        String username = "admin";
        String password = "admin";

        // create an instance of the Z-Way library; all the params are mandatory (we are not going to use the remote service/id)
        IZWayApi zwayApi = new ZWayApiHttp(ipAddress, 8083, "http", username, password, 0, false, new ZWaySimpleCallback());

        // get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();

        // search all sensors
        for (Device dev : allDevices.getAllDevices()) {
            if (dev.getDeviceType().equalsIgnoreCase("SensorMultilevel") || dev.getDeviceType().equalsIgnoreCase("SensorBinary")) {
                logger.info("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());

                // get only temperature and power consumption from available sensors
                if (dev.getProbeType().equalsIgnoreCase("temperature")) {
                    logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                } else if (dev.getProbeType().equalsIgnoreCase("meterElectric_watt")) {
                    logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                } else {
                    // get all measurements from sensors
                    logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " uom: " + dev.getMetrics().getScaleTitle());
                }
            }
        }

        // search all power outlets
        for (Device dev : allDevices.getAllDevices()) {
            if (dev.getDeviceType().equalsIgnoreCase("SwitchBinary")) {
                logger.debug("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
                // turn it on
                logger.info("Turn device " + dev.getNodeId() + " on...");
                dev.on();
            }
        }

        // wait 10 seconds...
        logger.info("Waiting 10 seconds...");
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info(String.valueOf(10 - i));
        }

        // search again all power outlets
        for (Device dev : allDevices.getAllDevices()) {
            if (dev.getDeviceType().equalsIgnoreCase("SwitchBinary")) {
                logger.debug("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
                // turn it off
                logger.info("Turn device " + dev.getNodeId() + " off...");
                dev.off();
            }
        }

    }
}
