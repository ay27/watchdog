package android.os.storage;
interface IMountService {
void setUsbMassStorageEnabled(boolean enable);
int formatVolume(String mountPoint);
void unmountVolume(String mountPoint, boolean force, boolean removeEncryption);
}