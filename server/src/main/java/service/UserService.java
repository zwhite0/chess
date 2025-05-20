package service;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        return new RegisterResult(null,null);
    }

    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult(null,null);
    }

    public void logout(LogoutRequest logoutRequest) {}
}
