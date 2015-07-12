package bitman.ay27.watchdog.model;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-12.
 */
public class SignInRecvForm {
    public int err;
    public String msg;
    public User user;

    public class User {
        public Device[] devices;
        public String email;
        public int uid;
        public String username;
    }

    public class Device {
        public String deviceid;
        public long id;
        public LastStatus last_status;
        public long owner;
        public String phone_number;
    }

    public class LastStatus {
        public long time;
        public String event;
    }
}
