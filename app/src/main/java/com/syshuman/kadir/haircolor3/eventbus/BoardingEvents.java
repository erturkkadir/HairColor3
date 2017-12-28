package com.syshuman.kadir.haircolor3.eventbus;

public class BoardingEvents {
    public static class onLoginSuccess {
        public String token;
        public onLoginSuccess(String token) {
            this.token = token;
        }
    }

    public static class onLoginFailed {
        public String error;
        public onLoginFailed(String error) {
            this.error = error;
        }
    }

    public static class onRegisterSuccess {
        public String token;
        public onRegisterSuccess(String token) {
            this.token = token;
        }
    }

    public static class onRegistrationFailed {
        public String error;
        public onRegistrationFailed(String error) {
            this.error = error;
        }
    }

    public static class onForgotEmailSuccess {
        public String message;
        public onForgotEmailSuccess(String message) {
            this.message = message;
        }
    }

    public static class onForgotEmailFailed {
        public String error;
        public onForgotEmailFailed(String error) {
            this.error = error;
        }
    }
}
