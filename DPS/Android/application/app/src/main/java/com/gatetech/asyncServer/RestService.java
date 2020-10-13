package com.gatetech.asyncServer;

public class RestService {

    public enum Status {
        INITIAL ("initial"),
        SUCCESSFUL ("successful"),
        FAILRESPONSE ("failresponse"),
        FAILURE ("failure");

        private final String stringValue;
        Status(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }

    public enum Action {

        SAVECLIENT ("saveclient"),
        FINDCLIENTS("findclients"),
        DELETECLIENT ("deleteclient"),
        UPDATECLIENT ("updateclient"),
        GETIMAGES ("getimages");

        private final String stringValue;
        Action(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }


}
