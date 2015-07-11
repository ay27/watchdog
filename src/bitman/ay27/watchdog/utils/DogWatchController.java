package bitman.ay27.watchdog.utils;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Spartan on 2015/7/10.
 */

/**
 * DogWatch Controller, if any error happened, a IllegalStateException will be casted
 */
public interface DogWatchController {

    /**
     * set DogWatch BLE device for library
     *
     * @param device DogWatch device
     * @return true for success
     */
    public boolean setDevice(BluetoothDevice device);

    /**
     * set time - year
     *
     * @param val year, in binary, it should meet 2000<=val
     */
    public void pushYear(char val);

    /**
     * set time - month
     *
     * @param val month, in binary, it should meet (1<=val && val<=12)
     */
    public void pushMonth(byte val);

    /**
     * set time - day
     *
     * @param val day in month, in binary, it should be valid (depending on current month and year).
     */
    public void pushDay(byte val);

    /**
     * set time - hour
     *
     * @param val hour, in binary, it should meet (0<=val && val<=23)
     */
    public void pushHour(byte val);

    /**
     * set time - minute
     *
     * @param val minute, in binary, it should meet (0<=val && val<=59)
     */
    public void pushMin(byte val);

    /**
     * set time - second
     *
     * @param val second, in binary, it should meet (0<=val && val<=59)
     */
    public void pushSec(byte val);

    /**
     * set RF calibrate value used for estimate distance
     *
     * @param val value represent in 2's complement, which indicates what's the expected RSSI at a distance of 1 meter to the DogWatch.
     */
    public void pushRfCalib(byte val);

    /**
     * set Broadcasting Power
     *
     * @param txLevel Broadcasting Power Level, which is the power DogWatch broadcasting its signal.
     */
    public void pushRfTxLevel(DogWatchTxPowerLevel txLevel);

    /**
     * request DogWatch to vibrate
     *
     * @param type vibrate type, STOP_VIBRATE to ask watch stop vibrate immediate
     */
    public void pushVibrate(DogWatchVibrateType type);

    /**
     * set the disconnect alarm function of the DogWatch
     *
     * @param enable true for enable, false for disable
     */
    public void pushDisconnectAlarmState(boolean enable);

    /**
     * get the battery status of DogWatch
     *
     * @return battery remaining percent
     */
    public int collectBattLevel();

    /**
     * get the current RSSI
     *
     * @return current RSSI, in dbm
     */
    public int collectCurrRSSI();

    /**
     * get the average RSSI
     *
     * @return average RSSI, in dbm
     */
    public int collectAvgRSSI();

    /**
     * get current estimated distant value
     *
     * @return current estimated distant, in meter
     */
    public float collectCurrDist();

    /**
     * get average estimated distant value
     *
     * @return average estimated distant, in meter
     */
    public float collectAvgDist();

    /**
     * get time - year
     *
     * @return year
     */
    public int collectYear();

    /**
     * get time - month
     *
     * @return month
     */
    public int collectMonth();

    /**
     * get time - day
     *
     * @return day
     */
    public int collectDay();

    /**
     * get time - hour
     *
     * @return hour
     */
    public int collectHour();

    /**
     * get time - minute
     *
     * @return minute
     */
    public int collectMin();

    /**
     * get time - second
     *
     * @return second
     */
    public int collectSec();

    /**
     * get RF calibrate value used for estimate distance
     *
     * @return represent in 2's complement, which indicates what's the expected RSSI at a distance of 1 meter to the DogWatch.
     */
    public int collectRfCalib();

    /**
     * get current Broadcasting Power, which is the power DogWatch broadcasting its signal.
     *
     * @return current Broadcasting Power
     */
    public DogWatchTxPowerLevel collectRfTxLevel();

    /**
     * DogWatch's RF transmit power
     */
    public enum DogWatchTxPowerLevel {
        /**
         * 4 dbm
         */
        RF_TOGGLE_LEVEL_MAX((byte) 4),
        /**
         * 0 dbm
         */
        RF_TOGGLE_LEVEL_NORMAL((byte) 3),
        /**
         * -6 dbm
         */
        RF_TOGGLE_LEVEL_LESS((byte) 2),
        /**
         * -23 dbm
         */
        RF_TOGGLE_LEVEL_LIMIT((byte) 1);

        private byte value;

        DogWatchTxPowerLevel(byte value) {
            this.value = value;
        }

        /**
         * get the value storage in DogWatch
         *
         * @return the value which can exchange with DogWatch
         */
        public byte getValue() {
            return value;
        }
    }

    /**
     * vibrate types which DogWatch can make
     */
    public enum DogWatchVibrateType {
        STOP_VIBRATE,
        NFC_DETACH,
        OUT_OF_RANGE,
    }
}
