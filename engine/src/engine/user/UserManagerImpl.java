package engine.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManagerImpl implements UserManager {

    private final Set<String> usersSet;

    public UserManagerImpl() {
        usersSet = new HashSet<>();
    }

    public synchronized void addUser(String username) {
        usersSet.add(username);
    }

    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usersSet.contains(username);
    }
}
