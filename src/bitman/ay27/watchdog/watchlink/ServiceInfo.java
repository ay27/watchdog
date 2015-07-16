package bitman.ay27.watchdog.watchlink;

import java.util.UUID;

/**
 * Created by Spartan on 2015/7/13.
 */
public class ServiceInfo {
    public class ServiceRecord {
        public UUID service;
        public UUID[] characteristics;
        public int[] namelist;

        protected ServiceRecord(UUID service, UUID[] characteristics, int[] namelist) {
            this.service = service;
            this.characteristics = characteristics;
            this.namelist = namelist;
        }
    }

    public ServiceRecord[] services;

    public ServiceInfo() {
        this.services = new ServiceRecord[]{
                new ServiceRecord(UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb"),    // Battery Service
                        new UUID[]{
                                UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"),    // Battery remaining characteristic
                        },
                        new int[]{
                                DogWatchService.CHARA_BATT_RAMAIN
                        }),
                new ServiceRecord(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"),    // DogWatch Service
                        new UUID[]{
                                UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"),    // CHAR_TIME_UTC
                                UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"),    // CHAR_RF_CALIBRATE
                                UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb"),    // CHAR_RF_TXLEVEL
                                UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"),    // CHAR_VIBRATE_TRIGGER
                                UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb"),    // CHAR_DISCONNECT_ALARM_SWITCH
                        },
                        new int[]{
                                DogWatchService.CHARA_TIME_UTC,
                                DogWatchService.CHARA_RF_CALIBRATE,
                                DogWatchService.CHARA_RF_TXLEVEL,
                                DogWatchService.CHARA_VIBRATE_TRIGGER,
                                DogWatchService.CHARA_DISCONNECT_ALARM_SWITCH,
                        }),
        };
    }


    public class ResloveResult {
        public UUID serviceUUID;
        public UUID characterUUID;

        protected ResloveResult(UUID serviceUUID, UUID characterUUID) {
            this.serviceUUID = serviceUUID;
            this.characterUUID = characterUUID;
        }
    }

    public ResloveResult resloveName(int name) {
        for (ServiceRecord record : services) {
            for (int i = 0; i < record.characteristics.length; ++i) {
                if (name == record.namelist[i])
                    return new ResloveResult(record.service, record.characteristics[i]);
            }
        }
        return null;
    }

    public int resloveUUID(UUID charaUUID) {
        for (ServiceRecord record : services) {
            for (int i = 0; i < record.characteristics.length; ++i) {
                if (charaUUID.equals(record.characteristics[i]))
                    return record.namelist[i];
            }
        }
        return -1;
    }
}